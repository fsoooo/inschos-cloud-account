package com.inschos.cloud.account.access.rpc.bean;

/**
 * Created by IceAnt on 2018/4/19.
 */
public class CompanyBean {

    /**
     * 公司名称
     */
    public String name;

    /**
     * 联系电话
     */
    public String tel;

    /**
     * 公司邮箱
     */
    public String email;

    /**
     * 组织机构代码
     */
    public String organization_code;

    /**
     * 营业执照编码
     */
    public String license_code;

    /**
     * 纳税人识别号
     */
    public String tax_code;

    /**
     * 头像
     */
    public String head;

    /**
     * 区域（省-市）/
     * public $area;
     * <p>
     * /** 详细地址
     */
    public String street_address;

    /**
     * 经度
     */
    public String longitude;

    /**
     * 纬度
     */
    public String latitude;

    /**
     * 认证状态 0:未认证 1:已认证
     */
    public int authentication;

    /**
     * 营业执照图片
     */
    public String license_image;
}
