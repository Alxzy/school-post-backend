package com.frn.findlovebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frn.findlovebackend.common.ErrorCode;
import com.frn.findlovebackend.exception.BussinessException;
import com.frn.findlovebackend.model.entity.Post;
import com.frn.findlovebackend.model.entity.PostThumb;
import com.frn.findlovebackend.model.entity.User;
import com.frn.findlovebackend.service.PostService;
import com.frn.findlovebackend.service.PostThumbService;
import com.frn.findlovebackend.mapper.PostThumbMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author Administrator
 * @description 针对表【post_thumb(帖子点赞记录)】的数据库操作Service实现
 * @createDate 2024-02-22 18:30:14
 */
@Service
public class PostThumbServiceImpl extends ServiceImpl<PostThumbMapper, PostThumb>
        implements PostThumbService {

    @Resource
    PostService postService;

    /**
     * 点赞操作
     *
     * @param loginUser 当前用户
     * @param postId    当前帖子id
     * @return
     */
    @Override
    public int doThumb(User loginUser, Long postId) {
        // 1.参数校验
        // 1.1 帖子是否存在
        Post post = postService.getById(postId);
        if (post == null) {
            throw new BussinessException(ErrorCode.PARAM_ERROR, "当前帖子不存在");
        }
        // 1.2 帖子状态是否允许点赞(是否通过审核)
        Integer reviewStatus = post.getReviewStatus();
        if (reviewStatus != 1) {
            throw new BussinessException(ErrorCode.OPERATION_ERROR, "帖子不允许被点赞");
        }

        Long userId = loginUser.getId();
        // 2.执行操作 每个用户串行点赞
        synchronized (String.valueOf(userId).intern()) {
            return doThumbInner(userId, postId);
        }
    }

    /**
     * 内部点赞操作
     *
     * @param userId
     * @param postId
     * @return 使用事务处理
     */
    @Transactional(rollbackFor = Exception.class)
    public int doThumbInner(Long userId, Long postId) {
        // 1.查询用户点赞状态
        PostThumb postThumb = new PostThumb();
        postThumb.setPostId(postId);
        postThumb.setUserId(userId);
        QueryWrapper<PostThumb> postThumbQueryWrapper = new QueryWrapper<>(postThumb);
        long count = this.count(postThumbQueryWrapper);
        // 2.根据点赞状态执行操作
        if (count > 0) {// 已点赞
            // 3.执行取消点赞操作
            boolean result = this.remove(postThumbQueryWrapper);
            if (result) {
                // 3.1 操作成功 帖子点赞数减 1
                boolean update = postService.update()
                        .eq("id", postId)
                        .gt("thumbNum", 0)
                        .setSql("thumbNum = thumbNum - 1").update();
                // 返回变化数值
                return update ? -1 : 0;
            } else {
                throw new BussinessException(ErrorCode.SYSTEM_ERROR);
            }
        } else {// 未点赞
            // 3.执行点赞操作
            boolean result = this.save(postThumb);
            if (result) {
                // 3.1 操作成功 帖子点赞数加 1
                boolean update = postService.update()
                        .eq("id", postId)
                        .setSql("thumbNum = thumbNum + 1").update();
                // 返回变化数值
                return update ? 1 : 0;
            } else {
                throw new BussinessException(ErrorCode.SYSTEM_ERROR);
            }
        }
    }
}




