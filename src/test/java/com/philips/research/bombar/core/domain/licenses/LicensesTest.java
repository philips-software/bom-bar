/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core.domain.licenses;

import com.philips.research.bombar.core.domain.Dependency;
import com.philips.research.bombar.core.domain.Project;
import com.philips.research.bombar.core.domain.Relation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class LicensesTest {
    private static final UUID PROJECT_ID = UUID.randomUUID();
    private static final String LICENSE_MIT = "MIT";
    private static final String LICENSE_APACHE_2 = "Apache-2.0";

    final Project project = new Project(PROJECT_ID);
    final Dependency parent = new Dependency(null, "Parent").setLicense(LICENSE_MIT);
    final Dependency dynamicChild = new Dependency(null, "Dynamic child").setLicense(LICENSE_MIT);
    final Dependency staticChild = new Dependency(null, "Static child").setLicense(LICENSE_MIT);

    @BeforeEach
    void beforeEach() {
        project.addDependency(parent).addDependency(dynamicChild).addDependency(staticChild);
        parent.addRelation(new Relation(Relation.Relationship.DYNAMIC_LINK, dynamicChild));
        parent.addRelation(new Relation(Relation.Relationship.STATIC_LINK, staticChild));
    }

    private void assertViolations(String... fragments) {
        final var violations = new LicenseChecker(Licenses.REGISTRY, project).violations();
        assertThat(violations).hasSize(fragments.length);
        for (var fragment : fragments) {
            assertThat(violations.get(0).getMessage()).containsIgnoringCase(fragment);
            violations.remove(0);
        }
    }

    @Test
    void raisesCopyleftViolation() {
        dynamicChild.setLicense("GPL-2.0-only");

        assertThat(new LicenseChecker(Licenses.REGISTRY, project).violations()).hasSize(1);
        assertViolations("incompatible copyleft");
    }

    @Test
    void raisesLgplViolationForStaticLinks() {
        staticChild.setLicense("LGPL-2.1-only");
        dynamicChild.setLicense("LGPL-2.1-only");

        assertViolations("incompatible copyleft");
    }

    @Test
    void acceptsInheritedLicense() {
        parent.setLicense("GPL-2.0-only");
        dynamicChild.setLicense("GPL-2.0-or-later");

        assertViolations();
    }

    @Test
    void acceptsBaseLicense() {
        parent.setLicense("GPL-2.0-or-later");
        dynamicChild.setLicense("GPL-2.0-only");

        assertViolations();
    }

    @Test
    void handlesGplWithClassPathException() {
        dynamicChild.setLicense("GPL-2.0-only WITH Classpath-exception-2.0");

        assertViolations();
    }
}
