import 'dart:math';

import 'package:amap_flutter_base/amap_flutter_base.dart';
import 'package:amap_flutter_map/amap_flutter_map.dart';
import 'package:amap_flutter_map_example/base_page.dart';
import 'package:amap_flutter_map_example/const_config.dart';
import 'package:amap_flutter_map_example/widgets/amap_switch_button.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';

class GeoFenceDemoPage extends BasePage {
  GeoFenceDemoPage(String title, String subTitle) : super(title, subTitle);

  @override
  Widget build(BuildContext context) {
    return _Body();
  }
}

class _Body extends StatefulWidget {
  const _Body();

  @override
  State<StatefulWidget> createState() => _State();
}

class _State extends State<_Body> {
  _State();

// Values when toggling Polygon color
  int colorsIndex = 0;
  List<Color> colors = <Color>[
    Colors.orange[200],
    Colors.yellow[200],
    Colors.red[200],
    Colors.green[200],
  ];

  Map<String, Circle> _polygons = <String, Circle>{};

  String selectedPolygonId;
  AMapController _mapController;

  void _onMapCreated(AMapController controller) {
    _mapController = controller;
    setState(() {});
  }

  LatLng _createLatLng(double lat, double lng) {
    return LatLng(lat, lng);
  }

  List<LatLng> _createPoints() {
    final List<LatLng> points = <LatLng>[];
    final int polygonCount = _polygons.length;
    final double offset = polygonCount * 5.0;
    points.add(_createLatLng(39.835334 + offset, 116.3710069));
    points.add(_createLatLng(39.843082 + offset, 116.3709830));
    points.add(_createLatLng(39.845932 + offset, 116.3642213));
    points.add(_createLatLng(39.845924 + offset, 116.3595219));
    points.add(_createLatLng(39.841562 + offset, 116.345568));
    points.add(_createLatLng(39.835347 + offset, 116.34575));
    return points;
  }

  @override
  void initState() {
    super.initState();
    // for (int i = 0; i < 10; i++) {
    //   setPoint(31.199748618478413 + i / 120, 121.42990455912913);
    // }

    // setState(() {});
  }

  void _add() {
    final Circle polygon = Circle(
        strokeColor: Colors.blue,
        fillColor: Colors.blue[200],
        strokeWidth: 1,
        radius: 100,
        center: _createLatLng(25.247593765719817 + Random().nextInt(10),
            101.35745582814145 + Random().nextInt(20)),
        strokeDottedLineType: DashLineType.none);
    selectedPolygonId = polygon.id;
    _polygons[polygon.id] = polygon;
    setState(() {});
  }

  void setPoint(double lat, double lng) {
    // for (int i = 0; i < 10; i++) {
    final Circle polygon = Circle(
        strokeColor: Colors.blue,
        fillColor: Colors.blue[200],
        strokeWidth: 1,
        radius: 100,
        center: _createLatLng(lat, lng),
        strokeDottedLineType: DashLineType.none);
    selectedPolygonId = polygon.id;
    _polygons[polygon.id] = polygon;
    // }
    setState(() {});
  }

  void _remove() {
    final Circle selectedPolygon = _polygons[selectedPolygonId];
    //有选中的Marker
    if (selectedPolygon != null) {
      setState(() {
        _polygons.remove(selectedPolygonId);
      });
    } else {
      print('无选中的Circle，无法删除');
    }
  }

  void _changeStrokeWidth() {
    final Circle selectedPolygon = _polygons[selectedPolygonId];
    double currentWidth = selectedPolygon.strokeWidth;
    if (currentWidth < 50) {
      currentWidth += 3;
    } else {
      currentWidth += 2;
    }
    //有选中的Marker
    if (selectedPolygon != null) {
      setState(() {
        _polygons[selectedPolygonId] =
            selectedPolygon.copyWith(strokeWidthParam: currentWidth);
      });
    } else {
      print('无选中的Polygon，无法修改宽度');
    }
  }

  void _changeColors() {
    final Circle polygon = _polygons[selectedPolygonId];
    setState(() {
      _polygons[selectedPolygonId] = polygon.copyWith(
        strokeColorParam: colors[++colorsIndex % colors.length],
        fillColorParam: colors[(colorsIndex + 1) % colors.length],
      );
    });
  }

  Future<void> _toggleVisible(value) async {
    final Circle polygon = _polygons[selectedPolygonId];
    setState(() {
      _polygons[selectedPolygonId] = polygon.copyWith(
        visibleParam: value,
      );
    });
  }

  void _changePoints() {
    final Circle polygon = _polygons[selectedPolygonId];
    // List<LatLng> currentPoints = polygon.points;
    // List<LatLng> newPoints = <LatLng>[];
    // newPoints.addAll(currentPoints);
    // newPoints.add(LatLng(39.828809, 116.360364));

    setState(() {
      _polygons[selectedPolygonId] = polygon.copyWith(
          centerParam: LatLng(31.085242325686117, 119.73216015906159));
    });
  }

  @override
  Widget build(BuildContext context) {
    final AMapWidget map = AMapWidget(
      privacyStatement: ConstConfig.amapPrivacyStatement,
      initialCameraPosition: CameraPosition(
          target: LatLng(31.247593765719817, 121.35745582814145), zoom: 10),
      myLocationStyleOptions: MyLocationStyleOptions(
        true,
        // circleFillColor: Colors.lightBlue,
        // circleStrokeColor: Colors.blue,
        // circleStrokeWidth: 1,
        icon: BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueOrange),
      ),
      scaleEnabled: true,
      onMapCreated: _onMapCreated,
      geofences: Set<Circle>.of(_polygons.values),
      onTap: (l) {
        setPoint(l.latitude, l.longitude);
      },
    );
    return Container(
      height: MediaQuery.of(context).size.height,
      width: MediaQuery.of(context).size.width,
      child: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Container(
            height: MediaQuery.of(context).size.height * 0.6,
            width: MediaQuery.of(context).size.width,
            child: map,
          ),
          Expanded(
            child: SingleChildScrollView(
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: <Widget>[
                  Row(
                    children: <Widget>[
                      Column(
                        children: <Widget>[
                          FlatButton(
                            child: const Text('添加'),
                            onPressed: _add,
                          ),
                          FlatButton(
                            child: const Text('删除'),
                            onPressed:
                                (selectedPolygonId == null) ? null : _remove,
                          ),
                          FlatButton(
                            child: const Text('修改边框宽度'),
                            onPressed: (selectedPolygonId == null)
                                ? null
                                : _changeStrokeWidth,
                          ),
                        ],
                      ),
                      Column(
                        children: <Widget>[
                          FlatButton(
                            child: const Text('修改边框和填充色'),
                            onPressed: (selectedPolygonId == null)
                                ? null
                                : _changeColors,
                          ),
                          AMapSwitchButton(
                            label: Text('显示'),
                            onSwitchChanged: (selectedPolygonId == null)
                                ? null
                                : _toggleVisible,
                            defaultValue: true,
                          ),
                          FlatButton(
                            child: const Text('修改坐标'),
                            onPressed: (selectedPolygonId == null)
                                ? null
                                : _changePoints,
                          ),
                        ],
                      ),
                    ],
                  )
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}
