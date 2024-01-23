package com.frn.findlovebackend.model.dto;

import com.frn.findlovebackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author Administrator
 * @version 1.0
 * @date 2024-01-23 22:38
 * 标签查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true) // 使得保证有父类的 equals and hashcode
public class TagQueryRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = 5754239415565198426L;
    /**
     * 分类
     */
    private String category;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 创建用户 id
     */
    private Long userId;
}
