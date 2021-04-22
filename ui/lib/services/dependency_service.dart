/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'dart:developer';

import 'package:flutter/foundation.dart';
import 'package:flutter/widgets.dart';
import 'package:provider/provider.dart';

import '../model/dependency.dart';
import 'bombar_client.dart';
import 'project_service.dart';

class DependencyService extends ChangeNotifier {
  factory DependencyService.of(BuildContext context) =>
      Provider.of<DependencyService>(context, listen: false);

  DependencyService(
      {required ProjectService projectService, BomBarClient? client})
      : _projectService = projectService,
        _client = client ?? BomBarClient() {
    _projectService.addListener(() {
      current = null;
      log('Cleared dependency selection');
      notifyListeners();
    });
  }

  final ProjectService _projectService;
  final BomBarClient _client;
  Dependency? current;

  Future<void> select(String? id) async {
    if (id == null || _projectService.current == null) return;

    current = await _client.getDependency(_projectService.current!.id, id);
    log('Selected dependency $id');
    notifyListeners();
  }

  Future<void> exempt(String rationale) async {
    if (current?.package == null) {
      throw new Exception('Cannot exempt dependency without assigned package');
    }
    await _client.exempt(
        _projectService.current!.id, current!.package!.id, rationale);
    return select(current!.id);
  }

  Future<void> unexempt() async {
    if (current?.package == null) {
      throw new Exception(
          'Cannot unexempt dependency without assigned package');
    }
    if (current!.exemption == null) {
      throw new Exception('Nothing to unexempt');
    }
    await _client.unexempt(_projectService.current!.id, current!.package!.id);
    return select(current!.id);
  }
}
