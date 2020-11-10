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

import com.philips.research.bombar.core.NotFoundException;
import com.philips.research.bombar.core.PackageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PackageInteractorTest {
    private static final String REFERENCE = "Package reference";
    private static final String LICENSE = "License";
    private static final String RATIONALE = "Rationale";

    private final PersistentStore store = mock(PersistentStore.class);
    private final PackageService interactor = new PackageInteractor(store);
    private final PackageDefinition pkg = new PackageDefinition(REFERENCE);

    @BeforeEach
    void beforeEach() {
        when(store.getPackageDefinition(REFERENCE)).thenReturn(Optional.of(pkg));
    }

    @Test
    void throws_exemptLicenseForUnknownPackage() {
        assertThatThrownBy(() -> interactor.exemptLicense("Unknown", LICENSE, RATIONALE))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void exemptsLicenseForPackage() {
        interactor.exemptLicense(REFERENCE, LICENSE, RATIONALE);

        assertThat(pkg.isLicenseExempted(LICENSE)).isTrue();
        assertThat(pkg.getLicenseExemptions().get(0).getRationale()).isEqualTo(RATIONALE);
    }

    @Test
    void revokesLicenseExemptionFromPackage() {
        pkg.exemptLicense(LICENSE, RATIONALE);

        interactor.revokeLicenseExemption(REFERENCE, LICENSE);

        assertThat(pkg.isLicenseExempted(LICENSE)).isFalse();
    }
}
