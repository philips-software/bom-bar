/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'dart:developer';

import 'package:dio/dio.dart';
import 'package:flutter/material.dart';

class SnapshotWidget<T> extends StatelessWidget {
  SnapshotWidget(this.snapshot, {Key? key, required this.builder})
      : super(key: key);

  final AsyncSnapshot<T> snapshot;
  final Widget Function(BuildContext context, T data) builder;

  @override
  Widget build(BuildContext context) {
    if (snapshot.hasError) {
      log(
        'Snapshot error: ${snapshot.error}',
        error: snapshot.error,
        stackTrace: snapshot.stackTrace,
      );
      if (snapshot.error is DioError) {
        final e = snapshot.error as DioError;
        return ErrorMessage(
            'Server failed with status ${e.response!.statusCode ?? "unknown"}');
      }
      return ErrorMessage(snapshot.error?.toString() ?? 'Oops!');
    }
    if (!snapshot.hasData) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text('Loading ...'),
            SizedBox(height: 10),
            CircularProgressIndicator.adaptive(),
          ],
        ),
      );
    }
    return builder(context, snapshot.data!);
  }
}

class ErrorMessage extends StatelessWidget {
  const ErrorMessage(this.text);

  final String text;

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Text(text, style: Theme.of(context).textTheme.headline5),
    );
  }
}
