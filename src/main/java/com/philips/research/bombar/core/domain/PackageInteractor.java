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
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PackageInteractor implements PackageService {
    private final PersistentStore store;

    public PackageInteractor(PersistentStore store) {
        this.store = store;
    }

    @Override
    public PackageDto getPackage(URI reference) {
        return DtoConverter.toDto(getPackageDefinition(reference));
    }

    @Override
    public List<PackageDto> findPackages(String fragment) {
        return store.findPackageDefinitions(fragment).stream()
                .map(DtoConverter::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void exemptLicense(URI reference, String license, String rationale) {
        final var pkg = getPackageDefinition(reference);
        pkg.exemptLicense(license, rationale);
    }

    @Override
    public void revokeLicenseExemption(URI reference, String license) {
        final var pkg = getPackageDefinition(reference);
        pkg.removeLicenseExemption(license);
    }

    private PackageDefinition getPackageDefinition(URI reference) {
        return store.getPackageDefinition(reference)
                .orElseThrow(() -> new NotFoundException("Package", reference));
    }
}
