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
    private final Type type;
    private final Dependency target;

    public Relation(Type type, Dependency target) {
        this.type = type;
        this.target = target;
    }

    public Type getType() {
        return type;
    }

    public Dependency getTarget() {
        return target;
    }

    public enum Type {
        UNRELATED,
        INDEPENDENT,
        DYNAMIC_LINK,
        STATIC_LINK,
        MODIFIED_CODE
    }
}
