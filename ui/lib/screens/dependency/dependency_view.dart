/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
import 'package:flutter/material.dart';

import '../../model/dependency.dart';
import '../../services/project_service.dart';
import '../widgets/snapshot_widget.dart';
import 'dependencies_card.dart';
import 'info_card.dart';
import 'issues_card.dart';

class DependencyView extends StatefulWidget {
  DependencyView(this.dependencyId);

  final String dependencyId;

  @override
  _DependencyViewState createState() => _DependencyViewState();
}

class _DependencyViewState extends State<DependencyView>
    with TickerProviderStateMixin {
  late final TabController _controller;
  late final ProjectService service;
  late Future<Dependency> loader;

  late final _tabs = {
    (Dependency dep) => Text('Depends on ${_qty(dep.dependencies.length)}'):
        (Dependency dependency) => DependenciesCard(
              dependency.dependencies,
              onChanged: _updateView,
            ),
    (Dependency dep) => Text('Dependency of ${_qty(dep.usages.length)}'):
        (Dependency dependency) => DependenciesCard(
              dependency.usages,
              onChanged: _updateView,
            ),
    (Dependency dep) => Text('Violations ${_qty(dep.issueCount)}'):
        (Dependency dependency) => SingleChildScrollView(
              child: IssuesCard(
                dependency,
                onChanged: _updateView,
              ),
            ),
  };

  String _qty(int value) => value != 0 ? '($value)' : '';

  void _updateView(Future<Dependency> future) {
    setState(() {
      loader = future;
    });
  }

  @override
  void initState() {
    super.initState();
    _controller = TabController(length: _tabs.length, vsync: this);
    service = ProjectService.of(context);
    loader = service.selectDependency(widget.dependencyId);
  }

  @override
  void didUpdateWidget(DependencyView oldWidget) {
    super.didUpdateWidget(oldWidget);
    loader = service.selectDependency(widget.dependencyId);
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<Dependency>(
      future: loader,
      builder: (context, snapshot) => SnapshotWidget<Dependency>(
        snapshot,
        builder: (context, dependency) {
          return Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            mainAxisSize: MainAxisSize.max,
            mainAxisAlignment: MainAxisAlignment.start,
            children: [
              InfoCard(dependency),
              TabBar(
                controller: _controller,
                tabs: _tabs.keys.map((fn) => fn(dependency)).toList(),
                labelColor: Theme.of(context).textTheme.bodyText1?.color,
              ),
              Flexible(
                child: Card(
                  child: TabBarView(
                    controller: _controller,
                    children: _tabs.values.map((fn) => fn(dependency)).toList(),
                  ),
                ),
              ),
            ],
          );
        },
      ),
    );
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }
}
