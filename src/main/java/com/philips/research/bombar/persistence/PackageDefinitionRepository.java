/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.persistence;

import com.philips.research.bombar.core.domain.PackageRef;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PackageDefinitionRepository extends CrudRepository<PackageEntity, Long> {
    Optional<PackageEntity> findByReference(PackageRef reference);

    List<PackageEntity> findFirst50BySearchContainingIgnoreCaseOrderByReference(String fragment);
}
