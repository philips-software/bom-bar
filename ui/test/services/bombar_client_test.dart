/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:bom_bar_ui/model/project.dart';
import 'package:bom_bar_ui/services/bombar_client.dart';
import 'package:dio/dio.dart';
import 'package:flutter_test/flutter_test.dart';

import 'dio_mock_server.dart';

void main() {
  group('$BomBarClient', () {
    late BomBarClient client;
    late DioMockServer server;

    setUp(() {
      client = BomBarClient();
      server = DioMockServer(client.dio);
    });

    group('Projects', () {
      const projectId = 'projectId';

      group('Get all projects', () {
        test('queries projects', () async {
          server.respondJson({
            'results': [
              {'id': projectId}
            ]
          });
          final projects = await client.getProjects();

          final request = server.requests.first;
          expect(request.method, 'GET');
          expect(request.path,
              BomBarClient.baseUrl.resolve('/projects/').toString());
          expect(projects.length, 1);
          expect(projects[0].id, projectId);
        });

        test('throws for server error status', () {
          server.respondStatus(404);

          expect(client.getProjects(), throwsA(isInstanceOf<DioError>()));
        });
      });

      group('Create new project', () {
        test('creates new project', () async {
          server.respondJson({'id': projectId}, statusCode: 201);

          final project = await client.createProject();

          final request = server.requests.first;
          expect(request.method, 'POST');
          expect(request.path,
              BomBarClient.baseUrl.resolve('projects/').toString());
          expect(project.id, projectId);
        });

        test('throws create failed', () {
          server.respondStatus(500);

          expect(client.createProject(), throwsA(isInstanceOf<DioError>()));
        });
      });

      group('Get project by identifier', () {
        test('gets project by id', () async {
          server.respondJson({'id': projectId});

          final project = await client.getProject(projectId);

          final request = server.requests.first;
          expect(request.method, 'GET');
          expect(request.path,
              BomBarClient.baseUrl.resolve('projects/$projectId').toString());
          expect(project.id, projectId);
        });

        test('throws project does not exist', () {
          server.respondStatus(404);

          expect(
              client.getProject(projectId), throwsA(isInstanceOf<DioError>()));
        });
      });

      group('Update project properties', () {
        test('updates project', () async {
          const title = 'Title';
          server.respondJson({'id': projectId});

          final project =
              await client.updateProject(Project(id: projectId, title: title));

          final request = server.requests.first;
          expect(request.method, 'PUT');
          expect(request.path,
              BomBarClient.baseUrl.resolve('projects/$projectId').toString());
          expect(request.data['title'], title);
          expect(project.id, projectId);
          expect(project.title, isNull);
        });

        test('throws if server fails to update', () {
          server.respondStatus(400);

          expect(client.updateProject(Project(id: projectId)),
              throwsA(isInstanceOf<DioError>()));
        });
      });

      group('Upload SPDX file', () {
        //TODO Don't know how to properly test this; needs update
      });

      group('Dependency operations', () {
        const dependencyId = 'dependencyId';

        group('Get project dependency', () {
          test('gets dependency for project', () async {
            server.respondJson({'id': dependencyId});

            final dependency =
                await client.getDependency(projectId, dependencyId);

            final request = server.requests.first;
            expect(request.method, 'GET');
            expect(
                request.path,
                BomBarClient.baseUrl
                    .resolve('projects/$projectId/dependencies/$dependencyId')
                    .toString());
            expect(dependency.id, dependencyId);
          });

          test('throws if dependency does not exist', () {
            server.respondStatus(404);

            expect(client.getDependency(projectId, dependencyId),
                throwsA(isInstanceOf<DioError>()));
          });
        });

        group('Project dependency exemption', () {
          test('exempts project dependency', () async {
            const message = 'Message';
            server.respondStatus(204);

            await client.exempt(projectId, dependencyId, message);

            final request = server.requests.first;
            expect(request.method, 'POST');
            expect(
                request.path,
                BomBarClient.baseUrl
                    .resolve('projects/$projectId/exempt/$dependencyId')
                    .toString());
            expect(request.data['rationale'], message);
          });

          test('throws if exemption fails on server', () {
            server.respondStatus(500);

            expect(client.exempt(projectId, dependencyId, ''),
                throwsA(isInstanceOf<DioError>()));
          });

          test('un-exempts project dependency', () async {
            server.respondStatus(204);

            await client.unexempt(projectId, dependencyId);

            final request = server.requests.first;
            expect(request.method, 'DELETE');
            expect(
                request.path,
                BomBarClient.baseUrl
                    .resolve('projects/$projectId/exempt/$dependencyId')
                    .toString());
          });

          test('throws if un-exemption fails on server', () {
            server.respondStatus(500);

            expect(client.unexempt(projectId, dependencyId),
                throwsA(isInstanceOf<DioError>()));
          });
        });

        group('Get license distribution for project', () {
          test('gets project license distribution', () async {
            var data = {'low': 23, 'high': 42};
            server.respondJson(data);

            final distribution = await client.getLicenseDistribution(projectId);

            final request = server.requests.first;
            expect(request.method, 'GET');
            expect(
                request.path,
                BomBarClient.baseUrl
                    .resolve('projects/$projectId/licenses')
                    .toString());
            expect(distribution, data);
            expect(distribution.keys.toList(), ['high', 'low']);
          });

          test('throws if distribution query fails', () {
            server.respondStatus(500);

            expect(client.getLicenseDistribution(projectId),
                throwsA(isInstanceOf<DioError>()));
          });
        });
      });
    });

    group('Packages', () {
      group('Find packages by filter', () {});

      group('Get package', () {});

      group('Update package approval', () {});

      group('(Un)exempt package license', () {});
    });
  });
}
