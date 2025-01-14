/*
 * Copyright Statement:
 *
 *   This software/firmware and related documentation ("MediaTek Software") are
 *   protected under relevant copyright laws. The information contained herein is
 *   confidential and proprietary to MediaTek Inc. and/or its licensors. Without
 *   the prior written permission of MediaTek inc. and/or its licensors, any
 *   reproduction, modification, use or disclosure of MediaTek Software, and
 *   information contained herein, in whole or in part, shall be strictly
 *   prohibited.
 *
 *   MediaTek Inc. (C) 2017. All rights reserved.
 *
 *   BY OPENING THIS FILE, RECEIVER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND AGREES
 *   THAT THE SOFTWARE/FIRMWARE AND ITS DOCUMENTATIONS ("MEDIATEK SOFTWARE")
 *   RECEIVED FROM MEDIATEK AND/OR ITS REPRESENTATIVES ARE PROVIDED TO RECEIVER
 *   ON AN "AS-IS" BASIS ONLY. MEDIATEK EXPRESSLY DISCLAIMS ANY AND ALL
 *   WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
 *   WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 *   NONINFRINGEMENT. NEITHER DOES MEDIATEK PROVIDE ANY WARRANTY WHATSOEVER WITH
 *   RESPECT TO THE SOFTWARE OF ANY THIRD PARTY WHICH MAY BE USED BY,
 *   INCORPORATED IN, OR SUPPLIED WITH THE MEDIATEK SOFTWARE, AND RECEIVER AGREES
 *   TO LOOK ONLY TO SUCH THIRD PARTY FOR ANY WARRANTY CLAIM RELATING THERETO.
 *   RECEIVER EXPRESSLY ACKNOWLEDGES THAT IT IS RECEIVER'S SOLE RESPONSIBILITY TO
 *   OBTAIN FROM ANY THIRD PARTY ALL PROPER LICENSES CONTAINED IN MEDIATEK
 *   SOFTWARE. MEDIATEK SHALL ALSO NOT BE RESPONSIBLE FOR ANY MEDIATEK SOFTWARE
 *   RELEASES MADE TO RECEIVER'S SPECIFICATION OR TO CONFORM TO A PARTICULAR
 *   STANDARD OR OPEN FORUM. RECEIVER'S SOLE AND EXCLUSIVE REMEDY AND MEDIATEK'S
 *   ENTIRE AND CUMULATIVE LIABILITY WITH RESPECT TO THE MEDIATEK SOFTWARE
 *   RELEASED HEREUNDER WILL BE, AT MEDIATEK'S OPTION, TO REVISE OR REPLACE THE
 *   MEDIATEK SOFTWARE AT ISSUE, OR REFUND ANY SOFTWARE LICENSE FEES OR SERVICE
 *   CHARGE PAID BY RECEIVER TO MEDIATEK FOR SUCH MEDIATEK SOFTWARE AT ISSUE.
 *
 *   The following software/firmware and/or related documentation ("MediaTek
 *   Software") have been modified by MediaTek Inc. All revisions are subject to
 *   any receiver's applicable license agreements with MediaTek Inc.
 */
package com.water.camera.feature.setting.shutterspeed;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.hardware.camera2.CameraCharacteristics;

import com.water.camera.common.debug.LogHelper;
import com.water.camera.common.debug.LogUtil;
import com.water.camera.common.device.CameraDeviceManagerFactory.CameraApi;
import com.water.camera.common.loader.DeviceDescription;
import com.water.camera.common.loader.FeatureEntryBase;
import com.water.camera.common.setting.ICameraSetting;

import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

/**
 * Shutter speed entry.
 */
public class ShutterSpeedEntry extends FeatureEntryBase {
    private static final LogUtil.Tag TAG = new LogUtil.Tag(ShutterSpeedEntry.class.getSimpleName());
    private String mCameraId = "0";

    /**
     * create an entry.
     *
     * @param context   current activity.
     * @param resources current resources.
     */
    public ShutterSpeedEntry(Context context, Resources resources) {
        super(context, resources);
    }

    @Override
    public void notifyBeforeOpenCamera(@Nonnull String cameraId, @Nonnull CameraApi cameraApi) {
        super.notifyBeforeOpenCamera(cameraId, cameraApi);
        mCameraId = cameraId;
        LogHelper.d(TAG, "[notifyBeforeOpenCamera] mCameraId = " + mCameraId);

    }

    @Override
    public boolean isSupport(CameraApi currentCameraApi, Activity activity) {
        switch (currentCameraApi) {
            case API1:
                return !isThirdPartyIntent(activity);
            case API2:
                return !isThirdPartyIntent(activity) && isSupportInAPI2(mCameraId);
            default:
                return false;
        }
    }

    @Override
    public String getFeatureEntryName() {
        return ShutterSpeedEntry.class.getName();
    }

    @Override
    public Class getType() {
        return ICameraSetting.class;
    }

    @Override
    public Object createInstance() {
        return new ShutterSpeed();
    }

    private boolean isSupportInAPI2(String cameraId) {
        ConcurrentHashMap<String, DeviceDescription>
                deviceDescriptionMap = mDeviceSpec.getDeviceDescriptionMap();
        if (cameraId == null || deviceDescriptionMap == null || deviceDescriptionMap.size() <= 0) {
            LogHelper.w(TAG, "[isSupportInAPI2] cameraId = " + cameraId + ",deviceDescriptionMap " +
                    "" + deviceDescriptionMap);
            return false;
        }
        if (!deviceDescriptionMap.containsKey(cameraId)) {
            LogHelper.w(TAG, "[isSupportInAPI2] cameraId " + cameraId + " does not in device map");
            return false;
        }
        CameraCharacteristics characteristics = deviceDescriptionMap.get(cameraId)
                .getCameraCharacteristics();
        if (characteristics == null) {
            LogHelper.d(TAG, "[isSupportInAPI2] characteristics is null");
            return false;
        }
        return ShutterSpeedHelper.isShutterSpeedSupported(characteristics);
    }
}
