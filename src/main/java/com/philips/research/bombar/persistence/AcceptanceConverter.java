/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.persistence;

import com.philips.research.bombar.core.domain.Package.Acceptance;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@SuppressWarnings("unused")
@Converter(autoApply = true)
class AcceptanceConverter implements AttributeConverter<Acceptance, Character> {
    private static final char DEFAULT = '?';
    private static final char APPROVED = '+';
    private static final char FORBIDDEN = '-';
    private static final char PER_PROJECT = 'P';
    private static final char NOT_A_PACKAGE = 'X';

    @Override
    public Character convertToDatabaseColumn(Acceptance acceptance) {
        switch (acceptance) {
            case DEFAULT:
                return DEFAULT;
            case APPROVED:
                return APPROVED;
            case FORBIDDEN:
                return FORBIDDEN;
            case PER_PROJECT:
                return PER_PROJECT;
            case NOT_A_PACKAGE:
                return NOT_A_PACKAGE;
            default:
                throw new IllegalArgumentException("No mapping defined for " + acceptance);
        }
    }

    @Override
    public Acceptance convertToEntityAttribute(Character character) {
        switch (character) {
            case DEFAULT:
                return Acceptance.DEFAULT;
            case APPROVED:
                return Acceptance.APPROVED;
            case FORBIDDEN:
                return Acceptance.FORBIDDEN;
            case PER_PROJECT:
                return Acceptance.PER_PROJECT;
            case NOT_A_PACKAGE:
                return Acceptance.NOT_A_PACKAGE;
            default:
                throw new IllegalArgumentException("No acceptance mapping defined for '" + character + "'");
        }
    }
}
