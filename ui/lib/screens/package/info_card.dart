/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'dart:developer';

import 'package:flutter/material.dart';

import '../../model/package.dart';
import '../../services/package_service.dart';
import '../package/approval_tile.dart';
import '../widgets/action_link.dart';
import '../widgets/edit_text_dialog.dart';

class InfoCard extends StatelessWidget {
  InfoCard(this.package);

  final Package package;

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Column(
        children: [
          ListTile(
            leading: Icon(Icons.extension),
            title: Text(package.titleStr,
                style: Theme.of(context).textTheme.headline4),
            subtitle: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'pkg:${package.reference}',
                  style: TextStyle(fontStyle: FontStyle.italic),
                ),
                SizedBox(height: 8),
                Text(package.vendor ?? '(Vendor unknown)'),
                if (package.homepage != null)
                  ActionLink(
                    child: Text('Home page:'),
                    url: package.homepage!,
                  ),
                if (package.description != null)
                  Padding(
                    padding: const EdgeInsets.only(top: 8),
                    child: Text(
                      package.description!,
                      style: TextStyle(fontStyle: FontStyle.italic),
                    ),
                  ),
              ],
            ),
          ),
          ApprovalTile(package),
          ButtonBar(
            children: [
              TextButton.icon(
                icon: Icon(Icons.shield),
                label: Text('EXEMPT LICENSE'),
                onPressed: () => _exemptLicense(context),
              ),
            ],
          ),
        ],
      ),
    );
  }

  void _exemptLicense(BuildContext context) async {
    final license =
        await EditTextDialog(title: 'License to exempt').show(context);
    if (license != null) {
      PackageService.of(context)
          .exempt(license)
          .catchError((error) => log('Failed to exempt license', error: error));
    }
  }
}
