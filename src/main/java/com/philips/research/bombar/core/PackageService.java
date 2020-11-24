/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

package com.philips.research.bombar.core;

import pl.tlinkowski.annotation.basic.NullOr;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface PackageService {

    /**
     * @param reference
     * @return package for the given reference
     */
    PackageDto getPackage(String reference);

    /**
     * Searches for packages by a fragment of their reference.
     *
     * @param reference
     * @return all matching packages
     */
    List<PackageDto> findPackages(String reference);

    /**
     * Accepts a license for the package.
     *
     * @param reference package identifier
     * @param license   license identifier
     * @param rationale context information for the exemption
     */
    void exemptLicense(String reference, String license, String rationale);

    /**
     * Revokes a license override for a package.
     *
     * @param reference package identifier
     * @param license   license identifier
     */
    void revokeLicenseExemption(String reference, String license);

    @SuppressWarnings("NotNullFieldNotInitialized")
    class PackageDto {
        public String reference;
        public Map<String, String> licenseExemptions = new HashMap<>();
        public String name;
        public @NullOr String vendor;
        public @NullOr URL homepage;
    }
}
