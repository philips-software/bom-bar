/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';
import 'package:yeet/yeet.dart';

import '../../model/project.dart';
import '../../services/project_service.dart';
import '../dependency/dependency_view.dart';
import '../widgets/app_drawer.dart';
import '../widgets/snapshot_widget.dart';
import 'dependencies_card.dart';
import 'info_card.dart';

class ProjectScreen extends StatefulWidget {
  ProjectScreen(this.projectId);

  final String projectId;

  @override
  _ProjectScreenState createState() => _ProjectScreenState();
}

class _ProjectScreenState extends State<ProjectScreen> {
  late ProjectService service;
  late Future<Project> loader;
  String? selectedDependencyId;

  @override
  void initState() {
    super.initState();
    service = ProjectService.of(context);
    loader = service.selectProject(widget.projectId);
  }

  @override
  Widget build(BuildContext context) {
    final isWide = MediaQuery.of(context).size.width > 1000;

    return Scaffold(
      appBar: AppBar(
        title: Text('Project'),
        actions: [
          IconButton(
            icon: Icon(Icons.refresh),
            onPressed: () => setState(() {
              loader = service.refreshProject();
              selectedDependencyId = null;
            }),
          )
        ],
      ),
      drawer: AppDrawer(),
      body: FutureBuilder<Project>(
        future: loader,
        builder: (context, snapshot) => SnapshotWidget<Project>(
          snapshot,
          builder: (context, project) {
            return Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              mainAxisSize: MainAxisSize.max,
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                InfoCard(
                  project,
                  onChanged: (future) => setState(() {
                    loader = future;
                  }),
                ),
                Expanded(
                  child: Row(
                    mainAxisSize: MainAxisSize.max,
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: [
                      if (project.dependencies.isNotEmpty)
                        Flexible(
                          child: DependenciesCard(project.dependencies,
                              onSelect: (d) {
                            if (isWide) {
                              setState(() => selectedDependencyId = d.id);
                            } else {
                              context.yeet('dependencies/${d.id}');
                            }
                          }),
                        ),
                      if (isWide)
                        selectedDependencyId != null
                            ? Flexible(
                                child: DependencyView(selectedDependencyId!),
                              )
                            : Container(),
                    ],
                  ),
                ),
              ],
            );
          },
        ),
      ),
    );
  }
}
