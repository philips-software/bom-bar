/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

export 'file_uploader/default_file_uploader.dart'
    if (dart.library.html) 'file_uploader/web_file_uploader.dart';
