/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:badges/badges.dart';
import 'package:flutter/material.dart';

import '../../model/dependency.dart';

class DependencyIcon extends StatelessWidget {
  DependencyIcon(this.dependency);

  final Dependency dependency;

  @override
  Widget build(BuildContext context) {
    return Tooltip(
      message: dependency.relation?.replaceAll('_', ' ') ?? 'Dependency',
      child: Badge(
        badgeContent: Text(
          dependency.totalIssues.toString(),
          style: TextStyle(color: Colors.white),
        ),
        showBadge: dependency.totalIssues > 0,
        child: _iconFor(dependency.relation),
      ),
    );
  }

  Icon _iconFor(String? relation) {
    return Icon(_relationIcon[relation] ??
        ((dependency.purl != null)
            ? Icons.extension
            : Icons.extension_outlined));
  }
}

const _relationIcon = <String, IconData>{
  'irrelevant': Icons.cancel_outlined,
  'independent': Icons.shopping_bag,
  'dynamic_link': Icons.link,
  'static_link': Icons.code,
  'modified_code': Icons.edit
};
