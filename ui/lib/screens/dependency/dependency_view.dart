/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
import 'package:bom_bar_ui/model/dependency.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../services/dependency_service.dart';
import 'dependencies_card.dart';
import 'info_card.dart';
import 'issues_card.dart';

class DependencyView extends StatefulWidget {
  @override
  _DependencyViewState createState() => _DependencyViewState();
}

class _DependencyViewState extends State<DependencyView>
    with TickerProviderStateMixin {
  late TabController _controller;

  final _tabs = {
    (Dependency _) => Text('Depends on'): (Dependency dependency) =>
        DependenciesCard(dependency.dependencies),
    (Dependency _) => Text('Dependency of'): (Dependency dependency) =>
        DependenciesCard(dependency.usages),
    (Dependency dependency) => Text('Violations'): (Dependency dependency) =>
        SingleChildScrollView(child: IssuesCard(dependency)),
  };

  @override
  void initState() {
    super.initState();
    _controller = TabController(length: _tabs.length, vsync: this);
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Consumer<DependencyService>(
      builder: (context, service, child) {
        final dependency = service.current;
        return dependency == null
            ? Container()
            : Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                mainAxisSize: MainAxisSize.max,
                mainAxisAlignment: MainAxisAlignment.start,
                children: [
                  InfoCard(dependency),
                  TabBar(
                    controller: _controller,
                    tabs: _tabs.keys.map((fn) => fn(dependency)).toList(),
                  ),
                  Flexible(
                    child: Card(
                      child: TabBarView(
                        controller: _controller,
                        children:
                            _tabs.values.map((fn) => fn(dependency)).toList(),
                      ),
                    ),
                  ),
                ],
              );
      },
    );
  }
}
