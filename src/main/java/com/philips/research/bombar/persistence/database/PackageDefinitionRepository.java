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
import org.springframework.data.repository.CrudRepository;

import java.net.URI;
import java.util.List;
import java.util.Optional;

public interface PackageDefinitionRepository extends CrudRepository<PackageDefinitionEntity, Long> {
    Optional<PackageDefinition> findByReference(URI reference);

    List<PackageDefinition> findFirst50BySearchLikeOrderByReference(String fragment);
}
