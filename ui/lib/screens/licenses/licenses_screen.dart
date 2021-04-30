/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
import 'package:flutter/material.dart';
import 'package:pie_chart/pie_chart.dart';

import '../../services/project_service.dart';
import '../widgets/snapshot_widget.dart';

class LicensesScreen extends StatelessWidget {
  LicensesScreen();

  @override
  Widget build(BuildContext context) {
    var projectService = ProjectService.of(context);

    return Scaffold(
      appBar: AppBar(
        title: Text('Licenses'),
      ),
      body: FutureBuilder<Map<String, int>>(
        future: projectService.licenseDistribution(),
        builder: (context, snapshot) => SnapshotWidget(
          snapshot,
          builder: (context, dynamic data) => (data.isEmpty)
              ? Center(child: Text('No licenses found'))
              : PieChart(
                  dataMap: _asPieData(data),
                  centerText: projectService.currentProject!.title,
                  initialAngleInDegree: -90,
                  chartValuesOptions: ChartValuesOptions(
                    showChartValuesInPercentage: true,
                    decimalPlaces: 0,
                  ),
                ),
        ),
      ),
    );
  }

  Map<String, double> _asPieData(Map<String, int> distribution) {
    return Map.fromIterable(
      distribution.entries,
      key: (e) => '${e.key} (${e.value})',
      value: (e) => e.value.toDouble(),
    );
  }
}
