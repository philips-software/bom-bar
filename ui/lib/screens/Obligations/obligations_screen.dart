/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
import 'package:bom_bar_ui/model/dependency.dart';
import 'package:bom_bar_ui/model/obligationItem.dart';
import 'package:bom_bar_ui/screens/Obligations/obligations_view.dart';
import 'package:flutter/material.dart';

import '../../services/project_service.dart';
import '../widgets/snapshot_widget.dart';

class ObligationsScreen extends StatelessWidget {
  ObligationsScreen();

  @override
  Widget build(BuildContext context) {
    var projectService = ProjectService.of(context);

    return Scaffold(
        appBar: AppBar(
          title: Text('Obligations!'),
        ),
        body: FutureBuilder<Map<String, List<Dependency>>>(
          future: projectService.obligations(),
          builder: (context, snapshot) => SnapshotWidget(snapshot,
              builder: (context, dynamic data) => (data.isEmpty)
                  ? Center(child: Text('No Obligations found'))
                  : buildObligationView(data)),
        ));
  }
}

Widget buildObligationView(Map<String, List<Dependency>> data) {
  var ObligationsItems = <ObligationItem>[];
  data.forEach((k, v) => ObligationsItems.add(ObligationItem(k, v, false)));
  return ObligationsView(ObligationsItems);
}
