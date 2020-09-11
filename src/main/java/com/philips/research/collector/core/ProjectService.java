package com.philips.research.collector.core;

import java.util.List;
import java.util.UUID;

public interface ProjectService {
    List<PackageDto> packages(UUID projectId);

    class PackageDto {
        String type;
        String namespace;
        String name;
        String version;
        List<PackageDto> children;
        String license;
    }
}

