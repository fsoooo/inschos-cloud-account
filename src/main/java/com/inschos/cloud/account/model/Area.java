package com.inschos.cloud.account.model;

import java.io.Serializable;

/**
 * Created by IceAnt on 2018/3/13.
 */
public class Area implements Serializable {

    public static final int LEVEL_PROVINCE = 1;
    public static final int LEVEL_CITY = 2;
    public static final int LEVEL_DISTRICT = 3;

    public static final int DEFAULT_ALL_ID_BASE = -10000;

    public static final int ROOT_ID = 1;

    /** 区域ID*/
    public int id;

    /** 行政区域代码*/
    public int area_code;

    /** 名称*/
    public String name;

    /** 上级区域ID*/
    public int parent_id;

    /** 简称*/
    public String short_name;

    /** 层级 1省 2市 3县 4街道*/
    public int level_type;

    /** */
    public String city_code;

    /** 邮编*/
    public String zip_code;

    /** */
    public String merger_name;

    /** 经度*/
    public String longitude;

    /** 纬度*/
    public String latitude;

    /** 拼音*/
    public String pinyin;

    public static int toAllId(int parentId){
        return DEFAULT_ALL_ID_BASE - parentId;
    }
}
