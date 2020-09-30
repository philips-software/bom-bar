/*
 * Copyright (c) 2020-2020, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.collector.core.domain;

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
