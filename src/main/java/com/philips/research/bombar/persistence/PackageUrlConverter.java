/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.persistence;

import com.github.packageurl.MalformedPackageURLException;
import com.github.packageurl.PackageURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tlinkowski.annotation.basic.NullOr;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@SuppressWarnings("unused")
@Converter(autoApply = true)
class PackageUrlConverter implements AttributeConverter<PackageURL, String> {
    static private final Logger LOG = LoggerFactory.getLogger(PackageUrlConverter.class);

    @Override
    public @NullOr String convertToDatabaseColumn(@NullOr PackageURL purl) {
        return purl != null ? purl.canonicalize() : null;
    }

    @Override
    public @NullOr PackageURL convertToEntityAttribute(@NullOr String string) {
        try {
            return (string != null) ? new PackageURL(string) : null;
        } catch (MalformedPackageURLException e) {
            LOG.error("Not a package URL: " + string, e);
            return null;
        }
    }
}
