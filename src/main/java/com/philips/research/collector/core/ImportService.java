package com.philips.research.collector.core;

import java.io.InputStream;
import java.util.UUID;

public interface ImportService {
    /***
     * Updates the project from the SPDX tag-value stream.
     * @param projectId Project to update
     * @param stream SPDX tag-value encoded BOM
     */
    void importSpdx(UUID projectId, InputStream stream);
}
