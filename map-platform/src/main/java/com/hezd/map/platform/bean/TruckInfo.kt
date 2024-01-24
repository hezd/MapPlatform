package com.hezd.map.platform.bean

import com.amap.api.navi.model.AMapCarInfo

/**
 * @author hezd
 * @date 2024/1/10 18:21
 * @description 货车信息
 */

data class TruckInfo @JvmOverloads constructor(
    /**
     *  Unknown,
     *  Mini,
     *  Light,
     *  Medium,
     *  Heavy;
     * 车辆类型，0未，1迷你型 2轻型 3中型 4重型
     */
    val carType: String?,
    /**
     * 车牌号
     */
    val carNumber: String?,
    /**
     * 货车长度
     */
    val truckLong: String?,
    /**
     * 货车宽
     */
    val truckWidth: String?,
    /**
     * 货车高
     */
    val truckHigh: String?,
    /**
     * 总重量
     */
    val truckWeight: String?,
    /**
     * 货车核定载重
     */
    val truckApprovedLoad: String?,
    /**
     * 轴数
     */
    val axesNumber: String?,
    /**
     * Unknown,
     * I,
     * II,
     * III,
     * IV,
     * V,
     * VI;
     * 排放标准 0未知 1国一 2国二 3国三 4国四 5国五 6国六
     */
    val emissionStandards: String?,
    /**
     *   Unknown,
     *   Blue,
     *   Yellow,
     *   Black,
     *   White,
     *   Green,
     *   YellowGreen;
     * 车牌颜色 0未知 1蓝牌 2黄牌 3黑牌 4白牌 5绿牌 6黄绿牌
     */
    val licensePlateColor: String? = null,
    /**
     * 货车类型
     * 例如高德1-微型货车 2-轻型/小型货车 3-中型货车 4-重型货车 默认取值4，目前不可更改
     * 腾讯地图枚举类型 微型，轻型，中型，重型，没有默认值需要选择
     */
    val mVehicleSize: String?,

    ) {
    fun toAMapCarInfo(): AMapCarInfo {
        return AMapCarInfo().apply {
            carType = this@TruckInfo.carType
            carNumber = this@TruckInfo.carNumber
            vehicleSize = "4"
            vehicleLoad = this@TruckInfo.truckWeight
            vehicleWeight = this@TruckInfo.truckApprovedLoad
            vehicleLength = this@TruckInfo.truckLong
            vehicleWidth = this@TruckInfo.truckWidth
            vehicleHeight = this@TruckInfo.truckHigh
            vehicleAxis = this@TruckInfo.axesNumber
            isVehicleLoadSwitch = true
            isRestriction = true
        }
    }

    class Builder {
        /**
         * 车辆类型，0小车，1货车
         */
        private var carType: String? = null

        /**
         * 车牌号
         */
        private var carNumber: String? = null

        /**
         * 货车长度
         */
        private var truckLong: String? = null

        /**
         * 货车宽
         */
        private var truckWidth: String? = null

        /**
         * 货车高
         */
        private var truckHigh: String? = null

        /**
         * 总重量
         */
        private var truckWeight: String? = null

        /**
         * 货车核定载重
         */
        private var truckApprovedLoad: String? = null

        /**
         * 轴数
         */
        private var axesNumber: String? = null

        /**
         * 排放标准
         */
        private var emissionStandards: String? = null

        /**
         * 车牌颜色
         */
        private var licensePlateColor: String? = null

        /**
         * 货车类型
         * 例如高德1-微型货车 2-轻型/小型货车 3-中型货车 4-重型货车 默认取值4，目前不可更改
         * 腾讯地图枚举类型 微型，轻型，中型，重型，没有默认值需要选择
         */
        private var mVehicleSize: String? = null

        fun carType(carType: String): Builder {
            this.carType = carType
            return this
        }

        fun carNumber(carNumber: String): Builder {
            this.carNumber = carNumber
            return this
        }

        fun truckLong(truckLong: String): Builder {
            this.truckLong = truckLong
            return this
        }

        fun truckWidth(truckWidth: String): Builder {
            this.truckWidth = truckWidth
            return this
        }

        fun truckHigh(truckHigh: String): Builder {
            this.truckHigh = truckHigh
            return this
        }


        fun truckWeight(truckWeight: String): Builder {
            this.truckWeight = truckWeight
            return this
        }

        fun truckApprovedLoad(truckApprovedLoad: String): Builder {
            this.truckApprovedLoad = truckApprovedLoad
            return this
        }

        fun axesNumber(axesNumber: String): Builder {
            this.axesNumber = axesNumber
            return this
        }

        fun emissionStandards(emissionStandards: String): Builder {
            this.emissionStandards = emissionStandards
            return this
        }

        fun licensePlateColor(licensePlateColor: String): Builder {
            this.licensePlateColor = licensePlateColor
            return this
        }

        fun mVehicleSize(mVehicleSize: String): Builder {
            this.mVehicleSize = mVehicleSize
            return this
        }

        fun build(): TruckInfo {
            return TruckInfo(
                carType = carType,
                carNumber = carNumber,
                truckLong = truckLong,
                truckWidth = truckWidth,
                truckHigh = truckHigh,
                truckWeight = truckWeight,
                truckApprovedLoad = truckApprovedLoad,
                axesNumber = axesNumber,
                emissionStandards = emissionStandards,
                licensePlateColor = licensePlateColor,
                mVehicleSize = mVehicleSize
            )
        }
    }

}
