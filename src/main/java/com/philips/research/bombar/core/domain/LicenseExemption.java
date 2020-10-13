/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

package com.philips.research.bombar.core.domain;

public class LicenseExemption {
    private String name;
    private String rationale;

    public LicenseExemption(String name, String rationale) {
        this.name = name;
        this.rationale = rationale;
    }

    public String getName() {
        return name;
    }

    public LicenseExemption setName(String name) {
        this.name = name;
        return this;
    }

    public String getRationale() {
        return rationale;
    }

    public LicenseExemption setRationale(String rationale) {
        this.rationale = rationale;
        return this;
    }
}
