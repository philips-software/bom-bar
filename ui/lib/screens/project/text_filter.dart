/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';

class TextFilter extends StatefulWidget {
  TextFilter({
    Key? key,
    required this.onChanged,
  }) : super(key: key);

  final Function(String filter) onChanged;

  @override
  _TextFilterState createState() => _TextFilterState();
}

class _TextFilterState extends State<TextFilter> {
  final _controller = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return TextField(
      controller: _controller,
      autofocus: true,
      decoration: InputDecoration(
        hintText: 'Filter',
        suffix: IconButton(
          icon: Icon(Icons.clear),
          onPressed: () {
            _controller.clear();
            widget.onChanged('');
          },
        ),
      ),
      onChanged: (value) => widget.onChanged(value),
    );
  }
}
