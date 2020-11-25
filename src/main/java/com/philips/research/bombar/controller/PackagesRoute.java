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

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

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
        final var reference = decode(id);
        final var pkg = service.getPackage(reference);
        return new PackageJson(pkg);
    }

    @PostMapping("{id}/license/{license}/exempt")
    void exemptLicense(@PathVariable String id, @PathVariable String license,
                       @RequestBody(required = false) @NullOr RationaleJson body,
                       @RequestParam(required = false, defaultValue = "no") boolean revoke) {
        final var reference = decode(id);
        if (!revoke) {
            final var rationale = (body != null && body.rationale != null) ? body.rationale : "";
            service.exemptLicense(reference, license, rationale);
        } else {
            service.revokeLicenseExemption(reference, license);
        }
    }

    private URI decode(String id) {
        return URI.create(URLDecoder.decode(id, StandardCharsets.UTF_8));
    }
}
