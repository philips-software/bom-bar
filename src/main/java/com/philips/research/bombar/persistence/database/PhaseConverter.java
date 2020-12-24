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

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import static com.philips.research.bombar.core.domain.Project.Phase;

@SuppressWarnings("unused")
@Converter(autoApply = true)
class PhaseConverter implements AttributeConverter<Phase, Character> {
    private static final char DEVELOPMENT = 'D';
    private static final char RELEASED = 'R';

    @Override
    public Character convertToDatabaseColumn(Phase phase) {
        switch (phase) {
            case DEVELOPMENT:
                return DEVELOPMENT;
            case RELEASED:
                return RELEASED;
            default:
                throw new IllegalArgumentException("No mapping defined for " + phase);
        }
    }

    @Override
    public Phase convertToEntityAttribute(Character character) {
        switch (character) {
            case DEVELOPMENT:
                return Phase.DEVELOPMENT;
            case RELEASED:
                return Phase.RELEASED;
            default:
                throw new IllegalArgumentException("No mapping defined for '" + character + "'");
        }

    }
}
