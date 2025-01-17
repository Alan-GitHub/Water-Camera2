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
package com.water.camera.feature.setting.matrixdisplay;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.water.camera.common.debug.LogHelper;
import com.water.camera.common.debug.LogUtil;
import com.water.camera.common.widget.Rotatable;

/**
 * Revolve view group, views can rotate width different orientation.
 */
public class MatrixDisplayRotateLayout extends ViewGroup implements Rotatable {
    private final static LogUtil.Tag TAG =
            new LogUtil.Tag(MatrixDisplayRotateLayout.class.getSimpleName());

    private static final int ORIENTATION_OFFSET = 270;
    private int mOrientation;

    private View mChild;

    /**
     * Set background as transparent.
     *
     * @param context The activity context.
     * @param attrs   The context attribute.
     */
    public MatrixDisplayRotateLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // The transparent background here is a workaround of the render issue
        // happened when the view is rotated as the device's orientation
        // changed. The view looks fine in landscape. After rotation, the view
        // is invisible.
        setBackgroundResource(android.R.color.transparent);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mChild = getChildAt(0);
        mChild.setPivotX(0);
        mChild.setPivotY(0);
    }

    @Override
    protected void onLayout(boolean change, int left, int top, int right, int bottom) {
        int width = right - left;
        int height = bottom - top;
        switch (ORIENTATION_OFFSET) {
            case 0:
            case 180:
                mChild.layout(0, 0, width, height);
                break;

            case 90:
            case 270:
                mChild.layout(0, 0, height, width);
                break;

            default:
                break;
        }
    }


    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int w = 0;
        int h = 0;
        switch (ORIENTATION_OFFSET) {
            case 0:
            case 180:
                measureChild(mChild, widthSpec, heightSpec);
                w = mChild.getMeasuredWidth();
                h = mChild.getMeasuredHeight();
                break;

            case 90:
            case 270:
                measureChild(mChild, heightSpec, widthSpec);
                w = mChild.getMeasuredHeight();
                h = mChild.getMeasuredWidth();
                break;

            default:
                break;
        }

        setMeasuredDimension(w, h);

        switch (ORIENTATION_OFFSET) {
            case 0:
                mChild.setTranslationX(0);
                mChild.setTranslationY(0);
                break;

            case 90:
                mChild.setTranslationX(0);
                mChild.setTranslationY(h);
                break;

            case 180:
                mChild.setTranslationX(w);
                mChild.setTranslationY(h);
                break;

            case 270:
                mChild.setTranslationX(w);
                mChild.setTranslationY(0);
                break;

            default:
                break;
        }
        mChild.setRotation(-ORIENTATION_OFFSET);
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @Override
    public void setOrientation(int orientation, boolean animation) {
        LogHelper.d(TAG, "[setOrientation]orientation = " + orientation + ",mOrientation = "
                + mOrientation + ",animation=" + animation);
        // Rotate the view counter-clockwise
        orientation = (orientation - ORIENTATION_OFFSET + 360) % 360;
        if (mOrientation != orientation) {
            mOrientation = orientation;
            setOrientation(mChild, mOrientation, animation);
            requestLayout();
        }
    }

    private static void setOrientation(View view, int orientation, boolean animation) {
        if (view == null) {
            LogHelper.d(TAG, "[setOrientation]view is null,return.");
            return;
        }
        if (view instanceof Rotatable) {
            ((Rotatable) view).setOrientation(orientation, animation);
        } else if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0, count = group.getChildCount(); i < count; i++) {
                setOrientation(group.getChildAt(i), orientation, animation);
            }
        }
    }
}
