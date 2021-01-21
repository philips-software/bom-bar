/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core.domain;

import com.philips.research.bombar.core.BusinessException;

public class DomainException extends BusinessException {
    public DomainException(String message) {
        super(message);
    }
}
