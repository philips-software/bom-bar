/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';

import '../../model/dependency.dart';
import '../../services/dependency_service.dart';
import '../widgets/dependency_tile.dart';

class DependenciesCard extends StatelessWidget {
  DependenciesCard(this.dependencies);

  final List<Dependency> dependencies;

  @override
  Widget build(BuildContext context) {
    final service = DependencyService.of(context);

    return dependencies.isNotEmpty
        ? ListView.builder(
            itemCount: dependencies.length,
            itemBuilder: (context, index) {
              final dependency = dependencies[index];
              return DependencyTile(
                dependency,
                onSelect: () => service.select(dependency.id),
              );
            })
        : Center(child: Text('(None)'));
  }
}
