/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:toast/toast.dart';

void showError(BuildContext context, Object error) {
  log(error.toString());
  Toast.show(error.toString(), context, duration: 10);
}
