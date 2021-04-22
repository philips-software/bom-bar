/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'screens/app_ui.dart';
import 'services/backend_service.dart';
import 'services/bombar_client.dart';
import 'services/dependency_service.dart';
import 'services/package_service.dart';
import 'services/project_service.dart';

void main() {
  runApp(BomBarApplication());
}

final _client = BomBarClient();
final _backendService = BackendService(client: _client);
final _projectService = ProjectService(client: _client);
final _dependencyService =
    DependencyService(projectService: _projectService, client: _client);
final _packageService = PackageService(client: _client);

class BomBarApplication extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        Provider(create: (_) => _backendService),
        ChangeNotifierProvider(create: (_) => _projectService),
        ChangeNotifierProvider(create: (_) => _dependencyService),
        ChangeNotifierProvider(create: (_) => _packageService),
      ],
      child: AppUI(),
    );
  }
}
