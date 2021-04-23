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

final routes = Yeet(
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
      builder: (context) =>
          ErrorWidget('Path "${context.currentPath}" does not exist.'),
    ),
  ],
);
