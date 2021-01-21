/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.persistence;

import com.philips.research.bombar.core.domain.Package;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DependencyRepository extends JpaRepository<DependencyEntity, Long> {
    List<DependencyEntity> findByPkg(Package pkg);

    void deleteByProject(ProjectEntity project);
}
