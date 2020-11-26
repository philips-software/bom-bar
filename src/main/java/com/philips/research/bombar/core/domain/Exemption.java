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

import pl.tlinkowski.annotation.basic.NullOr;

import java.util.Objects;

public final class Exemption<T> {
    private final T key;
    private final String rationale;

    public Exemption(T key, String rationale) {
        this.key = key;
        this.rationale = rationale;
    }

    public T getKey() {
        return key;
    }

    public String getRationale() {
        return rationale;
    }

    @Override
    public boolean equals(@NullOr Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exemption<?> exemption = (Exemption<?>) o;
        return Objects.equals(key, exemption.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
