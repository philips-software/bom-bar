/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:bom_bar_ui/model/package.dart';
import 'package:bom_bar_ui/model/project.dart';
import 'package:bom_bar_ui/services/backend_service.dart';
import 'package:bom_bar_ui/services/bom_bar_client.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/annotations.dart';
import 'package:mockito/mockito.dart';

import 'backend_service_test.mocks.dart';

@GenerateMocks([BomBarClient])
void main() {
  group('$BackendService', () {
    const projectId = 'projectId';
    late BackendService service;
    late MockBomBarClient client;

    setUp(() {
      client = MockBomBarClient();
      service = BackendService(client: client);
    });

    group('Projects list', () {
      test('queries list of projects', () async {
        final project = Project(id: projectId);
        when(client.getProjects()).thenAnswer((_) => Future.value([project]));

        final projects = await service.projects();

        expect(projects, [project]);
      });

      test('throws client communication failure', () {
        when(client.getProjects())
            .thenAnswer((_) => Future.error(Exception('Boom!')));

        expect(service.projects(), throwsA(isInstanceOf<Exception>()));
      });
    });

    group('Find packages', () {
      const filter = 'filter';

      test('find matching packages', () async {
        final package = Package(
            id: 'id', title: 'Package', reference: Uri.parse('package'));
        when(client.findPackagesById(filter: filter))
            .thenAnswer((_) => Future.value([package]));

        final packages = await service.packages(filter);

        expect(packages, [package]);
      });

      test('throws client communication failure', () {
        when(client.findPackagesById(filter: filter))
            .thenAnswer((_) => Future.error(Exception('Boom!')));

        expect(service.packages(filter), throwsA(isInstanceOf<Exception>()));
      });
    });
  });
}
