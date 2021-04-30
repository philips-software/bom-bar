/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
import 'dart:developer';

import 'package:flutter/material.dart';

import '../../model/package.dart';
import '../../services/package_service.dart';
import '../widgets/action_item.dart';
import '../widgets/edit_selection_dialog.dart';

class ApprovalTile extends StatelessWidget {
  ApprovalTile(this.package);

  final Package package;

  @override
  Widget build(BuildContext context) {
    return ListTile(
      leading: Icon(Icons.verified_user),
      title: ActionItem(
        child: Text('Approval: ${_approvalMapping[package.approval]}'),
        onPressed: () => _editApproval(context),
      ),
      subtitle: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: package.exemptions
            .map((license) => ActionItem(
                  icon: Icons.clear,
                  child: Text('Exempted license: $license'),
                  onPressed: () => _unExempt(context, license),
                ))
            .toList(growable: false),
      ),
    );
  }

  void _editApproval(BuildContext context) async {
    final update = await EditSelectionDialog<Approval>(
      title: 'Approval',
      values: _approvalMapping,
      selection: package.approval,
    ).show(context);
    if (update != null && update != package.approval) {
      PackageService.of(context).approval = update;
    }
  }

  void _unExempt(BuildContext context, String license) {
    PackageService.of(context)
        .unExempt(license)
        .catchError((error) => log('Unexempt failed', error: error));
  }
}

final _approvalMapping = {
  Approval.context: 'Depends on context (default)',
  Approval.confirmation: 'Requires per-project exemption',
  Approval.rejected: 'Never allowed',
  Approval.accepted: 'Always allowed',
  Approval.noPackage: 'Not a valid package',
};
