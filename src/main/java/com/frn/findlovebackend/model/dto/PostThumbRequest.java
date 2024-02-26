package com.frn.findlovebackend.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Administrator
 * @version 1.0
 * @date 2024-01-10 16:07
 * 帖子点赞请求
 */
@Data
public class PostThumbRequest implements Serializable {


    /**
     * 帖子 id
     */
    private Long postId;

}
