/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:bom_bar_ui/screens/widgets/project_icon.dart';
import 'package:flutter/material.dart';

import '../../model/package.dart';
import '../app_routes.dart';

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
            child: ListView.builder(
              itemCount: package.projects.length,
              itemBuilder: (BuildContext context, int index) {
                final project = package.projects[index];
                return ListTile(
                  leading: ProjectIcon(project),
                  title: Text(project.title),
                  subtitle: Text(project.dependencies
                      .map((dep) =>
                          '${dep.title} ${dep.version} (${dep.license})')
                      .join(", ")),
                  onTap: () => Navigator.pushNamed(
                    context,
                    projectRoute,
                    arguments: project.id,
                  ),
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}
