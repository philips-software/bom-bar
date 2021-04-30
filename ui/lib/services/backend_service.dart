/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
import 'package:flutter/widgets.dart';
import 'package:provider/provider.dart';

import '../model/package.dart';
import '../model/project.dart';
import 'bombar_client.dart';

class BackendService {
  factory BackendService.of(BuildContext context) =>
      Provider.of<BackendService>(context, listen: false);

  BackendService({required this.client});

  final BomBarClient client;

  Future<List<Project>> projects() async => client.getProjects();

  Future<List<Package>> packages(String fragment) async =>
      client.findPackagesById(filter: fragment);
}
