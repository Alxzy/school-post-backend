package com.frn.findlovebackend.service;

import com.frn.findlovebackend.model.entity.PostThumb;
import com.baomidou.mybatisplus.extension.service.IService;
import com.frn.findlovebackend.model.entity.User;

/**
* @author Administrator
* @description 针对表【post_thumb(帖子点赞记录)】的数据库操作Service
* @createDate 2024-02-22 18:30:14
*/
public interface PostThumbService extends IService<PostThumb> {

    int doThumb(User loginUser, Long postId);

    int doThumbInner(Long userId, Long postId);
}
