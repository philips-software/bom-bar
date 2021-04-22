/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'project.dart';

class Package {
  Package({
    this.id,
    this.reference,
    this.title,
    this.vendor,
    this.homepage,
    this.description,
    this.approval = Approval.context,
    this.exemptions,
    this.projects,
  });

  final String id;
  final Uri reference;
  final String title;
  final String vendor;
  final Uri homepage;
  final String description;
  Approval approval;
  final List<String> exemptions;
  final List<Project> projects;
}

enum Approval {
  rejected,
  confirmation,
  accepted,
  context,
  noPackage,
}

extension ApprovalName on Approval {
  String get name => _name(this);
}

String _name(dynamic object) => object.toString().split('.').last.toUpperCase();
