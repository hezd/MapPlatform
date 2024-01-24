package com.hezd.test.map

import android.os.Bundle
import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.hezd.map.platform.CalcRoteError
import com.hezd.map.platform.Map
import com.hezd.map.platform.MapCalcRouteCallback
import com.hezd.map.platform.MapPlatform
import com.hezd.map.platform.bean.GCJLatLng
import com.hezd.map.platform.bean.PathInfo
import com.hezd.test.map.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mapPlatform: MapPlatform

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initMap(savedInstanceState)

    }

    private fun initMap(savedInstanceState: Bundle?) {
        mapPlatform = MapPlatform.Builder(Map.TencentMap())
            .build()
        mapPlatform.init(this,savedInstanceState)
        val mapView = mapPlatform.getMapView(this)
        val layoutParams = LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT)
        mapView.layoutParams = layoutParams
        binding.mapLayout.addView(mapView)

        mapPlatform.startCalculatePath(GCJLatLng(40.007056,116.389895), GCJLatLng(39.894551,116.321515),object :
            MapCalcRouteCallback {
            override fun onSuccess(paths: List<PathInfo>) {
                if(paths.size>0){
                    mapPlatform.setRouteZoomToSpan(0)
                }
            }

            override fun onError(calcRoteError: CalcRoteError) {
                calcRoteError.errorCode
            }

        })
    }

    override fun onStart() {
        super.onStart()
        mapPlatform.onStart()
    }

    override fun onRestart() {
        super.onRestart()
        mapPlatform.onRestart()
    }

    override fun onResume() {
        super.onResume()
        mapPlatform.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapPlatform.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapPlatform.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapPlatform.onDestroy()

    }
}