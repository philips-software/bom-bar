/*
 * Copyright (c) 2020-2020, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.collector.core;

public class NotFoundException extends BusinessException {
    public NotFoundException(String type, Object id) {
        super(String.format("No %s '%s' found", type, id));
    }
}
