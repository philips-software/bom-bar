/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'dart:async';

/// Returns a stream that caches the most recent value from the [source],
/// so new listeners immediately receive the most recent value.
Stream<T> cached<T extends Object>(Stream<T> source) {
  var listeners = <StreamController>{};
  T? last;
  bool done = false;
  source.listen((event) {
    last = event;
    for (var listener in [...listeners]) listener.add(event);
  }, onError: (Object e, StackTrace s) {
    for (var listener in [...listeners]) listener.addError(e, s);
  }, onDone: () {
    done = true;
    last = null;
    for (var listener in listeners) listener.close();
  });

  return Stream.multi((StreamController newController) {
    if (done) {
      newController.close();
    } else {
      var lastEvent = last;
      if (lastEvent != null) newController.add(lastEvent);
      listeners.add(newController);
    }
    newController.onCancel = () {
      listeners.remove(newController);
    };
  });
}
