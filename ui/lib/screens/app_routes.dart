/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'dart:developer';

import 'package:bom_bar_ui/screens/licenses/licenses_screen.dart';
import 'package:bom_bar_ui/screens/package/package_screen.dart';
import 'package:bom_bar_ui/services/package_service.dart';
import 'package:bom_bar_ui/services/project_service.dart';
import 'package:flutter/material.dart';

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

abstract class AppRoutes {
  static Route<dynamic> onGenerateRoute(RouteSettings settings) {
    return MaterialPageRoute(
      builder: (context) => _screenFromRoute(context, settings),
    );
  }

  static Widget _screenFromRoute(BuildContext context, RouteSettings settings) {
    switch (settings.name) {
      case '/':
      case projectsRoute:
        return ProjectsScreen();
      case packagesRoute:
        return PackagesScreen();
      case projectRoute:
        ProjectService.of(context).select(settings.arguments);
        return ProjectScreen();
      case dependencyRoute:
        return DependencyScreen();
      case licensesRoute:
        return LicensesScreen();
      case packageRoute:
        PackageService.of(context).select(settings.arguments);
        return PackageScreen();
      default:
        log('No route defined for "${settings.name}"');
        return null;
    }
  }
}
