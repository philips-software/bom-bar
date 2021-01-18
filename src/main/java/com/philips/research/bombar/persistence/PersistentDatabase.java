/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2021 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

package com.philips.research.bombar.persistence;

import com.philips.research.bombar.core.PersistentStore;
import com.philips.research.bombar.core.domain.*;
import com.philips.research.bombar.core.domain.Package;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public List<Project> getProjects() {
        return projectRepository.findAll().stream().map(project -> (Project) project).collect(Collectors.toList());
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
    public Package createPackageDefinition(URI reference) {
        final var pkg = new PackageEntity(reference);
        return packageDefinitionRepository.save(pkg);
    }

    @Override
    public Optional<Package> getPackageDefinition(URI reference) {
        return packageDefinitionRepository.findByReference(reference).map(p -> p);
    }

    @Override
    public List<Package> findPackageDefinitions(String fragment) {
        final var pattern = '%' + fragment
                .replaceAll("\\\\|\\[|]", "")
                .replaceAll("%", "\\\\%")
                .replaceAll("_", "\\\\_")
                + '%';
        return new ArrayList<>(packageDefinitionRepository.findFirst50BySearchLikeIgnoreCaseOrderByReference(pattern));
    }

    @Override
    public Dependency createDependency(Project project, String id, String title) {
        final var dependency = new DependencyEntity(project, id, title);
        return dependencyRepository.save(dependency);
    }

    @Override
    public Project getProjectFor(Dependency dependency) {
        return ((DependencyEntity) dependency).project;
    }

    @Override
    public Relation createRelation(Relation.Relationship type, Dependency target) {
        return new Relation(type, target);
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
