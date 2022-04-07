package com.amap.flutter.map.overlays.geofence;

import com.amap.api.maps.model.AMapPara;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.LatLng;

import java.util.List;

class GeoFenceController implements GeoFenceOptionsSink {

    private final Circle circle;
    private final String id;

    GeoFenceController(Circle cir) {
        this.circle = cir;
        this.id = cir.getId();
    }

    public String getId() {
        return id;
    }

    public void remove() {
        circle.remove();
    }

//    @Override
//    public void setPoints(List<LatLng> points) {
////        circle.setPoints(points);
//    }

    @Override
    public void setCenter(LatLng latLng) {
        circle.setCenter(latLng);
    }

    @Override
    public void setUnionJson(String unionJson) {

    }

    @Override
    public void setStrokeWidth(float strokeWidth) {
        circle.setStrokeWidth(strokeWidth);
    }

    @Override
    public void setStrokeColor(int color) {
        circle.setStrokeColor(color);
    }

    @Override
    public void setFillColor(int color) {
        circle.setFillColor(color);
    }

    @Override
    public void setVisible(boolean visible) {
        circle.setVisible(visible);
    }

    @Override
    public void setStrokeDottedLineType(AMapPara.LineJoinType joinType) {
        //不支持动态修改
        circle.setStrokeDottedLineType(1);
    }

    @Override
    public void setRadius(double radius) {
        circle.setRadius(radius);
    }
}
