/*
 * Copyright (c) 2020-2020, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.collector.core.domain.licenses;

/**
 * Named license term with a description.
 */
public final class Term {
    private final String tag;
    private final String description;

    public Term(String tag, String description) {
        this.tag = tag;
        this.description = description;
    }

    public String getTag() {
        return tag;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", tag, description);
    }
}
