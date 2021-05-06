/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

/// Stub implementation for platforms where no (simple) file dialog exists yet.
class FileLoader {
  FileLoader();

  Future<List<int>> load() async {
    throw UnsupportedError('Not supported on this platform');
  }
}
