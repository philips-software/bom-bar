/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';

class FilterField extends StatefulWidget {
  FilterField({required this.onChanged});

  final Function(String filter, bool onlyErrors) onChanged;

  @override
  _FilterFieldState createState() => _FilterFieldState();
}

class _FilterFieldState extends State<FilterField> {
  final _controller = TextEditingController();
  var _onlyErrors = false;

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 12.0),
          child: TextField(
            controller: _controller,
            autofocus: true,
            decoration: InputDecoration(
              hintText: 'Filter',
              suffix: IconButton(
                icon: Icon(Icons.clear),
                onPressed: () {
                  _controller.clear();
                  widget.onChanged('', _onlyErrors);
                },
              ),
            ),
            onChanged: (value) => widget.onChanged(value, _onlyErrors),
          ),
        ),
        SwitchListTile(
          value: _onlyErrors,
          title: Text('Violations only'),
          onChanged: (value) => setState(() {
            _onlyErrors = value;
            widget.onChanged(_controller.text, value);
          }),
        ),
      ],
    );
  }
}
