package com.philips.research.collector.core.domain.licenses;

import com.philips.research.collector.core.domain.Package;

public class LicenseViolation {
    private final Package pkg;
    private final String message;

    LicenseViolation(Package pkg, String message) {
        this.pkg = pkg;
        this.message = message;
    }

    public Package getPkg() {
        return pkg;
    }

    @Override
    public String toString() {
        return "Package " + pkg + " " + message;
    }
}
