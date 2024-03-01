package com.frn.findlovebackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.frn.findlovebackend.annotation.AuthCheck;
import com.frn.findlovebackend.common.BaseResponse;
import com.frn.findlovebackend.common.DeleteRequest;
import com.frn.findlovebackend.common.ErrorCode;
import com.frn.findlovebackend.common.ResultUtils;
import com.frn.findlovebackend.constant.CommonConstant;
import com.frn.findlovebackend.exception.BussinessException;
import com.frn.findlovebackend.model.dto.PostAddRequest;
import com.frn.findlovebackend.model.dto.PostQueryRequest;
import com.frn.findlovebackend.model.dto.PostThumbRequest;
import com.frn.findlovebackend.model.dto.PostUpdateRequest;
import com.frn.findlovebackend.model.entity.Post;
import com.frn.findlovebackend.model.entity.PostThumb;
import com.frn.findlovebackend.model.entity.User;
import com.frn.findlovebackend.model.vo.PostVO;
import com.frn.findlovebackend.service.PostService;
import com.frn.findlovebackend.service.PostThumbService;
import com.frn.findlovebackend.service.UserService;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.Collection;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @version 1.0
 * @date 2024-02-04 14:50
 * 帖子模块接口
 */
@RestController
@RequestMapping("/post")
@Slf4j
public class PostController {
    @Resource
    PostService postService;

    @Resource
    UserService userService;

    @Resource
    PostThumbService postThumbService;

    // 单个帖子获取 缓存 ,key为 postId,value为 post
    LoadingCache<Long, Post> postGetCache = Caffeine.newBuilder().expireAfterWrite(12, TimeUnit.HOURS)
            .maximumSize(5_000).build(postId -> postService.getById(postId));

    // IO型线程池
    private final ExecutorService ioExecutorService = new ThreadPoolExecutor(4, 20, 15, TimeUnit.MINUTES, new ArrayBlockingQueue<>(5000));


    //region 增删改查

    /**
     * 添加帖子接口
     *
     * @param postAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addPost(@RequestBody PostAddRequest postAddRequest, HttpServletRequest request) {
        // 1.参数校验
        // 1.1 非业务
        if (postAddRequest == null) {
            throw new BussinessException(ErrorCode.PARAM_ERROR);
        }
        // 1.2 业务校验 方法
        Post post = new Post();
        BeanUtils.copyProperties(postAddRequest, post);
        postService.validPost(post, true);

        // 2.添加当前用户id
        User currentLoginUser = userService.getLoginUser(request);
        if (currentLoginUser == null) {
            throw new BussinessException(ErrorCode.NOT_LOGIN);
        }
        post.setUserId(currentLoginUser.getId());
        // 3.添加到数据库
        boolean result = postService.save(post);
        if (!result) {
            throw new BussinessException(ErrorCode.OPERATION_ERROR);
        }
        Long id = post.getId();
        // 4.移除缓存
        postGetCache.invalidate(id);
        // 5.返回 帖子id
        return ResultUtils.success(id);
    }

    /**
     * 删除帖子接口
     *
     * @param request
     * @return
     */
    @DeleteMapping("/delete")
    public BaseResponse<Boolean> deletePost(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        // 1.参数校验
        // 1.1 非空
        if (deleteRequest == null || request == null) {
            throw new BussinessException(ErrorCode.PARAM_ERROR);
        }
        // 1.2 业务校验 对应id的帖子是否存在
        Long postId = deleteRequest.getId();
        Post post = postService.getById(postId);
        if (post == null) {
            throw new BussinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 1.3 权限校验 仅本人和管理员可以删除
        User currentLoginUser = userService.getLoginUser(request);
        if (!userService.isAdmin(request) && !post.getUserId().equals(currentLoginUser.getId())) {
            throw new BussinessException(ErrorCode.NO_AUTO);
        }
        // 2.删除
        boolean result = postService.removeById(postId);
        // 3.移除缓存
        postGetCache.invalidate(postId);
        // 4.异步删除点赞信息
        CompletableFuture.runAsync(() ->{
            QueryWrapper<PostThumb> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("postId",postId);
            boolean isRemoved = postThumbService.remove(queryWrapper);
            if(!isRemoved) {
                log.error("postThumb delete failed, postId = {}", postId);
                throw new BussinessException(ErrorCode.SYSTEM_ERROR);
            }
        },ioExecutorService);
        // 5.返回
        return ResultUtils.success(result);
    }

    @PutMapping("/update")
    public BaseResponse<Boolean> updatePostById(@RequestBody PostUpdateRequest postUpdateRequest, HttpServletRequest request) {
        // 1.参数校验
        // 1.1 非空
        if (request == null || postUpdateRequest == null) {
            throw new BussinessException(ErrorCode.PARAM_ERROR);
        }
        // 1.2 业务校验
        // 帖子存在
        long postId = postUpdateRequest.getId();
        Post post = postService.getById(postId);
        if (post == null) {
            throw new BussinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否合法
        postService.validPost(post, false);
        // 1.3 权限校验
        User currentLoginUser = userService.getLoginUser(request);
        if (!userService.isAdmin(request) && !post.getUserId().equals(currentLoginUser.getId())) {
            throw new BussinessException(ErrorCode.NO_AUTO);
        }
        // 只有管理员可以修改 状态
        if (!post.getReviewStatus().equals(postUpdateRequest.getReviewStatus()) && !userService.isAdmin(request)) {
            throw new BussinessException(ErrorCode.NO_AUTO);
        }
        Post newPost = new Post();
        BeanUtils.copyProperties(postUpdateRequest, newPost);
        // 2.按照帖子id修改
        boolean result = postService.updateById(newPost);
        // 3.移除缓存
        postGetCache.invalidate(postId);
        // 4.返回
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取帖子
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Post> getPostById(Long id) {
        // 参数校验
        if (id <= 0) {
            throw new BussinessException(ErrorCode.PARAM_ERROR);
        }
        Post post = postGetCache.get(id);
        return ResultUtils.success(post);
    }

    @GetMapping("/list")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<List<Post>> listPost(PostQueryRequest postQueryRequest) {
        // 1.参数校验
        if (postQueryRequest == null) {
            throw new BussinessException(ErrorCode.PARAM_ERROR);
        }
        // 2.填充查询对象
        Post postQuery = new Post();
        BeanUtils.copyProperties(postQueryRequest, postQuery);
        QueryWrapper<Post> postQueryWrapper = new QueryWrapper<>(postQuery);
        // 3.查询
        List<Post> list = postService.list(postQueryWrapper);
        // 4.返回
        return ResultUtils.success(list);
    }

    /**
     * 分页查询请求
     *
     * @param postQueryRequest
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<PostVO>> listPostByPage(PostQueryRequest postQueryRequest, HttpServletRequest request) {
        // 1.参数校验
        // 1.1 非空
        if (postQueryRequest == null || request == null) {
            throw new BussinessException(ErrorCode.PARAM_ERROR);
        }
        // 1.2 业务 限制爬虫
        long pageSize = postQueryRequest.getPageSize();
        if (pageSize > 50) {
            throw new BussinessException(ErrorCode.PARAM_ERROR);
        }
        // 2.填充查询对象
        Post postQuery = new Post();
        BeanUtils.copyProperties(postQueryRequest, postQuery);
        // content 需支持模糊搜索
        String content = postQuery.getContent();
        postQuery.setContent(null);
        // 如果是管理员,则不用设置而为已通过,设置 状态为 已通过审核
        if (!userService.isAdmin(request)) {
            // 1为 已通过
            postQuery.setReviewStatus(1);
        }

        // 3.获取参数并查询
        // 3.1 获取参数
        long current = postQueryRequest.getCurrent();
        String sortField = postQueryRequest.getSortField();
        String sortOrder = postQueryRequest.getSortOrder();
        QueryWrapper<Post> postQueryWrapper = new QueryWrapper<>(postQuery);
        // 3.2 设置顺序
        postQueryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        postQueryWrapper.like(StringUtils.isNotBlank(content),"content",content);
        // 3.3 查询
        Page<Post> postPage = postService.page(new Page<>(current, pageSize), postQueryWrapper);
        // 3.4 设置默认点赞状态为 false
        Map<Long, List<PostVO>> postIdListMap = postPage.getRecords().stream().map(post -> {
            PostVO postVO = new PostVO();
            BeanUtils.copyProperties(post, postVO);
            postVO.setHasThumb(false);
            return postVO;
            // 必须维持原有的顺序
        }).collect(Collectors.groupingBy(PostVO::getId, LinkedHashMap::new, Collectors.toList()));
        // 4.如果登录,获取点赞状态
        try {
            User user = userService.getLoginUser(request);
            QueryWrapper<PostThumb> postThumbQueryWrapper = new QueryWrapper<>();
            postThumbQueryWrapper.in("postId", postIdListMap.keySet());
            postThumbQueryWrapper.eq("userId", user.getId());
            List<PostThumb> postThumbList = postThumbService.list(postThumbQueryWrapper);
            postThumbList.forEach(postThumb ->
                    postIdListMap.get(postThumb.getPostId()).get(0).setHasThumb(true));
        } catch (Exception e) {
            // 用户未登录，不处理
        }

        // 5.数据脱敏,并返回
        Page<PostVO> postVOPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        postVOPage.setRecords(postIdListMap.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));
        // 说明一下 前端不需要展示 帖子状态和状态信息即可 返回的数据都是已经过审核的
        return ResultUtils.success(postVOPage);
    }

    //endregion

    /**
     * 点赞操作
     */
    @PostMapping("/thumb")
//    @AuthCheck(anyRole = {"admin","user"})
    public BaseResponse<Integer> postThumb(@RequestBody PostThumbRequest postThumbRequest, HttpServletRequest request) {
        // 无业务参数校验
        Long postId = postThumbRequest.getPostId();
        if (postThumbRequest == null || postId <= 0) {
            throw new BussinessException(ErrorCode.PARAM_ERROR);
        }
        // 必须登录 获取用户id
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {// 未登录
            throw new BussinessException(ErrorCode.NOT_LOGIN);
        }
        int result = postThumbService.doThumb(loginUser, postId);
        if (result != 0) {
            // 移除缓存
            postGetCache.invalidate(postId);
        }
        return ResultUtils.success(result);
    }
}
