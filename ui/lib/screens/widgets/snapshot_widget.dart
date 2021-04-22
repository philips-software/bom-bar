/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'dart:developer';

import 'package:flutter/material.dart';

class SnapshotWidget<T> extends StatelessWidget {
  SnapshotWidget(this.snapshot, {Key key, @required this.builder})
      : super(key: key);

  final AsyncSnapshot<T> snapshot;
  final Widget Function(BuildContext context, T data) builder;

  @override
  Widget build(BuildContext context) {
    if (snapshot.hasError) {
      log('Snapshot failed:', error: snapshot.error.toString());
      return ErrorWidget(snapshot.error);
    }
    if (!snapshot.hasData) {
      return Center(
        child: CircularProgressIndicator.adaptive(),
      );
    }
    return builder(context, snapshot.data);
  }
}
