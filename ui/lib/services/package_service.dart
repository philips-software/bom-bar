/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'dart:async';
import 'dart:developer';

import 'package:flutter/foundation.dart';
import 'package:flutter/widgets.dart';
import 'package:provider/provider.dart';

import '../model/package.dart';
import 'bom_bar_client.dart';

/// Business logic abstraction for handling packages.
class PackageService extends ChangeNotifier {
  factory PackageService.of(BuildContext context) =>
      Provider.of<PackageService>(context, listen: false);

  PackageService({BomBarClient? client}) : _client = client ?? BomBarClient();

  final BomBarClient _client;
  Package? _currentPackage;
  String? error;

  Package? get current => _currentPackage;

  //TODO Replace by method call
  set approval(Approval approval) {
    _client.setApproval(_currentPackage!.id, approval).then((_) {
      _currentPackage!.approval = approval;
      notifyListeners();
    });
  }

  /// Approves the current package for [approval].
  Future<Package> approve(Approval approval) async {
    return _currentPackage!;
  }

  /// Selects the current package by it [packageId].
  Future<void> select(String packageId) => _execute(() async {
        _currentPackage = null;
        _currentPackage = await _client.getPackage(packageId);
        log('Selected package $packageId');
      });

  /// Exempts the [license] for the current package.
  Future<void> exempt(String license) => _execute(() async {
        await _client.exemptLicense(_currentPackage!.id, license);
        log('Exempted $license for ${_currentPackage!.id}');
        _currentPackage = await _client.getPackage(_currentPackage!.id);
      });

  /// Unexempts the [license] for the current package.
  Future<void> unExempt(String license) => _execute(() async {
        await _client.unExemptLicense(_currentPackage!.id, license);
        log('Un-exempted $license for ${_currentPackage!.id}');
        _currentPackage = await _client.getPackage(_currentPackage!.id);
      });

  Future<T> _execute<T>(Future<T> Function() func) async {
    try {
      error = null;
      return await func();
    } catch (e) {
      log('Backend communication failed', error: error);
      rethrow;
    } finally {
      notifyListeners();
    }
  }
}
