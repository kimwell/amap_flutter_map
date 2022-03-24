package com.amap.flutter.map.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.CustomMapStyleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Poi;
import com.amap.flutter.amap_flutter_map.R;
import com.amap.flutter.map.core.AMapOptionsSink;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.view.FlutterMain;

/**
 * @author whm
 * @date 2020/10/29 11:01 AM
 * @mail hongming.whm@alibaba-inc.com
 * @since
 */
public class ConvertUtil {

    private static final String CLASS_NAME = "ConvertUtil";

    public static float density;
    private static String apiKey;
    private static HashMap<String, BitmapDescriptor> mViewLruCache = new HashMap<>();

    public static void clearLruCache() {
        mViewLruCache.clear();
    }

    public static void setPrivacyStatement(Context context, Object object) {
        if (null == object) {
            return;
        }
        Map<?, ?> privacyStatementMap = toMap(object);
        Object hasContainsObj = privacyStatementMap.get("hasContains");
        Object hasShowObj = privacyStatementMap.get("hasShow");
        Object hasAgreeObj = privacyStatementMap.get("hasAgree");

        Class<MapsInitializer> clazz = MapsInitializer.class;

        if (null != hasContainsObj
                && null != hasShowObj) {
            boolean hasContains = toBoolean(hasContainsObj);
            boolean hasShow = toBoolean(hasShowObj);
            //使用反射的方法调用适配之前的版本
            try {
                Method method = clazz.getMethod("updatePrivacyShow", Context.class, boolean.class, boolean.class);
                method.invoke(null, context, hasContains, hasShow);
            } catch (Throwable e) {
//                e.printStackTrace();
            }
        }

        if (null != hasAgreeObj) {
            boolean hasAgree = toBoolean(hasAgreeObj);
            //使用反射的方法调用适配之前的版本
            try {
                Method method = clazz.getMethod("updatePrivacyAgree", Context.class, boolean.class);
                method.invoke(null, context, hasAgree);
            } catch (Throwable e) {
//                e.printStackTrace();
            }
        }
    }

    public static void checkApiKey(Object object) {
        if (null == object) {
            return;
        }
        Map<?, ?> keyMap = toMap(object);
        Object keyObject = keyMap.get("androidKey");
        if (null != keyObject) {
            final String aKey = toString(keyObject);
            if (TextUtils.isEmpty(apiKey)
                    || !aKey.equals(apiKey)) {
                apiKey = aKey;
                MapsInitializer.setApiKey(apiKey);
            }
        }
    }

    public static int toLocalMapType(int dartMapIndex) {
        int[] localTypeArray = {AMap.MAP_TYPE_NORMAL, AMap.MAP_TYPE_SATELLITE, AMap.MAP_TYPE_NIGHT, AMap.MAP_TYPE_NAVI, AMap.MAP_TYPE_BUS};
        if (dartMapIndex > localTypeArray.length) {
            return localTypeArray[0];
        }
        return localTypeArray[dartMapIndex];
    }

    public static CameraUpdate toCameraUpdate(Object o) {
        final List<?> data = toList(o);
        switch (toString(data.get(0))) {
            case "newCameraPosition":
                return CameraUpdateFactory.newCameraPosition(toCameraPosition(data.get(1)));
            case "newLatLng":
                return CameraUpdateFactory.newLatLng(toLatLng(data.get(1)));
            case "newLatLngBounds":
                return CameraUpdateFactory.newLatLngBounds(
                        toLatLngBounds(data.get(1)), toPixels(data.get(2)));
            case "newLatLngZoom":
                return CameraUpdateFactory.newLatLngZoom(toLatLng(data.get(1)), toFloat(data.get(2)));
            case "scrollBy":
                return CameraUpdateFactory.scrollBy( //
                        toFloatPixels(data.get(1)), //
                        toFloatPixels(data.get(2)));
            case "zoomBy":
                if (data.size() == 2) {
                    return CameraUpdateFactory.zoomBy(toFloat(data.get(1)));
                } else {
                    return CameraUpdateFactory.zoomBy(toFloat(data.get(1)), toPoint(data.get(2)));
                }
            case "zoomIn":
                return CameraUpdateFactory.zoomIn();
            case "zoomOut":
                return CameraUpdateFactory.zoomOut();
            case "zoomTo":
                return CameraUpdateFactory.zoomTo(toFloat(data.get(1)));
            default:
                throw new IllegalArgumentException("Cannot interpret " + o + " as CameraUpdate");
        }
    }


    private static Point toPoint(Object o) {
        final List<?> data = toList(o);
        return new Point(toPixels(data.get(0)), toPixels(data.get(1)));
    }

    public static float toFloatPixels(Object o) {
        return toFloat(o) * density;
    }

    public static int toPixels(Object o) {
        return (int) toFloatPixels(o);
    }

    /**
     * 将一个对象转换成CameraPosition
     *
     * @param o
     * @return
     */
    public static CameraPosition toCameraPosition(Object o) {
        final Map<?, ?> data = (Map<?, ?>) o;
        final CameraPosition.Builder builder = CameraPosition.builder();
        builder.bearing(toFloat(data.get("bearing")));
        builder.target(toLatLng(data.get("target")));
        builder.tilt(toFloat(data.get("tilt")));
        builder.zoom(toFloat(data.get("zoom")));
        return builder.build();
    }

    public static Object cameraPositionToMap(CameraPosition position) {
        if (position == null) {
            return null;
        }
        final Map<String, Object> data = new HashMap<>();
        data.put("bearing", position.bearing);
        data.put("target", latLngToList(position.target));
        data.put("tilt", position.tilt);
        data.put("zoom", position.zoom);
        return data;
    }

    /**
     * 转换AMapOptions
     *
     * @param o
     * @param sink
     */
    public static void interpretAMapOptions(Object o, @NonNull AMapOptionsSink sink) {
        try {
            final Map<?, ?> data = (Map<?, ?>) o;
            final Object mapType = data.get("mapType");
            if (mapType != null) {
                sink.setMapType(toLocalMapType(toInt(mapType)));
            }

            final Object buildingsEnabled = data.get("buildingsEnabled");
            if (null != buildingsEnabled) {
                sink.setBuildingsEnabled(toBoolean(buildingsEnabled));
            }

            final Object customMapStyleOptions = data.get("customStyleOptions");
            if (null != customMapStyleOptions) {
                CustomMapStyleOptions customMapStyleOptions1 = toCustomMapStyleOptions(customMapStyleOptions);
                sink.setCustomMapStyleOptions(customMapStyleOptions1);
            }

            final Object myLocationStyleData = data.get("myLocationStyle");
            if (null != myLocationStyleData) {
                sink.setMyLocationStyle(ConvertUtil.toMyLocationStyle(myLocationStyleData, density));
            }

            final Object screenAnchor = data.get("screenAnchor");
            if (null != screenAnchor) {
                final List<?> anchorData = toList(screenAnchor);
                sink.setScreenAnchor(toFloat(anchorData.get(0)), toFloat(anchorData.get(1)));
            }

            final Object compassEnabled = data.get("compassEnabled");
            if (null != compassEnabled) {
                sink.setCompassEnabled(toBoolean(compassEnabled));
            }

            final Object labelsEnabled = data.get("labelsEnabled");
            if (null != labelsEnabled) {
                sink.setLabelsEnabled(toBoolean(labelsEnabled));
            }

            final Object limitBounds = data.get("limitBounds");
            if (null != limitBounds) {
                final List<?> targetData = toList(limitBounds);
                sink.setLatLngBounds(toLatLngBounds(targetData));
            }

            final Object minMaxZoomPreference = data.get("minMaxZoomPreference");
            if (null != minMaxZoomPreference) {
                final List<?> targetData = toList(minMaxZoomPreference);
                sink.setMinZoomLevel(toFloatWrapperWithDefault(targetData.get(0), 3));
                sink.setMaxZoomLevel(toFloatWrapperWithDefault(targetData.get(1), 20));
            }

            final Object scaleEnabled = data.get("scaleEnabled");
            if (null != scaleEnabled) {
                sink.setScaleEnabled(toBoolean(scaleEnabled));
            }

            final Object touchPoiEnabled = data.get("touchPoiEnabled");
            if (null != touchPoiEnabled) {
                sink.setTouchPoiEnabled(toBoolean(touchPoiEnabled));
            }

            final Object trafficEnabled = data.get("trafficEnabled");
            if (null != trafficEnabled) {
                sink.setTrafficEnabled(toBoolean(trafficEnabled));
            }

            final Object rotateGesturesEnabled = data.get("rotateGesturesEnabled");
            if (null != rotateGesturesEnabled) {
                sink.setRotateGesturesEnabled(toBoolean(rotateGesturesEnabled));
            }

            final Object scrollGesturesEnabled = data.get("scrollGesturesEnabled");
            if (null != scrollGesturesEnabled) {
                sink.setScrollGesturesEnabled(toBoolean(scrollGesturesEnabled));
            }

            final Object tiltGesturesEnabled = data.get("tiltGesturesEnabled");
            if (null != tiltGesturesEnabled) {
                sink.setTiltGesturesEnabled(toBoolean(tiltGesturesEnabled));
            }

            final Object zoomGesturesEnabled = data.get("zoomGesturesEnabled");
            if (null != zoomGesturesEnabled) {
                sink.setZoomGesturesEnabled(toBoolean(zoomGesturesEnabled));
            }
        } catch (Throwable e) {
            LogUtil.e(CLASS_NAME, "interpretAMapOptions", e);
        }
    }

    private static CustomMapStyleOptions toCustomMapStyleOptions(Object o) {
        final Map<?, ?> map = toMap(o);
        final CustomMapStyleOptions customMapStyleOptions = new CustomMapStyleOptions();
        final Object enableData = map.get("enabled");
        if (null != enableData) {
            customMapStyleOptions.setEnable(toBoolean(enableData));
        }

        final Object styleData = map.get("styleData");
        if (null != styleData) {
            customMapStyleOptions.setStyleData((byte[]) styleData);
        }
        final Object styleExtraData = map.get("styleExtraData");
        if (null != styleExtraData) {
            customMapStyleOptions.setStyleExtraData((byte[]) styleExtraData);
        }
        return customMapStyleOptions;
    }

    private static final int[] LocationTypeMap = new int[]{MyLocationStyle.LOCATION_TYPE_SHOW, MyLocationStyle.LOCATION_TYPE_FOLLOW, MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE};

    private static MyLocationStyle toMyLocationStyle(Object o, float density) {
        final Map<?, ?> map = toMap(o);
        final MyLocationStyle myLocationStyle = new MyLocationStyle();
        final Object enableData = map.get("enabled");
        if (null != enableData) {
            myLocationStyle.showMyLocation(toBoolean(enableData));
        }
        //两端差异比较大，Android端设置成跟随但是不移动到中心点模式，与iOS端兼容
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);
//        final Object trackingMode = map.get("trackingMode");
//        if (null != trackingMode) {
//            int trackingModeIndex = toInt(trackingMode);
//            if (trackingModeIndex < LocationTypeMap.length) {
//                myLocationStyle.myLocationType(LocationTypeMap[trackingModeIndex]);
//            }
//        }

        final Object circleFillColorData = map.get("circleFillColor");
        if (null != circleFillColorData) {
            myLocationStyle.radiusFillColor(toInt(circleFillColorData));
        }
        final Object circleStrokeColorData = map.get("circleStrokeColor");
        if (null != circleStrokeColorData) {
            myLocationStyle.strokeColor(toInt(circleStrokeColorData));
        }

        final Object circleStrokeWidthData = map.get("circleStrokeWidth");
        if (null != circleStrokeWidthData) {
            myLocationStyle.strokeWidth(toPixels(circleStrokeWidthData));
        }

        final Object iconDta = map.get("icon");
        if (null != iconDta) {
            myLocationStyle.myLocationIcon(toBitmapDescriptor(iconDta, null));
        }
        return myLocationStyle;
    }

    public static Object location2Map(Location location) {
        if (null == location) {
            return null;
        }

        if (location.getAltitude() > 90 ||
                location.getAltitude() < -90 ||
                location.getLongitude() > 180 ||
                location.getLongitude() < -180) {
            return null;
        }

        final Map<String, Object> object = new HashMap<String, Object>();
        object.put("provider", location.getProvider());
        object.put("latLng", Arrays.asList(location.getLatitude(), location.getLongitude()));
        object.put("accuracy", location.getAccuracy());
        object.put("altitude", location.getAltitude());
        object.put("bearing", location.getBearing());
        object.put("speed", location.getSpeed());
        object.put("time", location.getTime());
        return object;
    }


    @SuppressLint("ResourceType")
    public static BitmapDescriptor toBitmapDescriptor(Object o, Context context) {
        final List<?> data = toList(o);
        switch (toString(data.get(0))) {
            case "defaultMarker":
                if (data.size() == 1) {
                    return BitmapDescriptorFactory.defaultMarker();
                } else {
                    return BitmapDescriptorFactory.defaultMarker(toFloat(data.get(1)));
                }
            case "fromAsset":
                if (data.size() == 2) {
                    return BitmapDescriptorFactory.fromAsset(
                            FlutterMain.getLookupKeyForAsset(toString(data.get(1))));
                } else {
                    return BitmapDescriptorFactory.fromAsset(
                            FlutterMain.getLookupKeyForAsset(toString(data.get(1)), toString(data.get(2))));
                }
            case "fromAssetImage":
                if (data.size() == 3) {
                    return BitmapDescriptorFactory.fromAsset(
                            FlutterMain.getLookupKeyForAsset(toString(data.get(1))));
                } else {
                    throw new IllegalArgumentException(
                            "'fromAssetImage' Expected exactly 3 arguments, got: " + data.size());
                }
            case "fromBytes":
                return getBitmapFromBytes(data);
            case "fromView":
                Log.i("APPLOG", data.get(1).toString());
                if (data.get(1) != null) {
                    HashMap<String, Object> map = (HashMap<String, Object>) data.get(1);
                    if (map.get("title") != null) {   // 直接View 组合实现
                        String text = map.get("title") + "";
                        String desc = map.get("desc") + "";
                        View view;
                        BitmapDescriptor descriptor = mViewLruCache.get(desc.isEmpty() ? text : desc);
                        if (descriptor != null) return descriptor;
                        if (desc.isEmpty()) {
                            view = View.inflate(context, R.layout.mark_layout, null);
                            TextView titleView = view.findViewById(R.id.tv_mark_title);
                            titleView.setText(text);
                            descriptor = BitmapDescriptorFactory.fromView(view);
                            mViewLruCache.put(text, descriptor);
                        } else {
                            view = View.inflate(context, R.layout.mark_circular_layout, null);
                            LinearLayout linear = view.findViewById(R.id.linear);
                            TextView titleView2 = view.findViewById(R.id.tv_title);
                            linear.setBackground(getCircleBg(Color.parseColor("#FFC72C"), titleView2, desc));
                            titleView2.setText(desc);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                titleView2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            }
                            descriptor = BitmapDescriptorFactory.fromView(view);
                            mViewLruCache.put(desc, descriptor);
                        }
                        return descriptor;
                    } else {
                        throw new IllegalArgumentException(
                                "'fromView'  arguments map map.get(\"name\") != null  ");
                    }
                } else {
                    throw new IllegalArgumentException(
                            "'fromView' Expected exactly map arguments, got: " + data);
                }


                // Cavas 实现
//               String text = data.get(1).toString();
//                if (!text.isEmpty()) {
//                    return BitmapDescriptorFactory.fromBitmap( getCircleText(text));
//                } else {
//                    throw new IllegalArgumentException(
//                            "'fromView' Expected exactly 2 arguments, got: " + data.size());
//                }
            default:
                throw new IllegalArgumentException("Cannot interpret " + o + " as BitmapDescriptor");
        }
    }

    /**
     * 获取圆型背景
     *
     * @param colorInt 整型色值
     */
    static GradientDrawable getCircleBg(int colorInt, TextView textView, String text) {
        Paint.FontMetrics fontMetrics2 = textView.getPaint().getFontMetrics();
// 高度
        float tH = fontMetrics2.bottom - fontMetrics2.top;
        // 宽度
        float w = textView.getPaint().measureText(text);
        GradientDrawable it = new GradientDrawable();
        it.setShape(GradientDrawable.OVAL);
        int width = Math.round(Math.max(w, tH)) * 5 / 4;
//        int width = (int) (Math.round(Math.max(w, tH)) * 5 / 4 + Math.round(Math.max(w, tH)) * text.length() * 0.1);
        it.setGradientRadius(width);
        it.setSize(width, width);
        it.setColor(colorInt);
        return it;
    }

    // Cavas 实现
    public static Bitmap getCircleText(String text) {
        int tempValue = Integer.parseInt(text);
        if (tempValue > 200) tempValue = 200;

        int width = tempValue / 2 + 5;
        width = width < 30 ? 30 : width;
        Bitmap bmp = Bitmap.createBitmap(width * 2, width * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        //背景
        Paint bgPaint = new Paint();//创建画笔对象  笔
        bgPaint.setColor(Color.YELLOW);//设置颜色
        bgPaint.setStyle(Paint.Style.FILL);//画笔样式为空心，也可以理解为描边
        bgPaint.setAntiAlias(true);//抗锯齿(去掉锯齿)

        //文字
        Paint tPaint = new Paint();
        tPaint.setColor(Color.BLACK);//设置颜色
        tPaint.setStyle(Paint.Style.FILL);//画笔样式为空心，也可以理解为描边
        tPaint.setStrokeWidth(4);//描边的宽度
        tPaint.setAntiAlias(true);//抗锯齿(去掉锯齿)
        tPaint.setTextAlign(Paint.Align.CENTER);//Paint设置水平居中

        int textSize = 12 * (tempValue / 18);
        //设置alpha不透明度，范围为0~255
        bgPaint.setAlpha(tempValue < 127 ? 127 : tempValue % 255);
        /**
         * 画圆 参数一：圆心的X轴坐标  参数二：圆心的Y轴坐标  参数三：圆的半径  参数四：画笔对象
         */
        canvas.drawCircle(width, width, width, bgPaint);//完成
        tPaint.setTextSize(textSize < 30 ? 30 : textSize);

        Rect bounds = new Rect();
        tPaint.getTextBounds(text, 0, text.length(), bounds);
        float offSet = (bounds.top + bounds.bottom) / 2;
        canvas.drawText(text, width, width - offSet, tPaint);
        canvas.save();
        canvas.restore();
        return bmp;
    }

    /**
     * 生成View的bitmap
     *
     * @param v
     * @param width
     * @param height
     * @return
     */
    public static Bitmap createViewBitmap(View v, int width, int height) {
        //测量使得view指定大小
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        v.measure(measuredWidth, measuredHeight);
        //调用layout方法布局后，可以得到view的尺寸大小
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        Bitmap bmp = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.TRANSPARENT);
        v.draw(c);
        return bmp;
    }

    public static List<BitmapDescriptor> toBitmapDescriptorList(Object o) {
        List<?> rawList = ConvertUtil.toList(o);
        List<BitmapDescriptor> bitmapDescriptorList = new ArrayList<BitmapDescriptor>();
        for (Object obj : rawList) {
            bitmapDescriptorList.add(ConvertUtil.toBitmapDescriptor(obj, null));
        }
        return bitmapDescriptorList;
    }

    private static BitmapDescriptor getBitmapFromBytes(List<?> data) {
        if (data.size() == 2) {
            try {
                Bitmap bitmap = toBitmap(data.get(1));
                return BitmapDescriptorFactory.fromBitmap(bitmap);
            } catch (Exception e) {
                throw new IllegalArgumentException("Unable to interpret bytes as a valid image.", e);
            }
        } else {
            throw new IllegalArgumentException(
                    "fromBytes should have exactly one argument, the bytes. Got: " + data.size());
        }
    }

    private static Bitmap toBitmap(Object o) {
        byte[] bmpData = (byte[]) o;
        Bitmap bitmap = BitmapFactory.decodeByteArray(bmpData, 0, bmpData.length);
        if (bitmap == null) {
            throw new IllegalArgumentException("Unable to decode bytes as a valid bitmap.");
        } else {
            return bitmap;
        }
    }

    public static Object poiToMap(Poi poi) {
        if (null == poi) {
            return null;
        }
        final Map<String, Object> data = new HashMap<>();
        data.put("id", poi.getPoiId());
        data.put("name", poi.getName());
        data.put("latLng", latLngToList(poi.getCoordinate()));
        return data;
    }

    public static Object latLngToList(LatLng latLng) {
        if (null == latLng) {
            return null;
        }
        return Arrays.asList(latLng.latitude, latLng.longitude);
    }

    public static LatLng toLatLng(Object o) {
        final List<?> data = (List<?>) o;
        return new LatLng((Double) data.get(0), (Double) data.get(1));
    }

    public static List<LatLng> toPoints(Object o) {
        final List<?> data = toList(o);
        final List<LatLng> points = new ArrayList<>(data.size());

        for (Object ob : data) {
            final List<?> point = toList(ob);
            points.add(new LatLng(toFloat(point.get(0)), toFloat(point.get(1))));
        }
        return points;
    }

    public static LatLngBounds toLatLngBounds(Object o) {
        if (o == null) {
            return null;
        }
        final List<?> data = toList(o);
        return new LatLngBounds(toLatLng(data.get(0)), toLatLng(data.get(1)));
    }

    private static Float toFloatWrapper(Object o) {
        return (o == null) ? null : toFloat(o);
    }

    public static Float toFloatWrapperWithDefault(Object o, float defaultValue) {
        return (o == null) ? defaultValue : toFloat(o);
    }

    public static boolean toBoolean(Object o) {
        return (Boolean) o;
    }

    public static int toInt(Object o) {
        return ((Number) o).intValue();
    }

    public static double toDouble(Object o) {
        return ((Number) o).doubleValue();
    }

    public static float toFloat(Object o) {
        return ((Number) o).floatValue();
    }

    public static List<?> toList(Object o) {
        return (List<?>) o;
    }

    public static Map<?, ?> toMap(Object o) {
        return (Map<?, ?>) o;
    }

    public static String toString(Object o) {
        return (String) o;
    }

    public static Object getKeyValueFromMapObject(Object object, String keyStr) {
        if (null == object) {
            return null;
        }
        try {
            Map<?, ?> mapData = toMap(object);
            return mapData.get(keyStr);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }


}
