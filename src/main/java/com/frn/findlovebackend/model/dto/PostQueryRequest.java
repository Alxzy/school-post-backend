package com.frn.findlovebackend.model.dto;

import com.frn.findlovebackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Administrator
 * @version 1.0
 * @date 2024-01-10 17:35
 * 帖子查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true) // 使得保证有父类的 equals and hashcode
public class PostQueryRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = 7937711339841647546L;
    /**
     * id
     */
    private Long id;

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

    /**
     * 浏览数
     */
    private Integer viewNum;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 创建用户id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
