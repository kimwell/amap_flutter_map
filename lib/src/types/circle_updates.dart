// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import 'dart:ui' show hashValues;

import 'package:amap_flutter_map/src/types/circle.dart';
import 'package:flutter/foundation.dart' show setEquals;

import 'types.dart';

/// 该类主要用以描述[Circle]的增删改等更新操作
class CircleUpdates {
  /// 通过Circle的前后更新集合构造一个CircleUpdates
  CircleUpdates.from(Set<Circle> previous, Set<Circle> current) {
    // ignore: unnecessary_null_comparison
    if (previous == null) {
      previous = Set<Circle>.identity();
    }

    // ignore: unnecessary_null_comparison
    if (current == null) {
      current = Set<Circle>.identity();
    }

    final Map<String, Circle> previousCircles = keyByCircleId(previous);
    final Map<String, Circle> currentCircles = keyByCircleId(current);

    final Set<String> prevCircleIds = previousCircles.keys.toSet();
    final Set<String> currentCircleIds = currentCircles.keys.toSet();

    Circle idToCurrentCircle(String id) {
      return currentCircles[id]!;
    }

    final Set<String> _circleIdsToRemove =
        prevCircleIds.difference(currentCircleIds);

    final Set<Circle> _circlesToAdd = currentCircleIds
        .difference(prevCircleIds)
        .map(idToCurrentCircle)
        .toSet();

    bool hasChanged(Circle current) {
      final Circle previous = previousCircles[current.id]!;
      return current != previous;
    }

    final Set<Circle> _circlesToChange = currentCircleIds
        .intersection(prevCircleIds)
        .map(idToCurrentCircle)
        .where(hasChanged)
        .toSet();

    circlesToAdd = _circlesToAdd;
    circleIdsToRemove = _circleIdsToRemove;
    circlesToChange = _circlesToChange;
  }

  /// 想要添加的Circle对象集合.
  Set<Circle>? circlesToAdd;

  /// 想要删除的Circle的id集合
  Set<String>? circleIdsToRemove;

  /// 想要更新的Circle对象集合
  Set<Circle>? circlesToChange;

  /// 转换成可以序列化的map
  Map<String, dynamic> toMap() {
    final Map<String, dynamic> updateMap = <String, dynamic>{};

    void addIfNonNull(String fieldName, dynamic value) {
      if (value != null) {
        updateMap[fieldName] = value;
      }
    }

    addIfNonNull('circlesToAdd', serializeOverlaySet(circlesToAdd!));
    addIfNonNull('circlesToChange', serializeOverlaySet(circlesToChange!));
    addIfNonNull('circleIdsToRemove', circleIdsToRemove?.toList());
    addIfNonNull('circlesRemoveAll', circleIdsToRemove?.toList());

    return updateMap;
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;
    if (other.runtimeType != runtimeType) return false;
    if (other is! CircleUpdates) return false;
    final CircleUpdates typedOther = other;
    return setEquals(circlesToAdd, typedOther.circlesToAdd) &&
        setEquals(circleIdsToRemove, typedOther.circleIdsToRemove) &&
        setEquals(circlesToChange, typedOther.circlesToChange);
  }

  @override
  int get hashCode =>
      hashValues(circlesToAdd, circleIdsToRemove, circlesToChange);

  @override
  String toString() {
    return '_CircleUpdates{CirclesToAdd: $circlesToAdd, '
        'CircleIdsToRemove: $circleIdsToRemove, '
        'CirclesToChange: $circlesToChange}';
  }
}
