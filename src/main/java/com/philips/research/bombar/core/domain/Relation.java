/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core.domain;

import java.util.Objects;

public class Relation {
    private final Relationship type;
    private final Dependency target;

    // Necessary for persistence
    @SuppressWarnings({"unused", "ConstantConditions"})
    protected Relation() {
        this.type = null;
        this.target = null;
    }

    Relation(Relationship type, Dependency target) {
        this.type = type;
        this.target = target;
        if (type == Relationship.IRRELEVANT) {
            target.setDevelopment();
        } else {
            target.setDelivered();
        }
    }

    public Relationship getType() {
        return type;
    }

    public Dependency getTarget() {
        return target;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Relation)) return false;
        Relation relation = (Relation) o;
        return type == relation.type && target.equals(relation.target);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(type, target);
    }

    public enum Relationship {
        IRRELEVANT,
        INDEPENDENT,
        DYNAMIC_LINK,
        STATIC_LINK,
        MODIFIED_CODE
    }
}
