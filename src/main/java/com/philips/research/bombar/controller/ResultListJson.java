/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.controller;

import java.util.List;

class ResultListJson<T> {
    final List<T> results;

    public ResultListJson(List<T> results) {
        this.results = results;
    }
}
