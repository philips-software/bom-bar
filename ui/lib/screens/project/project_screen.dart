/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:bom_bar_ui/screens/app_routes.dart';
import 'package:bom_bar_ui/screens/widgets/app_drawer.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../services/dependency_service.dart';
import '../../services/project_service.dart';
import '../dependency/dependency_view.dart';
import 'dependencies_card.dart';
import 'info_card.dart';

class ProjectScreen extends StatelessWidget {
  ProjectScreen();

  @override
  Widget build(BuildContext context) {
    final isWide = MediaQuery.of(context).size.width > 1000;
    final service = ProjectService.of(context);
    final dependencyService = DependencyService.of(context);

    return Scaffold(
      appBar: AppBar(
        title: Text('Project'),
        actions: [
          IconButton(
            icon: Icon(Icons.refresh),
            onPressed: () => service.refresh(),
          )
        ],
      ),
      drawer: AppDrawer(),
      body: Consumer<ProjectService>(
        builder: (context, service, child) {
          if (service.error != null) {
            return ErrorWidget(service.error);
          }
          if (service.current == null) {
            return Center(child: CircularProgressIndicator.adaptive());
          }
          return Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            mainAxisSize: MainAxisSize.max,
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: [
              InfoCard(service.current),
              Expanded(
                child: Row(
                    mainAxisSize: MainAxisSize.max,
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: [
                      if (service.current.dependencies.isNotEmpty)
                        Flexible(
                          child: DependenciesCard(
                            service.current.dependencies,
                            onSelect: (d) {
                              dependencyService.select(d.id).then((_) {
                                if (!isWide) {
                                  Navigator.pushNamed(context, dependencyRoute);
                                }
                              });
                            },
                          ),
                        ),
                      if (isWide)
                        Flexible(
                          child: DependencyView(),
                        ),
                    ]),
              ),
            ],
          );
        },
      ),
    );
  }
}
