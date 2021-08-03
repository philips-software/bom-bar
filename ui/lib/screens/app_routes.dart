/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';
import 'package:yeet/yeet.dart';

import 'dependency/dependency_screen.dart';
import 'licenses/licenses_screen.dart';
import 'licenses/obligations_screen.dart';
import 'package/package_screen.dart';
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
    ),
    Yeet(
      path: r'/projects/:id([\w-]+)',
      builder: (context) => ProjectScreen(context.params['id']!),
      children: [
        Yeet(
          path: r'dependencies/:id([\w-]+)',
          builder: (context) => DependencyScreen(context.params['id']!),
        ),
        Yeet(
          path: 'licenses',
          builder: (_) => LicensesScreen(),
        ),
        Yeet(
          path: 'obligations',
          builder: (_) => ObligationsScreen(),
        ),
      ],
    ),
    Yeet(
      path: '/packages',
      builder: (_) => PackagesScreen(),
    ),
    Yeet(
      path: r'/packages/:id([^/]+)',
      builder: (context) => PackageScreen(context.params['id']!),
    ),
    Yeet(
      path: ':_(.*)',
      builder: (context) =>
          ErrorWidget('Path "${context.currentPath}" does not exist.'),
    ),
  ],
);
