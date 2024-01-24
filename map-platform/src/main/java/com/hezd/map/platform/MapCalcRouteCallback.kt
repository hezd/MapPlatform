package com.hezd.map.platform

import com.hezd.map.platform.bean.PathInfo

/**
 * @author hezd
 * @date 2024/1/10 17:15
 * @description 路径规划回调
 */
interface MapCalcRouteCallback {

    fun onSuccess(paths: List<PathInfo>)

    fun onError(calcRoteError: CalcRoteError)
}