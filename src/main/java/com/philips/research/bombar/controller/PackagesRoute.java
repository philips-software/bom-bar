/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

package com.philips.research.bombar.controller;

import com.philips.research.bombar.core.PackageService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.tlinkowski.annotation.basic.NullOr;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("packages")
public class PackagesRoute {
    private final PackageService service;

    public PackagesRoute(PackageService service) {
        this.service = service;
    }

    @GetMapping
    ResultListJson<PackageJson> findPackages(@RequestParam String id) {
        final var list = service.findPackages(id);
        return new ResultListJson<>(PackageJson.toList(list));
    }

    @GetMapping("{id}")
    PackageJson getPackage(@PathVariable String id) {
        final var reference = toReference(id);
        final var pkg = service.getPackage(reference);
        return new PackageJson(pkg);
    }

    @PostMapping("{id}/approve/{approval}")
    void approvePackage(@PathVariable String id, @PathVariable String approval) {
        final var reference = toReference(id);
        final PackageService.Approval value = toApproval(approval);
        service.setApproval(reference, value);
    }

    private PackageService.Approval toApproval(String approval) {
        try {
            return PackageService.Approval.valueOf(approval.toUpperCase());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "'" + approval + "' is not a valid approval value: "
                            + Arrays.toString(PackageService.Approval.values()).toLowerCase());
        }
    }

    @PostMapping("{id}/license/{license}/exempt")
    void exemptLicense(@PathVariable String id, @PathVariable String license,
                       @RequestBody(required = false) @NullOr RationaleJson body,
                       @RequestParam(required = false, defaultValue = "no") boolean revoke) {
        final var reference = toReference(id);
        if (!revoke) {
            final var rationale = (body != null && body.rationale != null) ? body.rationale : "";
            service.exemptLicense(reference, license, rationale);
        } else {
            service.revokeLicenseExemption(reference, license);
        }
    }

    private URI toReference(String id) {
        return URI.create(URLDecoder.decode(id, StandardCharsets.UTF_8));
    }
}