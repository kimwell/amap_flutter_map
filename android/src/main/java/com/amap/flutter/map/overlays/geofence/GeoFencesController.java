package com.amap.flutter.map.overlays.geofence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.amap.api.fence.GeoFence;
import com.amap.api.fence.GeoFenceClient;
import com.amap.api.fence.GeoFenceListener;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.DPoint;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.flutter.map.MyMethodCallHandler;
import com.amap.flutter.map.overlays.AbstractOverlayController;
import com.amap.flutter.map.utils.Const;
import com.amap.flutter.map.utils.ConvertUtil;
import com.amap.flutter.map.utils.LogUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class GeoFencesController
        extends AbstractOverlayController<GeoFenceController>
        implements MyMethodCallHandler, LocationSource, AMapLocationListener {

    private static final String CLASS_NAME = "GeoFencesController";
    private static final String TAG = "GeoFencesController";

    private GeoFenceClient mClientAllAction;
    // 记录已经添加成功的围栏
    private final ConcurrentMap<String, GeoFence> fenceMap = new ConcurrentHashMap<String, GeoFence>();
    private final ConcurrentMap<String, String> customIdMap = new ConcurrentHashMap<String, String>();
    // 地理围栏的广播action
//    static final String GEOFENCE_BROADCAST_ACTION = "com.amap.geofence";
    static final String GEOFENCE_BROADCAST_ACTION = "com.amap.flutter.amap_flutter_map";

    private final ConcurrentMap mCustomEntitys;
    private final ConcurrentMap mCustomFences;

    public GeoFencesController(MethodChannel methodChannel, AMap amap, Context context) {
        super(methodChannel, amap, context);
        mCustomEntitys = new ConcurrentHashMap<String, Object>();
        mCustomFences = new ConcurrentHashMap<String, Object>();
        addReceiver();
    }

    @Override
    public void doMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        String methodId = call.method;
        LogUtil.i(CLASS_NAME, "doMethodCall===>" + methodId);
        switch (methodId) {
            case Const.METHOD_CIRCLE_UPDATE:
                invokePolylineOptions(call, result);
                break;
            case Const.METHOD_MAP_CLEAR_CIRCLE:
                removeAll(call, result);
                break;
        }
    }

    private void addFenceAll() {//.getApplicationContext()
        mClientAllAction = new GeoFenceClient(mContext);
        mClientAllAction.createPendingIntent(GEOFENCE_BROADCAST_ACTION);
        mClientAllAction.setActivateAction(GeoFenceClient.GEOFENCE_IN | GeoFenceClient.GEOFENCE_STAYED | GeoFenceClient.GEOFENCE_OUT);
    }

    private void drawCircle(GeoFence fence, GeoFenceOptionsBuilder builder, String dartId) {
        Log.d("LG", "drawCircledrawCircle添加围栏:" + fence.getFenceId());
        CircleOptions option = builder.build();
        Circle circle = amap.addCircle(option);
        GeoFenceController geoFenceController = new GeoFenceController(circle);
        mCustomEntitys.put(fence.getFenceId(), circle);

        controllerMapByDartId.put(dartId, geoFenceController);
        idMapByOverlyId.put(circle.getId(), dartId);
        Log.d("LG", "drawCircledrawCircle添加围栏2222:" + fence.getFenceId());
    }


    public void drawFenceToMap(GeoFenceOptionsBuilder builder, String dartId) {
        Iterator iter = fenceMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            GeoFence val = (GeoFence) entry.getValue();
            if (!mCustomFences.containsKey(dartId)) {
                Log.d("LG", "添加围栏:" + key);
//                drawFence(val, builder, dartId);
                drawCircle(val, builder, dartId);
            }
        }

    }

    public void removeAll(MethodCall methodCall, MethodChannel.Result result) {
        try {
            mClientAllAction.removeGeoFence();
            mCustomEntitys.clear();
            mCustomFences.clear();
            idMapByOverlyId.clear();
            controllerMapByDartId.clear();
            result.success(true);
        } catch (Exception e) {
            e.printStackTrace();
            result.success(false);
        }
    }

    public void removeReceiver() {
        try {
            mContext.unregisterReceiver(mGeoFenceReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param methodCall
     * @param result
     */
    public void invokePolylineOptions(MethodCall methodCall, MethodChannel.Result result) {
        if (null == methodCall) {
            return;
        }
        Object listToAdd = methodCall.argument("circlesToAdd");
        addByList((List<Object>) listToAdd);
        Object listToChange = methodCall.argument("circlesToChange");
        updateByList((List<Object>) listToChange);
        Object listIdToRemove = methodCall.argument("circleIdsToRemove");
        removeByIdList((List<Object>) listIdToRemove);
        result.success(null);
    }

    public void addByList(List<Object> circlesToAdd) {
        if (circlesToAdd.isEmpty()) return;
        Log.i(TAG, "addByList: " + circlesToAdd.size());
        if (circlesToAdd != null) {
            for (Object circleToAdd : circlesToAdd) {
                add(circleToAdd);
            }
        }
    }

    private void addReceiver() {
        IntentFilter fliter = new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION);
        fliter.addAction(GEOFENCE_BROADCAST_ACTION);
        mContext.getApplicationContext().registerReceiver(mGeoFenceReceiver, fliter);
    }

    private void add(Object geoFenceObj) {
        if (null != amap) {
//            addAmapLocation();
            final GeoFenceOptionsBuilder builder = new GeoFenceOptionsBuilder();
            final String dartId = GeoFenceUtil.interpretOptions(geoFenceObj, builder);
            if (!TextUtils.isEmpty(dartId)) {
                addFenceAll();
                if (!mCustomEntitys.containsKey(builder.unionJson)) {
                    CircleOptions option = builder.build();
                    DPoint point = new DPoint(option.getCenter().longitude, option.getCenter().latitude);
                    mClientAllAction.addGeoFence(point, 100F, builder.unionJson);
                    mCustomEntitys.put(builder.unionJson, dartId);

                    mClientAllAction.setGeoFenceListener(new GeoFenceListener() {
                        @Override
                        public void onGeoFenceCreateFinished(List<GeoFence> geoFenceList, int errorCode, String s) {
                            if (errorCode == GeoFence.ADDGEOFENCE_SUCCESS) {
                                for (GeoFence fence : geoFenceList) {
                                    Log.e(CLASS_NAME, "fenid:" + fence.getFenceId() + " customID:" + s + " " + fenceMap.containsKey(fence.getFenceId()));
                                    fenceMap.putIfAbsent(fence.getFenceId(), fence);
                                    customIdMap.putIfAbsent(fence.getFenceId(), s);
                                }
                                Log.e(CLASS_NAME, "回调添加成功个数:" + geoFenceList.size());
                                Log.e(CLASS_NAME, "回调添加围栏个数:" + fenceMap.size());
                                drawFenceToMap(builder, dartId);
                                Log.e(CLASS_NAME, "添加围栏成功！！");
                            } else {
                                Log.e(CLASS_NAME, "添加围栏失败！！！！ errorCode: " + errorCode);
                            }
                        }
                    });
                }

            }
        }
    }

    private void updateByList(List<Object> overlaysToChange) {
        if (overlaysToChange != null) {
            for (Object overlayToChange : overlaysToChange) {
                update(overlayToChange);
            }
        }
    }

    private void update(Object toUpdate) {
        Object dartId = ConvertUtil.getKeyValueFromMapObject(toUpdate, "id");
        if (null != dartId) {
            GeoFenceController controller = controllerMapByDartId.get(dartId);
            if (null != controller) {
                GeoFenceUtil.interpretOptions(toUpdate, controller);
            }
        }
    }

    private void removeByIdList(List<Object> toRemoveIdList) {
        if (toRemoveIdList == null) {
            return;
        }
        for (Object toRemoveId : toRemoveIdList) {
            if (toRemoveId == null) {
                continue;
            }
            String dartId = (String) toRemoveId;
            final GeoFenceController controller = controllerMapByDartId.remove(dartId);
            if (controller != null) {
                idMapByOverlyId.remove(controller.getId());
                controller.remove();
            }
        }
    }

    private final BroadcastReceiver mGeoFenceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "围栏围栏围栏围栏围栏围栏");

            // 接收广播
            if (intent.getAction().equals(GEOFENCE_BROADCAST_ACTION)) {
                Bundle bundle = intent.getExtras();
                String fenceID = bundle
                        .getString(GeoFence.BUNDLE_KEY_FENCEID);
                int status = bundle.getInt(GeoFence.BUNDLE_KEY_FENCESTATUS);
                int code = bundle.getInt(GeoFence.BUNDLE_KEY_LOCERRORCODE);
                Log.e(TAG, "定位失败" + code);
                Log.e(TAG, "当前状态" + status);
                StringBuffer sb = new StringBuffer();
                switch (status) {
                    case GeoFence.STATUS_LOCFAIL:
                        sb.append("定位失败");
                        Log.e(TAG, "定位失败" + code);
                        break;
                    case GeoFence.STATUS_IN:
                        sb.append("进入围栏 ").append(fenceID);
                        Log.e(TAG, "进入围栏" + fenceID);
                        break;
                    case GeoFence.STATUS_OUT:
                        sb.append("离开围栏 ").append(fenceID);
                        Log.e(TAG, "离开围栏" + fenceID);
                        break;
                    case GeoFence.STATUS_STAYED:
                        sb.append("停留在围栏内 ").append(fenceID);
                        break;
                    default:
                        break;
                }
                String str = sb.toString();
                Log.e(TAG, "围栏---" + str);
                String s = customIdMap.get(fenceID);
                Map<String, Object> obj = new HashMap<>();
                obj.put("status", status);
                obj.put("content", s);
                methodChannel.invokeMethod(Const.METHOD_GEOFENCE_RECEIVE, obj);

//                Message msg = Message.obtain();
//                msg.obj = str;
//                msg.what = 2;
//                handler.sendMessage(msg);
            }
        }
    };


    @Override
    public String[] getRegisterMethodIdArray() {
        return Const.METHOD_ID_LIST_FOR_CIRCLE;
    }

    private void drawFence(GeoFence fence, GeoFenceOptionsBuilder builder, String dartId) {
        switch (fence.getType()) {
            case GeoFence.TYPE_ROUND:
            case GeoFence.TYPE_AMAPPOI:
                drawCircle(fence, builder, dartId);
                break;
            case GeoFence.TYPE_POLYGON:
            case GeoFence.TYPE_DISTRICT:
//                drawPolygon(fence);
                break;
            default:
                break;
        }

        // 设置所有maker显示在当前可视区域地图中
//        LatLngBounds bounds = boundsBuilder.build();
//        mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
//        polygonPoints.clear();
//        removeMarkers();
    }

    private void addAmapLocation() {
//        UiSettings uiSettings = amap.getUiSettings();
//        if (uiSettings != null) {
//            uiSettings.setRotateGesturesEnabled(false);
//            uiSettings.setMyLocationButtonEnabled(true); // 设置默认定位按钮是否显示
//        }
        amap.setLocationSource(this);// 设置定位监听
//        amap.setMyLocationStyle(
//                new MyLocationStyle().radiusFillColor(Color.argb(0, 0, 0, 0))
//                        .strokeColor(Color.argb(0, 0, 0, 0)).myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.yd)));
        amap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
//        amap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
//        amap.moveCamera(CameraUpdateFactory.zoomTo(13));
    }

    private AMapLocationClient mlocationClient;
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClientOption mLocationOption;

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": "
                        + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
//                mFenceResult.setVisibility(View.VISIBLE);
//                mFenceResult.setText(errText);
            }
        }/**/
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            try {
                mlocationClient = new AMapLocationClient(mContext);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mLocationOption = new AMapLocationClientOption();
            // 设置定位监听
            mlocationClient.setLocationListener(this);
            // 设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

            // 设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            mlocationClient.startLocation();
        }

    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

}
