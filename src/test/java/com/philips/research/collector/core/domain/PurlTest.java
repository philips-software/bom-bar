package com.philips.research.collector.core.domain;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/***
 * Represents a package URL based on the specification found in https://github.com/package-url/purl-spec
 * Or in short: "pkg:&lt;type&gt;/[&lt;namespace&gt;]/&lt;name&gt;@&lt;version&gt;[?&lg;qualifiers&gt;#&lt;subpath&gt;]"
 */
class PurlTest {
    private static final String NAME = "Type/Namespace/Name";
    private static final String VERSION = "Version";

    @Test
    void throws_notStartingWithPkg() {
        assertThatThrownBy(() -> new Purl(URI.create("other:type/ns/name@version")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("pkg");
    }

    @Test
    void parsesFullPurl() {
        final var uri = URI.create("pkg:" + NAME + "@" + VERSION);

        final var purl = new Purl(uri);

        assertThat(purl.getName()).isEqualTo(NAME);
        assertThat(purl.getVersion()).isEqualTo(VERSION);
    }

    @Test
    void throws_missingTypePart() {
        assertThatThrownBy(() -> new Purl(URI.create("pkg:/")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("type part");
    }

    @Test
    void throws_missingNamePart() {
        assertThatThrownBy(() -> new Purl(URI.create("pkg:type")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name part");
    }

    @Test
    void throws_missingVersion() {
        assertThatThrownBy(() -> new Purl(URI.create("pkg:type/namespace/name")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("version");
    }

    @Test
    void decodesUrlEncodedValues() {
        final var expected = " %?@#/";
        final var encoded = URLEncoder.encode(expected, StandardCharsets.UTF_8);

        final var purl = new Purl(URI.create("pkg:" + encoded + "/" + encoded + "@" + encoded));

        assertThat(purl.getName()).isEqualTo(expected + "/" + expected);
        assertThat(purl.getVersion()).isEqualTo(expected);
    }
}
