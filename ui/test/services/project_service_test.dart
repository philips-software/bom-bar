/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:bom_bar_ui/model/dependency.dart';
import 'package:bom_bar_ui/model/package.dart';
import 'package:bom_bar_ui/model/project.dart';
import 'package:bom_bar_ui/plugins/file_loader.dart';
import 'package:bom_bar_ui/services/bom_bar_client.dart';
import 'package:bom_bar_ui/services/project_service.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/annotations.dart';
import 'package:mockito/mockito.dart';

import 'project_service_test.mocks.dart';

@GenerateMocks([BomBarClient, FileLoader])
void main() {
  group('$ProjectService', () {
    const projectId = 'projectId';
    const dependencyId = 'dependencyId';
    const packageId = 'packageId';
    const message = 'Message';
    late MockBomBarClient client;
    late MockFileLoader fileLoader;
    late ProjectService service;

    setUp(() async {
      client = MockBomBarClient();
      fileLoader = MockFileLoader();
      service = ProjectService(client: client, fileLoader: fileLoader);

      when(client.getProject(projectId))
          .thenAnswer((_) => Future.value(Project(id: projectId)));
    });

    group('Projects query', () {
      test('query to list all projects when no filter applied', () async {
        when(client.findProjects())
            .thenAnswer((_) => Future.value([Project(id: projectId)]));

        final projects = await service.findProjects();

        expect(projects, hasLength(1));
      });

      test('query to list only one filtered project', () async {
        const fragment = 'ProjectB';
        when(client.findProjects(fragment))
            .thenAnswer((_) => Future.value([Project(id: projectId)]));

        final projects = await service.findProjects(fragment);

        expect(projects, hasLength(1));
      });

      test('throws for list projects failure', () {
        when(client.findProjects())
            .thenAnswer((_) => Future.error(Exception('Boom!')));

        expect(service.findProjects(), throwsA(isInstanceOf<Exception>()));
      });
    });

    group('No project selected', () {
      test('creates new project', () async {
        when(client.createProject())
            .thenAnswer((_) => Future.value(Project(id: projectId)));

        final project = await service.createNew();

        expect(project.id, projectId);
        expect(service.currentProject, project);
      });

      test('throws if project creation failed', () {
        when(client.createProject()).thenThrow(Exception('Boom!'));

        expect(service.createNew(), throwsA(isInstanceOf<Exception>()));

        expect(service.currentProject, isNull);
      });

      test('selects project by id', () async {
        when(client.getProject(projectId))
            .thenAnswer((_) => Future.value(Project(id: projectId)));

        final selected = await service.selectProject(projectId);

        expect(selected.id, projectId);
        expect(service.currentProject, selected);
      });

      test('throws if refresh without current project', () {
        expect(service.refreshProject(),
            throwsA(isInstanceOf<NoProjectSelectedException>()));
      });

      test('throws if upload SPDX without current project', () async {
        expect(service.uploadSpdx(),
            throwsA(isInstanceOf<NoProjectSelectedException>()));
      });

      test('throws if license distribution without current project', () {
        expect(service.licenseDistribution(),
            throwsA(isInstanceOf<NoProjectSelectedException>()));
      });

      test('throws if obligations without current project', () {
        expect(service.obligations(),
            throwsA(isInstanceOf<NoProjectSelectedException>()));
      });

      test('throws if select dependency without current project', () {
        expect(service.selectDependency(dependencyId),
            throwsA(isInstanceOf<NoProjectSelectedException>()));
      });
    });

    group('Project selected', () {
      setUp(() async {
        await service.selectProject(projectId);
      });

      test('throws on select project failure', () async {
        const otherId = 'otherId';
        when(client.getProject(otherId))
            .thenAnswer((_) => Future.error(Exception('Boom!')));

        expect(
            service.selectProject(otherId), throwsA(isInstanceOf<Exception>()));

        expect(service.currentProject, isNull);
      });

      test('ignores reselection of same project', () async {
        final selected = await service.selectProject(projectId);

        verify(client.getProject(any)).called(1);
        expect(selected.id, projectId);
        expect(service.currentProject, selected);
      });

      test('refreshes selected project', () async {
        const updateId = 'updateId';
        when(client.getProject(projectId))
            .thenAnswer((_) => Future.value(Project(id: updateId)));

        final project = await service.refreshProject();

        expect(project.id, updateId);
        expect(service.currentProject, project);
      });

      test('throws refresh failed', () {
        when(client.getProject(projectId))
            .thenAnswer((_) => Future.error(Exception('Boom!')));

        expect(service.refreshProject(), throwsA(isInstanceOf<Exception>()));

        expect(service.currentProject!.id, projectId);
      });

      test('updates project', () async {
        const updatedId = 'updatedId';
        final update = Project(id: 'ignore');
        final result = Project(id: updatedId);
        when(client.updateProject(projectId, update))
            .thenAnswer((_) => Future.value(result));

        final project = await service.updateProject(update);

        expect(project.id, updatedId);
        expect(service.currentProject, project);
      });

      test('throws update failure', () async {
        final update = Project(id: 'ignore');
        when(client.updateProject(projectId, update))
            .thenAnswer((_) => Future.error(Exception('Boom!')));

        expect(
            service.updateProject(update), throwsA(isInstanceOf<Exception>()));

        expect(service.currentProject!.id, projectId);
      });

      test('loads license distribution', () async {
        final distribution = {'low': 42, 'high': 73};
        when(client.getLicenseDistribution(projectId))
            .thenAnswer((_) => Future.value(distribution));

        final result = await service.licenseDistribution();

        expect(result, distribution);
        expect(result.keys, ['high', 'low']);
      });

      test('throws if license distribution load fails', () {
        when(client.getLicenseDistribution(projectId))
            .thenAnswer((_) => Future.error(Exception('Boom!')));

        expect(
            service.licenseDistribution(), throwsA(isInstanceOf<Exception>()));
      });

      test('loads obligations', () async {
        final obligation_description = 'Obligation';
        final dependencies = [Dependency(id: dependencyId)];
        ;
        final obligations = {obligation_description: dependencies};
        when(client.findObligations(projectId))
            .thenAnswer((_) => Future.value(obligations));

        final result = await service.obligations();

        expect(result[obligation_description]!.first.id, dependencyId);
      });

      test('throws if obligations fail to load', () {
        when(client.findObligations(projectId))
            .thenAnswer((_) => Future.error(Exception('Boom!')));

        expect(service.obligations(), throwsA(isInstanceOf<Exception>()));
      });

      group('Uploading SPDX file', () {
        const spdxData = [1, 2, 3];

        setUp(() {
          when(fileLoader.load()).thenAnswer((_) => Future.value(spdxData));
        });

        test('selects and uploads file', () async {
          final project = await service.uploadSpdx();

          verify(client.uploadSpdx(projectId, spdxData));
          expect(project.id, projectId);
        });

        test('throws failure during upload file selection', () async {
          when(fileLoader.load())
              .thenAnswer((_) => Future.error(Exception('Boom!')));

          expect(service.uploadSpdx(), throwsA(isInstanceOf<Exception>()));

          expect(service.currentProject!.id, projectId);
        });

        test('throws failure during upload', () async {
          when(client.uploadSpdx(projectId, any))
              .thenAnswer((_) => Future.error(Exception('Boom!')));

          expect(service.uploadSpdx(), throwsA(isInstanceOf<Exception>()));

          expect(service.currentProject!.id, projectId);
        });
      });

      group('No dependency selected', () {
        test('selects dependency', () async {
          when(client.getDependency(projectId, dependencyId))
              .thenAnswer((_) => Future.value(Dependency(id: dependencyId)));

          final selected = await service.selectDependency(dependencyId);

          expect(selected.id, dependencyId);
          expect(service.currentDependency, selected);
        });

        test('throws if exempting without current dependency', () {
          expect(service.exemptDependency(message),
              throwsA(isInstanceOf<NoDependencySelectedException>()));
        });

        test('throws if un-exempting without current dependency', () {
          expect(service.unExemptDependency(),
              throwsA(isInstanceOf<NoDependencySelectedException>()));
        });
      });

      group('Dependency selected', () {
        setUp(() async {
          when(client.getDependency(projectId, dependencyId))
              .thenAnswer((_) => Future.value(Dependency(
                    id: dependencyId,
                    package: Package(id: packageId),
                  )));
          await service.selectDependency(dependencyId);
        });

        test('resets dependency selection on project change', () async {
          const otherId = 'otherId';
          when(client.getProject(otherId))
              .thenAnswer((_) => Future.value(Project(id: otherId)));

          await service.selectProject(otherId);

          expect(service.currentDependency, isNull);
        });

        test('throws if dependency selection fails', () {
          const otherId = 'otherId';
          when(client.getDependency(projectId, otherId))
              .thenAnswer((realInvocation) => Future.error(Exception('Boom!')));

          expect(service.selectDependency(otherId),
              throwsA(isInstanceOf<Exception>()));

          expect(service.currentDependency, isNull);
        });

        test('ignores dependency re-selection', () async {
          final selected = await service.selectDependency(dependencyId);

          expect(selected.id, dependencyId);
          verify(client.getDependency(any, any)).called(1);
        });

        test('exempts selected dependency', () async {
          when(client.exemptDependency(projectId, dependencyId, message))
              .thenAnswer((_) => Future.value());

          final updated = await service.exemptDependency(message);

          verify(client.exemptDependency(projectId, dependencyId, message));
          expect(updated.id, dependencyId);
          expect(service.currentDependency, updated);
        });

        test('un-exempts selected dependency', () async {
          when(client.unExemptDependency(projectId, dependencyId))
              .thenAnswer((_) => Future.value());

          final updated = await service.unExemptDependency();

          verify(client.unExemptDependency(projectId, dependencyId));
          expect(updated.id, dependencyId);
          expect(service.currentDependency, updated);
        });

        test('throws if dependency exemption fails', () {
          when(client.exemptDependency(projectId, dependencyId, message))
              .thenAnswer((_) => Future.error(Exception('Boom!')));

          expect(() => service.exemptDependency(message),
              throwsA(isInstanceOf<Exception>()));

          expect(service.currentDependency, isNotNull);
        });

        test('throws if dependency un-exemption fails', () {
          when(client.unExemptDependency(projectId, dependencyId))
              .thenAnswer((_) => Future.error(Exception('Boom!')));

          expect(() => service.unExemptDependency(),
              throwsA(isInstanceOf<Exception>()));

          expect(service.currentDependency, isNotNull);
        });
      });

      group('Anonymous dependency selected', () {
        setUp(() async {
          when(client.getDependency(projectId, dependencyId))
              .thenAnswer((_) => Future.value(Dependency(id: dependencyId)));
          await service.selectDependency(dependencyId);
        });

        test('throws if exempting without a package', () {
          expect(service.exemptDependency(message),
              throwsA(isInstanceOf<AnonymousDependencyException>()));
        });

        test('throws if un-exempting without a package', () {
          expect(service.unExemptDependency(),
              throwsA(isInstanceOf<AnonymousDependencyException>()));
        });
      });
    });
  });
}
