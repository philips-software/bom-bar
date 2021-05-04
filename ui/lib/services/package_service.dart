/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'dart:async';
import 'dart:developer';

import 'package:flutter/widgets.dart';
import 'package:provider/provider.dart';

import '../model/package.dart';
import 'bom_bar_client.dart';

/// Business logic abstraction for handling packages.
class PackageService {
  factory PackageService.of(BuildContext context) =>
      Provider.of<PackageService>(context, listen: false);

  PackageService({BomBarClient? client}) : _client = client ?? BomBarClient();

  final BomBarClient _client;
  Package? _currentPackage;

  Package? get currentPackage => _currentPackage;

  /// Selects the current package by it [packageId].
  Future<Package> select(String packageId) async {
    if (_currentPackage?.id == packageId) return _currentPackage!;
    _currentPackage = null;

    return _execute(() async {
      final package = await _client.getPackage(packageId);
      _currentPackage = package;
      log('Selected package ${package.id}');
      return package;
    });
  }

  Future<Package> refresh() async {
    _assertPackageSelected();

    return _execute(() async {
      final package = await _client.getPackage(_currentPackage!.id);
      _currentPackage = package;
      log('Refreshed package ${package.id}');
      return package;
    });
  }

  /// Approves the current package for [approval].
  Future<Package> approve(Approval approval) async {
    _assertPackageSelected();

    await _execute(() async {
      await _client.setApproval(_currentPackage!.id, approval);
      log('Approved $approval for ${_currentPackage!.id}');
    });
    return refresh();
  }

  /// Exempts the [license] for the current package.
  Future<Package> exempt(String license) async {
    _assertPackageSelected();

    await _execute(() async {
      await _client.exemptLicense(_currentPackage!.id, license);
      log('Exempted $license for ${_currentPackage!.id}');
    });
    return refresh();
  }

  /// Un-exempts the [license] for the current package.
  Future<Package> unExempt(String license) async {
    _assertPackageSelected();

    await _execute(() async {
      await _client.unExemptLicense(_currentPackage!.id, license);
      log('Un-exempted $license for ${_currentPackage!.id}');
    });
    return refresh();
  }

  void _assertPackageSelected() {
    if (_currentPackage == null) throw NoPackageSelectedException();
  }

  Future<T> _execute<T>(Future<T> Function() func) async {
    try {
      return await func();
    } catch (e) {
      log('Backend communication failed', error: e);
      rethrow;
    }
  }
}

class NoPackageSelectedException implements Exception {}
