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

import com.philips.research.bombar.core.domain.Project.Distribution;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@SuppressWarnings("unused")
@Converter(autoApply = true)
class DistributionConverter implements AttributeConverter<Distribution, Character> {
    private static final char OPEN_SOURCE = 'O';
    private static final char INTERNAL = 'I';
    private static final char SAAS = 'S';
    private static final char PROPRIETARY = 'P';

    @Override
    public Character convertToDatabaseColumn(Distribution distribution) {
        switch (distribution) {
            case OPEN_SOURCE:
                return OPEN_SOURCE;
            case INTERNAL:
                return INTERNAL;
            case SAAS:
                return SAAS;
            case PROPRIETARY:
                return PROPRIETARY;
            default:
                throw new IllegalArgumentException("No mapping defined for " + distribution);
        }
    }

    @Override
    public Distribution convertToEntityAttribute(Character character) {
        switch (character) {
            case OPEN_SOURCE:
                return Distribution.OPEN_SOURCE;
            case INTERNAL:
                return Distribution.INTERNAL;
            case SAAS:
                return Distribution.SAAS;
            case PROPRIETARY:
                return Distribution.PROPRIETARY;
            default:
                throw new IllegalArgumentException("No mapping defined for '" + character + "'");
        }
    }
}
