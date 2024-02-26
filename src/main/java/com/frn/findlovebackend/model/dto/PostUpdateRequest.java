package com.frn.findlovebackend.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Administrator
 * @version 1.0
 * @date 2024-01-10 16:07
 * 修改帖子请求
 */
@Data
public class PostUpdateRequest implements Serializable {

    private static final long serialVersionUID = 5149430148584223484L;
    /**
     * id
     */
    private long id;


    /**
     * 年龄
     */
    private Integer age;

    /**
     * 性别（0-男, 1-女）
     */
    private Integer gender;

    /**
     * 学历
     */
    private String education;

    /**
     * 地点
     */
    private String place;

    /**
     * 职业
     */
    private String job;

    /**
     * 爱好
     */
    private String hobby;

    /**
     * 感情经历
     */
    private String loveExp;

    /**
     * 内容（自我介绍）
     */
    private String content;

    /**
     * 照片地址
     */
    private String photo;

    /**
     * 联系方式
     */
    private String contact;

    /**
     * 状态（0-待审核, 1-通过, 2-拒绝）
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;


}
