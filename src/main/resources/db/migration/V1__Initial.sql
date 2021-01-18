/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

CREATE SEQUENCE hibernate_sequence START WITH 1;

CREATE TABLE dependencies
(
    id          BIGINT       NOT NULL,
    exemption   CLOB,
    issue_count INTEGER      NOT NULL,
    key         VARCHAR(255),
    license     CLOB         NOT NULL,
    title       CLOB         NOT NULL,
    version     VARCHAR(255) NOT NULL,
    package_id  BIGINT,
    project_id  BIGINT
);
ALTER TABLE dependencies
    ADD CONSTRAINT pk_dependencies PRIMARY KEY (id);

CREATE TABLE dependency_relations
(
    from_id       BIGINT  NOT NULL,
    dependency_id BIGINT,
    type          CHAR(1) NOT NULL
);

CREATE TABLE dependency_usages
(
    dependency_id BIGINT NOT NULL,
    usage_id      BIGINT NOT NULL
);
ALTER TABLE dependency_usages
    ADD CONSTRAINT pk_dependency_usages PRIMARY KEY (dependency_id, usage_id);

CREATE TABLE exempted_licenses
(
    package_id BIGINT       NOT NULL,
    license    VARCHAR(255) NOT NULL
);
ALTER TABLE exempted_licenses
    ADD CONSTRAINT pk_exempted_licenses PRIMARY KEY (package_id, license);

CREATE TABLE package_exemptions
(
    project_id  BIGINT       NOT NULL,
    rationale   CLOB         NOT NULL,
    package_ref VARCHAR(255) NOT NULL
);
ALTER TABLE package_exemptions
    ADD CONSTRAINT pk_package_exemptions PRIMARY KEY (project_id, package_ref);

CREATE TABLE packages
(
    id         BIGINT NOT NULL,
    acceptance CHAR(1),
    homepage   BLOB,
    name       CLOB   NOT NULL,
    reference  CLOB   NOT NULL,
    vendor     CLOB
);
ALTER TABLE packages
    ADD CONSTRAINT pk_packages PRIMARY KEY (id);

CREATE TABLE projects
(
    id           BIGINT  NOT NULL,
    distribution CHAR(1) NOT NULL,
    last_update  TIMESTAMP,
    phase        CHAR(1) NOT NULL,
    title        CLOB    NOT NULL,
    uuid         BINARY(16)  NOT NULL
);
ALTER TABLE projects
    ADD CONSTRAINT pk_projects PRIMARY KEY (id);

ALTER TABLE dependencies
    ADD CONSTRAINT fk_dependencies__project_id FOREIGN KEY (project_id) REFERENCES projects (id) NOCHECK;
ALTER TABLE dependency_usages
    ADD CONSTRAINT fk_dependency_usages__dependency_id FOREIGN KEY (dependency_id) REFERENCES dependencies (id) NOCHECK;
ALTER TABLE dependencies
    ADD CONSTRAINT fk_dependencies__package_id FOREIGN KEY (package_id) REFERENCES packages (id) NOCHECK;
ALTER TABLE exempted_licenses
    ADD CONSTRAINT fk_exempted_licenses__package_id FOREIGN KEY (package_id) REFERENCES packages (id) NOCHECK;
ALTER TABLE dependency_usages
    ADD CONSTRAINT fk_dependency_usages__usage_id FOREIGN KEY (usage_id) REFERENCES dependencies (id) NOCHECK;
ALTER TABLE dependency_relations
    ADD CONSTRAINT fk_dependency_relations__from_id FOREIGN KEY (from_id) REFERENCES dependencies (id) NOCHECK;
ALTER TABLE package_exemptions
    ADD CONSTRAINT fk_package_exemptions__project_id FOREIGN KEY (project_id) REFERENCES projects (id) NOCHECK;
ALTER TABLE dependency_relations
    ADD CONSTRAINT fk_dependency_relations__dependency_id FOREIGN KEY (dependency_id) REFERENCES dependencies (id) NOCHECK;
