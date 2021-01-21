/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core.domain;

public class Relation {
    private final Relationship type;
    private final Dependency target;

    // Necessary for persistence (sorry)
    @SuppressWarnings("unused")
    Relation() {
        //noinspection ConstantConditions
        this(null, null);
    }

    public Relation(Relationship type, Dependency target) {
        this.type = type;
        this.target = target;
    }

    public Relationship getType() {
        return type;
    }

    public Dependency getTarget() {
        return target;
    }

    public enum Relationship {
        UNRELATED,
        INDEPENDENT,
        DYNAMIC_LINK,
        STATIC_LINK,
        MODIFIED_CODE
    }
}
