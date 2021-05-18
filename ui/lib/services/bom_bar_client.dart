/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'dart:async';
import 'dart:developer';

import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';

import '../model/dependency.dart';
import '../model/package.dart';
import '../model/project.dart';
import 'model_adapters.dart';

/// Client for the BOM-Bar REST API.
class BomBarClient {
  static final baseUrl =
      Uri.http(kIsWeb && !kDebugMode ? '' : 'localhost:9090', '/');
  static final _projectsUrl = baseUrl.resolve('projects/');
  static final _packagesUrl = baseUrl.resolve('packages/');

  BomBarClient() {
    if (kDebugMode) {
      _enableHttpLogging();
    }
  }

  final dio = Dio();

  void _enableHttpLogging() {
    dio.interceptors.add(LogInterceptor(
      responseBody: false,
      requestHeader: false,
      responseHeader: false,
      logPrint: (o) => log(o as String),
    ));
  }

  /// Returns all or projects matching the provided [filter] fragment.
  Future<List<Project>> findProjectsBySearchFragment(String? filter) async {
    final response =
        await dio.get(_projectsUrl.toString(), queryParameters: {'q': filter});
    return toProjectList(response.data['results']);
  }

  /// Creates a new (empty) project.
  Future<Project> createProject() async {
    final response = await dio.postUri(_projectsUrl, data: {});
    return toProject(response.data);
  }

  /// Returns the project matching the [projectId].
  Future<Project> getProject(String projectId) async {
    final response = await dio.getUri(_projectsUrl.resolve(projectId));
    return toProject(response.data);
  }

  /// Updates the non-null [update] fields of project [projectId].
  Future<Project> updateProject(String projectId, Project update) async {
    final response = await dio.putUri(
      _projectsUrl.resolve(projectId),
      data: fromProject(update),
    );
    return toProject(response.data);
  }

  /// Selects and uploads an SBOM file for the specified [projectId].
  Future<void> uploadSpdx(String projectId, List<int> content) async {
    final formData = FormData.fromMap({
      'name': 'file',
      'file': MultipartFile.fromBytes(content, filename: projectId),
    });
    await dio.postUri(_projectsUrl.resolve('$projectId/upload'),
        data: formData);
  }

  /// Loads dependency [dependencyId] from project [projectId].
  Future<Dependency> getDependency(
      String projectId, String dependencyId) async {
    final response = await dio
        .getUri(_projectsUrl.resolve('$projectId/dependencies/$dependencyId'));
    return toDependency(response.data);
  }

  /// Exempts dependency [dependencyId] of [projectId] with a [rationale].
  Future<void> exemptDependency(
          String projectId, String dependencyId, String rationale) =>
      dio.postUri(
        _projectsUrl.resolve('$projectId/dependencies/$dependencyId/exempt'),
        data: {'rationale': rationale},
      );

  /// Un-exempts [dependencyId] of [projectId].
  Future<void> unExemptDependency(String projectId, String dependencyId) =>
      dio.deleteUri(
          _projectsUrl.resolve('$projectId/dependencies/$dependencyId/exempt'));

  /// Returns a map of licenses to their occurrence for the [projectId].
  Future<Map<String, int>> getLicenseDistribution(String projectId) async {
    final response = await dio.getUri<Map<String, dynamic>>(
        _projectsUrl.resolve('$projectId/licenses'));
    return {
      for (var l in response.data!.entries) l.key: l.value as int,
    };
  }

  /// Returns all packages matching the provided [filter] fragment.
  Future<List<Package>> findPackagesById({required String filter}) async {
    final response = await dio.getUri(_packagesUrl.resolve('?q=$filter'));
    return toPackageList(response.data['results']);
  }

  /// Returns the package matching the [packageId].
  Future<Package> getPackage(String packageId) async {
    final response = await dio.getUri(_packagesUrl.resolve(packageId));
    return toPackage(response.data);
  }

  /// Sets the use of [packageId] with the provided [approval].
  Future<void> setApproval(String packageId, Approval approval) {
    final value = fromApproval(approval);
    return dio.postUri(_packagesUrl.resolve('$packageId/approve/$value'));
  }

  /// Exempts the use of [license] for [packageId].
  Future<void> exemptLicense(String packageId, String license) =>
      dio.postUri(_packagesUrl.resolve('$packageId/exempt/$license'));

  /// Un-exempts the use of [license] for [packageId].
  Future<void> unExemptLicense(String packageId, String license) =>
      dio.deleteUri(_packagesUrl.resolve('$packageId/exempt/$license'));
}
