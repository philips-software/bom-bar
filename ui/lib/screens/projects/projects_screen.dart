/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:bom_bar_ui/screens/widgets/app_drawer.dart';
import 'package:flutter/material.dart';

import '../../model/project.dart';
import '../../services/backend_service.dart';
import '../../services/project_service.dart';
import '../app_routes.dart';
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
  void didChangeDependencies() {
    super.didChangeDependencies();
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
      body: FutureBuilder(
        future: projects,
        builder: (context, snapshot) => SnapshotWidget<List<Project>>(
          snapshot as AsyncSnapshot<List<Project>>,
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
    Navigator.pushNamed(context, projectRoute,
        arguments: projectService.current!.id);
    setState(() {});
  }
}
