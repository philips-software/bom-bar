/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

package com.philips.research.bombar.persistence.database;

import com.philips.research.bombar.core.domain.PackageDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DependencyRepository extends JpaRepository<DependencyEntity, Long> {
    List<DependencyEntity> findByPkg(PackageDefinition pkg);

    void deleteByProject(ProjectEntity project);
}
