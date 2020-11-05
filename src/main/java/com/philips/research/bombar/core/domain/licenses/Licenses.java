/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

package com.philips.research.bombar.core.domain.licenses;

import com.philips.research.bombar.core.domain.Project.Distribution;
import com.philips.research.bombar.core.domain.Relation.Relationship;

public class Licenses {
    public static final LicenseRegistry REGISTRY = new LicenseRegistry();
    private static final String PERMISSIVE = "(permissive)";

    private static final String ADVERTISING = "ADVERTISING";
    private static final String PATENTS = "PATENTS";

    static {
        // Generic terms
        REGISTRY.term(ADVERTISING, "Advertising clause");
        REGISTRY.term(PATENTS, "Patents clause");

        // Permissive licenses
        final var permissive = REGISTRY.license(PERMISSIVE)
                .accept(ADVERTISING).accept(PATENTS);
        REGISTRY.license("CC-PDDC", permissive);
        REGISTRY.license("WTFPL", permissive);
        REGISTRY.license("Unlicense", permissive);
        REGISTRY.license("CC0-1.0", permissive);
        REGISTRY.license("MIT", permissive);
        REGISTRY.license("X11", permissive);
        REGISTRY.license("ISC", permissive);
        REGISTRY.license("0BSD", permissive);
        REGISTRY.license("BSD-2-Clause", permissive);
        REGISTRY.license("BSD-3-Clause", permissive);
        REGISTRY.license("BSD-4-Clause", permissive).demand(ADVERTISING);
        REGISTRY.license("Python-2.0", permissive);
        REGISTRY.license("Apache-1.0", permissive);
        REGISTRY.license("Apache-1.1", permissive);
        REGISTRY.license("Apache-2.0", permissive).demand(PATENTS, Relationship.MODIFIED_CODE);
        REGISTRY.license("AFL-1.1", permissive);
        REGISTRY.license("AFL-1.2", permissive);
        REGISTRY.license("AFL-2.0", permissive);
        REGISTRY.license("AFL-2.1", permissive);
        REGISTRY.license("AFL-3.0", permissive);
        REGISTRY.license("SAX-PD", permissive);

        REGISTRY.license("CDDL-1.0", permissive).copyleft(Relationship.MODIFIED_CODE);
        REGISTRY.license("CDDL-1.1", permissive).copyleft(Relationship.MODIFIED_CODE);

        // LGPL licenses
        final var lgpl3 = REGISTRY.license("LGPL-3.0-only")
                .copyleft(Relationship.STATIC_LINK, Distribution.SAAS)
                .accept(PATENTS);
        REGISTRY.license("LGPL-3.0-or-later", lgpl3);

        final var lgpl2_1 = REGISTRY.license("LGPL-2.1-only")
                .copyleft(Relationship.STATIC_LINK, Distribution.SAAS)
                .accept(PATENTS);
        REGISTRY.license("LGPL-2.1-or-later", lgpl2_1)
                .compatibleWith(lgpl3);

        final var lgpl2 = REGISTRY.license("LGPL-2.0-only")
                .copyleft(Relationship.STATIC_LINK, Distribution.SAAS)
                .accept(PATENTS);
        REGISTRY.license("LGPL-2.0-or-later", lgpl2)
                .compatibleWith(lgpl2_1, lgpl3);

        // GPL licenses
        final var gpl3 = REGISTRY.license("GPL-3.0-only")
                .copyleft(Relationship.DYNAMIC_LINK, Distribution.SAAS)
                .accept(PATENTS);
        REGISTRY.license("GPL-3.0-or-later", gpl3);

        final var gpl2 = REGISTRY.license("GPL-2.0-only")
                .copyleft(Relationship.DYNAMIC_LINK, Distribution.SAAS);
        REGISTRY.license("GPL-2.0-or-later", gpl2)
                .compatibleWith(gpl3);
        REGISTRY.with("Classpath-exception-2.0", gpl2)
                .copyleft(gpl2, Relationship.STATIC_LINK, Distribution.SAAS);

        final var gpl1 = REGISTRY.license("GPL-1.0-only")
                .copyleft(Relationship.DYNAMIC_LINK, Distribution.SAAS);
        REGISTRY.license("GPL-1.0-or-later", gpl1)
                .compatibleWith(gpl2, gpl3);

        // AGPL licenses
        final var agpl3 = REGISTRY.license("AGPL-3.0-only")
                .copyleft(Relationship.INDEPENDENT)
                .accept(PATENTS);
        REGISTRY.license("AGPL-3.0-or-later", agpl3);
        final var agpl1 = REGISTRY.license("AGPL-1.0-only")
                .copyleft(Relationship.INDEPENDENT)
                .accept(PATENTS);
        REGISTRY.license("AGPL-1.0-or-later", agpl1)
                .compatibleWith(agpl3);

        // CECILL licenses
        final var cecill1 = REGISTRY.license("CECILL-1.0").copyleft();
        REGISTRY.license("CECILL-1.1").copyleft(cecill1)
                .compatibleWith(gpl1);
        final var cecill2 = REGISTRY.license("CECILL-2.0").copyleft()
                .compatibleWith(gpl2);
        final var cecill2_1 = REGISTRY.license("CECILL-2.1").copyleft()
                .accept(cecill2)
                .compatibleWith(gpl2, gpl3)
                .compatibleWith(agpl3);

        // MPL licenses
        final var mpl1_0 = REGISTRY.license("MPL-1.0").copyleft()
                .compatibleWith(gpl2);
        REGISTRY.license("MPL-1.1").copyleft(mpl1_0)
                .compatibleWith(gpl2);
        REGISTRY.license("MPL-2.0").copyleft(Relationship.MODIFIED_CODE)
                .compatibleWith(lgpl2, lgpl2_1, lgpl3)
                .compatibleWith(gpl3)
                .compatibleWith(agpl3);

        // EPL licenses
        REGISTRY.license("EPL-1.0").copyleft(Relationship.MODIFIED_CODE);
        REGISTRY.license("EPL-2.0").copyleft(Relationship.MODIFIED_CODE)
                .compatibleWith(gpl2);

        // EUPL licenses
        REGISTRY.license("EUPL-1.0").copyleft();
        REGISTRY.license("EUPL-1.1").copyleft()
                .accept(cecill2_1);
        REGISTRY.license("EUPL-1.2").copyleft()
                .accept(cecill2_1)
                .compatibleWith(gpl3)
                .compatibleWith(agpl3);

        //TODO This is just a (non-SPDX) placeholder
        REGISTRY.license("Proprietary").copyleft();
    }
}
