/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';

import '../../model/package.dart';
import '../../services/package_service.dart';
import '../widgets/action_item.dart';
import '../widgets/edit_selection_dialog.dart';

class ApprovalTile extends StatelessWidget {
  ApprovalTile(this.package, {required this.onChanged});

  final Package package;
  final Function(Future<Package>) onChanged;

  @override
  Widget build(BuildContext context) {
    return ListTile(
      leading: Icon(Icons.verified_user),
      title: ActionItem(
        onPressed: () => _editApproval(context),
        child: Text('Approval: ${_approvalMapping[package.approval]}'),
      ),
      subtitle: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: package.exemptions
            .map((license) => ActionItem(
                  icon: Icons.clear,
                  onPressed: () => _unExempt(context, license),
                  child: Text('Exempted license: $license'),
                ))
            .toList(growable: false),
      ),
    );
  }

  void _editApproval(BuildContext context) {
    EditSelectionDialog<Approval>(
      title: 'Approval',
      values: _approvalMapping,
      selection: package.approval,
    ).show(context).then((approval) {
      if (approval != null && approval != package.approval) {
        onChanged(PackageService.of(context).approve(approval));
      }
    });
  }

  void _unExempt(BuildContext context, String license) {
    onChanged(PackageService.of(context).unExempt(license));
  }
}

final _approvalMapping = {
  Approval.context: 'Depends on context (default)',
  Approval.confirmation: 'Requires per-project exemption',
  Approval.rejected: 'Never allowed',
  Approval.accepted: 'Always allowed',
  Approval.noPackage: 'Not a valid package',
};
