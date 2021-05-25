/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core.domain;

import com.github.packageurl.PackageURL;
import pl.tlinkowski.annotation.basic.NullOr;

import java.net.URI;
import java.util.Objects;

public final class PackageRef implements Comparable<PackageRef> {
    private final String ref;

    public PackageRef(String ref) {
        this.ref = ref;
    }

    public PackageRef(URI uri) {
        this.ref = stripVersion(uri.getRawSchemeSpecificPart());
    }

    public PackageRef(PackageURL purl) {
        ref = stripVersion(purl.canonicalize().substring(4));
    }

    private String stripVersion(String string) {
        final var pos = string.indexOf('@');
        return (pos < 0) ? string : string.substring(0, pos);
    }

    public String canonicalize() {
        return ref;
    }

    @Override
    public boolean equals(@NullOr Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PackageRef that = (PackageRef) o;
        return ref.equals(that.ref);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ref);
    }

    @Override
    public int compareTo(PackageRef o) {
        return ref.compareTo(o.ref);
    }

    @Override
    public String toString() {
        return canonicalize();
    }
}
