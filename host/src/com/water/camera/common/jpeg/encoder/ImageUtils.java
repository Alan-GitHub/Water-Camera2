/* Copyright Statement:
 *
 * This software/firmware and related documentation ("MediaTek Software") are
 * protected under relevant copyright laws. The information contained herein is
 * confidential and proprietary to MediaTek Inc. and/or its licensors. Without
 * the prior written permission of MediaTek inc. and/or its licensors, any
 * reproduction, modification, use or disclosure of MediaTek Software, and
 * information contained herein, in whole or in part, shall be strictly
 * prohibited.
 *
 * MediaTek Inc. (C) 2017. All rights reserved.
 *
 * BY OPENING THIS FILE, RECEIVER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND AGREES
 * THAT THE SOFTWARE/FIRMWARE AND ITS DOCUMENTATIONS ("MEDIATEK SOFTWARE")
 * RECEIVED FROM MEDIATEK AND/OR ITS REPRESENTATIVES ARE PROVIDED TO RECEIVER
 * ON AN "AS-IS" BASIS ONLY. MEDIATEK EXPRESSLY DISCLAIMS ANY AND ALL
 * WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NONINFRINGEMENT. NEITHER DOES MEDIATEK PROVIDE ANY WARRANTY WHATSOEVER WITH
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

package com.water.camera.common.jpeg.encoder;

import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.media.ImageWriter;
import android.util.Size;


//@water del debug;
//import libcore.io.Memory;

import com.water.camera.common.jpeg.encoder.Image.Plane;

import java.nio.ByteBuffer;

/**
 * Package private utility class for hosting commonly used Image related methods.
 */
class ImageUtils {

    /**
     * Only a subset of the formats defined in
     * {@link android.graphics.ImageFormat ImageFormat} and
     * {@link android.graphics.PixelFormat PixelFormat} are supported by
     * ImageReader. When reading RGB data from a surface, the formats defined in
     * {@link android.graphics.PixelFormat PixelFormat} can be used; when
     * reading YUV, JPEG or raw sensor data (for example, from the camera or video
     * decoder), formats from {@link android.graphics.ImageFormat ImageFormat}
     * are used.
     */
    public static int getNumPlanesForFormat(int format) {
        switch (format) {
            case ImageFormat.YV12:
            case ImageFormat.YUV_420_888:
            case ImageFormat.NV21:
                return 3;
            case ImageFormat.NV16:
                return 2;
            case PixelFormat.RGB_565:
            case PixelFormat.RGBA_8888:
            case PixelFormat.RGBX_8888:
            case PixelFormat.RGB_888:
            case ImageFormat.JPEG:
            case ImageFormat.YUY2:
          //@water del debug;
            //case ImageFormat.Y8:
            //case ImageFormat.Y16:
            case ImageFormat.RAW_SENSOR:
            case ImageFormat.RAW_PRIVATE:
            case ImageFormat.RAW10:
            case ImageFormat.RAW12:
            case ImageFormat.DEPTH16:
            case ImageFormat.DEPTH_POINT_CLOUD:
                return 1;
            case ImageFormat.PRIVATE:
                return 0;
            default:
                throw new UnsupportedOperationException(
                        String.format("Invalid format specified %d", format));
        }
    }

    /**
     * <p>
     * Copy source image data to destination Image.
     * </p>
     * <p>
     * Only support the copy between two non-{@link ImageFormat#PRIVATE PRIVATE} format
     * images with same properties (format, size, etc.). The data from the
     * source image will be copied to the byteBuffers from the destination Image
     * starting from position zero, and the destination image will be rewound to
     * zero after copy is done.
     * </p>
     *
     * @param src The source image to be copied from.
     * @param dst The destination image to be copied to.
     * @throws IllegalArgumentException If the source and destination images
     *             have different format, or one of the images is not copyable.
     */
    public static void imageCopy(Image src, Image dst) {
        if (src == null || dst == null) {
            throw new IllegalArgumentException("Images should be non-null");
        }
        if (src.getFormat() != dst.getFormat()) {
            throw new IllegalArgumentException("Src and dst images should have the same format");
        }
        if (src.getFormat() == ImageFormat.PRIVATE ||
                dst.getFormat() == ImageFormat.PRIVATE) {
            throw new IllegalArgumentException("PRIVATE format images are not copyable");
        }
        if (src.getFormat() == ImageFormat.RAW_PRIVATE) {
            throw new IllegalArgumentException(
                    "Copy of RAW_OPAQUE format has not been implemented");
        }
         if (!(dst.getOwner() instanceof ImageWriter)) {
            throw new IllegalArgumentException("Destination image is not from ImageWriter. Only"
                    + " the images from ImageWriter are writable");
         }
        Size srcSize = new Size(src.getWidth(), src.getHeight());
        Size dstSize = new Size(dst.getWidth(), dst.getHeight());
        if (!srcSize.equals(dstSize)) {
            throw new IllegalArgumentException("source image size " + srcSize + " is different"
                    + " with " + "destination image size " + dstSize);
        }

        Plane[] srcPlanes = src.getPlanes();
        Plane[] dstPlanes = dst.getPlanes();
        ByteBuffer srcBuffer = null;
        ByteBuffer dstBuffer = null;
        for (int i = 0; i < srcPlanes.length; i++) {
            int srcRowStride = srcPlanes[i].getRowStride();
            int dstRowStride = dstPlanes[i].getRowStride();
            srcBuffer = srcPlanes[i].getBuffer();
            dstBuffer = dstPlanes[i].getBuffer();
            if (!(srcBuffer.isDirect() && dstBuffer.isDirect())) {
                throw new IllegalArgumentException("Source and destination ByteBuffers must be"
                        + " direct byteBuffer!");
            }
            if (srcPlanes[i].getPixelStride() != dstPlanes[i].getPixelStride()) {
                throw new IllegalArgumentException("Source plane image pixel stride " +
                        srcPlanes[i].getPixelStride() +
                        " must be same as destination image pixel stride " +
                        dstPlanes[i].getPixelStride());
            }

            int srcPos = srcBuffer.position();
            srcBuffer.rewind();
            dstBuffer.rewind();
            if (srcRowStride == dstRowStride) {
                // Fast path, just copy the content if the byteBuffer all together.
                dstBuffer.put(srcBuffer);
            } else {
                // Source and destination images may have different alignment requirements,
                // therefore may have different strides. Copy row by row for such case.
                int srcOffset = srcBuffer.position();
                int dstOffset = dstBuffer.position();
                Size effectivePlaneSize = getEffectivePlaneSizeForImage(src, i);
                int srcByteCount = effectivePlaneSize.getWidth() * srcPlanes[i].getPixelStride();
                for (int row = 0; row < effectivePlaneSize.getHeight(); row++) {
                    if (row == effectivePlaneSize.getHeight() - 1) {
                        // Special case for NV21 backed YUV420_888: need handle the last row
                        // carefully to avoid memory corruption. Check if we have enough bytes to
                        // copy.
                        int remainingBytes = srcBuffer.remaining() - srcOffset;
                        if (srcByteCount > remainingBytes) {
                            srcByteCount = remainingBytes;
                        }
                    }
                    directByteBufferCopy(srcBuffer, srcOffset, dstBuffer, dstOffset, srcByteCount);
                    srcOffset += srcRowStride;
                    dstOffset += dstRowStride;
                }
            }

            srcBuffer.position(srcPos);
            dstBuffer.rewind();
        }
    }

    /**
     * Return the estimated native allocation size in bytes based on width, height, format,
     * and number of images.
     *
     * <p>This is a very rough estimation and should only be used for native allocation
     * registration in VM so it can be accounted for during GC.</p>
     *
     * @param width The width of the images.
     * @param height The height of the images.
     * @param format The format of the images.
     * @param numImages The number of the images.
     */
    public static int getEstimatedNativeAllocBytes(int width, int height, int format,
            int numImages) {
        double estimatedBytePerPixel;
        switch (format) {
            // 10x compression from RGB_888
            case ImageFormat.JPEG:
            case ImageFormat.DEPTH_POINT_CLOUD:
                estimatedBytePerPixel = 0.3;
                break;
//@water del debug;
//            case ImageFormat.Y8:
//                estimatedBytePerPixel = 1.0;
//                break;
            case ImageFormat.RAW10:
                estimatedBytePerPixel = 1.25;
                break;
            case ImageFormat.YV12:
            case ImageFormat.YUV_420_888:
            case ImageFormat.NV21:
            case ImageFormat.RAW12:
            case ImageFormat.PRIVATE: // A rough estimate because the real size is unknown.
                estimatedBytePerPixel = 1.5;
                break;
            case ImageFormat.NV16:
            case PixelFormat.RGB_565:
            case ImageFormat.YUY2:
            //@water del debug;
            //case ImageFormat.Y16:
            case ImageFormat.RAW_SENSOR:
            case ImageFormat.RAW_PRIVATE: // round estimate, real size is unknown
            case ImageFormat.DEPTH16:
                estimatedBytePerPixel = 2.0;
                break;
            case PixelFormat.RGB_888:
                estimatedBytePerPixel = 3.0;
                break;
            case PixelFormat.RGBA_8888:
            case PixelFormat.RGBX_8888:
                estimatedBytePerPixel = 4.0;
                break;
            default:
                throw new UnsupportedOperationException(
                        String.format("Invalid format specified %d", format));
        }

        return (int)(width * height * estimatedBytePerPixel * numImages);
    }

    private static Size getEffectivePlaneSizeForImage(Image image, int planeIdx) {
        switch (image.getFormat()) {
            case ImageFormat.YV12:
            case ImageFormat.YUV_420_888:
            case ImageFormat.NV21:
                if (planeIdx == 0) {
                    return new Size(image.getWidth(), image.getHeight());
                } else {
                    return new Size(image.getWidth() / 2, image.getHeight() / 2);
                }
            case ImageFormat.NV16:
                if (planeIdx == 0) {
                    return new Size(image.getWidth(), image.getHeight());
                } else {
                    return new Size(image.getWidth(), image.getHeight() / 2);
                }
            case PixelFormat.RGB_565:
            case PixelFormat.RGBA_8888:
            case PixelFormat.RGBX_8888:
            case PixelFormat.RGB_888:
            case ImageFormat.JPEG:
            case ImageFormat.YUY2:
            //@water del debug;
            //case ImageFormat.Y8:
            //case ImageFormat.Y16:
            case ImageFormat.RAW_SENSOR:
            case ImageFormat.RAW10:
            case ImageFormat.RAW12:
                return new Size(image.getWidth(), image.getHeight());
            case ImageFormat.PRIVATE:
                return new Size(0, 0);
            default:
                throw new UnsupportedOperationException(
                        String.format("Invalid image format %d", image.getFormat()));
        }
    }

    private static void directByteBufferCopy(ByteBuffer srcBuffer, int srcOffset,
            ByteBuffer dstBuffer, int dstOffset, int srcByteCount) {
        //@water del debug;
        //Memory.memmove(dstBuffer, dstOffset, srcBuffer, srcOffset, srcByteCount);
    }
}
