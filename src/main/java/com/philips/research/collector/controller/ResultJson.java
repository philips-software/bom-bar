/*
 * Copyright (c) 2020-2020, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.collector.controller;

import java.util.List;

class ResultJson<T> {
    final List<T> results;

    public ResultJson(List<T> results) {
        this.results = results;
    }
}
