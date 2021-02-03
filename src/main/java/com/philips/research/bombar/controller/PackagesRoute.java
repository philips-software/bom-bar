/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.controller;

import com.philips.research.bombar.core.PackageService;
import com.philips.research.bombar.core.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("packages")
public class PackagesRoute extends BaseRoute {
    private final PackageService packageService;

    public PackagesRoute(PackageService packageService, ProjectService projectService) {
        super(projectService);
        this.packageService = packageService;
    }

    @GetMapping
    ResultListJson<PackageJson> findPackages(@RequestParam(name = "q") String fragment) {
        final var list = packageService.findPackages(fragment);
        return new ResultListJson<>(PackageJson.toList(list));
    }

    @GetMapping("{id}")
    PackageJson getPackage(@PathVariable String id) {
        final var reference = toReference(id);
        final var pkg = packageService.getPackage(reference);
        final var projects = projectService.findPackageUse(reference);
        return new PackageJson(pkg).setProjects(projects);
    }

    @PostMapping("{id}/approve/{approval}")
    void approvePackage(@PathVariable String id, @PathVariable String approval) {
        final var reference = toReference(id);
        final PackageService.Approval value = toApproval(approval);
        packageService.setApproval(reference, value);
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

    @PostMapping("{id}/exempt/{license}")
    void exemptLicense(@PathVariable String id, @PathVariable String license) {
        final var reference = toReference(id);
        packageService.exemptLicense(reference, license);
    }

    @DeleteMapping("{id}/exempt/{license}")
    void unexemptLicense(@PathVariable String id, @PathVariable String license) {
        final var reference = toReference(id);
        packageService.unExemptLicense(reference, license);
    }
}
