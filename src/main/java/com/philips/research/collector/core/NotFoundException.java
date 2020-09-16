package com.philips.research.collector.core;

public class NotFoundException extends BusinessException {
    public NotFoundException(String type, Object id) {
        super(String.format("No %s '%s' found", type, id));
    }
}
