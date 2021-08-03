/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';

import '../../model/dependency.dart';
import '../widgets/dependency_tile.dart';
import 'filter.dart';
import 'text_filter.dart';

class DependenciesCard extends StatefulWidget {
  DependenciesCard(this.dependencies, {this.onSelect});

  final List<Dependency> dependencies;
  final Function(Dependency dependency)? onSelect;

  @override
  _DependenciesCardState createState() => _DependenciesCardState();
}

class _DependenciesCardState extends State<DependenciesCard> {
  String _filter = '';
  late Filter _violationsFilter,
      _exemptionsFilter,
      _anonymousFilter,
      _rootFilter,
      _developmentFilter,
      _deliveredFilter;

  @override
  void initState() {
    super.initState();
    _violationsFilter =
        Filter(filter: (dep) => dep.issueCount > 0, onChange: _refresh);
    _exemptionsFilter =
        Filter(filter: (dep) => dep.exemption != null, onChange: _refresh);
    _anonymousFilter =
        Filter(filter: (dep) => dep.purl == null, onChange: _refresh);
    _rootFilter = Filter(filter: (dep) => dep.isRoot, onChange: _refresh);
    _developmentFilter =
        Filter(filter: (dep) => dep.isDevelopment, onChange: _refresh);
    _deliveredFilter =
        Filter(filter: (dep) => dep.isDelivered, onChange: _refresh);
  }

  void _refresh() {
    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    final filtered = widget.dependencies
        .where((dep) => dep.titleStr.toLowerCase().contains(_filter))
        .where(_violationsFilter.filter)
        .where(_exemptionsFilter.filter)
        .where(_anonymousFilter.filter)
        .where(_rootFilter.filter)
        .where(_developmentFilter.filter)
        .where(_deliveredFilter.filter)
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
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 10.0),
            child: TextFilter(
              key: ValueKey('test'),
              onChanged: (filter) => setState(() {
                _filter = filter;
              }),
            ),
          ),
          Padding(
            padding: const EdgeInsets.only(top: 10.0, left: 10.0, right: 10.0),
            child: Wrap(
              children: [
                SelectFilter(label: 'Roots', filter: _rootFilter),
                SelectFilter(label: 'Delivered', filter: _deliveredFilter),
                SelectFilter(label: 'Development', filter: _developmentFilter),
                SelectFilter(label: 'Anonymous', filter: _anonymousFilter),
                SelectFilter(label: 'Violations', filter: _violationsFilter),
                SelectFilter(label: 'Exempted', filter: _exemptionsFilter),
              ],
            ),
          ),
          Flexible(
              child: Scrollbar(
                  isAlwaysShown: true,
                  child: ListView(
                      padding: EdgeInsets.zero,
                      children: filtered
                          .map((dep) => DependencyTile(
                                dep,
                                onSelect: () => widget.onSelect?.call(dep),
                              ))
                          .toList()))),
        ],
      ),
    );
  }
}
