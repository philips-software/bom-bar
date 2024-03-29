/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';

class EditSelectionDialog<T> {
  EditSelectionDialog(
      {required this.title, required this.values, this.selection});

  final String title;
  final Map<T, String> values;
  T? selection;

  Future<T?> show(BuildContext context) {
    return showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(title),
        content: StatefulBuilder(
          builder: (context, setState) => Material(
            type: MaterialType.transparency,
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: values.entries
                  .map(
                    (item) => Row(
                      children: [
                        Radio<T>(
                          value: item.key,
                          groupValue: selection,
                          onChanged: (v) => setState(() => selection = v),
                        ),
                        Text(item.value),
                      ],
                    ),
                  )
                  .toList(growable: false),
            ),
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: Text('CANCEL'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context, selection),
            child: Text('OK'),
          ),
        ],
      ),
    );
  }
}
