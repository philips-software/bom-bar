/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2021 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

package com.philips.research.bombar.persistence;

import pl.tlinkowski.annotation.basic.NullOr;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.net.URI;

/**
 * JPA converter for storing an URI as a String.
 */
@Converter(autoApply = true)
@SuppressWarnings({"unused", "RedundantSuppression"})
class UriConverter implements AttributeConverter<URI, String> {
    @Override
    public @NullOr String convertToDatabaseColumn(@NullOr URI uri) {
        return (uri != null) ? uri.toString() : null;
    }

    @Override
    public @NullOr URI convertToEntityAttribute(@NullOr String uri) {
        return (uri != null) ? URI.create(uri) : null;
    }
}