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
 * 举报查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true) // 使得保证有父类的 equals and hashcode
public class ReportQueryRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = 7937711339841647546L;
    /**
     * id
     */
    private Long id;

    /**
     * 创建用户id
     */
    private Long userId;

    /**
     * 被举报用户id
     */
    private Long reportedUserId;

    /**
     * 被举报帖子id
     */
    private Long reportedPostId;

    /**
     * 举报内容
     */
    private String content;

    /**
     * 状态（0-未处理, 1-已处理）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
