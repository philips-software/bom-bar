/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:bom_bar_ui/model/dependency.dart';
import 'package:bom_bar_ui/model/package.dart';
import 'package:bom_bar_ui/model/project.dart';
import 'package:bom_bar_ui/services/model_adapters.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  group('$Project conversion', () {
    const projectId = 'projectId';
    const title = 'Title';
    final updated = DateTime.now().toUtc();
    const issueCount = 42;
    const dependencyId = 'dependencyId';
    const license = 'License';

    group('$Project from JSON', () {
      test('maps all properties', () {
        final project = toProject({
          'id': projectId,
          'title': title,
          'updated': updated.toIso8601String(),
          'distribution': 'internal',
          'phase': 'development',
          'issues': issueCount,
          'packages': [
            {'id': dependencyId}
          ],
          'exemptions': [license],
        });

        expect(project.id, projectId);
        expect(project.title, title);
        expect(project.lastUpdate, updated);
        expect(project.distribution, Distribution.internal);
        expect(project.phase, Phase.development);
        expect(project.issueCount, issueCount);
        expect(project.dependencies[0].id, dependencyId);
        expect(project.exemptions, [license]);
      });

      test('defaults property values', () {
        final project = toProject({'id': projectId});

        expect(project.id, projectId);
        expect(project.title, isNull);
        expect(project.lastUpdate, isNull);
        expect(project.distribution, Distribution.unknown);
        expect(project.phase, Phase.unknown);
        expect(project.issueCount, isZero);
        expect(project.dependencies, isEmpty);
        expect(project.exemptions, isEmpty);
      });

      test('converts list of projects', () {
        final projects = toProjectList([
          {'id': projectId}
        ]);

        expect(projects.length, 1);
        expect(projects[0].id, projectId);
      });

      test('converts null to empty list of projects', () {
        expect(toProjectList(null), <Project>[]);
      });

      test('throws for missing project id', () {
        expect(
            () => toProject(<String, dynamic>{}),
            throwsA(predicate<MappingException>(
                (e) => e.message.contains('project id'))));
      });

      test('maps distribution', () {
        expect(toDistribution('internal'), Distribution.internal);
        expect(toDistribution('open_source'), Distribution.open_source);
        expect(toDistribution('proprietary'), Distribution.proprietary);
        expect(toDistribution('saas'), Distribution.saas);
        expect(toDistribution('undefined'), Distribution.unknown);
        expect(toDistribution(null), Distribution.unknown);
      });

      test('maps phase', () {
        expect(toPhase('development'), Phase.development);
        expect(toPhase('released'), Phase.released);
        expect(toPhase('undefined'), Phase.unknown);
        expect(toPhase(null), Phase.unknown);
      });
    });

    group('$Project to JSON', () {
      test('converts project', () {
        final json = fromProject(Project(
            id: projectId,
            title: title,
            distribution: Distribution.internal,
            phase: Phase.development));

        expect(json, {
          'id': projectId,
          'title': title,
          'distribution': 'internal',
          'phase': 'development'
        });
      });

      test('converts minimal project', () {
        final json = fromProject(Project(id: projectId));

        expect(json, {'id': projectId});
      });

      test('converts distribution', () {
        expect(fromDistribution(Distribution.internal), 'internal');
        expect(fromDistribution(Distribution.saas), 'saas');
        expect(fromDistribution(Distribution.proprietary), 'proprietary');
        expect(fromDistribution(Distribution.open_source), 'open_source');
        expect(fromDistribution(Distribution.unknown), null);
      });

      test('converts phase', () {
        expect(fromPhase(Phase.development), 'development');
        expect(fromPhase(Phase.released), 'released');
        expect(fromPhase(Phase.unknown), null);
      });
    });
  });

  group('$Dependency', () {
    const dependencyId = 'dependencyId';
    const title = 'title';
    final purl = Uri.parse('pkg:type/name');
    const relation = 'relation';
    final license = 'License';
    const issueCount = 23;
    const licenseIssue = 'License issue';
    const childId = 'childId';
    const parentId = 'parentId';
    const packageId = 'packageId';
    const exemption = 'Exemption';

    group('$Dependency from JSON', () {
      test('maps all properties', () {
        final dependency = toDependency({
          'id': dependencyId,
          'title': title,
          'purl': purl.toString(),
          'license': license,
          'relation': relation,
          'is_root': true,
          'is_development': true,
          'is_delivered': true,
          'issues': issueCount,
          'license_issues': [licenseIssue],
          'dependencies': [
            {'id': childId}
          ],
          'usages': [
            {'id': parentId}
          ],
          'package': {'id': packageId},
          'exemption': exemption
        });

        expect(dependency.id, dependencyId);
        expect(dependency.title, title);
        expect(dependency.purl, purl);
        expect(dependency.license, license);
        expect(dependency.relation, relation);
        expect(dependency.isRoot, isTrue);
        expect(dependency.isDevelopment, isTrue);
        expect(dependency.isDelivered, isTrue);
        expect(dependency.issueCount, issueCount);
        expect(dependency.licenseIssues, [licenseIssue]);
        expect(dependency.dependencies[0].id, childId);
        expect(dependency.usages[0].id, parentId);
        expect(dependency.package!.id, packageId);
        expect(dependency.exemption, exemption);
      });

      test('defaults property values', () {
        final dependency = toDependency({'id': dependencyId});

        expect(dependency.id, dependencyId);
        expect(dependency.title, isNull);
        expect(dependency.purl, isNull);
        expect(dependency.license, isNull);
        expect(dependency.relation, isNull);
        expect(dependency.isRoot, isFalse);
        expect(dependency.isDevelopment, isFalse);
        expect(dependency.isDelivered, isFalse);
        expect(dependency.issueCount, isZero);
        expect(dependency.licenseIssues, isEmpty);
        expect(dependency.dependencies, isEmpty);
        expect(dependency.usages, isEmpty);
        expect(dependency.package, isNull);
        expect(dependency.exemption, isNull);
      });

      test('converts list of dependencies', () {
        final dependencies = toDependencyList([
          {'id': dependencyId}
        ]);

        expect(dependencies[0].id, dependencyId);
      });

      test('converts null to empty list', () {
        expect(toDependencyList(null), isEmpty);
      });

      test('throws for missing dependency id', () {
        expect(
            () => toDependency(<String, dynamic>{}),
            throwsA(predicate<MappingException>(
                (e) => e.message.contains('dependency id'))));
      });
    });

    group('$Package from JSON', () {
      const packageId = 'packageId';
      final reference = Uri.parse('pkg:type/name');
      const name = 'Name';
      final homepage = Uri.parse('https://example.com/homepage');
      const description = 'Description';
      const exemption = 'exemption';
      const projectId = 'projectId';

      test('maps all properties', () {
        final package = toPackage({
          'id': packageId,
          'reference': reference.toString(),
          'name': name,
          'homepage': homepage.toString(),
          'description': description,
          'approval': 'approved',
          'exemptions': [exemption],
          'projects': [
            {'id': projectId}
          ]
        });

        expect(package.id, packageId);
        expect(package.reference, reference);
        expect(package.title, name);
        expect(package.homepage, homepage);
        expect(package.description, description);
        expect(package.approval, Approval.accepted);
        expect(package.exemptions, [exemption]);
        expect(package.projects[0].id, projectId);
      });

      test('defaults property values', () {
        final package = toPackage({'id': packageId});

        expect(package.id, packageId);
        expect(package.reference, isNull);
        expect(package.title, isNull);
        expect(package.homepage, isNull);
        expect(package.description, isNull);
        expect(package.approval, Approval.context);
        expect(package.exemptions, isEmpty);
        expect(package.projects, isEmpty);
      });

      test('converts list of packages', () {
        final packages = toPackageList([
          {'id': packageId}
        ]);

        expect(packages[0].id, packageId);
      });

      test('converts null to empty list of package', () {
        expect(toPackageList(null), isEmpty);
      });

      test('throws for missing package id', () {
        expect(
            () => toPackage(<String, dynamic>{}),
            throwsA(predicate<MappingException>(
                (e) => e.message.contains('package id'))));
      });

      test('maps to approval', () {
        expect(toApproval('context'), Approval.context);
        expect(toApproval('rejected'), Approval.rejected);
        expect(toApproval('needs_approval'), Approval.confirmation);
        expect(toApproval('approved'), Approval.accepted);
        expect(toApproval('not_a_package'), Approval.noPackage);
        expect(toApproval('context'), Approval.context);
        expect(toApproval('undefined'), Approval.context);
        expect(toApproval(null), Approval.context);
      });

      test('maps from approval', () {
        expect(fromApproval(Approval.context), 'context');
        expect(fromApproval(Approval.rejected), 'rejected');
        expect(fromApproval(Approval.confirmation), 'needs_approval');
        expect(fromApproval(Approval.accepted), 'approved');
        expect(fromApproval(Approval.noPackage), 'not_a_package');
        expect(fromApproval(Approval.context), 'context');
      });
    });
  });
}
