package com.android.designpattern.structural.adapter;

public class ObjectAdapter implements IAMap {

    private final TencentMap tencentMap;

    public ObjectAdapter() {
        tencentMap = new TencentMap();
    }

    @Override
    public AMapParams getAMapParams() {
        AMapParams aMapParams = new AMapParams();
        ITencentMap.TencentMapParams tencentMapParams = tencentMap.getTencentMapParams();
        aMapParams.aMapLongitude = tencentMapParams.tencentMapLongitude;
        aMapParams.aMapLatitude = tencentMapParams.tencentMapLatitude;
        return aMapParams;
    }
}
