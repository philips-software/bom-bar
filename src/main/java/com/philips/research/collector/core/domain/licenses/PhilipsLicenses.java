package com.philips.research.collector.core.domain.licenses;

import com.philips.research.collector.core.domain.Package;
import com.philips.research.collector.core.domain.Project;

public class PhilipsLicenses {
    public static final LicenseRegistry REGISTRY = new LicenseRegistry();

    // License categories
    private static final String FORBIDDEN_CATEGORY = "_forbidden";
    private static final String PROPRIETARY_CATEGORY = "_proprietary";
    private static final String RESTRICTED_CATEGORY = "_restricted";
    private static final String RECIPROCAL_CATEGORY = "_reciprocal";
    private static final String NOTICE_CATEGORY = "_notice";
    private static final String UNENCUMBERED_CATEGORY = "_unencumbered";

    // Attributes
    private static final String USE = "use";
    private static final String SOURCE = "source";
    private static final String APPROVAL = "approval";
    private static final String ADVERTISING = "advertising";
    private static final String TAINT = "taint";

    private static final String GPL = "GPL";

    static {
        // Attribute definitions
        REGISTRY.attribute(USE, "Use in code");
        REGISTRY.attribute(SOURCE, "Re-distribution of source code");
        REGISTRY.attribute(APPROVAL, "Approval from IP&S");
        REGISTRY.attribute(ADVERTISING, "Include a copy of the original license");
        REGISTRY.attribute(TAINT, "Alter the license of the combined work");

        // Unencumbered license definitions
        REGISTRY.license(UNENCUMBERED_CATEGORY)
                .require(USE);
        REGISTRY.license("0BSD", UNENCUMBERED_CATEGORY);
        REGISTRY.license("CC0-1.0", UNENCUMBERED_CATEGORY);
        REGISTRY.license("Unlicense", UNENCUMBERED_CATEGORY);
        REGISTRY.license("Public Domain", UNENCUMBERED_CATEGORY); // Not SPDX
        REGISTRY.license("WTFPL", UNENCUMBERED_CATEGORY);
        REGISTRY.license("Beerware", UNENCUMBERED_CATEGORY);

        REGISTRY.license(NOTICE_CATEGORY, UNENCUMBERED_CATEGORY)
                .require(ADVERTISING);
        REGISTRY.license("MIT", NOTICE_CATEGORY);
        REGISTRY.license("X11", NOTICE_CATEGORY);
        REGISTRY.license("MIT-CMU", NOTICE_CATEGORY);
        REGISTRY.license("Apache-1.0", NOTICE_CATEGORY);
        REGISTRY.license("Apache-1.1", NOTICE_CATEGORY);
        REGISTRY.license("BSL-1.0", NOTICE_CATEGORY);
        REGISTRY.license("BSD-2-Clause", NOTICE_CATEGORY);
        REGISTRY.license("BSD-3-Clause", NOTICE_CATEGORY);
        REGISTRY.license("CC-BY-1.0", NOTICE_CATEGORY);
        REGISTRY.license("CC-BY-2.0", NOTICE_CATEGORY);
        REGISTRY.license("CC-BY-2.5", NOTICE_CATEGORY);
        REGISTRY.license("CC-BY-3.0", NOTICE_CATEGORY);
        REGISTRY.license("CC-BY-4.0", NOTICE_CATEGORY);
        REGISTRY.license("ISC", NOTICE_CATEGORY);
        REGISTRY.license("MS-PL", NOTICE_CATEGORY);
        REGISTRY.license("Open-SSL", NOTICE_CATEGORY);
        REGISTRY.license("PHP-3.0", NOTICE_CATEGORY);
        REGISTRY.license("PHP-3.01", NOTICE_CATEGORY);
        REGISTRY.license("zlib", NOTICE_CATEGORY);
        REGISTRY.license("Libpng", NOTICE_CATEGORY);
        REGISTRY.license("JSON", NOTICE_CATEGORY);
        REGISTRY.license("Apache-2.0", NOTICE_CATEGORY)
                .deny(USE, Package.Relation.SOURCE_CODE);
        REGISTRY.license("Artistic-1.0", NOTICE_CATEGORY)
                .deny(USE, Package.Relation.SOURCE_CODE);
        REGISTRY.license("Artistic-2.0", NOTICE_CATEGORY)
                .deny(USE, Package.Relation.SOURCE_CODE);
        REGISTRY.license("AFL-x.y", NOTICE_CATEGORY)
                .deny(USE);
        REGISTRY.license("AFL-1.1", "AFL-x.y");
        REGISTRY.license("AFL-1.2", "AFL-x.y");
        REGISTRY.license("AFL-2.0", "AFL-x.y");
        REGISTRY.license("AFL-2.1", "AFL-x.y");
        REGISTRY.license("AFL-3.0", "AFL-x.y");

        REGISTRY.license(RECIPROCAL_CATEGORY, NOTICE_CATEGORY)
                .require(SOURCE)
                .require(APPROVAL, Package.Relation.SOURCE_CODE);
        REGISTRY.license("CDDL-1.0", RECIPROCAL_CATEGORY);
        REGISTRY.license("CDDL-1.1", RECIPROCAL_CATEGORY);
        REGISTRY.license("MPL-1.0", RECIPROCAL_CATEGORY);
        REGISTRY.license("MPL-1.1", RECIPROCAL_CATEGORY);
        REGISTRY.license("MPL-2.0", RECIPROCAL_CATEGORY);
        REGISTRY.license("EPL-1.0", RECIPROCAL_CATEGORY);
        REGISTRY.license("EPL-2.0", RECIPROCAL_CATEGORY);
        REGISTRY.license("CPL-1.0", RECIPROCAL_CATEGORY);
        REGISTRY.license("BSD-2-Clause-Patent", RECIPROCAL_CATEGORY);
        REGISTRY.license("SPL-1.0", RECIPROCAL_CATEGORY);
        REGISTRY.license("MS-RL", RECIPROCAL_CATEGORY);
        REGISTRY.license("SISSL", RECIPROCAL_CATEGORY);
        REGISTRY.license("SISSL-1.2", RECIPROCAL_CATEGORY);
        REGISTRY.license("CECILL-C", RECIPROCAL_CATEGORY);

        REGISTRY.license(RESTRICTED_CATEGORY, RECIPROCAL_CATEGORY)
                .deny(USE, Package.Relation.STATIC_LINK, Project.Distribution.SAAS)
                .require(TAINT, Project.Distribution.SAAS)
                .require(APPROVAL);
        REGISTRY.license("LGPL-2.0-only", RESTRICTED_CATEGORY);
        REGISTRY.license("LGPL-2.0-or-later", RESTRICTED_CATEGORY);
        REGISTRY.license("LGPL-2.1-only", RESTRICTED_CATEGORY);
        REGISTRY.license("LGPL-2.1-or-later", RESTRICTED_CATEGORY);
        REGISTRY.license("LGPL-3.0-only", RESTRICTED_CATEGORY);
        REGISTRY.license("LGPL-3.0-or-later", RESTRICTED_CATEGORY);
        REGISTRY.license("CC-BY-SA-1.0", RESTRICTED_CATEGORY);
        REGISTRY.license("CC-BY-SA-2.0", RESTRICTED_CATEGORY);
        REGISTRY.license("CC-BY-SA-2.5", RESTRICTED_CATEGORY);
        REGISTRY.license("CC-BY-SA-3.0", RESTRICTED_CATEGORY);
        REGISTRY.license("CC-BY-SA-4.0", RESTRICTED_CATEGORY);
        REGISTRY.license("Sleepycat", RESTRICTED_CATEGORY);
        REGISTRY.license(GPL, RESTRICTED_CATEGORY)
                .deny(USE, Package.Relation.INDEPENDENT);
        REGISTRY.license("GPL-1.0-only", GPL);
        REGISTRY.license("GPL-1.0-or-later", GPL);
        REGISTRY.license("GPL-2.0-only", GPL);
        REGISTRY.license("GPL-2.0-or-later", GPL);
        REGISTRY.license("GPL-3.0-only", GPL);
        REGISTRY.license("GPL-3.0-or-later", GPL);

        REGISTRY.license(PROPRIETARY_CATEGORY)
                .deny(TAINT);
        REGISTRY.license("Proprietary", PROPRIETARY_CATEGORY);
        REGISTRY.license("Internal", PROPRIETARY_CATEGORY);

        REGISTRY.license(FORBIDDEN_CATEGORY)
                .deny(USE);
        REGISTRY.license("APSL-1.0", FORBIDDEN_CATEGORY);
        REGISTRY.license("APSL-1.1", FORBIDDEN_CATEGORY);
        REGISTRY.license("APSL-1.2", FORBIDDEN_CATEGORY);
        REGISTRY.license("APSL-2.0", FORBIDDEN_CATEGORY);
        REGISTRY.license("OSL-1.0", FORBIDDEN_CATEGORY);
        REGISTRY.license("OSL-1.1", FORBIDDEN_CATEGORY);
        REGISTRY.license("OSL-2.0", FORBIDDEN_CATEGORY);
        REGISTRY.license("OSL-2.1", FORBIDDEN_CATEGORY);
        REGISTRY.license("OSL-3.0", FORBIDDEN_CATEGORY);
        REGISTRY.license("QPL-1.0", FORBIDDEN_CATEGORY);
        REGISTRY.license("AGPL-1.0-only", FORBIDDEN_CATEGORY);
        REGISTRY.license("AGPL-1.0-or-later", FORBIDDEN_CATEGORY);
        REGISTRY.license("AGPL-3.0-only", FORBIDDEN_CATEGORY);
        REGISTRY.license("AGPL-3.0-or-later", FORBIDDEN_CATEGORY);
        REGISTRY.license("SSPL-1.0", FORBIDDEN_CATEGORY);
        REGISTRY.license("CPAL-1.0", FORBIDDEN_CATEGORY);
        REGISTRY.license("EUPL-1.0", FORBIDDEN_CATEGORY);
        REGISTRY.license("EUPL-1.1", FORBIDDEN_CATEGORY);
        REGISTRY.license("EUPL-1.2", FORBIDDEN_CATEGORY);
    }
}
