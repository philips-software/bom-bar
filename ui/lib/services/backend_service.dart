/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
import 'package:flutter/widgets.dart';
import 'package:provider/provider.dart';

import '../model/package.dart';
import '../model/project.dart';
import 'bom_bar_client.dart';

//TODO Move to project and package services
class BackendService {
  factory BackendService.of(BuildContext context) =>
      Provider.of<BackendService>(context, listen: false);

  BackendService({required this.client});

  final BomBarClient client;

  /// Returns all projects.
  Future<List<Project>> projects() async => client.getProjects();

  /// Returns all packages that match the [fragment] with their (PURL-based) reference.
  /// Supports "package_fragment/name_fragment" as fragment specification.
  Future<List<Package>> packages(String fragment) async =>
      client.findPackagesById(filter: fragment);
}
