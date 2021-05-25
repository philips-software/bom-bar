/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.persistence;

import com.philips.research.bombar.core.domain.PackageRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tlinkowski.annotation.basic.NullOr;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@SuppressWarnings("unused")
@Converter(autoApply = true)
class PackageRefConverter implements AttributeConverter<PackageRef, String> {
    static private final Logger LOG = LoggerFactory.getLogger(PackageRefConverter.class);

    @Override
    public @NullOr String convertToDatabaseColumn(@NullOr PackageRef ref) {
        return ref != null ? ref.canonicalize() : null;
    }

    @Override
    public @NullOr PackageRef convertToEntityAttribute(@NullOr String string) {
        return (string != null) ? new PackageRef(string) : null;
    }
}
