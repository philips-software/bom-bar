/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:bom_bar_ui/model/dependency.dart';
import 'package:bom_bar_ui/model/package.dart';
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
    const projectId = 'projectId';
    const dependencyId = 'dependencyId';
    const packageId = 'packageId';
    const message = 'Message';
    late MockBomBarClient client;
    late ProjectService service;

    setUp(() async {
      client = MockBomBarClient();
      service = ProjectService(client: client);

      when(client.getProject(projectId))
          .thenAnswer((_) => Future.value(Project(id: projectId)));
    });

    group('No project selected', () {
      test('creates new project', () async {
        final newProject = Project(id: 'newId');
        when(client.createProject())
            .thenAnswer((_) => Future.value(newProject));

        final project = await service.createNew();

        expect(project, newProject);
        expect(service.currentProject, newProject);
      });

      test('throws if project creation failed', () {
        when(client.createProject()).thenThrow(Exception('Boom!'));

        expect(service.createNew(), throwsA(isInstanceOf<Exception>()));
        expect(service.currentProject, isNull);
      });

      test('selects project by id', () async {
        final project = Project(id: projectId);
        when(client.getProject(projectId))
            .thenAnswer((_) => Future.value(project));

        final selected = await service.selectProject(projectId);

        expect(selected, project);
        expect(service.currentProject, project);
      });

      test('throws on failure to select project', () async {
        when(client.getProject(projectId))
            .thenAnswer((_) => Future.error(Exception('Boom!')));

        expect(service.selectProject(projectId),
            throwsA(isInstanceOf<Exception>()));
        expect(service.currentProject, isNull);
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

      test('throws if select dependency without current project', () {
        expect(service.selectDependency(dependencyId),
            throwsA(isInstanceOf<NoProjectSelectedException>()));
      });
    });

    group('Project selected', () {
      setUp(() {
        service.selectProject(projectId);
      });

      test('ignores reselection of same project', () async {
        await service.selectProject(projectId);

        verify(client.getProject(any)).called(1);
        expect(service.currentProject, isNotNull);
      });

      test('refreshes selected project', () async {
        final project = await service.refreshProject();

        expect(project.id, projectId);
        expect(service.currentProject!.id, projectId);
        verify(client.getProject(projectId)).called(2);
      });

      test('throws refresh failed', () {
        when(client.getProject(projectId))
            .thenAnswer((_) => Future.error(Exception('Boom!')));

        expect(service.refreshProject(), throwsA(isInstanceOf<Exception>()));
        expect(service.currentProject, isNull);
      });

      test('updates project', () async {
        const updatedId = 'updatedId';
        final update = Project(id: projectId);
        final result = Project(id: updatedId);
        when(client.updateProject(update))
            .thenAnswer((_) => Future.value(result));

        final project = await service.updateProject(update);

        expect(project.id, updatedId);
        expect(service.currentProject!.id, updatedId);
      });

      test('throws update failure', () async {
        final update = Project(id: projectId);
        when(client.updateProject(update))
            .thenAnswer((_) => Future.error(Exception('Boom!')));

        expect(
            service.updateProject(update), throwsA(isInstanceOf<Exception>()));
        expect(service.currentProject, isNull);
      });

      test('select and upload file', () async {
        final project = await service.uploadSpdx();

        verify(client.uploadSpdx(projectId));
        expect(project.id, projectId);
      });

      test('throws failure during upload', () async {
        when(client.uploadSpdx(projectId))
            .thenAnswer((_) => Future.error(Exception('Boom!')));

        expect(service.uploadSpdx(), throwsA(isInstanceOf<Exception>()));
      });

      test('loads license distribution', () async {
        final distribution = <String, int>{};
        when(client.getLicenseDistribution(projectId))
            .thenAnswer((_) => Future.value(distribution));

        final result = await service.licenseDistribution();

        expect(result, distribution);
      });

      test('throws if server error', () {
        when(client.getLicenseDistribution(projectId))
            .thenAnswer((_) => Future.error(Exception('Boom!')));

        expect(
            service.licenseDistribution(), throwsA(isInstanceOf<Exception>()));
      });

      group('No dependency selected', () {
        test('selects dependency', () async {
          when(client.getDependency(projectId, dependencyId))
              .thenAnswer((_) => Future.value(Dependency(id: dependencyId)));

          final selected = await service.selectDependency(dependencyId);

          expect(selected.id, dependencyId);
          expect(service.currentDependency!.id, dependencyId);
        });

        test('throws if dependency selection fails', () {
          when(client.getDependency(any, any))
              .thenAnswer((realInvocation) => Future.error(Exception('Boom!')));

          expect(service.selectDependency(dependencyId),
              throwsA(isInstanceOf<Exception>()));
        });

        test('throws if exempting without current dependency', () {
          expect(service.exemptDependency(message),
              throwsA(isInstanceOf<NoDependencySelectedException>()));
        });

        test('throws if unexempting without current dependency', () {
          expect(service.unexemptDependency(),
              throwsA(isInstanceOf<NoDependencySelectedException>()));
        });
      });

      group('Dependency selected', () {
        late Dependency dependency;

        setUp(() async {
          dependency =
              Dependency(id: dependencyId, package: Package(id: packageId));
          when(client.getDependency(projectId, dependencyId))
              .thenAnswer((_) => Future.value(dependency));
          await service.selectDependency(dependencyId);
        });

        test('resets dependency selection on project change', () async {
          const otherId = 'otherId';
          when(client.getProject(otherId)).thenAnswer(
              (realInvocation) => Future.value(Project(id: otherId)));

          await service.selectProject(otherId);

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

          expect(updated.id, dependencyId);
          verify(client.exemptDependency(projectId, dependencyId, message))
              .called(1);
          verify(client.getDependency(projectId, dependencyId)).called(2);
        });

        test('unexempts selected dependency', () async {
          when(client.unexemptDependency(projectId, dependencyId))
              .thenAnswer((_) => Future.value());

          final updated = await service.unexemptDependency();

          expect(updated.id, dependencyId);
          verify(client.unexemptDependency(projectId, dependencyId)).called(1);
          verify(client.getDependency(projectId, dependencyId)).called(2);
        });

        test('throws if dependency exemption fails', () {
          when(client.exemptDependency(projectId, dependencyId, message))
              .thenAnswer((_) => Future.error(Exception('Boom!')));

          expect(() => service.exemptDependency(message),
              throwsA(isInstanceOf<Exception>()));
        });

        test('throws if dependency unexemption fails', () {
          when(client.unexemptDependency(projectId, dependencyId))
              .thenAnswer((_) => Future.error(Exception('Boom!')));

          expect(() => service.unexemptDependency(),
              throwsA(isInstanceOf<Exception>()));
        });
      });

      group('Anonymous dependency selected', () {
        late Dependency dependency;

        setUp(() {
          dependency = Dependency(id: dependencyId);
          when(client.getDependency(projectId, dependencyId))
              .thenAnswer((_) => Future.value(dependency));
        });

        test('throws if exempting without a package', () {
          expect(service.exemptDependency(message),
              throwsA(isInstanceOf<NoDependencySelectedException>()));
        });

        test('throws if unexempting without a package', () {
          expect(service.unexemptDependency(),
              throwsA(isInstanceOf<NoDependencySelectedException>()));
        });
      });
    });
  });
}
