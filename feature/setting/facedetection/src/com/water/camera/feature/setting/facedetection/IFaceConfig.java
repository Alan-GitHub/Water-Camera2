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
 *   MediaTek Inc. (C) 2016. All rights reserved.
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

package com.water.camera.feature.setting.facedetection;

import com.water.camera.common.utils.Size;

import java.util.List;

/**
 * It is the common flow to control face performing.
 */

public interface IFaceConfig {

    public static final String KEY_FACE_DETECTION = "key_face_detection";
    public static final String FACE_DETECTION_ON = "on";
    public static final String FACE_DETECTION_OFF = "off";

    /**
     * Set face monitor to device to get the face flow state.
     * @param monitor the monitor.
     */
    public void setFaceMonitor(FaceDeviceCtrl.IFacePerformerMonitor monitor);

    /**
     * this is to update image orientation to face algo.
     */
    public void updateImageOrientation();

    /**
     * Set device preview state.
     */
    public void resetFaceDetectionState();

    /**
     * For Face detection callback update flow.
     * @param listener the face update listener.
     */
    public void setFaceDetectionUpdateListener(OnDetectedFaceUpdateListener listener);

    /**
     * listener face detection device change.
     */
    public interface OnDetectedFaceUpdateListener {
        /**
         * The callback for face update.
         * @param faces the detected faces.
         */
        public void onDetectedFaceUpdate(Face[] faces);
    }

    /**
     * For Face detection value update flow.
     * @param listener the face value update listener.
     */
    public void setFaceValueUpdateListener(OnFaceValueUpdateListener listener);

    /**
     * listener face detection setting values change.
     */
    public interface OnFaceValueUpdateListener {
        /**
         * The callback for face setting value.
         * @param isSupport the face detection supported or not.
         * @param supportList the supported value for setting.
         */
        public void onFaceSettingValueUpdate(boolean isSupport, List<String> supportList);

        /**
         * The callback for preview size update.
         * @return the preview size.
         */
        public Size onFacePreviewSizeUpdate();

        /**
         * This is used to update image orientation for face algo.
         * @return the image orientation.
         */
        public int onUpdateImageOrientation();
    }
}
