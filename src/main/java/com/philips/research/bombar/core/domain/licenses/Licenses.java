/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core.domain.licenses;

import com.philips.research.bombar.core.domain.Project.Distribution;
import com.philips.research.bombar.core.domain.Relation.Relationship;

/**
 * Defines a sample of SPDX licenses, some of their attributes, and their
 * compatibility.
 *
 * <p>It is not the goal of these definitions to provide legal advice about license
 * attributes or license compatibility. If you have any  questions regarding
 * licensing compliance for your code or any other legal issues relating to
 * it, it’s up to you to do further research or consult with a professional.</p>
 *
 * <p>Input was taken from the following sources:
 * <ul>
 *   <li>https://www.gnu.org/licenses/license-list.en.html</li>
 *   <li>https://spdx.org/licenses (License texts for the identifiers)</li>
 *   <li>https://joinup.ec.europa.eu/collection/eupl/solution/joinup-licensing-assistant/jla-find-and-compare-software-licenses</li>
 * </ul>
 * </p>
 */
public class Licenses {
    public static final LicenseRegistry REGISTRY = new LicenseRegistry();
    private static final String PERMISSIVE = "(permissive)";

    private static final String ADVERTISING = "ADVERTISING";
    private static final String PATENTS = "PATENTS";
    private static final String INCLUDES_LICENSE = "INCLUDE_LICENSE";
    private static final String REDISTRIBUTE_CODE = "REDISTRIBUTE_CODE";

    static {
        // Generic terms
        REGISTRY.term(ADVERTISING, "Advertising clause");
        REGISTRY.term(PATENTS, "Patents clause");
        REGISTRY.term(INCLUDES_LICENSE, "Must include copy of original license");
        REGISTRY.term(REDISTRIBUTE_CODE, "Must distribute source code");

        // Permissive licenses
        final var permissive = REGISTRY.license(PERMISSIVE)
                .accepts(ADVERTISING).accepts(PATENTS);
        REGISTRY.license("CC-PDDC", permissive);
        REGISTRY.license("WTFPL", permissive);
        REGISTRY.license("Unlicense", permissive).requires(INCLUDES_LICENSE);
        REGISTRY.license("CC0-1.0", permissive).requires(INCLUDES_LICENSE);
        REGISTRY.license("MIT", permissive).requires(INCLUDES_LICENSE);
        REGISTRY.license("X11", permissive).requires(INCLUDES_LICENSE);
        REGISTRY.license("ISC", permissive).requires(INCLUDES_LICENSE);
        REGISTRY.license("0BSD", permissive).requires(INCLUDES_LICENSE);
        REGISTRY.license("BSD-2-Clause", permissive).requires(INCLUDES_LICENSE);
        REGISTRY.license("BSD-3-Clause", permissive).requires(INCLUDES_LICENSE);
        REGISTRY.license("BSD-4-Clause", permissive).demands(ADVERTISING);
        REGISTRY.license("Python-2.0", permissive).requires(INCLUDES_LICENSE);
        REGISTRY.license("Apache-1.0", permissive);
        REGISTRY.license("Apache-1.1", permissive).requires(INCLUDES_LICENSE);
        REGISTRY.license("Apache-2.0", permissive).demands(PATENTS, Relationship.MODIFIED_CODE);
        REGISTRY.license("AFL-1.1", permissive);
        REGISTRY.license("AFL-1.2", permissive);
        REGISTRY.license("AFL-2.0", permissive);
        REGISTRY.license("AFL-2.1", permissive);
        REGISTRY.license("AFL-3.0", permissive);
        REGISTRY.license("SAX-PD", permissive);
        REGISTRY.license("Artistic-2.0", permissive).requires(INCLUDES_LICENSE);
        REGISTRY.license("NCSA", permissive).requires(INCLUDES_LICENSE);
        REGISTRY.license("Zlib", permissive).requires(INCLUDES_LICENSE);
        REGISTRY.license("BSL-1.0", permissive).requires(INCLUDES_LICENSE);
        REGISTRY.license("JSON", permissive).requires(INCLUDES_LICENSE);
        REGISTRY.license("W3C", permissive).requires(INCLUDES_LICENSE);
        REGISTRY.license("W3C-19980720", permissive);
        REGISTRY.license("W3C-20150513", permissive);
        REGISTRY.license("CC-BY-1.0", permissive).requires(INCLUDES_LICENSE);
        REGISTRY.license("CC-BY-2.0", permissive).requires(INCLUDES_LICENSE);
        REGISTRY.license("CC-BY-2.5", permissive).requires(INCLUDES_LICENSE);
        REGISTRY.license("CC-BY-3.0", permissive).requires(INCLUDES_LICENSE);
        REGISTRY.license("CC-BY-4.0", permissive).requires(INCLUDES_LICENSE);

        // Public domain placeholder for missing SPDX identifier
        // See https://wiki.spdx.org/view/Legal_Team/Decisions/Dealing_with_Public_Domain_within_SPDX_Files
        REGISTRY.license("Public-Domain", permissive).requires(INCLUDES_LICENSE);

        // CDDL licenses
        REGISTRY.license("CDDL-1.0", permissive).copyleft(Relationship.MODIFIED_CODE).requires(REDISTRIBUTE_CODE);
        REGISTRY.license("CDDL-1.1", permissive).copyleft(Relationship.MODIFIED_CODE).requires(REDISTRIBUTE_CODE);

        // LGPL licenses
        final var lgpl3 = REGISTRY.license("LGPL-3.0-only")
                .copyleft(Relationship.STATIC_LINK, Distribution.SAAS)
                .accepts(PATENTS).requires(REDISTRIBUTE_CODE);
        REGISTRY.license("LGPL-3.0-or-later", lgpl3);

        final var lgpl2_1 = REGISTRY.license("LGPL-2.1-only")
                .copyleft(Relationship.STATIC_LINK, Distribution.SAAS)
                .accepts(PATENTS).requires(REDISTRIBUTE_CODE);
        REGISTRY.license("LGPL-2.1-or-later", lgpl2_1)
                .compatibleWith(lgpl3);

        final var lgpl2 = REGISTRY.license("LGPL-2.0-only")
                .copyleft(Relationship.STATIC_LINK, Distribution.SAAS)
                .accepts(PATENTS).requires(REDISTRIBUTE_CODE);
        REGISTRY.license("LGPL-2.0-or-later", lgpl2)
                .compatibleWith(lgpl2_1, lgpl3);

        // GPL licenses
        final var gpl3 = REGISTRY.license("GPL-3.0-only")
                .copyleft(Relationship.DYNAMIC_LINK, Distribution.SAAS)
                .accepts(PATENTS)
                .requires(REDISTRIBUTE_CODE);
        REGISTRY.license("GPL-3.0-or-later", gpl3);

        final var gpl2 = REGISTRY.license("GPL-2.0-only")
                .copyleft(Relationship.DYNAMIC_LINK, Distribution.SAAS)
                .requires(REDISTRIBUTE_CODE);
        REGISTRY.license("GPL-2.0-or-later", gpl2)
                .compatibleWith(gpl3);
        REGISTRY.with("Classpath-exception-2.0", gpl2)
                .copyleft(gpl2, Relationship.STATIC_LINK, Distribution.SAAS)
                .requires(REDISTRIBUTE_CODE);

        final var gpl1 = REGISTRY.license("GPL-1.0-only")
                .copyleft(Relationship.DYNAMIC_LINK, Distribution.SAAS)
                .requires(REDISTRIBUTE_CODE);
        REGISTRY.license("GPL-1.0-or-later", gpl1)
                .compatibleWith(gpl2, gpl3);

        // AGPL licenses
        final var agpl3 = REGISTRY.license("AGPL-3.0-only")
                .copyleft(Relationship.INDEPENDENT)
                .accepts(PATENTS);
        REGISTRY.license("AGPL-3.0-or-later", agpl3);
        final var agpl1 = REGISTRY.license("AGPL-1.0-only")
                .copyleft(Relationship.INDEPENDENT)
                .accepts(PATENTS);
        REGISTRY.license("AGPL-1.0-or-later", agpl1)
                .compatibleWith(agpl3);

        // MPL licenses
        REGISTRY.license("MPL-1.0").copyleft().requires(REDISTRIBUTE_CODE);
        REGISTRY.license("MPL-1.1").copyleft(Relationship.MODIFIED_CODE, Distribution.PROPRIETARY)
                .requires(REDISTRIBUTE_CODE);
        final var mpl2_0 = REGISTRY.license("MPL-2.0").copyleft(Relationship.STATIC_LINK)
                .requires(REDISTRIBUTE_CODE)
                .compatibleWith(lgpl2_1, lgpl3)
                .compatibleWith(gpl2, gpl3)
                .compatibleWith(agpl3);
        REGISTRY.license("MPL-2.0-no-copyleft-exception");

        // CPAL license
        REGISTRY.license("CPAL-1.0").copyleft(Relationship.MODIFIED_CODE, Distribution.SAAS);

        // OSL licenses
        REGISTRY.license("OSL-1.0").copyleft();
        REGISTRY.license("OSL-1.1").copyleft();
        REGISTRY.license("OSL-2.0").copyleft();
        final var osl2_1 = REGISTRY.license("OSL-2.1").copyleft();
        final var osl3_0 = REGISTRY.license("OSL-3.0").copyleft();

        // EPL licenses
        final var cpl1_0 = REGISTRY.license("CPL-1.0").copyleft(Relationship.STATIC_LINK)
                .requires(REDISTRIBUTE_CODE);
        final var epl1_0 = REGISTRY.license("EPL-1.0").copyleft(Relationship.STATIC_LINK)
                .requires(REDISTRIBUTE_CODE);
        final var epl2_0 = REGISTRY.license("EPL-2.0").copyleft(Relationship.STATIC_LINK)
                .requires(REDISTRIBUTE_CODE)
                .compatibleWith(gpl2, gpl3);

        // CECILL licenses
        final var cecill1_0 = REGISTRY.license("CECILL-1.0").copyleft()
                .compatibleWith(gpl1);
        REGISTRY.license("CECILL-1.1", cecill1_0);
        final var cecill2_0 = REGISTRY.license("CECILL-2.0").copyleft()
                .compatibleWith(gpl2, gpl3);
        final var cecill2_1 = REGISTRY.license("CECILL-2.1").copyleft()
                .compatibleWith(gpl2, gpl3)
                .compatibleWith(agpl3);

        // EUPL licenses
        final var eupl1_0 = REGISTRY.license("EUPL-1.0").copyleft()
                .compatibleWith(gpl2)
                .compatibleWith(osl2_1, osl3_0)
                .compatibleWith(cpl1_0)
                .compatibleWith(epl1_0)
                .compatibleWith(cecill2_0);
        final var eupl1_1 = REGISTRY.license("EUPL-1.1").copyleft()
                .compatibleWith(gpl2)
                .compatibleWith(osl2_1, osl3_0)
                .compatibleWith(cpl1_0)
                .compatibleWith(epl1_0)
                .compatibleWith(cecill2_0);
        final var eupl1_2 = REGISTRY.license("EUPL-1.2").copyleft()
                .compatibleWith(gpl2, gpl3)
                .compatibleWith(agpl3)
                .compatibleWith(osl2_1, osl3_0)
                .compatibleWith(epl1_0)
                .compatibleWith(cecill2_0, cecill2_1)
                .compatibleWith(mpl2_0)
                .compatibleWith(lgpl2_1, lgpl3)
                .compatibleWith(eupl1_1);
        cecill2_1.compatibleWith(eupl1_1, eupl1_2);

        // Microsoft licenses
        REGISTRY.license("MS-PL").requires(REDISTRIBUTE_CODE);
        REGISTRY.license("MS-RL").copyleft()
                .requires(REDISTRIBUTE_CODE);
    }
}
