package com.philips.research.collector.core;

import java.io.InputStream;
import java.util.List;

public interface CollectorService {
    void parseSpdx(String projectId, InputStream stream);

    List<PackageDto> packages(String projectId);

    class PackageDto {
        String type;
        String namespace;
        String name;
        String version;
        List<PackageDto> children;
        String license;
    }
}

