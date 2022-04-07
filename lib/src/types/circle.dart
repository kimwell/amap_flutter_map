// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import 'package:amap_flutter_base/amap_flutter_base.dart';
import 'package:flutter/material.dart' show Color;

import 'base_overlay.dart';
import 'polyline.dart';

/// 虚线类型
enum ReceiveStatus {
  STATUS_UNKNOWN, //= 0;
  STATUS_IN, //= 1; 进
  STATUS_OUT, //= 2;出
  STATUS_STAYED, //= 3;待着
  STATUS_LOCFAIL, //= 4;//定位失败
}

/// 圆相关的覆盖物类，内部的属性，描述了覆盖物的纹理、颜色、线宽等特征
class Circle extends BaseOverlay {
  /// 默认构造函数
  Circle({
    // this.points,
    required this.unionJson,
    this.center,
    this.strokeDottedLineType = DashLineType.none,
    double strokeWidth = 10,
    this.radius,
    this.strokeColor = const Color(0xcc3f91fc),
    this.fillColor = const Color(0x3376d4f3),
    this.visible = true,
  })  : this.strokeWidth = (strokeWidth <= 0 ? 10 : strokeWidth),
        super();

  /// 边框宽度,单位为逻辑像素，同Android中的dp，iOS中的point
  final double strokeWidth;

  /// 边框颜色,默认值为(0xCCC4E0F0)
  final Color strokeColor;

  /// 填充颜色,默认值为(0xC4E0F0CC)
  final Color fillColor;

  /// 是否可见
  final bool visible;

  /// 中点
  final LatLng? center;

  // // 地理围栏地点s
  // final List<LatLng>? points;

  /// 虚线类型
  final DashLineType strokeDottedLineType;

  final double? radius;

  // 唯一用来区分的对象
  final String unionJson;

  /// 实际copy函数
  Circle copyWith(
      {String? unionJsonParam,
      LatLng? centerParam,
      // List<LatLng>? pointsParam,
      double? strokeWidthParam,
      double? radiusParam,
      Color? strokeColorParam,
      Color? fillColorParam,
      bool? visibleParam,
      DashLineType? strokeDottedLineTypeParam}) {
    Circle copyPolyline = Circle(
      unionJson: unionJsonParam ?? unionJson,
      // points: pointsParam ?? points,
      center: centerParam ?? center,
      strokeWidth: strokeWidthParam ?? strokeWidth,
      strokeColor: strokeColorParam ?? strokeColor,
      fillColor: fillColorParam ?? fillColor,
      visible: visibleParam ?? visible,
      radius: radiusParam ?? radius,
      strokeDottedLineType: strokeDottedLineTypeParam ?? strokeDottedLineType,
    );
    copyPolyline.setIdForCopy(id);
    return copyPolyline;
  }

  Circle clone() => copyWith();

  /// 转换成可以序列化的map
  @override
  Map<String, dynamic> toMap() {
    final Map<String, dynamic> json = <String, dynamic>{};

    void addIfPresent(String fieldName, dynamic value) {
      if (value != null) {
        json[fieldName] = value;
      }
    }

    addIfPresent('id', id);
    // json['points'] = _pointsToJson(points);
    addIfPresent('unionJson', unionJson);
    addIfPresent('center', center?.toJson());
    addIfPresent('strokeWidth', strokeWidth);
    addIfPresent('strokeColor', strokeColor.value);
    addIfPresent('fillColor', fillColor.value);
    addIfPresent('visible', visible);
    addIfPresent('strokeDottedLineType', strokeDottedLineType.index);
    addIfPresent('radius', radius);
    return json;
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;
    if (other.runtimeType != runtimeType) return false;
    if (other is! Circle) return false;
    final Circle typedOther = other;
    return id == typedOther.id &&
        // listEquals(points, typedOther.points) &&
        strokeWidth == typedOther.strokeWidth &&
        strokeColor == typedOther.strokeColor &&
        fillColor == typedOther.fillColor &&
        visible == typedOther.visible &&
        center == typedOther.center &&
        unionJson == typedOther.unionJson &&
        radius == typedOther.radius &&
        strokeDottedLineType == typedOther.strokeDottedLineType;
  }

  @override
  int get hashCode => super.hashCode;

// dynamic _pointsToJson(points) {
//   final List<dynamic> result = <dynamic>[];
//   if (points != null) {
//     for (final LatLng point in points) {
//       result.add(point.toJson());
//     }
//   }
//   return result;
// }
}

Map<String, Circle> keyByCircleId(Iterable<Circle> circles) {
  // ignore: unnecessary_null_comparison
  if (circles == null) {
    return <String, Circle>{};
  }
  return Map<String, Circle>.fromEntries(circles.map(
      (Circle circle) => MapEntry<String, Circle>(circle.id, circle.clone())));
}
