/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';

import 'shared.dart';

class ActionLink extends StatelessWidget {
  ActionLink({this.child, @required this.url});

  final Widget child;
  final Uri url;

  @override
  Widget build(BuildContext context) {
    return Wrap(
      children: [
        if (child != null) child,
        SizedBox(width: 4),
        GestureDetector(
          child: Text(
            url.toString(),
            style: TextStyle(
              decoration: TextDecoration.underline,
              color: Colors.blueAccent,
            ),
          ),
          onTap: () => _launchUrl(context),
        ),
      ],
    );
  }

  void _launchUrl(BuildContext context) async {
    if (await canLaunch(url.toString())) {
      await launch(url.toString());
    } else {
      showError(context, 'Could not open URL "$url"');
    }
  }
}
