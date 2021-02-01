/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

-- noinspection SqlNoDataSourceInspectionForFile
-- noinspection SqlResolveForFile

ALTER TABLE dependencies
    ADD COLUMN is_source BOOLEAN NOT NULL DEFAULT FALSE;

CREATE TABLE package_sources
(
    project_id BIGINT NOT NULL,
    package_id BIGINT NOT NULL
);
ALTER TABLE package_sources
    ADD CONSTRAINT fk_sources__project_id FOREIGN KEY (project_id) REFERENCES projects (id) NOCHECK;
ALTER TABLE package_sources
    ADD CONSTRAINT fk_sources__package_id FOREIGN KEY (package_id) REFERENCES packages (id) NOCHECK;
