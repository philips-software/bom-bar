package com.philips.research.collector.core.domain;

import com.philips.research.collector.core.ProjectService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectInteractor implements ProjectService {
    @Override
    public ProjectDto createProject(String name) {
        return null;
    }

    @Override
    public List<PackageDto> packages(UUID projectId) {
        return null;
    }
}
