/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

package com.philips.research.bombar.core.domain;

public class Relation {
    private final Relationship type;
    private final Dependency target;

    // Necessary for persistence (sorry)
    @SuppressWarnings("unused")
    private Relation() {
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
