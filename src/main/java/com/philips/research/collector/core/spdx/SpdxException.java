/*
 * Copyright (c) 2020-2020, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.collector.core.spdx;

import com.philips.research.collector.core.BusinessException;

public class SpdxException extends BusinessException {
    public SpdxException(String message) {
        super(message);
    }

    public SpdxException(String message, Throwable cause) {
        super(message, cause);
    }
}
