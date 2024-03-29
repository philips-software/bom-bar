import 'package:bom_bar_ui/model/obligationItem.dart';
import 'package:bom_bar_ui/screens/dependency/dependency_screen.dart';
import 'package:bom_bar_ui/screens/widgets/dependency_tile.dart';
import 'package:flutter/material.dart';

class ObligationsView extends StatefulWidget {
  final List<ObligationItem> obligationItems;
  ObligationsView(this.obligationItems);

  @override
  State<ObligationsView> createState() => _ObligationsViewState();
}

class _ObligationsViewState extends State<ObligationsView> {
  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      child: Container(
        child: _buildPanel(),
      ),
    );
  }

  Widget _buildPanel() {
    return ExpansionPanelList(
      expansionCallback: (int index, bool isExpanded) {
        setState(() {
          widget.obligationItems[index].isExpanded = !isExpanded;
        });
      },
      children:
          widget.obligationItems.map<ExpansionPanel>((ObligationItem item) {
        return ExpansionPanel(
          headerBuilder: (BuildContext context, bool isExpanded) {
            return ListTile(
              title: Text(item.obligation),
            );
          },
          body: ListView.builder(
              shrinkWrap: true,
              itemCount: item.dependencies.length,
              itemBuilder: (context, index) {
                final dependency = item.dependencies[index];
                return DependencyTile(
                  dependency,
                  onSelect: () =>
                      navigateToDependencyScreen(context, dependency.id),
                );
              }),
          isExpanded: item.isExpanded,
        );
      }).toList(),
    );
  }

  void navigateToDependencyScreen(BuildContext context, String id) {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => DependencyScreen(id)),
    );
  }
}
