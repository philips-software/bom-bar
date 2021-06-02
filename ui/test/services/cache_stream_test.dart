/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'dart:async';

import 'package:bom_bar_ui/services/cache_stream.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  const value = 42;

  group('Cached Stream', () {
    test('forwards elements', () {
      final controller = StreamController<int>();
      final stream = cached(controller.stream);

      controller.add(value);

      expect(stream, emits(value));
      controller.close();
    });

    test('forwards last prior element first', () async {
      final existing = cached(Stream.fromIterable([1, 2, value, 4]));
      await existing.firstWhere((element) => element == value);

      expect(existing, emits(value));
    });

    test('propagates errors', () {
      final error = AssertionError('Boom');
      final controller = StreamController<int>();
      final stream = cached(controller.stream);

      controller.addError(error);

      expect(stream, emitsError(error));
      controller.close();
    });

    test('propagates closing of stream', () {
      final stream = cached(Stream.value(value));

      expect(
        stream,
        emitsInOrder([
          emits(value),
          emitsDone,
        ]),
      );
    });

    test('closes new listeners after stream is already done', () async {
      final stream = cached(Stream.value(value));
      await stream.drain();

      expect(stream, emitsDone);
    });

    test('drops forwarding to canceled listeners', () async {
      final controller = StreamController<int>();
      final stream = cached(controller.stream);
      int? received;
      await stream.listen((v) => received = v).cancel();

      controller.add(value);
      await controller.close();

      expect(received, null);
    });
  });
}
