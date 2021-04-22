/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'dart:async';
import 'dart:developer';

import 'package:bom_bar_ui/model/package.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/widgets.dart';
import 'package:provider/provider.dart';

import 'bombar_client.dart';

class PackageService extends ChangeNotifier {
  factory PackageService.of(BuildContext context) =>
      Provider.of<PackageService>(context, listen: false);

  PackageService({BomBarClient? client}) : _client = client ?? BomBarClient();

  final BomBarClient _client;
  Package? _current;
  String? error;

  Package? get current => _current;

  set approval(Approval approval) {
    _client.setApproval(_current!.id, approval).then((_) {
      _current!.approval = approval;
      notifyListeners();
    });
  }

  Future<void> select(String id) => _execute(() async {
        _current = null;
        _current = await _client.getPackage(id);
        log('Selected package $id');
      });

  Future<void> exempt(String license) => _execute(() async {
        await _client.exemptLicense(_current!.id, license);
        log('Exempted $license for ${_current!.id}');
        _current = await _client.getPackage(_current!.id);
      });

  Future<void> unExempt(String license) => _execute(() async {
        await _client.unExemptLicense(_current!.id, license);
        log('Un-exempted $license for ${_current!.id}');
        _current = await _client.getPackage(_current!.id);
      });

  Future<T> _execute<T>(Future<T> Function() func) async {
    try {
      error = null;
      return await func();
    } catch (e) {
      error = e.toString();
      rethrow;
    } finally {
      notifyListeners();
    }
  }
}
