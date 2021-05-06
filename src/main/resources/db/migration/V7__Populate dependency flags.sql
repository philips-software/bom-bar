/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

-- noinspection SqlNoDataSourceInspectionForFile
-- noinspection SqlResolveForFile

UPDATE dependencies AS d
SET is_root = (SELECT count(*) FROM dependency_relations AS r WHERE r.dependency_id = d.id) = 0;

UPDATE dependencies AS d
SET is_development = (SELECT count(*) FROM dependency_relations AS r WHERE r.dependency_id = d.id AND r.type = '-') > 0;

UPDATE dependencies AS d
SET is_delivered = (SELECT count(*)
                    FROM dependency_relations AS r
                    WHERE r.dependency_id = d.id
                      AND r.type!='-') > 0 OR is_root;
