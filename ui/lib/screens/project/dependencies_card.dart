/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';

import '../../model/dependency.dart';
import '../widgets/dependency_tile.dart';
import 'filter_field.dart';

class DependenciesCard extends StatefulWidget {
  DependenciesCard(this.dependencies, {this.onSelect});

  final List<Dependency> dependencies;
  final Function(Dependency dependency) onSelect;

  @override
  _DependenciesCardState createState() => _DependenciesCardState();
}

class _DependenciesCardState extends State<DependenciesCard> {
  String _filter = '';
  bool _onlyErrors = false;

  @override
  Widget build(BuildContext context) {
    final filtered = widget.dependencies
        .where((dep) => dep.title.toLowerCase().contains(_filter))
        .where((dep) => !_onlyErrors || dep.issueCount > 0)
        .toList(growable: false);
    final dependencyCount = (filtered.length != widget.dependencies.length)
        ? '${filtered.length}/${widget.dependencies.length}'
        : widget.dependencies.length.toString();

    return Card(
      child: Column(
        mainAxisSize: MainAxisSize.max,
        mainAxisAlignment: MainAxisAlignment.start,
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Padding(
            padding: const EdgeInsets.only(left: 12.0, top: 8.0),
            child: Text(
              'Dependencies ($dependencyCount)',
              style: Theme.of(context).textTheme.headline6,
            ),
          ),
          FilterField(onChanged: _onFilterChange),
          Flexible(
            child: ListView(
              padding: EdgeInsets.zero,
              children: filtered
                  .map((dep) => DependencyTile(
                        dep,
                        onSelect: () => widget.onSelect(dep),
                      ))
                  .toList(),
            ),
          ),
        ],
      ),
    );
  }

  void _onFilterChange(String filter, bool onlyErrors) {
    setState(() {
      _filter = filter.toLowerCase();
      _onlyErrors = onlyErrors;
    });
  }
}
