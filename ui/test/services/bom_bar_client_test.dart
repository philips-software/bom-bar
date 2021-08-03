/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:bom_bar_ui/model/package.dart';
import 'package:bom_bar_ui/model/project.dart';
import 'package:bom_bar_ui/services/bom_bar_client.dart';
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

      group('Get projects based on search query', () {
        test('queries for all projects with no search fragment', () async {
          server.respondJson({
            'results': [
              {'id': projectId}
            ]
          });

          final projects = await client.findProjects();
          final request = server.requests.first;

          expect(request.method, 'GET');
          expect(request.path,
              BomBarClient.baseUrl.resolve('/projects/').toString());
          expect(request.queryParameters, {'q': null});
          expect(projects.length, 1);
          expect(projects[0].id, projectId);
        });

        test('queries for projects matching search fragment', () async {
          server.respondJson({
            'results': [
              {'id': projectId}
            ]
          });

          const fragment = 'ProjectA';
          final projects = await client.findProjects(fragment);
          final request = server.requests.first;

          expect(request.method, 'GET');
          expect(request.path,
              BomBarClient.baseUrl.resolve('/projects/').toString());
          expect(request.queryParameters, {'q': fragment});
          expect(projects.length, 1);
          expect(projects[0].id, projectId);
        });

        test('throws for server error status', () {
          server.respondStatus(404);

          expect(client.findProjects(), throwsA(isInstanceOf<DioError>()));
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

          final project = await client.updateProject(
              projectId, Project(id: 'irrelevant', title: title));

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

          expect(client.updateProject(projectId, Project(id: 'irrelevant')),
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
        });

        test('throws if distribution query fails', () {
          server.respondStatus(500);

          expect(client.getLicenseDistribution(projectId),
              throwsA(isInstanceOf<DioError>()));
        });
      });

      group('Find dependencies per obligation of a project', () {
        test('find dependencies per obligation', () async {
          final obligation_description = 'obligation';
          final dependencyId = 'DependencyId';
          server.respondJson({
            obligation_description: [
              {'id': dependencyId}
            ]
          });

          final obligations = await client.findObligations(projectId);

          final request = server.requests.first;
          expect(request.method, 'GET');
          expect(
              request.path,
              BomBarClient.baseUrl
                  .resolve('projects/$projectId/obligations')
                  .toString());
          expect(obligations[obligation_description]!.first.id, dependencyId);
        });

        test('throws if distribution query fails', () {
          server.respondStatus(500);

          expect(client.findObligations(projectId),
              throwsA(isInstanceOf<DioError>()));
        });
      });

      group('Upload SPDX file', () {
        test('Uploads bill-of-materials', () async {
          server.respondStatus(200);

          await client.uploadSpdx(projectId, [1, 2, 3]);

          final request = server.requests.first;
          expect(request.method, 'POST');
          expect(
              request.path,
              BomBarClient.baseUrl
                  .resolve('/projects/$projectId/upload')
                  .toString());
        });

        test('throws for upload failure', () {
          server.respondStatus(400);

          expect(client.uploadSpdx(projectId, [42]),
              throwsA(isInstanceOf<DioError>()));
        });
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

            await client.exemptDependency(projectId, dependencyId, message);

            final request = server.requests.first;
            expect(request.method, 'POST');
            expect(
                request.path,
                BomBarClient.baseUrl
                    .resolve(
                        'projects/$projectId/dependencies/$dependencyId/exempt')
                    .toString());
            expect(request.data['rationale'], message);
          });

          test('throws if exemption fails on server', () {
            server.respondStatus(500);

            expect(client.exemptDependency(projectId, dependencyId, ''),
                throwsA(isInstanceOf<DioError>()));
          });

          test('un-exempts project dependency', () async {
            server.respondStatus(204);

            await client.unExemptDependency(projectId, dependencyId);

            final request = server.requests.first;
            expect(request.method, 'DELETE');
            expect(
                request.path,
                BomBarClient.baseUrl
                    .resolve(
                        'projects/$projectId/dependencies/$dependencyId/exempt')
                    .toString());
          });

          test('throws if un-exemption fails on server', () {
            server.respondStatus(500);

            expect(client.unExemptDependency(projectId, dependencyId),
                throwsA(isInstanceOf<DioError>()));
          });
        });
      });
    });

    group('Packages', () {
      const packageId = 'packageId';

      group('Find packages by filter', () {
        test('queries packages by filter', () async {
          const filter = 'type/name';
          server.respondJson({
            'results': [
              {'id': packageId}
            ]
          });

          final packages = await client.findPackagesById(filter: filter);

          final request = server.requests.first;
          expect(request.method, 'GET');
          expect(request.path,
              BomBarClient.baseUrl.resolve('packages/?q=$filter').toString());
          expect(packages[0].id, packageId);
        });

        test('throws if server fails', () {
          server.respondStatus(400);

          expect(client.findPackagesById(filter: ''),
              throwsA(isInstanceOf<DioError>()));
        });
      });

      group('Get package', () {
        test('gets package by id', () async {
          server.respondJson({'id': packageId});

          final package = await client.getPackage(packageId);

          final request = server.requests.first;
          expect(request.method, 'GET');
          expect(request.path,
              BomBarClient.baseUrl.resolve('packages/$packageId').toString());
          expect(package.id, packageId);
        });

        test('throws if package not found', () {
          server.respondStatus(404);

          expect(
              client.getPackage(packageId), throwsA(isInstanceOf<DioError>()));
        });
      });

      group('Update package approval', () {
        test('approves package', () async {
          server.respondStatus(204);

          await client.setApproval(packageId, Approval.accepted);

          final request = server.requests.first;
          expect(request.method, 'POST');
          expect(
              request.path,
              BomBarClient.baseUrl
                  .resolve('packages/$packageId/approve/approved')
                  .toString());
        });

        test('throws if package not found', () {
          server.respondStatus(404);

          expect(client.setApproval(packageId, Approval.rejected),
              throwsA(isInstanceOf<DioError>()));
        });
      });

      group('(Un)exempt package license', () {
        const license = 'License';

        test('exempts package license', () async {
          server.respondStatus(204);

          await client.exemptLicense(packageId, license);

          final request = server.requests.first;
          expect(request.method, 'POST');
          expect(
              request.path,
              BomBarClient.baseUrl
                  .resolve('packages/$packageId/exempt/$license')
                  .toString());
        });

        test('un-exempts package license', () async {
          server.respondStatus(204);

          await client.unExemptLicense(packageId, license);

          final request = server.requests.first;
          expect(request.method, 'DELETE');
          expect(
              request.path,
              BomBarClient.baseUrl
                  .resolve('packages/$packageId/exempt/$license')
                  .toString());
        });

        test('throws if package not found', () {
          server.respondStatus(404);

          expect(client.exemptLicense(packageId, license),
              throwsA(isInstanceOf<DioError>()));
        });
      });
    });
  });
}
