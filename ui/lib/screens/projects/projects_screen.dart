/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';
import 'package:yeet/yeet.dart';

import '../../model/project.dart';
import '../../services/backend_service.dart';
import '../../services/project_service.dart';
import '../widgets/app_drawer.dart';
import '../widgets/snapshot_widget.dart';
import 'project_tile.dart';

class ProjectsScreen extends StatefulWidget {
  @override
  _ProjectsScreenState createState() => _ProjectsScreenState();
}

class _ProjectsScreenState extends State<ProjectsScreen> {
  late BackendService backendService;
  late ProjectService projectService;
  late Future<List<Project>> projects;

  @override
  void initState() {
    super.initState();
    backendService = BackendService.of(context);
    projectService = ProjectService.of(context);
    projects = backendService.projects();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('All projects'),
      ),
      drawer: AppDrawer(),
      body: FutureBuilder<List<Project>>(
        future: projects,
        builder: (context, snapshot) => SnapshotWidget<List<Project>>(
          snapshot,
          builder: (context, projects) => GridView.extent(
            maxCrossAxisExtent: 400,
            childAspectRatio: 3,
            children: projects
                .map((project) => ProjectTile(project))
                .toList(growable: false),
          ),
        ),
      ),
      floatingActionButton: FloatingActionButton(
        child: Icon(Icons.add),
        onPressed: () => _createProject(context),
      ),
    );
  }

  Future<void> _createProject(BuildContext context) async {
    await projectService.createNew();
    context.yeet('/projects/${projectService.currentProject!.id}');
    setState(() {});
  }
}
