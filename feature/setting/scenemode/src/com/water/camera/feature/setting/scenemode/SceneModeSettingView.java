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
package com.water.camera.feature.setting.scenemode;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.res.TypedArray;
import android.preference.PreferenceFragment;

import com.water.camera.R;
import com.water.camera.common.debug.LogUtil;
import com.water.camera.common.preference.Preference;
import com.water.camera.common.setting.ICameraSettingView;

import java.util.ArrayList;
import java.util.List;

/**
 * Scene mode setting view.
 */

public class SceneModeSettingView implements ICameraSettingView,
        SceneModeSelector.OnItemClickListener {
    private static final LogUtil.Tag TAG
            = new LogUtil.Tag(SceneModeSettingView.class.getSimpleName());
    private static final String OFF_VALUE = "off";
    private List<String> mOriginalEntries = new ArrayList<>();
    private List<String> mOriginalEntryValues = new ArrayList<>();
    private List<Integer> mOriginalIcons = new ArrayList<>();
    private List<String> mEntries = new ArrayList<>();
    private List<String> mEntryValues = new ArrayList<>();
    private List<Integer> mIcons = new ArrayList<>();

    private Activity mActivity;
    private String mKey = null;
    private String mSummary = null;
    private String mSelectedValue = null;
    private boolean mEnabled;

    private OnValueChangeListener mListener;
    private Preference mPreference;
    private SceneModeSelector mSceneModeSelector;

    @Override
    public void onItemClick(String value) {
        setValue(value);
        if (mListener != null) {
            mListener.onValueChanged(value);
        }
    }

    /**
     * Listener to listen picture size value changed.
     */
    public interface OnValueChangeListener {
        /**
         * Callback when picture size value changed.
         *
         * @param value The changed picture size, such as "1920x1080".
         */
        void onValueChanged(String value);
    }

    /**
     * Setting view constructor.
     *
     * @param activity The instance of camera activity.
     * @param key The scene mode setting key.
     */
    public SceneModeSettingView(Activity activity, String key) {
        mActivity = activity;
        mKey = key;
        String[] originalEntriesInArray = mActivity.getResources()
                .getStringArray(R.array.scene_mode_entries);
        String[] originalEntryValuesInArray = mActivity.getResources()
                .getStringArray(R.array.scene_mode_entryvalues);

        TypedArray array = mActivity.getResources().obtainTypedArray(R.array.scene_mode_icons);
        int n = array.length();
        int[] originalIconsInArray = new int[n];
        for (int i = 0; i < n; ++i) {
            originalIconsInArray[i] = array.getResourceId(i, 0);
        }
        array.recycle();

        for (String value : originalEntriesInArray) {
            mOriginalEntries.add(value);
        }
        for (String value : originalEntryValuesInArray) {
            mOriginalEntryValues.add(value);
        }
        for (int icon : originalIconsInArray) {
            mOriginalIcons.add(icon);
        }
    }

    @Override
    public void loadView(PreferenceFragment fragment) {
        if (mSceneModeSelector == null) {
            mSceneModeSelector = new SceneModeSelector();
            mSceneModeSelector.setOnItemClickListener(this);
        }

        fragment.addPreferencesFromResource(R.xml.scene_mode_preference);
        mPreference = (Preference) fragment.getPreferenceManager().findPreference(mKey);
        mPreference.setRootPreference(fragment.getPreferenceScreen());
        mPreference.setId(R.id.scene_mode_setting);
        mPreference.setContentDescription(fragment.getActivity().getResources()
                .getString(R.string.pref_camera_scenemode_content_description));
        mPreference.setSummary(mSummary);
        mPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(android.preference.Preference preference) {
                mSceneModeSelector.setSelectedValue(mSelectedValue);
                mSceneModeSelector.setEntriesAndEntryValues(mEntries, mEntryValues, mIcons);

                FragmentTransaction transaction = mActivity.getFragmentManager()
                        .beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.setting_container,
                        mSceneModeSelector, "picture_size_selector").commit();
                return true;
            }
        });
        mPreference.setEnabled(mEnabled);
    }

    @Override
    public void refreshView() {
        if (mPreference != null) {
            mPreference.setSummary(mSummary);
            mPreference.setEnabled(mEnabled);
        }
    }

    @Override
    public void unloadView() {

    }

    @Override
    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return mEnabled;
    }

    /**
     * Set listener to listen the changed picture size value.
     *
     * @param listener The instance of {@link OnValueChangeListener}.
     */
    public void setOnValueChangeListener(OnValueChangeListener listener) {
        mListener = listener;
    }

    /**
     * Set current scene mode entry values.
     *
     * @param entryValues Current entry values.
     */
    public void setEntryValues(List<String> entryValues) {
        mEntries.clear();
        mEntryValues.clear();
        mIcons.clear();

        if (entryValues.size() == 1
                && "hdr".equals(entryValues.get(0))) {
            mEntryValues.add(OFF_VALUE);
            int index = mOriginalEntryValues.indexOf(OFF_VALUE);
            mEntries.add(mOriginalEntries.get(index));
            mIcons.add(mOriginalIcons.get(index));
        } else {
            for (int i = 0; i < mOriginalEntryValues.size(); i++) {
                String originalEntryValue = mOriginalEntryValues.get(i);
                for (int j = 0; j < entryValues.size(); j++) {
                    String entryValue = entryValues.get(j);
                    if (entryValue.equals(originalEntryValue)) {
                        mEntryValues.add(entryValue);
                        mEntries.add(mOriginalEntries.get(i));
                        mIcons.add(mOriginalIcons.get(i));
                        break;
                    }
                }
            }
        }

    }

    /**
     * Set current scene mode value.
     *
     * @param value Current scene mode value.
     */
    public void setValue(String value) {
        mSelectedValue = value;
        if ("hdr".equals(mSelectedValue)) {
            mSelectedValue = "auto";
        }
        int index = mEntryValues.indexOf(mSelectedValue);
        if (index >= 0 && index < mEntries.size()) {
            mSummary = mEntries.get(index);
        }
    }
}
