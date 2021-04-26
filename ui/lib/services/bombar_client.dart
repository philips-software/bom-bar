/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'dart:async';
import 'dart:developer';

import 'package:bom_bar_ui/model/package.dart';
import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';

import '../model/dependency.dart';
import '../model/project.dart';
import '../plugins/file_uploader.dart';
import 'model_adapters.dart';

class BomBarClient {
  static final baseUrl =
      Uri.http(kIsWeb && !kDebugMode ? '' : 'localhost:9090', '/');
  static final _projectsUrl = baseUrl.resolve('projects/');
  static final _packagesUrl = baseUrl.resolve('packages/');

  BomBarClient() {
    if (kDebugMode) {
      dio.interceptors.add(LogInterceptor(
        responseBody: false,
        requestHeader: false,
        responseHeader: false,
        logPrint: (o) => log(o as String),
      ));
    }
  }

  final dio = Dio();

  Future<List<Project>> getProjects() async {
    final response = await dio.getUri(_projectsUrl);
    return toProjectList(response.data['results']);
  }

  Future<Project> createProject() async {
    final response = await dio.postUri(_projectsUrl, data: {});
    return toProject(response.data);
  }

  Future<Project> getProject(String id) async {
    final response = await dio.getUri(_projectsUrl.resolve(id));
    return toProject(response.data);
  }

  Future<Project> updateProject(Project project) async {
    final response = await dio.putUri(
      _projectsUrl.resolve(project.id),
      data: fromProject(project),
    );
    return toProject(response.data);
  }

  Future<void> uploadSpdx(String id) =>
      FileUploader(_projectsUrl.resolve('$id/upload')).upload();

  Future<Dependency> getDependency(String projectId, String id) async {
    final response =
        await dio.getUri(_projectsUrl.resolve('$projectId/dependencies/$id'));
    return toDependency(response.data);
  }

  Future<void> exempt(String projectId, String id, String rationale) =>
      dio.postUri(
        _projectsUrl.resolve('$projectId/exempt/$id'),
        data: {'rationale': rationale},
      );

  Future<void> unexempt(String projectId, String id) =>
      dio.deleteUri(_projectsUrl.resolve('$projectId/exempt/$id'));

  Future<Map<String, int>> getLicenseDistribution(String projectId) async {
    final response = await dio.getUri<Map<String, dynamic>>(
        _projectsUrl.resolve('$projectId/licenses'));
    final licenses = response.data!.entries.toList(growable: false)
      ..sort((l, r) => -(l.value as int).compareTo(r.value));
    return Map.fromIterable(
      licenses,
      key: (e) => e.key as String,
      value: (e) => e.value as int,
    );
  }

  Future<List<Package>> findPackagesById({required String filter}) async {
    final response = await dio.getUri(_packagesUrl.resolve('?q=$filter'));
    return toPackageList(response.data['results']);
  }

  Future<Package> getPackage(String id) async {
    final response = await dio.getUri(_packagesUrl.resolve(id));
    return toPackage(response.data);
  }

  Future<void> setApproval(String packageId, Approval approval) {
    final value = fromApproval(approval);
    return dio.postUri(_packagesUrl.resolve('$packageId/approve/$value'));
  }

  exemptLicense(String packageId, String license) =>
      dio.postUri(_packagesUrl.resolve('$packageId/exempt/$license'));

  unExemptLicense(String packageId, String license) =>
      dio.deleteUri(_packagesUrl.resolve('$packageId/exempt/$license'));
}
