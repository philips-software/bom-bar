package com.philips.research.collector.controller;

import java.util.List;

class ResultJson<T> {
    final List<T> results;

    public ResultJson(List<T> results) {
        this.results = results;
    }
}
