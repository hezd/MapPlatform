package com.hezd.map.platform

import androidx.annotation.DrawableRes

/**
 * @author hezd
 * @date 2024/1/19 17:35
 * @description
 */
class MarkOptions private constructor(
    /**
     * 起始点marker图标
     */
    @DrawableRes val startIconId: Int = R.mipmap.app_icon_start_point,

    /**
     * 目的地marker图标
     */
    @DrawableRes val endIconId: Int = R.mipmap.app_icon_end_point,
    /**
     * 当前定位点marker图标
     */
    @DrawableRes val locationIconId: Int = R.mipmap.ic_location,
) {

    class Builder {
        /**
         * 起始点marker图标
         */
        private var startIconId: Int = R.mipmap.app_icon_start_point

        /**
         * 目的地marker图标
         */
        private var endIconId: Int = R.mipmap.app_icon_end_point

        /**
         * 当前定位点marker图标
         */
        private var locationIconId: Int = R.mipmap.ic_location

        fun startIconId(@DrawableRes startIconId: Int): Builder {
            this.startIconId = startIconId
            return this
        }

        fun destIconId(@DrawableRes destIconId: Int): Builder {
            this.endIconId = destIconId
            return this
        }

        fun locationIconId(@DrawableRes locationIconId: Int): Builder {
            this.locationIconId = locationIconId;
            return this
        }

        fun build(): MarkOptions {
            return MarkOptions(startIconId, endIconId, locationIconId)
        }
    }
}