/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:badges/badges.dart';
import 'package:bom_bar_ui/model/project.dart';
import 'package:bom_bar_ui/screens/widgets/project_icon.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

import '../app_routes.dart';

class ProjectTile extends StatelessWidget {
  static final dateFormat = DateFormat.yMMMMd();

  ProjectTile(this.project);

  final Project project;

  @override
  Widget build(BuildContext context) {
    final lastUpdate = (project.lastUpdate != null)
        ? dateFormat.format(project.lastUpdate.toLocal())
        : '(never)';

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
        title: Text(project.title.isNotEmpty ? project.title : '(Untitled)'),
        subtitle: Text(lastUpdate),
        isThreeLine: true,
        onTap: () {
          Navigator.popAndPushNamed(context, projectRoute,
              arguments: project.id);
        },
      ),
    );
  }
}
