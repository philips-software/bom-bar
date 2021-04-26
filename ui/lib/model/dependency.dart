/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package.dart';

class Dependency {
  Dependency({
    required this.id,
    this.title,
    this.purl,
    this.version,
    this.license,
    this.relation,
    this.source,
    this.issueCount = 0,
    this.licenseIssues = const [],
    this.dependencies = const [],
    this.usages = const [],
    this.package,
    this.exemption,
  });

  final String id;
  final String? title;
  final Uri? purl;
  final String? version;
  final String? license;
  final String? relation;
  final bool? source;
  final int issueCount;
  final List<String> licenseIssues;
  final List<Dependency> dependencies;
  final List<Dependency> usages;
  final Package? package;
  final String? exemption;

  String get titleStr => title ?? id;
  String get versionStr => version ?? '?';
  int get totalIssues {
    return issueCount +
        (dependencies.map((dep) => dep.totalIssues).fold(0, ((l, r) => l + r)));
  }
}
