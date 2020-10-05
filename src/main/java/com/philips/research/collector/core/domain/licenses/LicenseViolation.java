/*
 * Copyright (c) 2020-2020, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.collector.core.domain.licenses;

import com.philips.research.collector.core.domain.Dependency;

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
        return "Package" + dependency + " " + message;
    }
}
