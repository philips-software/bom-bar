/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:bom_bar_ui/model/package.dart';
import 'package:bom_bar_ui/screens/widgets/snapshot_widget.dart';
import 'package:flutter/material.dart';

import '../../services/package_service.dart';
import '../widgets/app_drawer.dart';
import 'info_card.dart';
import 'projects_card.dart';

class PackageScreen extends StatefulWidget {
  PackageScreen(this.packageId);

  final String packageId;

  @override
  _PackageScreenState createState() => _PackageScreenState();
}

class _PackageScreenState extends State<PackageScreen> {
  late final PackageService service;

  late Future<Package> loader;

  @override
  void initState() {
    super.initState();
    service = PackageService.of(context);
    loader = service.select(widget.packageId);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Package'),
        actions: [
          IconButton(
            icon: Icon(Icons.refresh),
            onPressed: () => setState(() {
              service.refresh();
            }),
          )
        ],
      ),
      drawer: AppDrawer(),
      body: _body(context),
    );
  }

  Widget _body(BuildContext context) {
    return FutureBuilder<Package>(
      future: loader,
      builder: (context, snapshot) => SnapshotWidget<Package>(
        snapshot,
        builder: (context, package) {
          return Column(
            children: [
              InfoCard(
                package,
                onChanged: (future) => setState(() {
                  loader = future;
                }),
              ),
              Flexible(
                child: ProjectsCard(package),
              ),
            ],
          );
        },
      ),
    );
  }
}
