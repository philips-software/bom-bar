/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
import 'dart:async';

import 'package:flutter/material.dart';

class NameFilter extends StatefulWidget {
  NameFilter({this.hint, required this.onChanged});

  final String? hint;
  final void Function(String) onChanged;

  @override
  _NameFilterState createState() => _NameFilterState();
}

class _NameFilterState extends State<NameFilter> {
  final _debounce = _Debounce(Duration(milliseconds: 500));

  @override
  void initState() {
    super.initState();
  }

  @override
  void dispose() {
    _debounce.close();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return TextField(
      decoration: InputDecoration(
        hintText: widget.hint,
      ),
      autofocus: true,
      onChanged: (text) => _debounce.run(() => setState(() {
            widget.onChanged(text);
          })),
    );
  }
}

/// Waits for a [period] before invoking an action.
class _Debounce {
  _Debounce(this.period);

  final Duration period;
  Timer? _timer;

  run(Function() action) {
    close();
    _timer = Timer(period, action);
  }

  void close() {
    if (_timer != null) {
      _timer!.cancel();
    }
  }
}
