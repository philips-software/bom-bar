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

  Future<Project> createNew() async {
    _current = null;

    return _execute(() async {
      final created = await _client.createProject();
      _current = created;
      log('Created new project ${_current!.id}');
      return created;
    });
  }

  Future<Project> select(String id) async {
    if (_current?.id == id) return _current!;

    _current = null;
    return _execute(() async {
      final updated = await _client.getProject(id);
      _current = updated;
      log('Selected project ${updated.id}');
      return updated;
    });
  }

  Future<Project> refresh() async {
    _assertProjectSelected();

    final id = _current!.id;
    _current = null;
    return _execute(() async {
      final refreshed = await _client.getProject(id);
      _current = refreshed;
      log('Refreshed project $id');
      return refreshed;
    });
  }

  Future<Project> update(Project update) {
    _current = null;
    return _execute(() async {
      final updated = await _client.updateProject(update);
      _current = updated;
      log('Updated project ${update.id}');
      return updated;
    });
  }

  Future<void> uploadSpdx() async {
    _assertProjectSelected();

    return _execute(() async {
      //TODO Split file selection from upload
      //TODO Move upload to client
      await _client.uploadSpdx(_current!.id);
      log('Uploaded SPDX file');
    });
  }

  Future<Map<String, int>> licenseDistribution() async {
    _assertProjectSelected();

    return _execute(() => _client.getLicenseDistribution(_current!.id));
  }

  void _assertProjectSelected() {
    if (_current == null) throw NoProjectSelectedException();
  }

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

class NoProjectSelectedException implements Exception {}
