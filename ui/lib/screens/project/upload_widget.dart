/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';

import '../../services/project_service.dart';
import '../widgets/action_button.dart';

class UploadWidget extends StatefulWidget {
  UploadWidget({Key? key, this.onUpdated}) : super(key: key);

  final Function()? onUpdated;

  @override
  _UploadWidgetState createState() => _UploadWidgetState();
}

enum _Status { IDLE, DONE, ERROR }

class _UploadWidgetState extends State<UploadWidget> {
  var _status = _Status.IDLE;

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        ActionButton(
          icon: Icons.upload_file,
          onPressed: () => _onPressed(context),
        ),
        if (_status == _Status.DONE) Icon(Icons.check, color: Colors.green),
        if (_status == _Status.ERROR) Icon(Icons.error, color: Colors.red),
      ],
    );
  }

  void _onPressed(BuildContext context) {
    final service = ProjectService.of(context);

    // Cannot indicate "loading" state because service does no complete on
    // pressing Cancel in the dialog.
    nextState = _Status.IDLE;
    service.uploadSpdx().then((_) {
      nextState = _Status.DONE;
      widget.onUpdated?.call();
    }).catchError((_) {
      nextState = _Status.ERROR;
    });
  }

  set nextState(_Status value) => setState(() {
        _status = value;
      });
}
