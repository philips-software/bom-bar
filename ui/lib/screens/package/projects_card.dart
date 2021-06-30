/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';
import 'package:yeet/yeet.dart';

import '../../model/package.dart';
import '../widgets/project_icon.dart';

class ProjectsCard extends StatelessWidget {
  ProjectsCard(this.package);

  final Package package;

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Padding(
            padding: const EdgeInsets.only(top: 4, left: 8),
            child: Text(
              'Used in projects (${package.projects.length})',
              textAlign: TextAlign.left,
              style: Theme.of(context).textTheme.headline6,
            ),
          ),
          Expanded(
            child: Scrollbar(
              isAlwaysShown: true,
              child: ListView.builder(
                itemCount: package.projects.length,
                itemBuilder: (BuildContext context, int index) {
                  final project = package.projects[index];
                  return ListTile(
                    leading: ProjectIcon(project),
                    title: Text(project.titleStr),
                    subtitle: Text(project.dependencies
                        .map((dep) =>
                            '${dep.isRoot ? "\uD83D\uDEE0 " : ''}${dep.title} ${dep.version} (${dep.license})')
                        .join(', ')),
                    onTap: () => context.yeet('/projects/${project.id}'),
                  );
                },
              ),
            ),
          ),
        ],
      ),
    );
  }
}
