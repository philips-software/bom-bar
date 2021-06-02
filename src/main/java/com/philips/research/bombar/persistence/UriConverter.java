/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tlinkowski.annotation.basic.NullOr;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.net.URI;

/**
 * JPA converter for storing an URI as a String.
 */
@Converter(autoApply = true)
@SuppressWarnings({"unused"})
class UriConverter implements AttributeConverter<URI, String> {
    private static final Logger LOG = LoggerFactory.getLogger(UriConverter.class);

    @Override
    public @NullOr String convertToDatabaseColumn(@NullOr URI uri) {
        return (uri != null) ? uri.toString() : null;
    }

    @Override
    public @NullOr URI convertToEntityAttribute(@NullOr String uri) {
        try {
            return (uri != null) ? URI.create(uri) : null;
        } catch (Exception e) {
            LOG.warn("Ignored malformed URI '{}'", uri);
            return null;
        }
    }
}
