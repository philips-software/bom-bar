/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
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
