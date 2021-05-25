/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.persistence;

import com.philips.research.bombar.core.PersistentStore;
import com.philips.research.bombar.core.domain.Dependency;
import com.philips.research.bombar.core.domain.Package;
import com.philips.research.bombar.core.domain.PackageRef;
import com.philips.research.bombar.core.domain.Project;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
public class PersistentDatabase implements PersistentStore {
    private final ProjectRepository projectRepository;
    private final DependencyRepository dependencyRepository;
    private final PackageDefinitionRepository packageDefinitionRepository;

    public PersistentDatabase(ProjectRepository projectRepository,
                              DependencyRepository dependencyRepository,
                              PackageDefinitionRepository packageDefinitionRepository) {
        this.packageDefinitionRepository = packageDefinitionRepository;
        this.projectRepository = projectRepository;
        this.dependencyRepository = dependencyRepository;
    }

    @Override
    public List<Project> findProjects(String fragment) {
        return new ArrayList<>(projectRepository.findFirst50ByTitleContainingIgnoreCaseOrderByLastUpdateDesc(fragment));
    }

    @Override
    public Project createProject() {
        final var project = new ProjectEntity(UUID.randomUUID());
        return projectRepository.save(project);
    }

    @Override
    public Optional<Project> getProject(UUID projectId) {
        return projectRepository.findFirstByUuid(projectId).map(p -> p);
    }

    @Override
    public Package createPackageDefinition(PackageRef reference) {
        final var pkg = new PackageEntity(reference);
        return packageDefinitionRepository.save(pkg);
    }

    @Override
    public Optional<Package> getPackageDefinition(PackageRef reference) {
        return packageDefinitionRepository.findByReference(reference).map(p -> p);
    }

    @Override
    public List<Package> findPackageDefinitions(String fragment) {
        return new ArrayList<>(packageDefinitionRepository.findFirst50BySearchContainingIgnoreCaseOrderByReference(fragment));
    }

    @Override
    public Dependency createDependency(Project project, @NullOr String id, String title) {
        final var dependency = new DependencyEntity(project, id, title);
        return dependencyRepository.save(dependency);
    }

    @Override
    public Project getProjectFor(Dependency dependency) {
        return ((DependencyEntity) dependency).project;
    }

    @Override
    public List<Dependency> findDependencies(Package pkg) {
        return new ArrayList<>(dependencyRepository.findByPkg(pkg));
    }

    @Override
    public void deleteDependencies(Project project) {
        dependencyRepository.deleteByProject((ProjectEntity) project);
    }
}
