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
import pl.tlinkowski.annotation.basic.NullOr;

import java.net.URL;
import java.util.Map;

class PackageJson {
    @NullOr String id;
    @NullOr String name;
    @NullOr String vendor;
    @NullOr URL homepage;
    @NullOr Map<String, String> exemptions;

    PackageJson(PackageService.PackageDto dto) {
        this.id = dto.reference;
        this.name = dto.name;
        this.vendor = dto.vendor;
        this.homepage = dto.homepage;
        this.exemptions = dto.licenseExemptions;
    }

    public static @NullOr PackageJson fromDto(PackageService.@NullOr PackageDto pkg) {
        return (pkg != null) ? new PackageJson(pkg) : null;
    }
}

