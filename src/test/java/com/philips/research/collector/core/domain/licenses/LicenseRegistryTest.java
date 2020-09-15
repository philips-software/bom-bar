package com.philips.research.collector.core.domain.licenses;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LicenseRegistryTest {

    private static final String TAG_A = "Tag A";
    private static final String TAG_B = "Tag B";
    private static final String TAG_C = "Tag C";
    private static final String LICENSE = "License";

    private final LicenseRegistry registry = new LicenseRegistry();

    private enum Condition {LOWER, VALUE, HIGHER}

    @Nested
    class BuildRegistry {
        @Test
        void registersAttributes() {
            final var attr1 = registry.attribute(TAG_A, "A");
            final var attr2 = registry.attribute(TAG_B, "B");

            assertThat(registry.getAttributes()).containsExactlyInAnyOrder(attr1, attr2);
        }

        @Test
        void throws_duplicateAttribute() {
            registry.attribute(TAG_A, "Description");
            assertThatThrownBy(() -> registry.attribute(TAG_A.toUpperCase(), "Description"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("duplicate");
        }

        @Test
        void registersLicense() {
            registry.license("x");
            registry.license("zzz");
            registry.license("Y");

            assertThat(registry.getLicenses()).containsExactly("x", "Y", "zzz");
        }

        @Test
        void throws_duplicateLicense() {
            registry.license(LICENSE);
            assertThatThrownBy(() -> registry.license(LICENSE.toUpperCase()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("duplicate");
        }

        @Test
        @Disabled
        void registersInheritedLicense() {
            final var attrA = registry.attribute(TAG_A, "Description");
            final var attrB = registry.attribute(TAG_B, "Description");
            registry.license("Parent").require(TAG_A);
            registry.license(LICENSE, "Parent").require((TAG_B));

//            assertThat(registry.requires(LICENSE)).containsExactlyInAnyOrder(attrA, attrB);
        }

        @Test
        void throwIfParentUnknown() {
            assertThatThrownBy(() -> registry.license(LICENSE, "Parent"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Parent")
                    .hasMessageContaining("Unknown reference");
        }
    }

    @Nested
    class LicenseComparison {
        private static final String OTHER = "Other";

        @Test
        void comparesLicenses() {
            final var attr = registry.attribute(TAG_A, "A");
            registry.attribute(TAG_B, "B");
            registry.license(LICENSE).require(TAG_A).require(TAG_B);
            registry.license(OTHER).deny(TAG_A).require(TAG_B);

            final var conflicts = registry.compare(LICENSE, OTHER);

            assertThat(conflicts).containsExactly(attr);
        }
    }

    @Nested
    class LicenseEvaluation {
        private static final String PRODUCT = "Product";
        private static final String PACKAGE = "Package";
        private static final String LICENSE2 = "License 2";

        final Attribute attrA = registry.attribute(TAG_A, "A");
        final Attribute attrB = registry.attribute(TAG_B, "B");
        final Attribute attrC = registry.attribute(TAG_C, "C");

        @Test
        void raisesConflictsWithProductLicense() {
            registry.license(PRODUCT).require(TAG_A);
            registry.license(LICENSE).deny(TAG_A, Condition.VALUE).require(TAG_B);
            registry.license(LICENSE2).deny(TAG_B);

            final var eval = registry.evaluate(PRODUCT)
                    .and(PACKAGE, LICENSE, Condition.HIGHER)
                    .and("Other", LICENSE2);

            assertThat(eval.getViolations()).hasSize(1);
            final var violation = eval.getViolations().get(0);
            assertThat(violation.getPackage()).isEqualTo(PACKAGE);
            assertThat(violation.getLicense()).isEqualTo(LICENSE);
            assertThat(violation.getAttributes()).containsExactly(attrA);
        }

        @Test
        void raisesIncompatibilitiesBetweenPackages() {
            registry.license(PRODUCT);
            registry.license(LICENSE).require(TAG_A).deny(TAG_B).require(TAG_C);
            registry.license(LICENSE2).deny(TAG_A).require(TAG_B).require(TAG_C);

            final var eval = registry.evaluate(PRODUCT)
                    .and("X", LICENSE)
                    .and("Y", LICENSE2);

            assertThat(eval.getViolations()).isEmpty();
            assertThat(eval.incompatibilities()).containsExactlyInAnyOrder(attrA, attrB);
            assertThat(eval.requires()).containsExactlyInAnyOrder(attrA, attrB, attrC);
        }
    }
}
