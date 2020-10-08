/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

package com.philips.research.collector.controller;

import java.util.List;

class ResultJson<T> {
    final List<T> results;

    public ResultJson(List<T> results) {
        this.results = results;
    }
}
