package com.hezd.map.platform.tencentmap

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.hezd.map.platform.MarkOptions
import com.hezd.map.platform.OnRouteLineClickListener
import com.hezd.map.platform.R
import com.tencent.navix.api.map.MapApi
import com.tencent.navix.api.model.NavRoutePlan
import com.tencent.navix.core.NavigatorContext
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory
import com.tencent.tencentmap.mapsdk.maps.TencentMap
import com.tencent.tencentmap.mapsdk.maps.TencentMap.OnPolylineClickListener
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory
import com.tencent.tencentmap.mapsdk.maps.model.LatLng
import com.tencent.tencentmap.mapsdk.maps.model.LatLngBounds
import com.tencent.tencentmap.mapsdk.maps.model.Marker
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions
import com.tencent.tencentmap.mapsdk.maps.model.OverlayLevel
import com.tencent.tencentmap.mapsdk.maps.model.Polyline
import com.tencent.tencentmap.mapsdk.maps.model.PolylineOptions

class RouteView(context: Context, attrs: AttributeSet?) :
    LinearLayout(context, attrs) {


    private var selectIndex = 0
    private var marker: Marker? = null

    private lateinit var tencentMap: MapApi
    private var plan: NavRoutePlan<*>? = null
    private var markOptions: MarkOptions? = null
    private val DP_20 =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            20f,
            NavigatorContext.share().applicationContext.resources.displayMetrics
        ).toInt()

    private val ROUTE_COLOR_MAIN: Int = 0xFF00CC66.toInt()
    private val ROUTE_COLOR_MAIN_STROKE: Int = 0xFF009449.toInt()
    private val ROUTE_COLOR_BACKUP: Int = 0xFFAFDBC7.toInt()
    private val ROUTE_COLOR_BACKUP_STROKE: Int = 0xFF8BB8A3.toInt()

    private var onPolylineClickListener: TencentMap.OnPolylineClickListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_route, this)

    }

    fun injectMap(map: MapApi, markOptions: MarkOptions? = null) {
        tencentMap = map
        this.markOptions = markOptions
    }

    fun setOnRouteLineClickListener(onRouteLineClickListener: OnRouteLineClickListener) {
        removeOnPolyLineClickListener()
        onPolylineClickListener =
            OnPolylineClickListener { polyLine, _ ->
                polylineMap.onEachIndexed { index, entry ->
                    if (entry.value == polyLine) {
                        selectIndex = index
                        drawRoute()
                        onRouteLineClickListener.onClick(index)
                        return@OnPolylineClickListener
                    }
                }
            }
        tencentMap.addOnPolylineClickListener(onPolylineClickListener)
    }

    fun removeOnPolyLineClickListener() {
        onPolylineClickListener?.let {
            tencentMap.removeOnPolylineClickListener(it)
        }
    }

    fun currentIndex(): Int {
        return selectIndex
    }

    fun selectRoute(index: Int) {
        selectIndex = index
        drawRoute()
    }

    fun updateRoutePlan(routePlan: NavRoutePlan<*>?) {

        plan = routePlan
        selectIndex = 0
        drawRoute()
    }

    fun clear() {
        polylineMap.forEach {
            it.value.remove()
        }

        polylineMap.clear()

        startMarker?.remove()
        startMarker = null

        endMarker?.remove()
        endMarker = null
    }


    private val polylineMap = mutableMapOf<Int, Polyline>()
    private var startMarker: Marker? = null
    private var endMarker: Marker? = null

    private fun drawRoute() {

        clear()

        plan?.apply {
            val startIconId = markOptions?.startIconId ?: R.mipmap.app_icon_start_point
            val endIconId = markOptions?.endIconId ?: R.mipmap.app_icon_end_point
            startMarker = tencentMap.addMarker(
                MarkerOptions(LatLng(startPoi.latitude, startPoi.longitude))
                    .icon(
                        BitmapDescriptorFactory.fromBitmap(
                            BitmapFactory.decodeResource(
                                resources,
                                startIconId
                            )
                        )
                    )
            )
            endMarker = tencentMap.addMarker(
                MarkerOptions(LatLng(endPoi.latitude, endPoi.longitude))
                    .icon(
                        BitmapDescriptorFactory.fromBitmap(
                            BitmapFactory.decodeResource(
                                resources,
                                endIconId
                            )
                        )
                    )
            )
        }

        val builder = LatLngBounds.Builder()
        plan?.routeDatas?.forEachIndexed { index, routeData ->

            var zIndex = 100
            val indexes = intArrayOf(0, routeData.routePoints.size)
            var colors = intArrayOf(ROUTE_COLOR_BACKUP, ROUTE_COLOR_BACKUP)
            var borderColors = intArrayOf(ROUTE_COLOR_BACKUP_STROKE, ROUTE_COLOR_BACKUP_STROKE)
            if (index == selectIndex) {
                colors = intArrayOf(ROUTE_COLOR_MAIN, ROUTE_COLOR_MAIN)
                borderColors = intArrayOf(ROUTE_COLOR_MAIN_STROKE, ROUTE_COLOR_MAIN_STROKE)
                zIndex = 200
            }

            builder.include(routeData.routePoints)

            val options = PolylineOptions()
            options.addAll(routeData.routePoints)
                .arrow(true)
                .arrowTexture(
                    BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(
                            resources,
                            R.mipmap.app_arrow_texture
                        )
                    )
                )
                .color(Color.GREEN)
                .lineType(0)
                .arrowSpacing(150)
                .zIndex(zIndex)
                .level(OverlayLevel.OverlayLevelAboveBuildings)
                .width(32f)
                .clickable(true)
                .borderWidth(4f)
                .borderColors(borderColors)
                .colors(colors, indexes)

            polylineMap[index] = tencentMap.addPolyline(options)
        }

        tencentMap.moveCamera(
            CameraUpdateFactory.newLatLngBoundsRect(
                builder.build(),
                DP_20,
                DP_20,
                DP_20,
                DP_20
            )
        )
    }

    fun showLocation(latLng: LatLng) {
        clear()
        // 创建 MarkerOptions 对象
        val markerOptions: MarkerOptions = MarkerOptions(latLng)
            .title("Current Location")
            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_location))


        // 添加 Marker 到地图上
        marker = tencentMap.addMarker(markerOptions)
        tencentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

    fun zoomIn() {
        tencentMap.moveCamera(CameraUpdateFactory.zoomIn())
    }

    fun zoomOut() {
        tencentMap.moveCamera(CameraUpdateFactory.zoomOut())
    }

    fun removeMarker() {
        marker?.remove()
    }

}