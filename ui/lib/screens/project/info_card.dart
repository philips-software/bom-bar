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

  InfoCard(this.project, {required this.onChanged});

  final Project project;
  final Function(Future<Project>) onChanged;

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
                  onPressed: () => _editTitle(context),
                  child: Text(
                    project.titleStr,
                    style: style.headline4,
                  ),
                ),
                if (project.issueCount > 0)
                  Text(
                    '${project.issueCount} license errors',
                    style: TextStyle(color: Colors.red),
                  ),
                if (!kIsWeb)
                  ActionItem(
                    label: 'UUID',
                    icon: Icons.copy,
                    onPressed: () =>
                        Clipboard.setData(ClipboardData(text: project.id)),
                    child: Text(project.id),
                  ),
                Wrap(
                  spacing: 12.0,
                  children: [
                    ActionItem(
                      label: 'Distribution',
                      onPressed: () => _editDistribution(context),
                      child: Text(project.distribution!.name),
                    ),
                    ActionItem(
                      label: 'Phase',
                      onPressed: () => _editPhase(context),
                      child: Text(project.phase!.name),
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
                    if (kIsWeb)
                      UploadWidget(
                        key: Key(project.id),
                        onUpdated: onChanged,
                      ),
                  ],
                )
              ],
            ),
            trailing: Row(mainAxisSize: MainAxisSize.min, children: <Widget>[
              ActionButton(
                icon: Icons.pie_chart,
                onPressed: () => context.yeet('licenses'),
              ),
              ActionButton(
                icon: Icons.add_task,
                onPressed: () => context.yeet('obligations'),
              )
            ]),
          ),
        ],
      ),
    );
  }

  void _editTitle(BuildContext context) {
    EditTextDialog(title: 'Project title', value: project.title)
        .show(context)
        .then((value) {
      if (value != null && value != project.title) {
        onChanged(ProjectService.of(context)
            .updateProject(Project(id: project.id, title: value)));
      }
    });
  }

  void _editPhase(BuildContext context) {
    _editBySelection(
        context: context,
        title: 'Project phase',
        items: {
          for (var p in Phase.values.where((e) => e != Phase.unknown))
            p: p.name,
        },
        value: project.phase,
        projectFrom: (dynamic p) => Project(id: project.id, phase: p));
  }

  void _editDistribution(BuildContext context) {
    _editBySelection<Distribution?>(
      context: context,
      title: 'Target distribution',
      items: {
        for (var d
            in Distribution.values.where((e) => e != Distribution.unknown))
          d: d.name,
      },
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
      if (result != null && result != value) {
        onChanged(
            ProjectService.of(context).updateProject(projectFrom!(result)));
      }
    });
  }
}
