/*
 * Copyright (c) 2020-2020, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.collector.core.domain;

public class Relation implements Comparable<Relation> {
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

    @Override
    public int compareTo(Relation other) {
        return target.compareTo(other.target);
    }

    public enum Type {
        UNRELATED,
        INDEPENDENT,
        DYNAMIC_LINK,
        STATIC_LINK,
        MODIFIED_CODE
    }
}
