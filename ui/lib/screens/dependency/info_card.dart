/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:bom_bar_ui/screens/app_routes.dart';
import 'package:bom_bar_ui/screens/widgets/action_button.dart';
import 'package:bom_bar_ui/screens/widgets/dependency_icon.dart';
import 'package:flutter/material.dart';

import '../../model/dependency.dart';

class InfoCard extends StatelessWidget {
  InfoCard(this.dependency);

  final Dependency dependency;

  @override
  Widget build(BuildContext context) {
    final style = Theme.of(context).textTheme;

    return Card(
      child: Column(
        children: [
          ListTile(
            leading: DependencyIcon(dependency),
            title: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                    '${dependency.title} ${dependency.version ?? "(no version)"}',
                    style: style.headline4),
                Text('SPDX ID: ${dependency.id}'),
                if (dependency.purl != null) Text(dependency.purl.toString()),
                SizedBox(height: 8),
                Text(dependency.license.isNotEmpty
                    ? 'License: ${dependency.license}'
                    : '(No license)'),
              ],
            ),
            trailing: (dependency.package != null)
                ? ActionButton(
                    icon: Icons.chevron_right,
                    onPressed: () => Navigator.of(context).pushNamed(
                        packageRoute,
                        arguments: dependency.package.id),
                  )
                : null,
          ),
        ],
      ),
    );
  }
}
