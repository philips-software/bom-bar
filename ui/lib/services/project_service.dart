/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'dart:async';
import 'dart:developer';

import 'package:dio/dio.dart';
import 'package:flutter/widgets.dart';
import 'package:provider/provider.dart';

import '../model/dependency.dart';
import '../model/project.dart';
import '../plugins/file_loader.dart';
import 'bom_bar_client.dart';

/// Business logic abstraction for handling projects.
class ProjectService {
  factory ProjectService.of(BuildContext context) =>
      Provider.of<ProjectService>(context, listen: false);

  ProjectService({BomBarClient? client, FileLoader? fileLoader})
      : _client = client ?? BomBarClient(),
        _fileLoader = fileLoader ?? FileLoader();

  final BomBarClient _client;
  final FileLoader _fileLoader;
  Project? _currentProject;
  Dependency? _currentDependency;

  Project? get currentProject => _currentProject;

  Dependency? get currentDependency => _currentDependency;

  /// Creates a new project.
  Future<Project> createNew() async {
    _unselectProject();

    return _execute(() async {
      var project = await _client.createProject();
      _currentProject = project;
      log('Created new project ${project.id}');
      return project;
    });
  }

  /// Returns all projects that match the [fragment] with their (PURL-based) reference.
  /// When no [fragment] is received, defaults to all project.
  /// Supports "projects_fragment/name_fragment" as fragment specification.
  Future<List<Project>> findProjects([String? fragment]) => _execute(() async {
        final projects = await _client.findProjects(fragment);
        log('Searching for projects ' +
            ((fragment != null)
                ? 'matching fragment: "$fragment"'
                : 'without fragment'));
        return projects;
      });

  /// Changes the current project to [projectId].
  Future<Project> selectProject(String projectId) async {
    if (_currentProject?.id == projectId) return _currentProject!;

    _unselectProject();
    return _execute(() async {
      var project = await _client.getProject(projectId);
      _currentProject = project;
      log('Selected project ${project.id}');
      return project;
    });
  }

  /// Unconditionally reloads the currently selected project.
  Future<Project> refreshProject() async {
    _assertProjectSelected();

    _currentDependency == null;
    return _execute(() async {
      final project = await _client.getProject(_currentProject!.id);
      _currentProject = project;
      log('Refreshed project ${project.id}');
      return project;
    });
  }

  /// Updates the current project with the provided non-null fields.
  Future<Project> updateProject(Project update) {
    _assertProjectSelected();

    return _execute(() async {
      var project = await _client.updateProject(_currentProject!.id, update);
      _currentProject = project;
      log('Updated project ${_currentProject!.id}');
      return project;
    });
  }

  Future<Project> uploadSpdx() async {
    _assertProjectSelected();

    await _execute(() async {
      final content = await _fileLoader.load();
      await _client.uploadSpdx(_currentProject!.id, content);
      log('Uploaded SPDX file');
    });
    return refreshProject();
  }

  Future<Map<String, int>> licenseDistribution() async {
    _assertProjectSelected();

    return _execute(() async {
      final raw = await _client.getLicenseDistribution(_currentProject!.id);
      final sorted = raw.entries.toList()
        ..sort((l, r) => -(l.value).compareTo(r.value));
      return {
        for (var l in sorted) l.key: l.value,
      };
    });
  }

  Future<Map<String, List<Dependency>>> obligations() async {
    _assertProjectSelected();

    return _client.findObligations(_currentProject!.id);
  }

  Future<Dependency> selectDependency(String id) async {
    _assertProjectSelected();
    if (_currentDependency?.id == id) return _currentDependency!;
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
    _assertPackageDependencySelected();

    await _execute(() async {
      await _client.exemptDependency(
          _currentProject!.id, _currentDependency!.id, rationale);
      log('Exempted dependency ${_currentDependency!.id}');
    });
    return refreshDependency();
  }

  Future<Dependency> unExemptDependency() async {
    _assertPackageDependencySelected();

    await _execute(() async {
      await _client.unExemptDependency(
          _currentProject!.id, _currentDependency!.id);
      log('Un-exempted dependency ${_currentDependency!.id}');
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

  void _assertPackageDependencySelected() {
    _assertDependencySelected();
    if (_currentDependency!.package == null) {
      throw AnonymousDependencyException();
    }
  }

  Future<T> _execute<T>(Future<T> Function() func) async {
    try {
      return await func();
    } on DioError catch (e) {
      log('Backend communication failed', error: e.error);
      rethrow;
    }
  }
}

class NoProjectSelectedException implements Exception {}

class NoDependencySelectedException implements Exception {}

class AnonymousDependencyException implements Exception {}
