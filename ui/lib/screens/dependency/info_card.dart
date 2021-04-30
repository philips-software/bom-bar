/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';
import 'package:yeet/yeet.dart';

import '../../model/dependency.dart';
import '../widgets/action_button.dart';
import '../widgets/dependency_icon.dart';

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
                Text('${dependency.title} ${dependency.version}',
                    style: style.headline4),
                Text('SPDX ID: ${dependency.id}'),
                if (dependency.purl != null) Text(dependency.purl.toString()),
                SizedBox(height: 8),
                if (dependency.license == null)
                  Text('(Unknown)')
                else if (dependency.license!.isEmpty)
                  Text('(No license)')
                else
                  Text('License: ${dependency.license}'),
              ],
            ),
            trailing: (dependency.package != null)
                ? ActionButton(
                    icon: Icons.chevron_right,
                    onPressed: () =>
                        context.yeet('/packages/${dependency.package!.id}'),
                  )
                : null,
          ),
        ],
      ),
    );
  }
}
