package com.amap.flutter.map.overlays.geofence;

import com.amap.api.maps.model.AMapPara;
import com.amap.api.maps.model.LatLng;

import java.util.List;

interface GeoFenceOptionsSink {
//    void setPoints(List<LatLng> points);
    // 设置中点
    void setCenter(LatLng latLng);

    void setUnionJson(String unionJson);

    // 边框线的类型
    void setStrokeDottedLineType(AMapPara.LineJoinType lineType);

    // 设置角度
    void setRadius(double radius);
    //边框宽度
    void setStrokeWidth(float strokeWidth);

    //边框颜色
    void setStrokeColor(int color);

    //填充颜色
    void setFillColor(int color);

    //是否显示
    void setVisible(boolean visible);

}
