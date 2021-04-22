/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';

import 'action_button.dart';

class ActionItem extends StatelessWidget {
  ActionItem(
      {@required this.child,
      this.label,
      this.icon = Icons.edit,
      @required this.onPressed});

  final Widget child;
  final String label;
  final IconData icon;
  final void Function() onPressed;

  @override
  Widget build(BuildContext context) {
    return Wrap(
      crossAxisAlignment: WrapCrossAlignment.center,
      children: [
        if (label != null) Text('$label: '),
        child,
        ActionButton(
          icon: icon,
          onPressed: onPressed,
        ),
      ],
    );
  }
}
