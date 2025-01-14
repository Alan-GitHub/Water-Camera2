/*
 *   Copyright Statement:
 *
 *     This software/firmware and related documentation ("MediaTek Software") are
 *     protected under relevant copyright laws. The information contained herein is
 *     confidential and proprietary to MediaTek Inc. and/or its licensors. Without
 *     the prior written permission of MediaTek inc. and/or its licensors, any
 *     reproduction, modification, use or disclosure of MediaTek Software, and
 *     information contained herein, in whole or in part, shall be strictly
 *     prohibited.
 *
 *     MediaTek Inc. (C) 2016. All rights reserved.
 *
 *     BY OPENING THIS FILE, RECEIVER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND AGREES
 *    THAT THE SOFTWARE/FIRMWARE AND ITS DOCUMENTATIONS ("MEDIATEK SOFTWARE")
 *     RECEIVED FROM MEDIATEK AND/OR ITS REPRESENTATIVES ARE PROVIDED TO RECEIVER
 *     ON AN "AS-IS" BASIS ONLY. MEDIATEK EXPRESSLY DISCLAIMS ANY AND ALL
 *     WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
 *     WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 *     NONINFRINGEMENT. NEITHER DOES MEDIATEK PROVIDE ANY WARRANTY WHATSOEVER WITH
 *     RESPECT TO THE SOFTWARE OF ANY THIRD PARTY WHICH MAY BE USED BY,
 *     INCORPORATED IN, OR SUPPLIED WITH THE MEDIATEK SOFTWARE, AND RECEIVER AGREES
 *     TO LOOK ONLY TO SUCH THIRD PARTY FOR ANY WARRANTY CLAIM RELATING THERETO.
 *     RECEIVER EXPRESSLY ACKNOWLEDGES THAT IT IS RECEIVER'S SOLE RESPONSIBILITY TO
 *     OBTAIN FROM ANY THIRD PARTY ALL PROPER LICENSES CONTAINED IN MEDIATEK
 *     SOFTWARE. MEDIATEK SHALL ALSO NOT BE RESPONSIBLE FOR ANY MEDIATEK SOFTWARE
 *     RELEASES MADE TO RECEIVER'S SPECIFICATION OR TO CONFORM TO A PARTICULAR
 *     STANDARD OR OPEN FORUM. RECEIVER'S SOLE AND EXCLUSIVE REMEDY AND MEDIATEK'S
 *     ENTIRE AND CUMULATIVE LIABILITY WITH RESPECT TO THE MEDIATEK SOFTWARE
 *     RELEASED HEREUNDER WILL BE, AT MEDIATEK'S OPTION, TO REVISE OR REPLACE THE
 *     MEDIATEK SOFTWARE AT ISSUE, OR REFUND ANY SOFTWARE LICENSE FEES OR SERVICE
 *     CHARGE PAID BY RECEIVER TO MEDIATEK FOR SUCH MEDIATEK SOFTWARE AT ISSUE.
 *
 *     The following software/firmware and/or related documentation ("MediaTek
 *     Software") have been modified by MediaTek Inc. All revisions are subject to
 *     any receiver's applicable license agreements with MediaTek Inc.
 */

package com.water.camera.feature.setting.postview;

import com.water.camera.common.ICameraContext;
import com.water.camera.common.app.IApp;
import com.water.camera.common.mode.photo.ThumbnailHelper;
import com.water.camera.common.setting.ISettingManager;
import com.water.camera.common.setting.SettingBase;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;


public class PostView extends SettingBase {
    public static final String KEY_POSTVIEW = "key_postview";
    private static final String VALUE_ON = "on";
    private static final String VALUE_OFF = "off";
    private boolean mIsSupported;
    private IApp mApp;

    /**
     * Initialize setting. This will be called when do open camera.
     *
     * @param app the instance of IApp.
     * @param cameraContext the CameraContext.
     * @param settingController the SettingController.
     */
    public void init(IApp app,
                     ICameraContext cameraContext,
                     ISettingManager.SettingController settingController) {
        super.init(app, cameraContext, settingController);
        mApp = app;
    }


    @Override
    public void unInit() {

    }

    @Override
    public void postRestrictionAfterInitialized() {

    }

    @Override
    public SettingType getSettingType() {
        return SettingType.PHOTO;
    }

    @Override
    public String getKey() {
        return KEY_POSTVIEW;
    }

    @Override
    public IParametersConfigure getParametersConfigure() {
        return null;
    }

    @Override
    public ICaptureRequestConfigure getCaptureRequestConfigure() {
        return new PostViewCaptureRequestConfig(this,
                mApp.getActivity().getApplicationContext(), mSettingController);
    }

    @Override
    public void overrideValues(@Nonnull String headerKey, String currentValue,
                               List<String> supportValues) {
        super.overrideValues(headerKey, currentValue, supportValues);
        ThumbnailHelper.overrideSupportedValue(headerKey, VALUE_ON.equalsIgnoreCase(getValue()));
    }

    /**
     * init values for setting base.
     * @param isSupported
     */
    public void initSupportValue(boolean isSupported) {
        List<String> supportValues = new ArrayList<>();
        mIsSupported = isSupported;
        supportValues.add(VALUE_OFF);
        if (mIsSupported) {
            supportValues.add(VALUE_ON);
        }
        setSupportedPlatformValues(supportValues);
        setSupportedEntryValues(supportValues);
        setEntryValues(supportValues);
        setValue(mIsSupported ? VALUE_ON : VALUE_OFF);
    }
}