/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
import 'package:bom_bar_ui/model/dependency.dart';
import 'package:flutter/material.dart';

import '../../services/project_service.dart';
import '../widgets/snapshot_widget.dart';

class ObligationsScreen extends StatelessWidget {
  ObligationsScreen();

  final ScrollController controller = ScrollController();

  @override
  Widget build(BuildContext context) {
    var projectService = ProjectService.of(context);
    List<Dependency> dependencies = [];
    String? selectedDependencyId;

    return Scaffold(
        appBar: AppBar(
          title: Text('Obligations!'),
        ),
        body: FutureBuilder<Map<String, List<Dependency>>>(
          future: projectService.obligations(),
          builder: (context, snapshot) => SnapshotWidget(snapshot,
              builder: (context, dynamic data) => (data.isEmpty)
                  ? Center(child: Text('No Obligations found'))
                  : ListView.builder(
                      itemCount: data.length,
                      itemBuilder: (BuildContext context, int index) {
                        var key = data.keys.elementAt(index);
                        return Row(mainAxisSize: MainAxisSize.min, children: [
                          Column(
                            children: <Widget>[
                              Column(
                                children: [
                                  ListTile(
                                      title: Text('$key'),
                                      trailing: Icon(Icons.chevron_right),
                                      onTap: () {
                                        dependencies = data[key];
                                      })
                                ],
                              ),
                              Divider(
                                height: 2.0,
                              ),
                            ],
                          ),
                          // Flexible(
                          //   child: DependenciesCard(dependencies,
                          //       onChanged: ),
                          // )
                        ]);
                        // ],
                        // );
                      },
                    )),
        ));
  }

  // void _selectDependencyById(BuildContext context, String id) {
  //   final service = ProjectService.of(context);
  //   service.selectDependency(id);
  // }
}
