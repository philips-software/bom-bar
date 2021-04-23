/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'dart:async';
import 'dart:developer';

import 'package:flutter/foundation.dart';
import 'package:flutter/widgets.dart';
import 'package:provider/provider.dart';

import '../model/project.dart';
import 'bombar_client.dart';

class ProjectService extends ChangeNotifier {
  factory ProjectService.of(BuildContext context) =>
      Provider.of<ProjectService>(context, listen: false);

  ProjectService({BomBarClient? client}) : _client = client ?? BomBarClient();

  final BomBarClient _client;
  Project? _current;
  String? error;

  Project? get current => _current;

  Future<void> createNew() async {
    _current = null;

    _execute(() async {
      _current = await _client.createProject();
      log('Created new project ${_current!.id}');
    });
  }

  Future<void> select(String id) async {
    if (_current?.id == id) return;

    _current = null;
    _execute(() async {
      _current = await _client.getProject(id);
      log('Selected project $id');
    });
  }

  Future<void> refresh() async {
    if (current == null) return;

    final id = _current!.id;
    _current = null;
    _execute(() async {
      _current = await _client.getProject(id);
      log('Refreshed project $id');
    });
  }

  Future<void> update(Project update) => _execute(() async {
        _current = await _client.updateProject(update);
        log('Updated project ${_current!.id}');
      });

  Future<void> uploadSpdx() => _execute(() async {
        await _client.uploadSpdx(_current!.id);
        log('Uploaded SPDX file');
        select(_current!.id);
      });

  Future<Map<String, int>> licenseDistribution() =>
      _execute(() => _client.getLicenseDistribution(_current!.id));

  Future<T> _execute<T>(Future<T> Function() func) async {
    try {
      error = null;
      return await func();
    } catch (e) {
      error = e.toString();
      rethrow;
    } finally {
      notifyListeners();
    }
  }
}
