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

public class Exemption {
    private final String key;
    private final String rationale;

    public Exemption(String key, String rationale) {
        this.key = key;
        this.rationale = rationale;
    }

    public String getKey() {
        return key;
    }

    public String getRationale() {
        return rationale;
    }
}
