import 'package:bom_bar_ui/model/dependency.dart';

class ObligationItem {
  ObligationItem(this.obligation, this.dependencies, this.isExpanded);

  String obligation;
  List<Dependency> dependencies;
  bool isExpanded;
}
