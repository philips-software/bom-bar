/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core.spdx;

import com.philips.research.bombar.core.BusinessException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.BiConsumer;

class TagValueParser {

    private static final String START_DELIMITER = "<text>";
    private static final String END_DELIMITER = "</text>";

    private final BiConsumer<String, String> callback;

    private int lineNr;
    private String tag = "";
    private String value = "";
    private boolean isInText;

    /***
     * @param callback Callback for every tag-value encountered in the stream
     */
    TagValueParser(BiConsumer<String, String> callback) {
        this.callback = callback;
    }

    void parse(InputStream stream) {
        lineNr = 0;
        try (final var reader = new BufferedReader(new InputStreamReader(stream))) {
            reader.lines().forEach(this::parse);
        } catch (BusinessException e) {
            throw new SpdxException("Line " + lineNr + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new SpdxException("Error reading data from tag-value stream", e);
        }
    }

    private void parse(String line) {
        lineNr++;
        if (!isInText) {
            if (line.isBlank() || line.startsWith("##")) {
                return;
            }

            extractTagAndValue(line);
            if (value.startsWith(START_DELIMITER)) {
                value = value.substring(START_DELIMITER.length());
                isInText = true;
            }
        } else {
            value += '\n' + line;
        }

        if (value.endsWith(END_DELIMITER)) {
            value = value.substring(0, value.length() - END_DELIMITER.length());
            isInText = false;
        }

        if (!isInText && !value.equals("NOASSERTION")) {
            callback.accept(tag, value.equals("NONE") ? "" : value);
        }
    }

    private void extractTagAndValue(String line) {
        final var pos = line.indexOf(':');
        if (pos < 0) {
            throw new SpdxException("Line " + lineNr + " is not in tag-value format: " + line);
        }
        tag = line.substring(0, pos).trim();
        value = line.substring(pos + 1).stripLeading();
    }
}
