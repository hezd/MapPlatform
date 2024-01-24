package com.hezd.map.platform

import com.hezd.map.platform.amap.AMapStrategy
import com.hezd.map.platform.tencentmap.TencentMapStrategy

/**
 * @author hezd
 * @date 2024/1/12 14:30
 * @description 地图平台
 */
sealed class Map : IMap {
    open class AMap : Map() {
        override fun getPlatForm() = AMapStrategy()
    }

    open class TencentMap : Map() {
        override fun getPlatForm(): MapStrategy = TencentMapStrategy()
    }
}

interface IMap {
    fun getPlatForm(): MapStrategy
}