/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.persistence;

import com.philips.research.bombar.core.domain.Relation.Relationship;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@SuppressWarnings("unused")
@Converter(autoApply = true)
class RelationshipConverter implements AttributeConverter<Relationship, Character> {
    private static final char UNRELATED = '-';
    private static final char INDEPENDENT = 'I';
    private static final char DYNAMIC_LINK = 'D';
    private static final char STATIC_LINK = 'S';
    private static final char MODIFIED_CODE = 'M';

    @Override
    public Character convertToDatabaseColumn(Relationship relationship) {
        switch (relationship) {
            case UNRELATED:
                return UNRELATED;
            case INDEPENDENT:
                return INDEPENDENT;
            case DYNAMIC_LINK:
                return DYNAMIC_LINK;
            case STATIC_LINK:
                return STATIC_LINK;
            case MODIFIED_CODE:
                return MODIFIED_CODE;
            default:
                throw new IllegalArgumentException("No mapping defined for " + relationship);
        }
    }

    @Override
    public Relationship convertToEntityAttribute(Character character) {
        switch (character) {
            case UNRELATED:
                return Relationship.UNRELATED;
            case INDEPENDENT:
                return Relationship.INDEPENDENT;
            case DYNAMIC_LINK:
                return Relationship.DYNAMIC_LINK;
            case STATIC_LINK:
                return Relationship.STATIC_LINK;
            case MODIFIED_CODE:
                return Relationship.MODIFIED_CODE;
            default:
                throw new IllegalArgumentException("No mapping defined for '" + character + "'");
        }
    }
}
