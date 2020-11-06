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

import org.junit.jupiter.api.Test;

import static com.philips.research.bombar.core.domain.licenses.Licenses.REGISTRY;
import static org.assertj.core.api.Assertions.assertThat;

class LicensesTest {
    private static final LicenseType LGPL_2 = REGISTRY.licenseType("LGPL-2.0-only");
    private static final LicenseType LGPL_2_PLUS = REGISTRY.licenseType("LGPL-2.0-or-later");
    private static final LicenseType LGPL_3 = REGISTRY.licenseType("LGPL-3.0-only");

    @Test
    void definesLgplOrLater() {
        assertCompatible(LGPL_2, LGPL_2_PLUS);
        assertCompatible(LGPL_2_PLUS, LGPL_2);
        System.out.println("2+ demands:" + LGPL_2_PLUS.demandsGiven());
        System.out.println("3 accepts:" + LGPL_3.accepts());
        assertCompatible(LGPL_2_PLUS, LGPL_3);
        assertIncompatible(LGPL_3, LGPL_2_PLUS);
    }

    void assertCompatible(LicenseType from, LicenseType to) {
        assertThat(to.issuesAccepting(from))
                .withFailMessage("Expecting " + from + " to be compatible with " + to)
                .isEmpty();
    }

    void assertIncompatible(LicenseType from, LicenseType to) {
        assertThat(to.issuesAccepting(from))
                .withFailMessage("Expecting " + from + " NOT to be compatible with " + to)
                .isNotEmpty();
    }
}
