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

import com.philips.research.bombar.core.domain.Dependency;

public class LicenseViolation {
    private final Dependency dependency;
    private final String message;

    LicenseViolation(Dependency dependency, String message) {
        this.dependency = dependency;
        this.message = message;
    }

    public Dependency getDependency() {
        return dependency;
    }

    @Override
    public String toString() {
        return "Package " + dependency + " " + message;
    }
}
