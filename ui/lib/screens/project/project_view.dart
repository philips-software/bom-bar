/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';

import '../../model/dependency.dart';
import '../../services/project_service.dart';
import 'dependencies_card.dart';
import 'info_card.dart';

class ProjectView extends StatelessWidget {
  ProjectView({this.onSelect});

  final Function(Dependency dependency)? onSelect;

  @override
  Widget build(BuildContext context) {
    final service = ProjectService.of(context);

    return SingleChildScrollView(
      child: Column(
        children: [
          InfoCard(service.current!),
          if (service.current!.dependencies.isNotEmpty)
            DependenciesCard(
              service.current!.dependencies,
              onSelect: onSelect,
            ),
        ],
      ),
    );
  }
}
