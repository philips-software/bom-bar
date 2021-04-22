/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
import 'package:flutter/material.dart';

import '../../model/dependency.dart';
import 'dependency_icon.dart';

class DependencyTile extends StatelessWidget {
  DependencyTile(this.dep, {this.onSelect});

  final Dependency dep;
  final Function() onSelect;

  @override
  Widget build(BuildContext context) {
    bool hasId = dep.id != null;

    return ListTile(
      key: ValueKey(dep.id),
      dense: true,
      leading: DependencyIcon(dep),
      title: Text('${dep.title} - ${dep.version}'),
      subtitle: dep.license.isNotEmpty
          ? Text('License: ${dep.license}')
          : Text('(No license)'),
      trailing: (hasId) ? Icon(Icons.chevron_right) : null,
      onTap: (hasId) ? onSelect : null,
    );
  }
}
