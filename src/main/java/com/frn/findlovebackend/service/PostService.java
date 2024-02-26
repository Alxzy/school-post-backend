package com.frn.findlovebackend.service;

import com.frn.findlovebackend.model.entity.Post;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【post(帖子)】的数据库操作Service
* @createDate 2024-01-29 17:16:24
*/
public interface PostService extends IService<Post> {

    /**
     * 业务校验 帖子属性
     * @param post
     * @param add 是否为添加
     */
    void validPost(Post post, boolean add);
}
