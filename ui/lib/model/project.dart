/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/foundation.dart';

import 'dependency.dart';

class Project {
  Project({
    @required this.id,
    this.title,
    this.lastUpdate,
    this.distribution,
    this.phase,
    this.issueCount,
    this.dependencies,
    this.exemptions,
  });

  final String id;
  String title;
  final DateTime lastUpdate;
  final Distribution distribution;
  final Phase phase;
  final int issueCount;
  final List<Dependency> dependencies;
  final List<String> exemptions;
}

enum Distribution { open_source, internal, saas, proprietary, unknown }

extension DistributionName on Distribution {
  String get name => _name(this);
}

enum Phase { development, released, unknown }

extension PhaseName on Phase {
  String get name => _name(this);
}

String _name(dynamic object) => object.toString().split('.').last.toUpperCase();
