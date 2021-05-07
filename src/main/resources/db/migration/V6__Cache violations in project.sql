/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

-- noinspection SqlNoDataSourceInspectionForFile
-- noinspection SqlResolveForFile

ALTER TABLE projects
    ADD COLUMN issue_count INTEGER NOT NULL DEFAULT 0;

UPDATE projects
set issue_count = (SELECT SUM(dependencies.issue_count) FROM dependencies WHERE project_id = projects.id);
