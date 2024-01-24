package com.hezd.map.platform

import android.content.Context
import android.os.Bundle
import android.view.View
import com.hezd.map.platform.bean.GCJLatLng
import com.hezd.map.platform.bean.TruckInfo

/**
 * @author hezd
 * @date 2024/1/10 14:27
 * @description 路径规划策略
 */
interface MapStrategy : UiLifecycle {
    /**
     * 初始化
     */
    fun init(context: Context, savedInstanceState: Bundle? = null,markOptions: MarkOptions?)

    /**
     * 获取地图View
     */
    fun getMapView(context: Context): View

    /**
     * 开始路径规划
     */
    fun startCalculatePath(
        start: GCJLatLng,
        end: GCJLatLng,
        truckInfo: TruckInfo?=null,
        calcRoteCallback: MapCalcRouteCallback?,
    )

    /**
     * 开始定位
     */
    fun startLocation()

    /**
     * 导航路线被点击时回调
     */
    fun setOnRouteLineClickListener(onRouteLineClickListener: OnRouteLineClickListener)

    /**
     * 设置某个路线图层以自适应缩放
     */
    fun setRouteZoomToSpan(index:Int)

    /**
     * 地图放大
     */
    fun setZoomIn()

    /**
     * 地图缩小
     */
    fun setZoomOut()
}