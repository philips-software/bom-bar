/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core.domain;

import com.github.packageurl.MalformedPackageURLException;
import com.github.packageurl.PackageURL;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

class PackageRefTest {
    private static final String REFERENCE = "reference";

    static PackageURL purlOf(String purl) {
        try {
            return new PackageURL(purl);
        } catch (MalformedPackageURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Test
    void createsFromLiteralString() {
        final var ref = new PackageRef(REFERENCE);

        assertThat(ref.canonicalize()).isEqualTo(REFERENCE);
    }

    @Test
    void createsFromURI() {
        final var ref = new PackageRef(URI.create("uri:type/%40name@version"));

        assertThat(ref.canonicalize()).isEqualTo("type/%40name");
    }

    @Test
    void extractsUriFromPackageUrl() {
        assertThat(new PackageRef(purlOf("pkg:type/ns/name@version")).canonicalize()).isEqualTo("type/ns/name");
        assertThat(new PackageRef(purlOf("pkg:type/name@version")).canonicalize()).isEqualTo("type/name");
        assertThat(new PackageRef(purlOf("pkg:type/%40name@version")).canonicalize()).isEqualTo("type/%40name");
    }

    @Test
    void implementsComparable() {
        final var first = new PackageRef("A");
        final var last = new PackageRef("B");

        //noinspection EqualsWithItself
        assertThat(first.compareTo(first)).isZero();
        assertThat(first.compareTo(last)).isEqualTo(-1);
        assertThat(last.compareTo(first)).isEqualTo(1);
    }

    @Test
    void implementsEquals() {
        EqualsVerifier.forClass(PackageRef.class)
                .withNonnullFields("ref")
                .verify();
    }
}
