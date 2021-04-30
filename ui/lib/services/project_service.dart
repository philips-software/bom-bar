/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'dart:async';
import 'dart:developer';

import 'package:flutter/widgets.dart';
import 'package:provider/provider.dart';

import '../model/dependency.dart';
import '../model/project.dart';
import 'bombar_client.dart';

class ProjectService {
  factory ProjectService.of(BuildContext context) =>
      Provider.of<ProjectService>(context, listen: false);

  ProjectService({BomBarClient? client}) : _client = client ?? BomBarClient();

  final BomBarClient _client;
  Project? _currentProject;
  Dependency? _currentDependency;
  String? error;

  Project? get currentProject => _currentProject;

  Dependency? get currentDependency => _currentDependency;

  Future<Project> createNew() async {
    _unselectProject();

    return _execute(() async {
      var project = await _client.createProject();
      _currentProject = project;
      log('Created new project ${project.id}');
      return project;
    });
  }

  Future<Project> selectProject(String id) async {
    if (_currentProject?.id == id) return _currentProject!;

    _unselectProject();
    return _execute(() async {
      var project = await _client.getProject(id);
      _currentProject = project;
      log('Selected project ${project.id}');
      return project;
    });
  }

  Future<Project> refreshProject() async {
    _assertProjectSelected();

    final id = _currentProject!.id;
    _unselectProject();
    return _execute(() async {
      var project = await _client.getProject(id);
      _currentProject = project;
      log('Refreshed project ${project.id}');
      return project;
    });
  }

  Future<Project> updateProject(Project update) {
    _currentProject = null;
    return _execute(() async {
      var project = await _client.updateProject(update);
      _currentProject = project;
      log('Updated project ${project.id}');
      return project;
    });
  }

  Future<Project> uploadSpdx() async {
    _assertProjectSelected();

    await _execute(() async {
      //TODO Split file selection from upload
      //TODO Move upload to client
      await _client.uploadSpdx(_currentProject!.id);
      log('Uploaded SPDX file');
    });
    return refreshProject();
  }

  Future<Map<String, int>> licenseDistribution() async {
    _assertProjectSelected();

    return _execute(() => _client.getLicenseDistribution(_currentProject!.id));
  }

  Future<Dependency> selectDependency(String id) async {
    _assertProjectSelected();
    if (id == _currentDependency?.id) return _currentDependency!;
    _currentDependency = null;

    return _execute(() async {
      var dependency = await _client.getDependency(_currentProject!.id, id);
      _currentDependency = dependency;
      log('Selected dependency ${dependency.id}');
      return dependency;
    });
  }

  Future<Dependency> refreshDependency() async {
    _assertDependencySelected();
    final id = _currentDependency!.id;
    _currentDependency = null;
    return selectDependency(id);
  }

  Future<Dependency> exemptDependency(String rationale) async {
    _assertDependencySelected();

    await _execute(() async {
      await _client.exemptDependency(
          _currentProject!.id, _currentDependency!.id, rationale);
      log('Exempted dependency ${_currentDependency!.id}');
    });
    return refreshDependency();
  }

  Future<Dependency> unexemptDependency() async {
    _assertDependencySelected();

    await _execute(() async {
      await _client.unexemptDependency(
          _currentProject!.id, _currentDependency!.id);
      log('Unexempted dependency ${_currentDependency!.id}');
    });
    return refreshDependency();
  }

  void _unselectProject() {
    _currentProject = null;
    _currentDependency = null;
  }

  void _assertProjectSelected() {
    if (_currentProject == null) throw NoProjectSelectedException();
  }

  void _assertDependencySelected() {
    if (_currentDependency == null) throw NoDependencySelectedException();
  }

  Future<T> _execute<T>(Future<T> Function() func) async {
    try {
      return await func();
    } catch (e) {
      log('Backend communication failed', error: e);
      rethrow;
    }
  }
}

class NoProjectSelectedException implements Exception {}

class NoDependencySelectedException implements Exception {}
