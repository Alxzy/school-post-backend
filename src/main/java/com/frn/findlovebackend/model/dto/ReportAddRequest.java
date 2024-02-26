package com.frn.findlovebackend.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Administrator
 * @version 1.0
 * @date 2024-01-10 16:07
 * 添加举报请求
 */
@Data
public class ReportAddRequest implements Serializable {
    /**
     * 被举报帖子id
     */
    private Long reportedPostId;
    /**
     * 举报内容
     */
    private String content;

    private static final long serialVersionUID = 6512528324455293432L;
}
