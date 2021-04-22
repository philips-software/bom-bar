/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:bom_bar_ui/model/project.dart';
import 'package:flutter/material.dart';

class ProjectIcon extends StatelessWidget {
  ProjectIcon(this.project);

  final Project project;

  @override
  Widget build(BuildContext context) {
    return Icon(_projectIcons[project.phase] ?? Icons.warning);
  }
}

final _projectIcons = {
  Phase.development: Icons.build,
  Phase.released: Icons.check_circle,
};
