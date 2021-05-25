/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core.domain;

import com.philips.research.bombar.core.NotFoundException;
import com.philips.research.bombar.core.PackageService;
import com.philips.research.bombar.core.PackageService.Approval;
import com.philips.research.bombar.core.PersistentStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PackageInteractorTest {
    private static final URI REFERENCE = URI.create("Package/reference");
    private static final PackageRef PACKAGE_REF = new PackageRef(REFERENCE);
    private static final String LICENSE = "License";
    private static final String FRAGMENT = "Fragment";

    private final PersistentStore store = mock(PersistentStore.class);
    private final PackageService interactor = new PackageInteractor(store);
    private final Package pkg = new Package(PACKAGE_REF);

    @BeforeEach
    void beforeEach() {
        when(store.getPackageDefinition(PACKAGE_REF)).thenReturn(Optional.of(pkg));
    }

    @Test
    void throws_getUnknownPackage() {
        assertThatThrownBy(() -> interactor.getPackage(URI.create("Unknown")))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getsPackageDefinition() {
        final var dto = interactor.getPackage(REFERENCE);

        assertThat(dto.reference).isEqualTo(REFERENCE);
    }

    @Test
    void findsPackagesByReferenceFragment() {
        when(store.findPackageDefinitions(FRAGMENT)).thenReturn(List.of(pkg));

        final var results = interactor.findPackages(FRAGMENT);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).reference).isEqualTo(REFERENCE);
    }

    @Test
    void updatesPackageDefinitionApproval() {
        interactor.setApproval(REFERENCE, Approval.APPROVED);

        assertThat(pkg.getAcceptance()).isEqualTo(Package.Acceptance.APPROVED);
    }

    @Test
    void throws_exemptLicenseForUnknownPackage() {
        assertThatThrownBy(() -> interactor.exemptLicense(URI.create("Unknown"), LICENSE))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void exemptsLicenseForPackage() {
        interactor.exemptLicense(REFERENCE, LICENSE);

        assertThat(pkg.isLicenseExempted(LICENSE)).isTrue();
    }

    @Test
    void revokesLicenseExemptionFromPackage() {
        pkg.exemptLicense(LICENSE);

        interactor.unExemptLicense(REFERENCE, LICENSE);

        assertThat(pkg.isLicenseExempted(LICENSE)).isFalse();
    }
}
