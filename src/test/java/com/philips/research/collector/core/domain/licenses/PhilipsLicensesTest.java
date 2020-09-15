
package com.philips.research.collector.core.domain.licenses;

import com.philips.research.collector.core.domain.Package;
import com.philips.research.collector.core.domain.Project;
import org.junit.jupiter.api.Test;

import static com.philips.research.collector.core.domain.licenses.PhilipsLicenses.REGISTRY;
import static org.assertj.core.api.Assertions.assertThat;

class PhilipsLicensesTest {
    @Test
    void openSourceWithStaticLGPL() {
        assertThat(REGISTRY.evaluate("LGPL-2.1-only", Project.Distribution.OPEN_SOURCE)
                .and("innocent","MIT", Package.Relation.SOURCE_CODE)
                .and("LGPL", "LGPL-2.1-only", Package.Relation.STATIC_LINK)
                .getViolations()).isEmpty();
    }

    @Test
    void proprietaryWithStaticLink() {
        assertThat(REGISTRY.evaluate("Proprietary", Project.Distribution.PROPRIETARY)
                .and("innocent","MIT", Package.Relation.SOURCE_CODE)
                .and("LGPL", "LGPL-2.1-only", Package.Relation.STATIC_LINK)
                .getViolations()).isNotEmpty();
    }

    @Test
    void proprietaryWithDynamicLink() {
        assertThat(REGISTRY.evaluate("Proprietary", Project.Distribution.PROPRIETARY)
                .and("innocent","MIT", Package.Relation.SOURCE_CODE)
                .and("LGPL", "LGPL-2.1-only", Package.Relation.DYNAMIC_LINK)
                .getViolations()).isEmpty();
    }
}
