package com.amap.flutter.map.overlays;

import android.content.Context;

import com.amap.api.maps.AMap;

import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.MethodChannel;

/**
 * @author whm
 * @date 2020/11/10 7:42 PM
 * @mail hongming.whm@alibaba-inc.com
 * @since
 */
public abstract class AbstractOverlayController<T> {
    protected final Map<String, T> controllerMapByDartId;
    protected final Map<String, String> idMapByOverlyId;
    protected final MethodChannel methodChannel;
    protected final AMap amap;
    protected final Context mContext;
    public AbstractOverlayController(MethodChannel methodChannel, AMap amap,Context context){
        this.methodChannel = methodChannel;
        this.amap = amap;
        this.mContext = context;
        controllerMapByDartId = new HashMap<String, T>(12);
        idMapByOverlyId = new HashMap<String, String>(12);
    }
}
