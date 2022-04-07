package com.amap.flutter.map.overlays.geofence;

import com.amap.api.location.DPoint;
import com.amap.api.maps.model.AMapPara;
import com.amap.api.maps.model.LatLng;
import com.amap.flutter.map.utils.ConvertUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author whm
 * @date 2020/11/12 10:11 AM
 * @mail hongming.whm@alibaba-inc.com
 * @since
 */
class GeoFenceUtil {

    static String interpretOptions(Object o, GeoFenceOptionsSink sink) {
        final Map<?, ?> data = ConvertUtil.toMap(o);
        final Object center = data.get("center");
        if (center != null) {
            sink.setCenter(ConvertUtil.toLatLng(center));
        }

        final Object width = data.get("strokeWidth");
        if (width != null) {
            sink.setStrokeWidth(ConvertUtil.toFloatPixels(width));
        }
        final Object unionJson = data.get("unionJson");
        if (unionJson != null) {
            sink.setUnionJson(ConvertUtil.toString(unionJson));
        }

        final Object radius = data.get("radius");
        if (radius != null) {
            sink.setRadius(ConvertUtil.toFloatPixels(radius));
        }

        final Object strokeColor = data.get("strokeColor");
        if (strokeColor != null) {
            sink.setStrokeColor(ConvertUtil.toInt(strokeColor));
        }

        final Object fillColor = data.get("fillColor");
        if (fillColor != null) {
            sink.setFillColor(ConvertUtil.toInt(fillColor));
        }

        final Object visible = data.get("visible");
        if (visible != null) {
            sink.setVisible(ConvertUtil.toBoolean(visible));
        }

        final Object strokeDottedLineType = data.get("strokeDottedLineType");
        if (strokeDottedLineType != null) {
            sink.setStrokeDottedLineType(AMapPara.LineJoinType.valueOf(ConvertUtil.toInt(strokeDottedLineType)));
        }

        final String geofenceId = (String) data.get("id");
        if (geofenceId == null) {
            throw new IllegalArgumentException("geofenceId was null");
        } else {
            return geofenceId;
        }
    }

    public static List<DPoint> toAMapGeoFenceList(String points) {
        List<DPoint> list = new ArrayList<DPoint>();
        if (points == null || points.equals("")) {
            return list;
        }
        String[] pointArray = points.split(";");
        for (int i = 0; i < pointArray.length; i++) {
            if (pointArray[i] == null || pointArray[i].equals("")) {
                continue;
            }
            String[] point = pointArray[i].split(",");
            try {
                if (point.length != 2) {
                    continue;
                }
                double lng = Double.parseDouble(point[0]);
                double lat = Double.parseDouble(point[1]);
                DPoint dPoint = new DPoint(lat, lng);
                list.add(dPoint);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static List<LatLng> toAMapList(String points) {
        List<LatLng> list = new ArrayList<LatLng>();
        if (points == null || points.equals("")) {
            return list;
        }
        String[] pointArray = points.split(";");

        for (int i = 0; i < pointArray.length; i++) {
            if (pointArray[i] == null || pointArray[i].equals("")) {
                continue;
            }
            String[] point = pointArray[i].split(",");
            try {
                if (point.length != 2) {
                    continue;
                }
                double lat = Double.parseDouble(point[1]);
                double lng = Double.parseDouble(point[0]);
                LatLng latLng = new LatLng(lat, lng);
                list.add(latLng);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return list;
    }


}
