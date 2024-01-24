package com.hezd.map.platform

/**
 * @author hezd
 * @date 2024/1/10 15:36
 * @description
 */
interface UiLifecycle {
    fun onStart()
    fun onRestart()
    fun onPause()
    fun onResume()
    fun onStop()
    fun onDestroy()
}