/* Copyright Statement:
 *
 * This software/firmware and related documentation ("MediaTek Software") are
 * protected under relevant copyright laws. The information contained herein is
 * confidential and proprietary to MediaTek Inc. and/or its licensor. Without
 * the prior written permission of MediaTek inc. and/or its licensor, any
 * reproduction, modification, use or disclosure of MediaTek Software, and
 * information contained herein, in whole or in part, shall be strictly
 * prohibited.
 *
 * MediaTek Inc. (C) 2016. All rights reserved.
 *
 * BY OPENING THIS FILE, RECEIVER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND AGREES
 * THAT THE SOFTWARE/FIRMWARE AND ITS DOCUMENTATIONS ("MEDIATEK SOFTWARE")
 * RECEIVED FROM MEDIATEK AND/OR ITS REPRESENTATIVES ARE PROVIDED TO RECEIVER
 * ON AN "AS-IS" BASIS ONLY. MEDIATEK EXPRESSLY DISCLAIMS ANY AND ALL
 * WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT. NEITHER DOES MEDIATEK PROVIDE ANY WARRANTY WHATSOEVER WITH
 * RESPECT TO THE SOFTWARE OF ANY THIRD PARTY WHICH MAY BE USED BY,
 * INCORPORATED IN, OR SUPPLIED WITH THE MEDIATEK SOFTWARE, AND RECEIVER AGREES
 * TO LOOK ONLY TO SUCH THIRD PARTY FOR ANY WARRANTY CLAIM RELATING THERETO.
 * RECEIVER EXPRESSLY ACKNOWLEDGES THAT IT IS RECEIVER'S SOLE RESPONSIBILITY TO
 * OBTAIN FROM ANY THIRD PARTY ALL PROPER LICENSES CONTAINED IN MEDIATEK
 * SOFTWARE. MEDIATEK SHALL ALSO NOT BE RESPONSIBLE FOR ANY MEDIATEK SOFTWARE
 * RELEASES MADE TO RECEIVER'S SPECIFICATION OR TO CONFORM TO A PARTICULAR
 * STANDARD OR OPEN FORUM. RECEIVER'S SOLE AND EXCLUSIVE REMEDY AND MEDIATEK'S
 * ENTIRE AND CUMULATIVE LIABILITY WITH RESPECT TO THE MEDIATEK SOFTWARE
 * RELEASED HEREUNDER WILL BE, AT MEDIATEK'S OPTION, TO REVISE OR REPLACE THE
 * MEDIATEK SOFTWARE AT ISSUE, OR REFUND ANY SOFTWARE LICENSE FEES OR SERVICE
 * CHARGE PAID BY RECEIVER TO MEDIATEK FOR SUCH MEDIATEK SOFTWARE AT ISSUE.
 *
 * The following software/firmware and/or related documentation ("MediaTek
 * Software") have been modified by MediaTek Inc. All revisions are subject to
 * any receiver's applicable license agreements with MediaTek Inc.
 */
package com.water.camera.common.relation;

import com.google.common.base.Splitter;
import com.water.camera.common.setting.ICameraSetting;
import com.water.camera.common.setting.SettingTable;

import java.util.ArrayList;
import java.util.List;

/**
 * This class for parse the restriction info and dispatch the entry values in it
 * to the target features.
 */
public class RestrictionDispatcher {
    private final SettingTable mSettingTable;

    /**
     * Dispatch restriction.
     *
     * @param settingTable settingTable used by RestrictionDispatcher.
     */
    public RestrictionDispatcher(SettingTable settingTable) {
        mSettingTable = settingTable;
    }

    /**
     * Dispatch the restriction info which is written in the {@link Relation} object
     * to the target features.
     *
     * @param relation The object to carry restriction info.
     */
    public void dispatch(Relation relation) {
        String headerKey = relation.getHeaderKey();
        List<String> bodyKeys = relation.getBodyKeys();

        // dispatch restriction one by one
        for (String bodyKey: bodyKeys) {
            ICameraSetting setting = mSettingTable.get(bodyKey);
            if (setting != null) {
                String value = relation.getBodyValue(bodyKey);
                String entryValues = relation.getBodyEntryValues(bodyKey);
                List<String> entryValuesList = null;
                // format entryValues to list
                if (entryValues != null) {
                    entryValuesList = new ArrayList<>();
                    Iterable<String> entryValueIterable =
                            Splitter.on(Relation.BODY_SPLITTER)
                                    .trimResults()
                                    .omitEmptyStrings()
                                    .split(entryValues);
                    for (String entryValue: entryValueIterable) {
                        entryValuesList.add(entryValue);
                    }
                }
                setting.overrideValues(headerKey, value, entryValuesList);
            }
        }
    }
}
