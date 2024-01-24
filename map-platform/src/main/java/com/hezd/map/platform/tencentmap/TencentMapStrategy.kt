package com.hezd.map.platform.tencentmap

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hezd.map.platform.CalcRoteError
import com.hezd.map.platform.MapCalcRouteCallback
import com.hezd.map.platform.MapStrategy
import com.hezd.map.platform.MarkOptions
import com.hezd.map.platform.OnRouteLineClickListener
import com.hezd.map.platform.R
import com.hezd.map.platform.bean.GCJLatLng
import com.hezd.map.platform.bean.PathInfo
import com.hezd.map.platform.bean.TruckInfo
import com.hezd.map.platform.formatSToHMStr
import com.tencent.map.geolocation.TencentLocation
import com.tencent.map.geolocation.TencentLocationListener
import com.tencent.map.geolocation.TencentLocationManager
import com.tencent.navix.api.NavigatorZygote
import com.tencent.navix.api.layer.NavigatorLayerRootDrive
import com.tencent.navix.api.layer.NavigatorViewStub
import com.tencent.navix.api.model.NavDriveRoute
import com.tencent.navix.api.model.NavError
import com.tencent.navix.api.model.NavRoutePlan
import com.tencent.navix.api.model.NavRouteReqParam
import com.tencent.navix.api.model.NavSearchPoint
import com.tencent.navix.api.navigator.NavigatorDrive
import com.tencent.navix.api.observer.SimpleNavigatorDriveObserver
import com.tencent.navix.api.plan.DriveRoutePlanOptions
import com.tencent.navix.api.plan.DriveRoutePlanRequestCallback
import com.tencent.navix.api.plan.RoutePlanRequester
import com.tencent.navix.ui.NavigatorLayerViewDrive
import com.tencent.tencentmap.mapsdk.maps.model.LatLng


/**
 * @author hezd
 * @date 2024/1/11 18:16
 * @description
 */
class TencentMapStrategy : MapStrategy {
    private var navigatorDrive: NavigatorDrive? = null
    private var layerRootDrive: NavigatorLayerRootDrive? = null
    private var layerViewDrive: NavigatorLayerViewDrive? = null
    private var routeView: RouteView? = null
    private var mapView: View? = null
    private var locationManager: TencentLocationManager? = null

    private val driveObserver: SimpleNavigatorDriveObserver =
        object : SimpleNavigatorDriveObserver() {
            override fun onWillArriveDestination() {
                super.onWillArriveDestination()
                if (navigatorDrive != null) {
                    navigatorDrive!!.stopNavigation()
                }
            }
        }

    @SuppressLint("InflateParams")
    override fun init(context: Context, savedInstanceState: Bundle?,markOptions: MarkOptions?) {

        // 创建驾车 NavigatorDrive
        navigatorDrive = NavigatorZygote.with(context.applicationContext).navigator(
            NavigatorDrive::class.java
        )

        // 创建导航地图层 NavigatorLayerRootDrive
        mapView = LayoutInflater.from(context).inflate(R.layout.layout_nav, null)
        val navigatorViewStub = mapView?.findViewById<NavigatorViewStub>(R.id.navigator_view_stub)
        navigatorViewStub?.setTravelMode(NavRouteReqParam.TravelMode.TravelModeDriving)
        navigatorViewStub?.inflate()
        layerRootDrive = navigatorViewStub?.getNavigatorView()

        // 创建默认面板 NavigatorLayerViewDrive，并添加到导航地图层
        layerViewDrive = NavigatorLayerViewDrive(context)
        layerRootDrive?.addViewLayer(layerViewDrive)

        // 将导航地图层绑定到Navigator
        navigatorDrive?.bindView(layerRootDrive)

        // 注册导航监听
        navigatorDrive?.registerObserver(driveObserver)

        initRouteView(context,markOptions)

        // 单次定位
        locationManager = TencentLocationManager.getInstance(context.applicationContext, null)
    }

    private fun initRouteView(context: Context,markOptions: MarkOptions?) {
        routeView = RouteView(context, null)
        routeView?.injectMap(layerRootDrive!!.mapApi,markOptions)
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
        routeView?.removeMarker()
        routeView?.clear()
        val routePlanRequesterBuilder =
            RoutePlanRequester.Companion.newBuilder(NavRouteReqParam.TravelMode.TravelModeDriving)
                .start(NavSearchPoint(start.latitude, start.longitude))
                .end(NavSearchPoint(end.latitude, end.longitude))

        if (truckInfo != null) {
            val truckType = when (truckInfo.carType) {
                "0" -> DriveRoutePlanOptions.TruckOptions.TruckType.Unknown
                "1" -> DriveRoutePlanOptions.TruckOptions.TruckType.Mini
                "2" -> DriveRoutePlanOptions.TruckOptions.TruckType.Light
                "3" -> DriveRoutePlanOptions.TruckOptions.TruckType.Medium
                "4" -> DriveRoutePlanOptions.TruckOptions.TruckType.Heavy
                else -> DriveRoutePlanOptions.TruckOptions.TruckType.Heavy
            }
            val plateColor = when (truckInfo.licensePlateColor) {
                "0" -> DriveRoutePlanOptions.TruckOptions.PlateColor.Unknown
                "1" -> DriveRoutePlanOptions.TruckOptions.PlateColor.Blue
                "2" -> DriveRoutePlanOptions.TruckOptions.PlateColor.Yellow
                "3" -> DriveRoutePlanOptions.TruckOptions.PlateColor.Black
                "4" -> DriveRoutePlanOptions.TruckOptions.PlateColor.White
                "5" -> DriveRoutePlanOptions.TruckOptions.PlateColor.Green
                "6" -> DriveRoutePlanOptions.TruckOptions.PlateColor.YellowGreen
                else -> DriveRoutePlanOptions.TruckOptions.PlateColor.Yellow
            }
            val emissionStandards = when (truckInfo.emissionStandards) {
                "0" -> DriveRoutePlanOptions.TruckOptions.EmissionStandard.Unknown
                "1" -> DriveRoutePlanOptions.TruckOptions.EmissionStandard.I
                "2" -> DriveRoutePlanOptions.TruckOptions.EmissionStandard.II
                "3" -> DriveRoutePlanOptions.TruckOptions.EmissionStandard.III
                "4" -> DriveRoutePlanOptions.TruckOptions.EmissionStandard.IV
                "5" -> DriveRoutePlanOptions.TruckOptions.EmissionStandard.V
                "6" -> DriveRoutePlanOptions.TruckOptions.EmissionStandard.VI
                else -> DriveRoutePlanOptions.TruckOptions.EmissionStandard.V
            }
            routePlanRequesterBuilder
                .options(
                    DriveRoutePlanOptions.Companion.newBuilder()
                        .licenseNumber(truckInfo.carNumber) // 车牌号
                        .truckOptions(
                            DriveRoutePlanOptions.TruckOptions.newBuilder()
                                .setHeight(truckInfo.truckHigh?.toFloat() ?: 0f) // 设置货车高度。单位：m
                                .setLength(truckInfo.truckLong?.toFloat() ?: 0f) // 设置货车长度。单位：m
                                .setWidth(truckInfo.truckWidth?.toFloat() ?: 0f) // 设置货车宽度。单位：m
                                .setWeight(truckInfo.truckWeight?.toFloat() ?: 0f) // 设置货车重量。单位：t
                                .setAxisCount(truckInfo.axesNumber?.toInt() ?: 0) // 设置货车轴数
//                                .setAxisLoad(
//                                    4f
//                                ) // 设置货车轴重。单位：t
                                .setPlateColor(plateColor) // 设置车牌颜色。
                                .setTrailerType(DriveRoutePlanOptions.TruckOptions.TrailerType.Container) // 设置是否是拖挂车。
                                .setTruckType(truckType) // 设置货车类型。
//                                .setEmissionStandard(emissionStandards) // 设置排放标准
                                .setPassType(DriveRoutePlanOptions.TruckOptions.PassType.NoNeed) // 设置通行证。
                                .setEnergyType(DriveRoutePlanOptions.TruckOptions.EnergyType.Diesel) // 设置能源类型。
                                .setFunctionType(DriveRoutePlanOptions.TruckOptions.FunctionType.Normal) // 设置
                                .build()
                        )
                        .build()
                )
        }

        val routePlanRequester = routePlanRequesterBuilder.build()
        navigatorDrive?.searchRoute(routePlanRequester,
            DriveRoutePlanRequestCallback { navRoutePlan: NavRoutePlan<NavDriveRoute?>?, error: NavError? ->
                if (error != null) {
                    // handle error
                    calcRoteCallback?.onError(CalcRoteError(error.errorCode, error.message))
                    return@DriveRoutePlanRequestCallback
                }
                if (navRoutePlan != null) {
                    // handle result
                    routeView?.let {
                        it.updateRoutePlan(navRoutePlan)
                        val routes = navRoutePlan.routes
                        val paths = routes.map {
                            it ?: return@map null
                            val title = it.tag
                            val time = formatSToHMStr(it.getTime() * 60)
                            val distance = it.distance / 1000
                            val distanceInfo = "${distance}公里  ¥${it.fee}"

                            return@map PathInfo(
                                title = title,
                                navTime = time,
                                distanceInfo = distanceInfo
                            )
                        }.filterNotNull()
                        calcRoteCallback?.onSuccess(paths)
                    }
                }
            })
    }

    override fun startLocation() {
        routeView?.clear()
        locationManager?.requestSingleLocationFresh(object : TencentLocationListener {
            override fun onLocationChanged(
                tencentLocation: TencentLocation,
                errorCode: Int,
                reason: String,
            ) {
                if (errorCode == TencentLocation.ERROR_OK) {
                    // 获取到位置信息，更新地图
                    val currentLatLng = LatLng(tencentLocation.latitude, tencentLocation.longitude)
                    // 在地图上添加标记表示当前位置
                    addMarker(currentLatLng)
                }
            }

            override fun onStatusUpdate(p0: String?, p1: Int, p2: String?) {
            }

            override fun onGnssInfoChanged(p0: Any?) {
            }

            override fun onNmeaMsgChanged(p0: String?) {
            }

        }, Looper.getMainLooper())
    }

    private fun addMarker(currentLatLng: LatLng) {
        // 获取当前位置坐标，这里假设定位成功后获取到经纬度
        routeView?.showLocation(currentLatLng)

    }

    /**
     * 腾讯地图demo中是不支持，需要在调研一下，暂时空实现
     */
    override fun setOnRouteLineClickListener(onRouteLineClickListener: OnRouteLineClickListener)  {
        routeView?.setOnRouteLineClickListener(onRouteLineClickListener)
    }

    override fun setRouteZoomToSpan(index: Int) {
        routeView?.selectRoute(index)
    }

    override fun setZoomIn() {
        routeView?.zoomIn()
    }

    override fun setZoomOut() {
        routeView?.zoomOut()
    }

    override fun onStart() {
        layerRootDrive?.onStart()
    }

    override fun onRestart() {
        layerRootDrive?.onRestart()
    }

    override fun onPause() {
        layerRootDrive?.onPause()
    }

    override fun onResume() {
        layerRootDrive?.onResume()
    }

    override fun onStop() {
        layerRootDrive?.onStop()
    }

    override fun onDestroy() {
        layerRootDrive?.onDestroy()

        // 移除导航监听
        navigatorDrive?.unregisterObserver(driveObserver)

        // 移除默认面板
        layerRootDrive?.removeViewLayer(layerViewDrive)

        // 解绑导航地图
        navigatorDrive?.unbindView(layerRootDrive)
        // 移除mapView
        (mapView?.parent as ViewGroup).removeView(mapView)

        // 关闭导航
        navigatorDrive?.stopNavigation()

        // 移除监听器
        routeView?.removeOnPolyLineClickListener()
        layerRootDrive = null
        navigatorDrive = null
        routeView = null
        mapView = null
    }

}