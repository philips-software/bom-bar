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
import org.springframework.web.bind.annotation.*;
import pl.tlinkowski.annotation.basic.NullOr;

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

    @GetMapping("{reference}")
    PackageJson getPackage(@PathVariable String reference) {
        final var pkg = service.getPackage(reference);
        return new PackageJson(pkg);
    }

    @PostMapping("{reference}/license/{license}/exempt")
    void exemptLicense(@PathVariable String reference, @PathVariable String license,
                       @RequestBody(required = false) @NullOr RationaleJson body,
                       @RequestParam(required = false, defaultValue = "no") boolean revoke) {
        if (!revoke) {
            final var rationale = (body != null && body.rationale != null) ? body.rationale : "";
            service.exemptLicense(reference, license, rationale);
        } else {
            service.revokeLicenseExemption(reference, license);
        }
    }
}
