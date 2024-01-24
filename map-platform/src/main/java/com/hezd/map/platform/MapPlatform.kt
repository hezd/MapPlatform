package com.hezd.map.platform

import android.content.Context
import android.os.Bundle
import android.view.View
import com.hezd.map.platform.bean.GCJLatLng
import com.hezd.map.platform.bean.TruckInfo

/**
 * @author hezd
 * @date 2024/1/10 14:10
 * @description
 */
class MapPlatform private constructor(

    /**
     * 导航策略
     */
    private val strategy: MapStrategy,
    private val markOptions: MarkOptions? = null,
) {


    /**
     * 路径规划初始化
     */
    fun init(context: Context, savedInstanceState: Bundle? = null) {
        strategy.init(context, savedInstanceState, markOptions)
    }

    /**
     * 获取地图View
     */
    fun getMapView(context: Context): View = strategy.getMapView(context)

    /**
     * 开始路径规划
     * @param start 起始点经纬度
     * @param end 终点经纬度
     * @param calcRoteCallback 路径规划回调
     */
    fun startCalculatePath(
        start: GCJLatLng,
        end: GCJLatLng,
        calcRoteCallback: MapCalcRouteCallback?,
        truckInfo: TruckInfo? = null,
    ) {
        strategy.startCalculatePath(start, end, truckInfo, calcRoteCallback)
    }


    /**
     * 定位
     */
    fun startLocation() {
        strategy.startLocation()
    }

    /**
     * 导航路线被点击时回调
     */
    fun setOnRouteLineClickListener(onRouteLineClickListener: OnRouteLineClickListener) {
        strategy.setOnRouteLineClickListener(onRouteLineClickListener)
    }

    /**
     * 设置某个路线图层以自适应缩放
     */
    fun setRouteZoomToSpan(index: Int) {
        strategy.setRouteZoomToSpan(index)
    }

    /**
     * 地图放大
     */
    fun setZoomIn() {
        strategy.setZoomIn()
    }

    /**
     * 地图缩小
     */
    fun setZoomOut() {
        strategy.setZoomOut()
    }

    fun onStart() {
        strategy.onStart()
    }

    fun onRestart() {
        strategy.onRestart()
    }

    fun onResume() {
        strategy.onResume()
    }

    fun onPause() {
        strategy.onPause()
    }


    fun onStop() {
        strategy.onStop()
    }

    fun onDestroy() {
        strategy.onDestroy()
    }


    class Builder constructor(private val map: Map) {

        /**
         * 车辆信息json数据
         */
        private var markOptions: MarkOptions? = null

        fun markOptions(markOptions: MarkOptions): Builder {
            this.markOptions = markOptions
            return this
        }

        fun build(): MapPlatform {
            return MapPlatform(map.getPlatForm(), markOptions)
        }
    }
}