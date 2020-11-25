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

import com.philips.research.bombar.core.PackageService.PackageDto;
import pl.tlinkowski.annotation.basic.NullOr;

import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class PackageJson {
    @NullOr String id;
    @NullOr URI reference;
    @NullOr String name;
    @NullOr String vendor;
    @NullOr URL homepage;
    @NullOr Map<String, String> exemptions;

    PackageJson(PackageDto dto) {
        this.id = encode(encode(dto.reference.toString()));
        this.reference = dto.reference;
        this.name = dto.name;
        this.vendor = dto.vendor;
        this.homepage = dto.homepage;
        this.exemptions = dto.licenseExemptions;
    }

    private static String encode(String string) {
        return URLEncoder.encode(string, StandardCharsets.UTF_8);
    }

    public static @NullOr PackageJson fromDto(@NullOr PackageDto pkg) {
        return (pkg != null) ? new PackageJson(pkg) : null;
    }

    public static List<PackageJson> toList(List<PackageDto> list) {
        return list.stream()
                .map(PackageJson::new)
                .collect(Collectors.toList());
    }
}

