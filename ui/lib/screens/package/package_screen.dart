/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:yeet/yeet.dart';

import '../../services/package_service.dart';
import '../widgets/app_drawer.dart';
import 'info_card.dart';
import 'projects_card.dart';

class PackageScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final isRoot = !Navigator.of(context).canPop();

    return Scaffold(
      appBar: AppBar(
        title: Text('Package'),
        actions: [
          IconButton(
            icon: Icon(Icons.search),
            onPressed: () => context.yeet('/packages'),
          )
        ],
      ),
      drawer: isRoot ? AppDrawer() : null,
      body: Consumer<PackageService>(
        builder: (context, service, _) {
          if (service.error != null) {
            return ErrorWidget(service.error!);
          }
          if (service.current == null) {
            return Center(child: CircularProgressIndicator.adaptive());
          }
          return Column(
            children: [
              InfoCard(service.current!),
              Flexible(child: ProjectsCard(service.current!)),
            ],
          );
        },
      ),
    );
  }
}
