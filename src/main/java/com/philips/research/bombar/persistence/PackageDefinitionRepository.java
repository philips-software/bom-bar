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

import org.springframework.data.repository.CrudRepository;

import java.net.URI;
import java.util.List;
import java.util.Optional;

public interface PackageDefinitionRepository extends CrudRepository<PackageEntity, Long> {
    Optional<PackageEntity> findByReference(URI reference);

    List<PackageEntity> findFirst50BySearchLikeIgnoreCaseOrderByReference(String fragment);
}
