/*
 * Copyright (c) 2020-2020, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.collector.core.domain.licenses;

import com.philips.research.collector.core.domain.Project;
import com.philips.research.collector.core.domain.Relation;

public class Licenses {
    public static final LicenseRegistry REGISTRY = new LicenseRegistry();
    private static final String PERMISSIVE = "(permissive)";

    private static final String ADVERTISING = "ADVERTISING";
    private static final String PATENTS = "PATENTS";

    static {
        REGISTRY.term(ADVERTISING, "Advertising clause");
        REGISTRY.term(PATENTS, "Patents clause");

        REGISTRY.license(PERMISSIVE)
                .accept(ADVERTISING).accept(PATENTS);
        REGISTRY.license("CC-PDDC", PERMISSIVE);
        REGISTRY.license("CC0-1.0", PERMISSIVE);
        REGISTRY.license("MIT", PERMISSIVE);
        REGISTRY.license("X11", PERMISSIVE);
        REGISTRY.license("0BSD", PERMISSIVE);
        REGISTRY.license("BSD-2-Clause", PERMISSIVE);
        REGISTRY.license("BSD-3-Clause", PERMISSIVE);
        REGISTRY.license("BSD-4-Clause", PERMISSIVE).demand(ADVERTISING);
        REGISTRY.license("Python-2.0", PERMISSIVE);
        REGISTRY.license("Apache-1.0", PERMISSIVE);
        REGISTRY.license("Apache-1.1", PERMISSIVE);
        REGISTRY.license("Apache-2.0", PERMISSIVE).demand(PATENTS);
        REGISTRY.license("AFL-3.0", PERMISSIVE);

        REGISTRY.license("CDDL-1.0", PERMISSIVE).copyleft();
        REGISTRY.license("CDDL-1.1", PERMISSIVE).copyleft();

        final var cecill1 = REGISTRY.license("CECILL-1.0").copyleft();
        REGISTRY.license("CECILL-1.1").copyleft(cecill1);
        final var cecill2 = REGISTRY.license("CECILL-2.0").copyleft();
        final var cecill2_1 = REGISTRY.license("CECILL-2.1").copyleft()
                .accept(cecill2);

        final var mpl1_0 = REGISTRY.license("MPL-1.0").copyleft();
        REGISTRY.license("MPL-1.1").copyleft(mpl1_0);
        final var mpl2_0 = REGISTRY.license("MPL-2.0").copyleft();

        REGISTRY.license("EUPL-1.0").copyleft();
        REGISTRY.license("EUPL-1.1").copyleft()
                .accept(cecill2_1);
        final var eupl1_2 = REGISTRY.license("EUPL-1.2").copyleft()
                .accept(cecill2_1);

        final var lgpl2 = REGISTRY.license("LGPL-2.0-only")
                .copyleft(Relation.Type.STATIC_LINK, Project.Distribution.SAAS);
        final var lgpl2plus = REGISTRY.license("LGPL-2.0-or-later")
                .copyleft(Relation.Type.STATIC_LINK, Project.Distribution.SAAS)
                .accept(lgpl2);
        lgpl2.accept(lgpl2plus);
        REGISTRY.license("LGPL-2.1-only")
                .copyleft(lgpl2, Relation.Type.STATIC_LINK, Project.Distribution.SAAS)
                .accept(lgpl2plus)
                .accept(mpl2_0);
        REGISTRY.license("LGPL-2.1-or-later")
                .copyleft(lgpl2plus, Relation.Type.STATIC_LINK, Project.Distribution.SAAS)
                .accept(lgpl2)
                .accept(mpl2_0);
        final var lgpl3 = REGISTRY.license("LGPL-3.0-only")
                .copyleft(Relation.Type.STATIC_LINK, Project.Distribution.SAAS)
                .accept(lgpl2).accept(lgpl2plus)
                .accept(mpl2_0)
                .accept(PATENTS);
        final var lgpl3plus = REGISTRY.license("LGPL-3.0-or-later")
                .copyleft(Relation.Type.STATIC_LINK, Project.Distribution.SAAS)
                .accept(lgpl3)
                .accept(lgpl2).accept(lgpl2plus)
                .accept(mpl2_0)
                .accept(PATENTS);
        lgpl3.accept(lgpl3plus);

        final var gpl1 = REGISTRY.license("GPL-1.0-only")
                .copyleft(Relation.Type.INDEPENDENT)
                .accept(cecill1);
        final var gpl1plus = REGISTRY.license("GPL-1.0-or-later")
                .copyleft(Relation.Type.INDEPENDENT)
                .accept(gpl1)
                .accept(cecill1);
        gpl1.accept(gpl1plus);
        final var gpl2 = REGISTRY.license("GPL-2.0-only")
                .copyleft(Relation.Type.INDEPENDENT)
                .accept(gpl1plus)
                .accept(eupl1_2)
                .accept(cecill2)
                .accept(cecill2_1)
                .accept(mpl1_0);
        final var gpl2plus = REGISTRY.license("GPL-2.0-or-later")
                .copyleft(Relation.Type.INDEPENDENT)
                .accept(gpl2)
                .accept(lgpl2).accept(lgpl2plus)
                .accept(gpl1plus)
                .accept(eupl1_2)
                .accept(cecill2).accept(cecill2_1)
                .accept(mpl2_0);
        gpl2.accept(lgpl2plus);
        final var gpl3 = REGISTRY.license("GPL-3.0-only")
                .copyleft(Relation.Type.INDEPENDENT)
                .accept(lgpl3).accept(lgpl3plus)
                .accept(lgpl2plus)
                .accept(eupl1_2)
                .accept(cecill2).accept(cecill2_1)
                .accept(mpl2_0)
                .accept(PATENTS);
        final var gpl3plus = REGISTRY.license("GPL-3.0-or-later")
                .copyleft(Relation.Type.INDEPENDENT)
                .accept(gpl3)
                .accept(lgpl2plus)
                .accept(lgpl3).accept(lgpl3plus)
                .accept(gpl1plus).accept(gpl2plus)
                .accept(eupl1_2)
                .accept(cecill2).accept(cecill2_1)
                .accept(mpl2_0)
                .accept(PATENTS);
        gpl3.accept(gpl3plus);

        final var agpl1 = REGISTRY.license("AGPL-1.0-only")
                .copyleft(Relation.Type.UNRELATED);
        final var agpl1plus = REGISTRY.license("AGPL-1.0-or-later")
                .copyleft(Relation.Type.UNRELATED)
                .accept(agpl1);
        agpl1.accept(agpl1plus);
        final var agpl3 = REGISTRY.license("AGPL-3.0-only")
                .copyleft(Relation.Type.UNRELATED)
                .accept(agpl1plus)
                .accept(lgpl3).accept(lgpl3plus)
                .accept(gpl3).accept(gpl3plus)
                .accept(eupl1_2)
                .accept(cecill2_1)
                .accept(mpl2_0)
                .accept(PATENTS);
        final var agpl3plus = REGISTRY.license("AGPL-3.0-or-later")
                .copyleft(Relation.Type.UNRELATED)
                .accept(agpl3)
                .accept(agpl1plus)
                .accept(lgpl3).accept(lgpl3plus)
                .accept(gpl3).accept(gpl3plus)
                .accept(eupl1_2)
                .accept(cecill2_1)
                .accept(mpl2_0)
                .accept(PATENTS);
        agpl3.accept(agpl3plus);
    }
}
