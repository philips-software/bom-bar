/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core;

import pl.tlinkowski.annotation.basic.NullOr;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public interface PackageService {

    /**
     * @param reference
     * @return package for the given reference
     */
    PackageDto getPackage(URI reference);

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
     */
    void exemptLicense(URI reference, String license);

    /**
     * Revokes a license override for a package.
     *
     * @param reference package identifier
     * @param license   license identifier
     */
    void unExemptLicense(URI reference, String license);

    /**
     * Updates approval of the package.
     *
     * @param reference package identifier
     * @param approval  updated status
     */
    void setApproval(URI reference, Approval approval);

    enum Approval {
        CONTEXT, REJECTED, NEEDS_APPROVAL, APPROVED, NOT_A_PACKAGE
    }

    @SuppressWarnings("NotNullFieldNotInitialized")
    class PackageDto {
        public URI reference;
        public List<String> licenseExemptions = new ArrayList<>();
        public String name;
        public @NullOr String vendor;
        public @NullOr URI homepage;
        public @NullOr String description;
        public Approval approval;
    }
}
