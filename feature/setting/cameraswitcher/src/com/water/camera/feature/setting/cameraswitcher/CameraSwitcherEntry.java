package com.water.camera.feature.setting.cameraswitcher;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;

import com.water.camera.common.device.CameraDeviceManagerFactory.CameraApi;
import com.water.camera.common.loader.FeatureEntryBase;
import com.water.camera.common.setting.ICameraSetting;

/**
 * Switch Camera setting entry.
 *
 */
public class CameraSwitcherEntry extends FeatureEntryBase {
    private CameraSwitcher mCameraSwitcher;

    /**
     * create an entry.
     *
     * @param context   current activity.
     * @param resources current resources.
     */
    public CameraSwitcherEntry(Context context, Resources resources) {
        super(context, resources);
    }

    @Override
    public boolean isSupport(CameraApi currentCameraApi, Activity activity) {
        return true;
    }

    @Override
    public String getFeatureEntryName() {
        return CameraSwitcherEntry.class.getName();
    }

    @Override
    public Class getType() {
        return ICameraSetting.class;
    }

    @Override
    public Object createInstance() {
        if (mCameraSwitcher == null) {
            mCameraSwitcher = new CameraSwitcher();
        }
        return mCameraSwitcher;
    }
}
