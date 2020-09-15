package com.philips.research.collector.core.domain.licenses;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AttributeTest {
    private static final String TAG = "Tag";
    private static final String DESCRIPTION = "Description";

    @Test
    void createsInstance() {
        final var attr = new Attribute(TAG, DESCRIPTION);

        assertThat(attr.getTag()).isEqualTo(TAG);
        assertThat(attr.getDescription()).isEqualTo(DESCRIPTION);
    }
}
