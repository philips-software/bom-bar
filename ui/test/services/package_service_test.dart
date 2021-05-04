/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:bom_bar_ui/model/package.dart';
import 'package:bom_bar_ui/services/bom_bar_client.dart';
import 'package:bom_bar_ui/services/package_service.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/annotations.dart';
import 'package:mockito/mockito.dart';

import 'package_service_test.mocks.dart';

@GenerateMocks([BomBarClient])
void main() {
  group('$PackageService', () {
    const packageId = 'packageId';
    const license = 'License';

    late MockBomBarClient client;
    late PackageService service;

    setUp(() {
      client = MockBomBarClient();
      service = PackageService(client: client);

      when(client.getPackage(packageId))
          .thenAnswer((_) => Future.value(Package(id: packageId)));
    });

    group('Find packages', () {
      const filter = 'filter';

      test('find matching packages', () async {
        when(client.findPackagesById(filter: filter))
            .thenAnswer((_) => Future.value([Package(id: 'id')]));

        final packages = await service.findPackages(filter);

        expect(packages, hasLength(1));
      });

      test('throws client communication failure', () {
        when(client.findPackagesById(filter: filter))
            .thenAnswer((_) => Future.error(Exception('Boom!')));

        expect(
            service.findPackages(filter), throwsA(isInstanceOf<Exception>()));
      });
    });

    group('No package selected', () {
      test('selects current package', () async {
        final package = await service.select(packageId);

        expect(package.id, packageId);
        expect(service.currentPackage, package);
      });

      test('throws for refresh without current package', () {
        expect(service.refresh(),
            throwsA(isInstanceOf<NoPackageSelectedException>()));
      });

      test('throws approval without current package', () {
        expect(service.approve(Approval.noPackage),
            throwsA(isInstanceOf<NoPackageSelectedException>()));
      });

      test('throws for exemption without current package', () {
        expect(service.exempt(license),
            throwsA(isInstanceOf<NoPackageSelectedException>()));
      });

      test('throws for un-exemption without current package', () {
        expect(service.unExempt(license),
            throwsA(isInstanceOf<NoPackageSelectedException>()));
      });
    });

    group('Current package selected', () {
      setUp(() async {
        await service.select(packageId);
      });

      test('ignores re-selection of the same package', () async {
        final selected = await service.select(packageId);

        expect(selected.id, packageId);
        verify(client.getPackage(packageId)).called(1);
      });

      test('refreshes current package', () async {
        const updateId = 'updateId';
        when(client.getPackage(packageId))
            .thenAnswer((_) => Future.value(Package(id: updateId)));

        final updated = await service.refresh();

        expect(updated.id, updateId);
        expect(service.currentPackage, updated);
      });

      test('throws for refresh failure', () {
        when(client.getPackage(packageId))
            .thenAnswer((_) => Future.error(Exception('Boom!')));

        expect(service.refresh(), throwsA(isInstanceOf<Exception>()));

        expect(service.currentPackage, isNotNull);
      });

      test('throws for select package failure', () {
        const otherId = 'otherId';
        when(client.getPackage(otherId))
            .thenAnswer((_) => Future.error(Exception('Boom!')));

        expect(service.select(otherId), throwsA(isInstanceOf<Exception>()));

        expect(service.currentPackage, isNull);
      });

      test('approves current packages', () async {
        const updateId = 'updateId';
        when(client.setApproval(packageId, Approval.confirmation))
            .thenAnswer((_) => Future.value(Package(id: updateId)));
        when(client.getPackage(packageId))
            .thenAnswer((_) => Future.value(Package(id: updateId)));

        final approved = await service.approve(Approval.confirmation);

        verify(client.setApproval(packageId, Approval.confirmation));
        expect(approved.id, updateId);
        expect(service.currentPackage, approved);
      });

      test('throws for approval failure', () {
        when(client.setApproval(packageId, Approval.confirmation))
            .thenAnswer((_) => Future.error(Exception('Boom!')));

        expect(service.approve(Approval.confirmation),
            throwsA(isInstanceOf<Exception>()));

        expect(service.currentPackage, isNotNull);
      });

      test('exempts license for current package', () async {
        const updateId = 'updateId';
        when(client.exemptLicense(packageId, license))
            .thenAnswer((_) => Future.value());
        when(client.getPackage(packageId))
            .thenAnswer((_) => Future.value(Package(id: updateId)));

        final updated = await service.exempt(license);

        expect(updated.id, updateId);
        expect(service.currentPackage, updated);
      });

      test('throws for license exemption failure', () {
        when(client.exemptLicense(packageId, license))
            .thenAnswer((_) => Future.error(Exception('Boom!')));

        expect(service.exempt(license), throwsA(isInstanceOf<Exception>()));

        expect(service.currentPackage, isNotNull);
      });

      test('un-exempts license for current package', () async {
        const updateId = 'updateId';
        when(client.unExemptLicense(packageId, license))
            .thenAnswer((_) => Future.value());
        when(client.getPackage(packageId))
            .thenAnswer((_) => Future.value(Package(id: updateId)));

        final updated = await service.unExempt(license);

        expect(updated.id, updateId);
        expect(service.currentPackage, updated);
      });

      test('throws for license un-exemption failure', () {
        when(client.unExemptLicense(packageId, license))
            .thenAnswer((_) => Future.error(Exception('Boom!')));

        expect(service.unExempt(license), throwsA(isInstanceOf<Exception>()));

        expect(service.currentPackage, isNotNull);
      });
    });
  });
}
