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

public interface PackageService {
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
}
