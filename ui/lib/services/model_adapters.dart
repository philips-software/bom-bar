/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
import 'dart:developer';

import 'package:bom_bar_ui/model/package.dart';
import 'package:collection/collection.dart' show IterableExtension;

import '../model/dependency.dart';
import '../model/project.dart';

const _distributions = {
  Distribution.internal: 'internal',
  Distribution.open_source: 'open_source',
  Distribution.proprietary: 'proprietary',
  Distribution.saas: 'saas'
};

const _phases = {Phase.development: 'development', Phase.released: 'released'};

Project toProject(Map<String, Object> map) => Project(
      id: map['id'] as String,
      title: map['title'] as String? ?? '?',
      lastUpdate: toDateTime(map['updated'] as String?),
      distribution: toDistribution(map['distribution'] as String),
      phase: toPhase(map['phase'] as String),
      issueCount: map['issues'] as int? ?? 0,
      dependencies: toDependencyList(map['packages'] as List<dynamic>? ?? []),
      exemptions: toStringList(map['exemptions'] as List<Object>? ?? []),
    );

DateTime? toDateTime(String? iso) {
  if (iso == null) {
    return null;
  }
  return DateTime.parse(iso);
}

Distribution toDistribution(String value) {
  value = value.toLowerCase();
  return _distributions.entries
          .firstWhereOrNull((element) => element.value == value)
          ?.key ??
      Distribution.unknown;
}

Phase toPhase(String value) {
  value = value.toLowerCase();
  return _phases.entries
          .firstWhereOrNull((element) => element.value == value)
          ?.key ??
      Phase.unknown;
}

List<Project>? toProjectList(List<dynamic>? list) =>
    list?.map((map) => toProject(map)).toList(growable: false);

Map<String, Object?> fromProject(Project project) => {
      'id': project.id,
      'title': project.title,
      'distribution': _distributions[project.distribution],
      'phase': _phases[project.phase],
    };

Dependency toDependency(Map<String, Object> map) => Dependency(
      id: map['id'] as String,
      title: map['title'] as String? ?? '?',
      purl: toUrl(map['purl'] as String?),
      version: map['version'] as String? ?? '?',
      license: map['license'] as String? ?? '?',
      relation: map['relation'] as String?,
      source: map['source'] as bool? ?? false,
      issueCount: map['issues'] as int? ?? 0,
      licenseIssues: toStringList(map['license_issues'] as List<Object>? ?? []),
      dependencies:
          toDependencyList(map['dependencies'] as List<dynamic>? ?? []),
      usages: toDependencyList(map['usages'] as List<dynamic>? ?? []),
      package: (map['package'] != null)
          ? toPackage(map['package'] as Map<String, Object>)
          : null,
      exemption: map['exemption'] as String?,
    );

Package toPackage(Map<String, Object> map) => Package(
      id: map['id'] as String? ?? '?',
      reference: toUrl(map['reference'] as String?)!,
      title: map['name'] as String? ?? '?',
      vendor: map['vendor'] as String?,
      homepage: toUrl(map['homepage'] as String?),
      description: map['description'] as String?,
      approval: toApproval(map['approval'] as String? ?? '?'),
      exemptions: toStringList(map['exemptions'] as List<Object>? ?? []),
      projects: toProjectList(map['projects'] as List<dynamic>? ?? [])!,
    );

List<Package> toPackageList(List<dynamic> list) =>
    list.map((pkg) => toPackage(pkg)).toList(growable: false);

Uri? toUrl(String? string) => (string != null) ? Uri.parse(string) : null;

List<Dependency> toDependencyList(List<dynamic> list) =>
    list.map((map) => toDependency(map)).toList(growable: false);

List<String> toStringList(List<Object> list) =>
    list.map((s) => s.toString()).toList(growable: false);

Approval toApproval(String approval) {
  switch (approval) {
    case 'context':
      return Approval.context;
    case 'rejected':
      return Approval.rejected;
    case 'needs_approval':
      return Approval.confirmation;
    case 'approved':
      return Approval.accepted;
    case 'not_a_package':
      return Approval.noPackage;
    default:
      log('Adapting approval "$approval"', error: 'No mapping defined');
      return Approval.context;
  }
}
