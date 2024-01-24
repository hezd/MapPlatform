package com.hezd.test.map

import android.app.Application
import com.tencent.navix.api.NavigatorConfig
import com.tencent.navix.api.NavigatorZygote
import com.tencent.tencentmap.mapsdk.maps.TencentMapInitializer

/**
 * @author hezd
 * @date 2024/1/23 18:00
 * @description
 */
class MapPlatformApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // 设置同意地图SDK隐私协议
        TencentMapInitializer.setAgreePrivacy(this, true)

        // 初始化导航SDK
        NavigatorZygote.with(this).init(
            NavigatorConfig.builder() // 设置同意导航SDK隐私协议
                .setUserAgreedPrivacy(true) // 设置自定义的可区分设备的ID
                .setDeviceId("tyt_custom_id_123456")
                .experiment().setUseSharedMap(false) // 单实例有泄漏问题，使用多实例
                .build())
    }
}