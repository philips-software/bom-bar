/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:collection/collection.dart' show IterableExtension;

import '../model/dependency.dart';
import '../model/package.dart';
import '../model/project.dart';

const _distributions = {
  Distribution.internal: 'internal',
  Distribution.open_source: 'open_source',
  Distribution.proprietary: 'proprietary',
  Distribution.saas: 'saas'
};

const _phases = {
  Phase.development: 'development',
  Phase.released: 'released',
};

const _approvals = {
  Approval.context: 'context',
  Approval.rejected: 'rejected',
  Approval.confirmation: 'needs_approval',
  Approval.accepted: 'approved',
  Approval.noPackage: 'not_a_package',
};

Project toProject(Map<String, dynamic> map) => Project(
      id: _mandatory(map['id'] as String?, 'project id'),
      title: map['title'] as String?,
      lastUpdate: toDateTime(map['updated'] as String?),
      distribution: toDistribution(map['distribution'] as String?),
      phase: toPhase(map['phase'] as String?),
      issueCount: map['issues'] as int? ?? 0,
      dependencies: toDependencyList(map['packages'] as List<dynamic>? ?? []),
      exemptions: toStringList(map['exemptions'] as List<Object>? ?? []),
    );

T _mandatory<T>(T? value, String field) {
  if (value == null) {
    throw new MappingException('Missing mandatory $field');
  }
  return value;
}

DateTime? toDateTime(String? iso) {
  if (iso == null) {
    return null;
  }
  return DateTime.parse(iso);
}

Map<String, Object?> fromProject(Project project) => {
      'id': project.id,
      if (project.title != null) 'title': project.title,
      if (project.distribution != null)
        'distribution': fromDistribution(project.distribution),
      if (project.phase != null) 'phase': fromPhase(project.phase),
    };

Dependency toDependency(Map<String, dynamic> map) => Dependency(
      id: _mandatory(map['id'] as String?, 'dependency id'),
      title: map['title'] as String?,
      purl: toUrl(map['purl'] as String?),
      version: map['version'] as String?,
      license: map['license'] as String?,
      relation: map['relation'] as String?,
      source: map['source'] as bool? ?? false,
      issueCount: map['issues'] as int? ?? 0,
      licenseIssues: toStringList(map['license_issues'] as List<dynamic>?),
      dependencies: toDependencyList(map['dependencies'] as List<dynamic>?),
      usages: toDependencyList(map['usages'] as List<dynamic>?),
      package: (map['package'] != null)
          ? toPackage(map['package'] as Map<String, dynamic>)
          : null,
      exemption: map['exemption'] as String?,
    );

Package toPackage(Map<String, dynamic> map) => Package(
      id: _mandatory(map['id'] as String?, 'package id'),
      reference: toUrl(map['reference'] as String?),
      title: map['name'] as String?,
      vendor: map['vendor'] as String?,
      homepage: toUrl(map['homepage'] as String?),
      description: map['description'] as String?,
      approval: toApproval(map['approval'] as String?),
      exemptions: toStringList(map['exemptions'] as List<dynamic>?),
      projects: toProjectList(map['projects'] as List<dynamic>?),
    );

List<Package> toPackageList(List<dynamic>? list) =>
    list?.map((pkg) => toPackage(pkg)).toList(growable: false) ?? [];

Uri? toUrl(String? string) => (string != null) ? Uri.parse(string) : null;

List<Dependency> toDependencyList(List<dynamic>? list) =>
    list?.map((map) => toDependency(map)).toList(growable: false) ?? [];

List<String> toStringList(List<dynamic>? list) =>
    list?.map((s) => s.toString()).toList(growable: false) ?? [];

Distribution toDistribution(String? value) {
  value = value?.toLowerCase();
  return _distributions.entries
          .firstWhereOrNull((element) => element.value == value)
          ?.key ??
      Distribution.unknown;
}

String? fromDistribution(Distribution? distribution) =>
    _distributions[distribution];

Phase toPhase(String? value) {
  value = value?.toLowerCase();
  return _phases.entries
          .firstWhereOrNull((element) => element.value == value)
          ?.key ??
      Phase.unknown;
}

String? fromPhase(Phase? phase) => _phases[phase];

Approval toApproval(String? value) {
  value = value?.toLowerCase();
  return _approvals.entries
          .firstWhereOrNull((element) => element.value == value)
          ?.key ??
      Approval.context;
}

String fromApproval(Approval approval) {
  return _approvals[approval] ?? _approvals[Approval.context]!;
}

List<Project> toProjectList(List<dynamic>? list) =>
    list?.map((map) => toProject(map)).toList(growable: false) ?? [];
