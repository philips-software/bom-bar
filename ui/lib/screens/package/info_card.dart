/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';

import '../../model/package.dart';
import '../../services/package_service.dart';
import '../package/approval_tile.dart';
import '../widgets/action_link.dart';
import '../widgets/edit_text_dialog.dart';

class InfoCard extends StatelessWidget {
  InfoCard(this.package, {required this.onChanged});

  final Package package;
  final Function(Future<Package>) onChanged;

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
                    url: package.homepage!,
                    child: Text('Home page:'),
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
          ApprovalTile(
            package,
            onChanged: onChanged,
          ),
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

  void _exemptLicense(BuildContext context) {
    EditTextDialog(title: 'License to exempt').show(context).then((license) {
      if (license != null) {
        onChanged(PackageService.of(context).exempt(license));
      }
    });
  }
}
