/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';

import '../../model/dependency.dart';

typedef DependencyFilter = bool Function(Dependency);

class Filter {
  Filter({required DependencyFilter filter, required this.onChange})
      : _filter = filter;

  final DependencyFilter _filter;
  final void Function() onChange;
  bool _selected = false;

  bool get selected => _selected;
  set selected(bool enabled) {
    _selected = enabled;
    onChange();
  }

  bool filter(Dependency dep) => !selected || _filter(dep);
}

class SelectFilter extends StatelessWidget {
  const SelectFilter({required this.label, required this.filter});

  final String label;
  final Filter filter;

  @override
  Widget build(BuildContext context) {
    return FilterChip(
      label: Text(label),
      selected: filter.selected,
      onSelected: (enabled) => filter.selected = enabled,
    );
  }
}
