package com.amap.flutter.map.overlays.geofence;

import com.amap.api.maps.model.AMapPara;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;

import java.util.List;

/**
 * @author whm
 * @date 2020/11/12 9:51 AM
 * @mail hongming.whm@alibaba-inc.com
 * @since
 */
class GeoFenceOptionsBuilder implements GeoFenceOptionsSink {
    final CircleOptions circleOptions;
     String unionJson;

    GeoFenceOptionsBuilder() {
        circleOptions = new CircleOptions();
        //必须设置为true，否则会出现线条转折处出现断裂的现象
        circleOptions.usePolylineStroke(true);
    }

    public CircleOptions build() {
        return circleOptions;
    }

    public String getUnionJson() {
        return unionJson;
    }

    public void setUnionJson(String unionJson) {
        this.unionJson = unionJson;
    }

    @Override
    public void setStrokeWidth(float strokeWidth) {
        circleOptions.strokeWidth(strokeWidth);
    }

    @Override
    public void setStrokeColor(int color) {
        circleOptions.strokeColor(color);
    }

    @Override
    public void setFillColor(int color) {
        circleOptions.fillColor(color);
    }

    @Override
    public void setVisible(boolean visible) {
        circleOptions.visible(visible);
    }

//    @Override
//    public void setPoints(List<LatLng> points) {
//    }

    @Override
    public void setCenter(LatLng latLng) {
        circleOptions.center(latLng);
    }

    @Override
    public void setStrokeDottedLineType(AMapPara.LineJoinType lineType) {
        circleOptions.setStrokeDottedLineType(lineType.getTypeValue());

    }

    @Override
    public void setRadius(double radius) {
circleOptions.radius(radius);
    }
}
