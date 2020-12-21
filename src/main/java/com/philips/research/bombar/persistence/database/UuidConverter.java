/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

package com.philips.research.bombar.persistence.database;

import pl.tlinkowski.annotation.basic.NullOr;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.UUID;

/**
 * JPA converter for storing a UUID as a String.
 */
@Converter(autoApply = true)
@SuppressWarnings({"unused", "RedundantSuppression"})
class UuidConverter implements AttributeConverter<UUID, String> {
    @Override
    public @NullOr String convertToDatabaseColumn(@NullOr UUID uuid) {
        return (uuid != null) ? uuid.toString() : null;
    }

    @Override
    public @NullOr UUID convertToEntityAttribute(@NullOr String uuid) {
        return (uuid != null) ? UUID.fromString(uuid) : null;
    }
}
