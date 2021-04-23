/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:badges/badges.dart';
import 'package:bom_bar_ui/model/project.dart';
import 'package:bom_bar_ui/screens/widgets/project_icon.dart';
import 'package:flutter/material.dart';
import 'package:yeet/yeet.dart';

class ProjectTile extends StatelessWidget {
  ProjectTile(this.project);

  final Project project;

  @override
  Widget build(BuildContext context) {
    return Card(
      child: ListTile(
        leading: Badge(
          badgeContent: Text(
            project.issueCount.toString(),
            style: TextStyle(color: Colors.white),
          ),
          showBadge: project.issueCount > 0,
          child: ProjectIcon(project),
        ),
        title: Text(project.titleStr),
        subtitle: Text(project.lastUpdateStr),
        isThreeLine: true,
        onTap: () => context.yeet('/projects/${project.id}'),
      ),
    );
  }
}
