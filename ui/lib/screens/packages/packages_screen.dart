/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';
import 'package:yeet/yeet.dart';

import '../../model/package.dart';
import '../../services/package_service.dart';
import '../widgets/app_drawer.dart';
import '../widgets/name_filter.dart';
import '../widgets/snapshot_widget.dart';

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
          hint: 'Package name fragment',
          onChanged: (fragment) => setState(() {
            filter = fragment;
          }),
        ),
      ),
      drawer: AppDrawer(),
      body: FutureBuilder(
        future: PackageService.of(context).findPackages(filter),
        builder: (context, snapshot) => SnapshotWidget<List<Package>>(
          snapshot as AsyncSnapshot<List<Package>>,
          builder: (context, list) {
            return (list.isNotEmpty)
                ? Scrollbar(
                    isAlwaysShown: true,
                    child: ListView.builder(
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
                    ),
                  )
                : Center(child: Text('(No matching packages found)'));
          },
        ),
      ),
    );
  }
}
