package com.hezd.map.platform.amap

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.navi.AMapNavi
import com.amap.api.navi.AMapNaviListener
import com.amap.api.navi.enums.PathPlanningStrategy
import com.amap.api.navi.model.AMapCalcRouteResult
import com.amap.api.navi.model.AMapCarInfo
import com.amap.api.navi.model.RouteOverlayOptions
import com.amap.api.navi.view.RouteOverLay
import com.hezd.map.platform.MapCalcRouteCallback
import com.hezd.map.platform.MapStrategy
import com.hezd.map.platform.MarkOptions
import com.hezd.map.platform.OnRouteLineClickListener
import com.hezd.map.platform.R
import com.hezd.map.platform.ROUTE_SELECTED_TRANSPARENCY
import com.hezd.map.platform.ROUTE_UNSELECTED_TRANSPARENCY
import com.hezd.map.platform.TAG
import com.hezd.map.platform.bean.GCJLatLng
import com.hezd.map.platform.bean.PathInfo
import com.hezd.map.platform.bean.TruckInfo
import com.hezd.map.platform.formatSToHMStr
import kotlin.math.floor

/**
 * @author hezd
 * @date 2024/1/10 14:30
 * @description 高德路径规划
 */
class AMapStrategy : MapStrategy, AMapLocationListener {
    private var mapView: MapView? = null
    private var aMap: AMap? = null
    private lateinit var aMapNavi: AMapNavi
    private lateinit var locationClientSingle: AMapLocationClient
    private var route1OverLay: RouteOverLay? = null
    private var route2OverLay: RouteOverLay? = null
    private var route3OverLay: RouteOverLay? = null
    private var locationMarker: Marker? = null
    private var aMapNaviListener: AMapNaviListener? = null

    override fun init(context: Context, savedInstanceState: Bundle?, markOptions: MarkOptions?) {
        mapView = MapView(context)
        mapView?.onCreate(savedInstanceState)
        aMap = mapView?.map

        aMapNavi = AMapNavi.getInstance(context.applicationContext)

        locationClientSingle = AMapLocationClient(context.applicationContext)
        val locationClientSingleOption = AMapLocationClientOption()
        locationClientSingleOption.setOnceLocation(true)
        locationClientSingleOption.setLocationCacheEnable(false)
        locationClientSingle.setLocationOption(locationClientSingleOption)
        locationClientSingle.setLocationListener(this)

        route1OverLay = RouteOverLay(aMap, null, context)
        route2OverLay = RouteOverLay(aMap, null, context)
        route3OverLay = RouteOverLay(aMap, null, context)

        val overlayOptions = RouteOverlayOptions()
        overlayOptions.lineWidth = 70f

        val startBitmap = if (markOptions == null) BitmapFactory.decodeResource(
            context.resources,
            R.mipmap.app_icon_start_point
        ) else {
            BitmapFactory.decodeResource(context.resources, markOptions.startIconId)
        }
        val endBitmap = if (markOptions == null) BitmapFactory.decodeResource(
            context.resources,
            R.mipmap.app_icon_end_point
        ) else {
            BitmapFactory.decodeResource(context.resources, markOptions.endIconId)
        }
        route1OverLay?.setStartPointBitmap(startBitmap)
        route1OverLay?.setEndPointBitmap(endBitmap)
        route2OverLay?.setStartPointBitmap(startBitmap)
        route2OverLay?.setEndPointBitmap(endBitmap)
        route3OverLay?.setStartPointBitmap(startBitmap)
        route3OverLay?.setEndPointBitmap(endBitmap)

        route1OverLay?.showRouteStart(false)
        route2OverLay?.showRouteStart(false)
        route3OverLay?.showRouteStart(false)

        route1OverLay?.showRouteEnd(false)
        route2OverLay?.showRouteEnd(false)
        route3OverLay?.showRouteEnd(false)

        route1OverLay?.routeOverlayOptions = overlayOptions
        route2OverLay?.routeOverlayOptions = overlayOptions
        route3OverLay?.routeOverlayOptions = overlayOptions

        route1OverLay?.showForbiddenMarker(false)
        route2OverLay?.showForbiddenMarker(false)
        route3OverLay?.showForbiddenMarker(false)

    }

    override fun getMapView(context: Context): View {
        if (mapView == null)
            throw RuntimeException("map view not initialization,invoke init method first")
        return mapView!!
    }

    override fun startCalculatePath(
        start: GCJLatLng,
        end: GCJLatLng,
        truckInfo: TruckInfo?,
        calcRoteCallback: MapCalcRouteCallback?,
    ) {
        clearRouteOverlay()
        val aMapStart = start.toAMapLatLng()
        val aMapEnd = end.toAMapLatLng()

        aMapNaviListener = object : AMapNaviAdaptListener() {
            override fun onCalculateRouteFailure(error: AMapCalcRouteResult) {
                Log.e(
                    TAG,
                    "amap calculate error,code=${error.errorCode},message=${error.errorDescription}"
                )
            }

            override fun onCalculateRouteSuccess(result: AMapCalcRouteResult) {
                val pathList = aMapNavi.naviPaths.map { it.value }.map {
                    val title = it.labels
                    val time = formatSToHMStr(it.allTime)
                    val length = (it.allLength / 1000.0 * 10).toInt() / 10.0
                    val distance = floor(length).toInt().toString()
                    val formatDistance = distance + "公里  ¥" + it.tollCost
                    PathInfo(title, time, formatDistance)
                }
                drawNaviPath()
                calcRoteCallback?.onSuccess(pathList)
            }

            private fun drawNaviPath() {
                val naviPaths = aMapNavi.naviPaths.values.toList()
                if (naviPaths.isNotEmpty()) {
                    route1OverLay?.aMapNaviPath = naviPaths[0]
                    route1OverLay?.setTransparency(1f)
                    route1OverLay?.setLightsVisible(false)
                    route1OverLay?.addToMap()
                    route1OverLay?.zoomToSpan()
                }

                if (naviPaths.size >= 2) {
                    route2OverLay?.aMapNaviPath = naviPaths[1]
                    route2OverLay?.setTransparency(0.3f)
                    route2OverLay?.setLightsVisible(false)
                    route2OverLay?.addToMap()
                    route2OverLay?.zoomToSpan()
                }

                if (naviPaths.size >= 3) {
                    route3OverLay?.aMapNaviPath = naviPaths[2]
                    route3OverLay?.setTransparency(0.3f)
                    route3OverLay?.setLightsVisible(false)
                    route3OverLay?.addToMap()
                    route3OverLay?.zoomToSpan()
                }
            }

        }
        aMapNavi.addAMapNaviListener(aMapNaviListener)
        val aMapCarInfo: AMapCarInfo
        if (truckInfo == null) {
            aMapCarInfo = AMapCarInfo().apply {
                carType = "0" // 设置车辆类型，0小车，1货车
            }
        } else {
            aMapCarInfo = truckInfo.toAMapCarInfo()
        }
        aMapNavi.setCarInfo(aMapCarInfo)
        aMapNavi.calculateDriveRoute(
            arrayListOf(aMapStart), arrayListOf(aMapEnd), null,
            PathPlanningStrategy.DRIVING_MULTIPLE_ROUTES_DEFAULT
        )
    }

    override fun startLocation() {
        locationClientSingle.startLocation()
    }

    private fun setOverlayTransparency(route1: Float, route2: Float, route3: Float) {
        route1OverLay?.setTransparency(route1)
        route2OverLay?.setTransparency(route2)
        route3OverLay?.setTransparency(route3)
    }

    override fun setOnRouteLineClickListener(onRouteLineClickListener: OnRouteLineClickListener) {
        aMap?.setOnPolylineClickListener {
            if (route1OverLay?.polylineIdList?.contains(it.id) == true) {
                onRouteLineClickListener.onClick(0)
            } else if (route2OverLay?.polylineIdList?.contains(it.id) == true) {
                onRouteLineClickListener.onClick(1)
            } else if (route2OverLay?.polylineIdList?.contains(it.id) == true) {
                onRouteLineClickListener.onClick(2)
            }
        }
    }

    override fun setRouteZoomToSpan(index: Int) {
        when (index) {
            0 -> {
                route1OverLay?.zoomToSpan()
                setOverlayTransparency(
                    ROUTE_SELECTED_TRANSPARENCY,
                    ROUTE_UNSELECTED_TRANSPARENCY,
                    ROUTE_UNSELECTED_TRANSPARENCY
                )
            }

            1 -> {
                route2OverLay?.zoomToSpan()
                setOverlayTransparency(
                    ROUTE_UNSELECTED_TRANSPARENCY,
                    ROUTE_SELECTED_TRANSPARENCY,
                    ROUTE_UNSELECTED_TRANSPARENCY
                )
            }

            2 -> {
                route3OverLay?.zoomToSpan()
                setOverlayTransparency(
                    ROUTE_UNSELECTED_TRANSPARENCY,
                    ROUTE_UNSELECTED_TRANSPARENCY,
                    ROUTE_SELECTED_TRANSPARENCY
                )

            }
        }
    }

    override fun setZoomIn() {
        aMap?.animateCamera(CameraUpdateFactory.zoomIn())
    }

    override fun setZoomOut() {
        aMap?.animateCamera(CameraUpdateFactory.zoomOut())
    }

    override fun onStart() = Unit
    override fun onRestart() = Unit
    override fun onStop() = Unit

    private fun clearRouteOverlay() {
        route1OverLay?.removeFromMap()
        route2OverLay?.removeFromMap()
        route3OverLay?.removeFromMap()

    }

    override fun onPause() {
        mapView?.onPause()
    }

    override fun onResume() {
        mapView?.onResume()
    }

    override fun onDestroy() {
        mapView?.onDestroy()
        mapView = null

        route1OverLay?.destroy()
        route1OverLay = null
        route2OverLay?.destroy()
        route2OverLay = null
        route3OverLay?.destroy()
        route3OverLay = null

        aMapNavi.removeAMapNaviListener(aMapNaviListener)
        aMapNavi.stopNavi()
        AMapNavi.destroy()

        locationClientSingle.onDestroy()

    }

    /**
     * 定位回调
     */
    override fun onLocationChanged(amapLocation: AMapLocation) {
        if (amapLocation.errorCode == 0) {
            val mCurrentLatLng = LatLng(amapLocation.latitude, amapLocation.longitude)
            if (locationMarker == null) {
                locationMarker = aMap?.addMarker(
                    MarkerOptions().position(mCurrentLatLng).icon(
                        BitmapDescriptorFactory.fromResource(R.mipmap.ic_location)
                    )
                )
            } else {
                locationMarker?.position = mCurrentLatLng
            }
            aMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, 15f))
        }
    }


}