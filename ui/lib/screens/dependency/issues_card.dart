/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
import 'package:flutter/material.dart';

import '../../model/dependency.dart';
import '../../services/project_service.dart';
import '../widgets/edit_text_dialog.dart';

class IssuesCard extends StatelessWidget {
  IssuesCard(this.dependency, {required this.onChanged});

  final Dependency dependency;
  final Function(Future<Dependency>) onChanged;

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

  void _unexempt(BuildContext context) {
    onChanged(ProjectService.of(context).unexemptDependency());
  }

  void _exemptDependency(BuildContext context) {
    EditTextDialog(
      title: 'Exemption rationale',
      value: dependency.exemption ?? '',
      lines: 5,
    ).show(context).then((rationale) {
      if (rationale != null) {
        onChanged(ProjectService.of(context).exemptDependency(rationale));
      }
    });
  }
}
