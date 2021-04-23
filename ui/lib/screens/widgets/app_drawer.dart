/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

import 'package:flutter/material.dart';
import 'package:yeet/yeet.dart';

class AppDrawer extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Drawer(
        child: ListView(
      padding: EdgeInsets.zero,
      children: [
        DrawerHeader(
          decoration: BoxDecoration(
            color: Theme.of(context).accentColor,
          ),
          child: Text('BOM-bar',
              style: Theme.of(context).accentTextTheme.headline3),
        ),
        ListTile(
          leading: Icon(Icons.language),
          title: Text('Projects'),
          onTap: () => context.yeet('/projects'),
        ),
        ListTile(
          leading: Icon(Icons.extension),
          title: Text('Packages'),
          onTap: () => context.yeet('/packages'),
        ),
        AboutListTile(
          icon: Icon(Icons.info_outlined),
          applicationName: 'BOM-bar',
          applicationLegalese:
              'Copyright © Koninklijke Philips N.V.\nLicense: MIT',
        )
      ],
    ));
  }
}
