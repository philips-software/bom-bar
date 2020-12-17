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
import com.philips.research.bombar.core.domain.PackageDefinition.Acceptance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PackageInteractor implements PackageService {
    private static final Logger LOG = LoggerFactory.getLogger(PackageInteractor.class);

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
    public void exemptLicense(URI reference, String license) {
        final var pkg = getPackageDefinition(reference);
        pkg.exemptLicense(license);
        LOG.info("Exempted license '{}' for package {}", license, reference);
    }

    @Override
    public void unExemptLicense(URI reference, String license) {
        final var pkg = getPackageDefinition(reference);
        pkg.removeLicenseExemption(license);
        LOG.info("Revoked license '{}' exemption for package {}", license, reference);
    }

    @Override
    public void setApproval(URI reference, Approval approval) {
        final var pkg = getPackageDefinition(reference);
        pkg.setAcceptance(map(approval));
        LOG.info("Updated approval of {} to {}", reference, approval.name());
    }

    Acceptance map(Approval approval) {
        switch (approval) {
            case APPROVED:
                return Acceptance.APPROVED;
            case NEEDS_APPROVAL:
                return Acceptance.PER_PROJECT;
            case REJECTED:
                return Acceptance.FORBIDDEN;
            case NOT_A_PACKAGE:
                return Acceptance.NOT_A_PACKAGE;
            case CONTEXT:
            default:
                return Acceptance.DEFAULT;
        }
    }

    private PackageDefinition getPackageDefinition(URI reference) {
        return store.getPackageDefinition(reference)
                .orElseThrow(() -> new NotFoundException("Package", reference));
    }
}
