/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

export 'file_loader/default_file_loader.dart'
    if (dart.library.html) 'file_loader/web_file_loader.dart';
