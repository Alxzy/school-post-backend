package com.frn.findlovebackend.model.vo;

import com.frn.findlovebackend.model.entity.Post;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author Administrator
 * @version 1.0
 * @date 2024-01-29 17:23
 *
 * 帖子视图
 */
@Data
public class PostVO{
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
     * 是否已点赞
     */
    private Boolean hasThumb;

    private static final long serialVersionUID = 1805654601122032149L;
}
