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

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Purl {
    private final String reference;
    private final String version;

    public Purl(String reference, String version) {
        this.reference = reference;
        this.version = version;
    }

    public Purl(URI purl) {
        final var scheme = purl.getScheme();
        if (scheme != null && !scheme.equals("pkg")) {
            throw new IllegalArgumentException("Expected scheme of " + purl + " to be 'pkg:'");
        }
        final var string = purl.toString();
        final var name = name(string);
        final var parts = name.split("/").length;
        if (parts < 1) {
            throw new IllegalArgumentException("Missing type part in " + purl);
        }
        if (parts < 2) {
            throw new IllegalArgumentException("Missing name part in " + purl);
        }
        final var path = path(string);
        reference = decoded(name) + (!path.isBlank() ? '#' + decoded(path) : "");
        version = decoded(version(string));
    }

    private String decoded(String string) {
        return URLDecoder.decode(string, StandardCharsets.UTF_8);
    }

    public String getReference() {
        return reference;
    }

    public String getVersion() {
        return version;
    }

    private String name(String string) {
        final var startPos = string.indexOf(':');
        final var endPos = firstPosOrLength(string, '@', '?', '#');
        return string.substring((startPos >= 0) ? startPos + 1 : 0, endPos);
    }

    private String version(String string) {
        final var startPos = string.indexOf('@');
        if (startPos < 0) {
            throw new IllegalArgumentException("Missing version");
        }

        final var endPos = firstPosOrLength(string, '?', '#');
        return string.substring(startPos + 1, endPos);
    }

    private String path(String string) {
        final var pos = string.indexOf('#');
        return (pos >= 0) ? string.substring(pos + 1) : "";
    }

    private int firstPosOrLength(String string, Character... chars) {
        return Arrays.stream(chars)
                .mapToInt(string::indexOf)
                .filter(i -> i >= 0)
                .min()
                .orElse(string.length());
    }

    public URI toUri() {
        final var pos = reference.indexOf('#');
        final var name = this.reference.substring(0, (pos >= 0) ? pos : this.reference.length());
        final var path = (pos >= 0) ? '#' + this.reference.substring(pos + 1) : "";
        return URI.create("pkg:" + name + "@" + version + path);
    }

    @Override
    public String toString() {
        return toUri().toASCIIString();
    }
}
