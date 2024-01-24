package com.hezd.map.platform.bean

import com.amap.api.navi.model.NaviLatLng
import com.tencent.navix.api.model.NavSearchPoint

/**
 * @author hezd
 * @date 2024/1/10 16:54
 * @description GCJ-02 - 国测局坐标
 */
data class GCJLatLng(val latitude: Double, val longitude: Double) {
    /**
     * 转化为高德坐标
     */
    fun toAMapLatLng(): NaviLatLng = NaviLatLng(latitude, longitude)

    /**
     * 转化为腾讯坐标
     */
    fun toTencentMapLatLng(): NavSearchPoint = NavSearchPoint(latitude, longitude)
}
