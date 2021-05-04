/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';

import '../../model/dependency.dart';
import '../../model/project.dart';
import '../../services/project_service.dart';
import 'dependencies_card.dart';
import 'info_card.dart';

class ProjectView extends StatelessWidget {
  ProjectView({required this.onChanged, this.onSelect});

  final Function(Future<Project>) onChanged;
  final Function(Dependency dependency)? onSelect;

  @override
  Widget build(BuildContext context) {
    final service = ProjectService.of(context);

    return SingleChildScrollView(
      child: Column(
        children: [
          InfoCard(
            service.currentProject!,
            onChanged: onChanged,
          ),
          if (service.currentProject!.dependencies.isNotEmpty)
            DependenciesCard(
              service.currentProject!.dependencies,
              onSelect: onSelect,
            ),
        ],
      ),
    );
  }
}
