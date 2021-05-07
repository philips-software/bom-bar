/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

-- noinspection SqlNoDataSourceInspectionForFile
-- noinspection SqlResolveForFile

ALTER TABLE dependencies
    ADD COLUMN purl CLOB;

UPDATE dependencies as d
SET purl = (SELECT reference from packages as p where p.id = d.package_id);
UPDATE dependencies
SET purl = CONCAT('pkg:', purl, '@', REPLACE(version, '+', '%2B'))
WHERE purl is not null;
