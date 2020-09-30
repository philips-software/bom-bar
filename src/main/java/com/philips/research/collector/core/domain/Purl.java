/*
 * Copyright (c) 2020-2020, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.collector.core.domain;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Purl {
    private final String name;
    private final String version;

    public Purl(URI purl) {
        final var string = purl.toString();
        if (!string.startsWith("pkg:")) {
            throw new IllegalArgumentException("PURL scheme must be 'pkg:'");
        }
        final var path = path(string);
        final var parts = path.split("/").length;
        if (parts < 1) {
            throw new IllegalArgumentException("Missing type part");
        }
        if (parts < 2) {
            throw new IllegalArgumentException("Missing name part");
        }
        name = decoded(path);
        version = decoded(version(string));
    }

    private String decoded(String string) {
        return URLDecoder.decode(string, StandardCharsets.UTF_8);
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    private String path(String string) {
        final var startPos = string.indexOf(':') + 1;
        final var endPos = firstPosOrLength(string, '@', '?', '#');
        return string.substring(startPos, endPos);
    }

    private String version(String string) {
        final var startPos = string.indexOf('@');
        if (startPos < 0) {
            throw new IllegalArgumentException("Missing version");
        }

        final var endPos = firstPosOrLength(string, '?', '#');
        return string.substring(startPos + 1, endPos);
    }

    private int firstPosOrLength(String string, Character... chars) {
        return Arrays.stream(chars)
                .mapToInt(string::indexOf)
                .filter(i -> i >= 0)
                .min()
                .orElse(string.length());
    }
}
