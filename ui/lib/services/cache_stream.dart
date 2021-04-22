/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'dart:async';

Stream<T> cached<T extends Object>(Stream<T> source) {
  var listeners = <StreamController>{};
  T last;
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

  return Stream.multi((StreamController c) {
    if (done) {
      c.close();
    } else {
      var lastEvent = last;
      if (lastEvent != null) c.add(lastEvent);
      listeners.add(c);
    }
    c.onCancel = () {
      listeners.remove(c);
    };
  });
}
