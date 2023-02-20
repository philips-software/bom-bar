/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.persistence;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

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
