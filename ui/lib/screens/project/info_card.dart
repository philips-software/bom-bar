/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/services.dart';
import 'package:intl/intl.dart';
import 'package:yeet/yeet.dart';

import '../../model/project.dart';
import '../../services/project_service.dart';
import '../widgets/action_button.dart';
import '../widgets/action_item.dart';
import '../widgets/edit_selection_dialog.dart';
import '../widgets/edit_text_dialog.dart';
import '../widgets/project_icon.dart';
import 'upload_widget.dart';

class InfoCard extends StatelessWidget {
  static final dateFormat = DateFormat.yMMMMEEEEd().add_Hm();

  InfoCard(this.project);

  final Project project;

  @override
  Widget build(BuildContext context) {
    final style = Theme.of(context).textTheme;

    return Card(
      child: Column(
        children: [
          ListTile(
              leading: ProjectIcon(project),
              title: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  ActionItem(
                    child: Text(
                      project.titleStr,
                      style: style.headline4,
                    ),
                    onPressed: () => _editTitle(context),
                  ),
                  if (project.issueCount > 0)
                    Text(
                      '${project.issueCount} license errors',
                      style: TextStyle(color: Colors.red),
                    ),
                  if (!kIsWeb)
                    ActionItem(
                      label: 'UUID',
                      child: Text(project.id),
                      icon: Icons.copy,
                      onPressed: () => Clipboard.setData(
                          new ClipboardData(text: project.id)),
                    ),
                  Wrap(
                    spacing: 12.0,
                    children: [
                      ActionItem(
                        label: 'Distribution',
                        child: Text(project.distribution!.name),
                        onPressed: () => _editDistribution(context),
                      ),
                      ActionItem(
                        label: 'Phase',
                        child: Text(project.phase!.name),
                        onPressed: () => _editPhase(context),
                      ),
                    ],
                  ),
                  Wrap(
                    crossAxisAlignment: WrapCrossAlignment.center,
                    children: [
                      if (project.lastUpdate != null)
                        Text(
                            'Last update: ${dateFormat.format(project.lastUpdate!.toLocal())}')
                      else
                        Text('(No bill-of-materials imported)'),
                      if (kIsWeb) UploadWidget(key: ObjectKey(project)),
                    ],
                  )
                ],
              ),
              trailing: ActionButton(
                icon: Icons.pie_chart,
                onPressed: () => context.yeet('licenses'),
              )),
        ],
      ),
    );
  }

  void _editTitle(BuildContext context) {
    EditTextDialog(title: 'Project title', value: project.title)
        .show(context)
        .then((value) {
      if (value != null) {
        final service = ProjectService.of(context);
        service.updateProject(Project(id: project.id, title: value));
      }
    });
  }

  void _editPhase(BuildContext context) {
    _editBySelection(
        context: context,
        title: 'Project phase',
        items: Map.fromIterable(
            Phase.values.where((element) => element != Phase.unknown),
            key: (v) => v,
            value: (v) => (v as Phase).name),
        value: project.phase,
        projectFrom: (dynamic p) => Project(id: project.id, phase: p));
  }

  void _editDistribution(BuildContext context) {
    _editBySelection<Distribution?>(
      context: context,
      title: 'Target distribution',
      items: Map.fromIterable(
          Distribution.values
              .where((element) => element != Distribution.unknown),
          key: (v) => v,
          value: (v) => (v as Distribution).name),
      value: project.distribution,
      projectFrom: (d) => Project(id: project.id, distribution: d!),
    );
  }

  void _editBySelection<T>({
    required BuildContext context,
    required String title,
    required Map<T, String> items,
    T? value,
    Project Function(T value)? projectFrom,
  }) {
    EditSelectionDialog(title: title, values: items, selection: value)
        .show(context)
        .then((result) {
      if (result != null) {
        final service = ProjectService.of(context);
        service.updateProject(projectFrom!(result));
      }
    });
  }
}
