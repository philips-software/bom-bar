package com.philips.research.collector.core.domain.licenses;

import com.philips.research.collector.core.domain.Package;
import com.philips.research.collector.core.domain.Project;
import org.junit.jupiter.api.Test;

import static com.philips.research.collector.core.domain.licenses.PhilipsLicenses.REGISTRY;
import static org.assertj.core.api.Assertions.assertThat;

class PhilipsLicensesTest {

    @Test
    void productWithGplParts() {
        final var eval = REGISTRY.evaluate("Proprietary")
                .and("innocent", "MIT")
                .and("allowed", "LGPL-2.1-only", Package.Relation.DYNAMIC_LINK)
                .and("not allowed", "GPL-2.0-only");

        assertThat(eval.getViolations()).hasSize(1);
    }

    @Test
    void openSourceWithGplParts() {
        final var eval = REGISTRY.evaluate("GPL-1.0-only", Project.Distribution.OPEN_SOURCE)
                .and("innocent", "GPL-1.0-only");

        assertThat(eval.getViolations()).isEmpty();
    }
}
