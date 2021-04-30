/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';
import 'package:yeet/yeet.dart';

import '../services/package_service.dart';
import 'dependency/dependency_screen.dart';
import 'licenses/licenses_screen.dart';
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
      ],
    ),
    Yeet(
      path: '/packages',
      builder: (_) => PackagesScreen(),
      children: [
        Yeet(
            path: r':id([^/]+)',
            builder: (context) {
              //TODO Should be part of screen handling
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
