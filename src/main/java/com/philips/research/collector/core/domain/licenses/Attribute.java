package com.philips.research.collector.core.domain.licenses;

/**
 * Named attribute value with a description.
 */
public final class Attribute {
    private final String tag;
    private final String description;

    public Attribute(String tag, String description) {
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
