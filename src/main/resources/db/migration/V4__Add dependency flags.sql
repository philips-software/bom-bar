/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

-- noinspection SqlNoDataSourceInspectionForFile
-- noinspection SqlResolveForFile

ALTER TABLE dependencies
    ADD COLUMN is_root BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE dependencies
    ADD COLUMN is_development BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE dependencies
    ADD COLUMN is_delivered BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE dependencies DROP COLUMN is_source;

DROP TABLE package_sources;
