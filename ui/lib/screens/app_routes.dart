/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:bom_bar_ui/screens/licenses/licenses_screen.dart';
import 'package:bom_bar_ui/screens/package/package_screen.dart';
import 'package:bom_bar_ui/services/package_service.dart';
import 'package:bom_bar_ui/services/project_service.dart';
import 'package:flutter/material.dart';
import 'package:yeet/yeet.dart';

import 'dependency/dependency_screen.dart';
import 'packages/packages_screen.dart';
import 'project/project_screen.dart';
import 'projects/projects_screen.dart';

const projectsRoute = '/projects';
const projectRoute = '/project';
const packagesRoute = '/packages';
const packageRoute = '/package';
const dependencyRoute = '/dependency';
const licensesRoute = '/licenses';

final yeet = Yeet(
  children: [
    Yeet(
      path: '/',
      builder: (_) => ProjectsScreen(),
    ),
    Yeet(
      path: '/projects',
      builder: (_) => ProjectsScreen(),
      children: [
        Yeet(
          path: r':id([\w-]+)',
          builder: (context) {
            ProjectService.of(context).select(context.params['id']!);
            return ProjectScreen();
          },
          children: [
            Yeet(
              path: 'licenses',
              builder: (_) => LicensesScreen(),
            ),
            Yeet(path: r'dependency', builder: (context) => DependencyScreen())
          ],
        ),
      ],
    ),
    Yeet(
      path: '/packages',
      builder: (_) => PackagesScreen(),
      children: [
        Yeet(
            path: r':id([^/]+)',
            builder: (context) {
              PackageService.of(context).select(context.params['id']!);
              return PackageScreen();
            })
      ],
    ),
    Yeet(
      path: ':_(.*)',
      builder: (context) => _PathNotFoundWidget(context.currentPath),
    ),
  ],
);

class _PathNotFoundWidget extends StatelessWidget {
  _PathNotFoundWidget(this.path);

  final String path;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Text('Path "$path" does not exist.'),
      ),
    );
  }
}
