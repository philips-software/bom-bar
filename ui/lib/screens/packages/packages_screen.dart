/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:bom_bar_ui/model/package.dart';
import 'package:bom_bar_ui/screens/widgets/app_drawer.dart';
import 'package:bom_bar_ui/screens/widgets/snapshot_widget.dart';
import 'package:bom_bar_ui/services/backend_service.dart';
import 'package:flutter/material.dart';
import 'package:yeet/yeet.dart';

import 'name_filter.dart';

class PackagesScreen extends StatefulWidget {
  @override
  _PackagesScreenState createState() => _PackagesScreenState();
}

class _PackagesScreenState extends State<PackagesScreen> {
  String filter = '';

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: NameFilter(
          hint: 'package name fragment',
          onChanged: (fragment) => setState(() {
            filter = fragment;
          }),
        ),
      ),
      drawer: AppDrawer(),
      body: FutureBuilder(
        future: BackendService.of(context).packages(filter),
        builder: (context, snapshot) => SnapshotWidget<List<Package>>(
          snapshot as AsyncSnapshot<List<Package>>,
          builder: (context, list) {
            return (list.isNotEmpty)
                ? ListView.builder(
                    itemCount: list.length,
                    itemBuilder: (context, index) {
                      final package = list[index];

                      return ListTile(
                        leading: Icon(Icons.extension),
                        title: Text(package.titleStr),
                        subtitle: Text(package.vendor ?? '(Vendor unknown)'),
                        onTap: () => context.yeet('/packages/${package.id}'),
                      );
                    },
                  )
                : Center(child: Text('(No matching packages found)'));
          },
        ),
      ),
    );
  }
}
