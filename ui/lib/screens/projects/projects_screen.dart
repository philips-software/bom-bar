/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:bom_bar_ui/screens/packages/name_filter.dart';
import 'package:flutter/material.dart';
import 'package:yeet/yeet.dart';

import '../../model/project.dart';
import '../../services/project_service.dart';
import '../widgets/app_drawer.dart';
import '../widgets/snapshot_widget.dart';
import 'project_tile.dart';

class ProjectsScreen extends StatefulWidget {
  @override
  _ProjectsScreenState createState() => _ProjectsScreenState();
}

class _ProjectsScreenState extends State<ProjectsScreen> {
  late ProjectService service;
  late Future<List<Project>> projects;
  String _filter = '';

  @override
  void initState() {
    super.initState();
    service = ProjectService.of(context);
    projects = service.allProjects();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: NameFilter(
          hint: 'search for Projects',
          onChanged: (fragment) => setState(() {
            _filter = fragment;
            projects = service.findProjects(_filter);
          }),
        ),
        actions: [
          IconButton(
            icon: Icon(Icons.refresh),
            onPressed: () => setState(() {
              projects = service.allProjects();
            }),
          ),
        ],
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
        onPressed: () => _createProject(context),
        child: Icon(Icons.add),
      ),
    );
  }

  Future<void> _createProject(BuildContext context) async {
    await service.createNew();
    context.yeet('/projects/${service.currentProject!.id}');
    setState(() {});
  }
}
