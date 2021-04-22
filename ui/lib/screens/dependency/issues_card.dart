/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
import 'package:bom_bar_ui/model/dependency.dart';
import 'package:bom_bar_ui/screens/widgets/edit_text_dialog.dart';
import 'package:bom_bar_ui/screens/widgets/shared.dart';
import 'package:bom_bar_ui/services/dependency_service.dart';
import 'package:flutter/material.dart';

class IssuesCard extends StatelessWidget {
  IssuesCard(this.dependency);

  final Dependency dependency;

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Column(
        children: [
          ...dependency.licenseIssues
              .map((issue) => ListTile(
                    dense: true,
                    leading: Icon(
                      Icons.error,
                      color: Colors.red,
                    ),
                    title: Text(
                      issue,
                      style: TextStyle(color: Colors.red),
                    ),
                  ))
              .toList(growable: false),
          if (dependency.exemption != null)
            ListTile(
              leading: Icon(Icons.shield, color: Colors.green),
              title: Text('Exemption', style: TextStyle(color: Colors.green)),
              subtitle: Text(dependency.exemption!.isNotEmpty
                  ? dependency.exemption!
                  : '(Without rationale)'),
            ),
          ButtonBar(
            children: [
              if (dependency.package != null)
                TextButton.icon(
                  icon: Icon(Icons.shield),
                  label: Text('EXEMPT'),
                  onPressed: () => _exemptDependency(context),
                ),
              if (dependency.exemption != null)
                TextButton.icon(
                  icon: Icon(Icons.delete),
                  label: Text('UN-EXEMPT'),
                  onPressed: () => _unexempt(context),
                ),
            ],
          )
        ],
      ),
    );
  }

  void _unexempt(BuildContext context) => DependencyService.of(context)
      .unexempt()
      .catchError((error) => showError(context, error));

  Future<void> _exemptDependency(BuildContext context) async {
    final rationale = await EditTextDialog(
      title: 'Exemption rationale',
      value: dependency.exemption ?? '',
      lines: 5,
    ).show(context);

    if (rationale != null) {
      DependencyService.of(context)
          .exempt(rationale)
          .catchError((error) => showError(context, error));
    }
  }
}
