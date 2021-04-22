/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';

class ActionButton extends StatelessWidget {
  ActionButton({this.icon = Icons.edit, this.onPressed});

  final IconData icon;
  final void Function() onPressed;

  @override
  Widget build(BuildContext context) {
    return MouseRegion(
      cursor: SystemMouseCursors.click,
      child: IconButton(
        icon: Icon(icon),
        color: Theme.of(context).accentColor,
        padding: EdgeInsets.only(left: 8),
        constraints: BoxConstraints(),
        onPressed: onPressed,
      ),
    );
  }
}
