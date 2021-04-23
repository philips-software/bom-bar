/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:bom_bar_ui/model/project.dart';
import 'package:bom_bar_ui/services/bombar_client.dart';
import 'package:bom_bar_ui/services/project_service.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/annotations.dart';
import 'package:mockito/mockito.dart';

import 'project_service_test.mocks.dart';

@GenerateMocks([BomBarClient])
void main() {
  group('$ProjectService', () {
    const previousId = 'previousId';
    const unknownId = 'unknownId';
    late MockBomBarClient client;
    late ProjectService service;

    setUp(() async {
      client = MockBomBarClient();
      service = ProjectService(client: client);

      when(client.getProject(previousId))
          .thenAnswer((_) => Future.value(Project(id: previousId)));
      when(client.getProject(unknownId))
          .thenAnswer((_) => Future.error(Exception("Unknown project")));
      await service.select(previousId);
    });

    void _clearCurrentProject() {
      when(client.getProject(previousId))
          .thenAnswer((_) => Future.error(Exception()));
      expect(service.refresh(), throwsA(isInstanceOf<Exception>()));
      expect(service.current, isNull);
    }

    group('Project creation', () {
      test('creates on server and automatically selects', () async {
        final newProject = Project(id: 'newId');
        when(client.createProject())
            .thenAnswer((_) => Future.value(newProject));

        final created = await service.createNew();

        expect(created.id, newProject.id);
        expect(service.current, newProject);
      });

      test('throws for server failure', () {
        when(client.createProject()).thenThrow(Exception('Boom!'));

        expect(service.createNew(), throwsA(isInstanceOf<Exception>()));
        expect(service.current, isNull);
      });
    });

    group('Current project selection', () {
      const otherId = 'otherId';

      test('selects project by id', () async {
        final otherProject = Project(id: otherId);
        when(client.getProject(otherId))
            .thenAnswer((_) => Future.value(otherProject));

        final selected = await service.select(otherId);

        expect(selected, otherProject);
        expect(service.current, otherProject);
      });

      test('throws on failure to select project', () async {
        when(client.getProject(otherId))
            .thenAnswer((_) => Future.error(Exception('Boom!')));

        expect(service.select(otherId), throwsA(isInstanceOf<Exception>()));
        expect(service.current, isNull);
      });

      test('ignores reselection of same project', () async {
        final same = await service.select(previousId);

        expect(same.id, previousId);
        expect(same, service.current);
        verify(client.getProject(any)).called(1);
      });
    });

    group('Current project refresh', () {
      test('refreshes project', () async {
        final same = await service.refresh();

        expect(same, service.current);
        verify(client.getProject(previousId)).called(2);
      });

      test('throws refresh failed', () {
        when(client.getProject(previousId))
            .thenAnswer((_) => Future.error(Exception('Boom!')));

        expect(service.refresh(), throwsA(isInstanceOf<Exception>()));
        expect(service.current, isNull);
      });

      test('throws refresh no project', () {
        _clearCurrentProject();

        expect(service.refresh(),
            throwsA(isInstanceOf<NoProjectSelectedException>()));
      });
    });

    group('Update project', () {
      const updatedId = 'updatedId';

      test('reloads updated project', () async {
        final update = Project(id: previousId);
        final result = Project(id: updatedId);
        when(client.updateProject(update))
            .thenAnswer((_) => Future.value(result));

        final updated = await service.update(update);

        expect(updated, result);
        expect(updated, service.current);
      });

      test('throws update failure', () async {
        final update = Project(id: previousId);
        when(client.updateProject(update))
            .thenAnswer((_) => Future.error(Exception('Boom!')));

        expect(service.update(update), throwsA(isInstanceOf<Exception>()));
        expect(service.current, isNull);
      });
    });

    group('Upload bill-of-materials', () {
      test('select and upload file', () async {
        await service.uploadSpdx();

        verify(client.uploadSpdx(previousId));
      });

      test('throws if no current project', () async {
        _clearCurrentProject();

        expect(service.uploadSpdx(),
            throwsA(isInstanceOf<NoProjectSelectedException>()));
      });

      test('throws failure during upload', () async {
        when(client.uploadSpdx(previousId))
            .thenAnswer((_) => Future.error(Exception('Boom!')));

        expect(service.uploadSpdx(), throwsA(isInstanceOf<Exception>()));
      });
    });

    group('License distribution', () {
      test('loads license distribution', () async {
        final distribution = <String, int>{};
        when(client.getLicenseDistribution(previousId))
            .thenAnswer((_) => Future.value(distribution));

        final result = await service.licenseDistribution();

        expect(result, distribution);
      });

      test('throws if no project selected', () {
        _clearCurrentProject();

        expect(service.licenseDistribution(),
            throwsA(isInstanceOf<NoProjectSelectedException>()));
      });

      test('throws if server error', () {
        when(client.getLicenseDistribution(previousId))
            .thenAnswer((_) => Future.error(Exception('Boom!')));

        expect(
            service.licenseDistribution(), throwsA(isInstanceOf<Exception>()));
      });
    });
  });
}
