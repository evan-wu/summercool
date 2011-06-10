/**
 * Project: simpleimage-1.1
 * 
 * File Created at 2010-9-13
 * $Id$
 * 
 * Copyright 2008 Alibaba.com Croporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package com.bdconsulting.toolkit.image;

/**
 * TODO Comment of ImageFormat
 * @author wendell
 *
 */
public enum ImageFormat {
    JPEG, TIFF, PNG, BMP, GIF, ICO, RAW, PSD, UNKNOWN;
    
    public static ImageFormat getImageFormat(String suffix){
        if("JPEG".equalsIgnoreCase(suffix)){
            return JPEG;
        }else if("JPG".equalsIgnoreCase(suffix)){
            return JPEG;
        }else if("BMP".equalsIgnoreCase(suffix)){
            return BMP;
        }else if("GIF".equalsIgnoreCase(suffix)){
            return GIF;
        }else if("PNG".equalsIgnoreCase(suffix)){
            return PNG;
        }else if("TIFF".equalsIgnoreCase(suffix)){
            return TIFF;
        }else if("TFF".equalsIgnoreCase(suffix)){
            return TIFF;
        }else{
            return UNKNOWN;
        }
    }
    
    public static String getDesc(ImageFormat format){
        if(JPEG == format){
            return "JPEG";
        }else if(BMP == format){
            return "BMP";
        }else if(GIF == format){
            return "GIF";
        }else if(PNG == format){
            return "PNG";
        }else if(TIFF == format){
            return "TIFF";
        }else{
            return "UNKNOWN";
        }
    }
}
