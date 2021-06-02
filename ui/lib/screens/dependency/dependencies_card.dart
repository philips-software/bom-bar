/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';

import '../../model/dependency.dart';
import '../../services/project_service.dart';
import '../widgets/dependency_tile.dart';

class DependenciesCard extends StatelessWidget {
  DependenciesCard(this.dependencies, {this.onChanged});

  final List<Dependency> dependencies;
  final Function(Future<Dependency>)? onChanged;

  @override
  Widget build(BuildContext context) {
    return dependencies.isNotEmpty
        ? ListView.builder(
            itemCount: dependencies.length,
            itemBuilder: (context, index) {
              final dependency = dependencies[index];
              return DependencyTile(
                dependency,
                onSelect: () => _selectDependencyById(context, dependency.id),
              );
            })
        : Center(child: Text('(None)'));
  }

  void _selectDependencyById(BuildContext context, String id) {
    final service = ProjectService.of(context);
    onChanged?.call(service.selectDependency(id));
  }
}
