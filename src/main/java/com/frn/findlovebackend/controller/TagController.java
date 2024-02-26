package com.frn.findlovebackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.frn.findlovebackend.annotation.AuthCheck;
import com.frn.findlovebackend.common.BaseResponse;
import com.frn.findlovebackend.common.DeleteRequest;
import com.frn.findlovebackend.common.ErrorCode;
import com.frn.findlovebackend.common.ResultUtils;
import com.frn.findlovebackend.exception.BusinessException;
import com.frn.findlovebackend.model.dto.TagQueryRequest;
import com.frn.findlovebackend.model.entity.Tag;
import com.frn.findlovebackend.model.enums.TagCategoryEnum;
import com.frn.findlovebackend.service.TagService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @version 1.0
 * @date 2023-12-28 17:55
 * 标签模块接口
 */
@RestController
@RequestMapping("/tag")
public class TagController {
    @Resource
    TagService tagService;

    // TagMap 缓存
    private final Cache<String, Map<String, List<Tag>>> tagMapCache = Caffeine.newBuilder().build();

    // 整个 TagMap 缓存 key
    private static final String FULL_TAG_MAP_KEY = "f";


    @PostMapping("/add")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Long> addTag(@RequestBody Tag tag, HttpServletRequest request) {
        // 校验参数
        if (tag == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        String tagName = tag.getTagName();
        String category = tag.getCategory();
        // 校验
        if (StringUtils.isAnyBlank(tagName, category)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        long tagId = tagService.addTag(tagName, category, request);
        // 清除缓存
        tagMapCache.invalidate(FULL_TAG_MAP_KEY);

        return ResultUtils.success(tagId);
    }

    @DeleteMapping("/delete")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> deleteTagById(@RequestBody DeleteRequest deleteRequest) {
        // 校验参数
        if (deleteRequest == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        Long id = deleteRequest.getId();
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        boolean isSuccess = tagService.deleteTagById(id);
        // 清除缓存
        tagMapCache.invalidate(FULL_TAG_MAP_KEY);
        return ResultUtils.success(isSuccess);
    }

    @PutMapping("/update")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> updateTag(@RequestBody Tag tag, HttpServletRequest request) {
        // 校验参数
        if (tag == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        String tagName = tag.getTagName();
        String category = tag.getCategory();
        Long id = tag.getId();
        // 校验
        if (StringUtils.isAnyBlank(tagName, category) || id == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        boolean isSuccess = tagService.updateTagById(id, tagName, category, request);
        // 清除缓存
        tagMapCache.invalidate(FULL_TAG_MAP_KEY);
        return ResultUtils.success(isSuccess);
    }

    //region 组相关

    /**
     * 查询组
     * @return
     */
    @GetMapping("/category/list")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<List<String>> listTagCategory() {
        return ResultUtils.success(TagCategoryEnum.getValues());
    }

    /**
     * 获取所有标签分组
     * @return
     */
    @GetMapping("/category/map")

    public BaseResponse<Map<String, List<Tag>>> getTagMap() {
        // 1.获取标签列表
        List<Tag> tagList = tagService.list();
        if (tagList == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 2.优先查找缓存,数据精简脱敏
        Map<String, List<Tag>> tagMap = tagMapCache.get(FULL_TAG_MAP_KEY, key -> tagList.stream().map(tag -> {
            Tag newTag = new Tag();
            newTag.setTagName(tag.getTagName());
            newTag.setPostNum(tag.getPostNum());
            newTag.setCategory(tag.getCategory());
            return newTag;
            // 3.按类别分组 并返回
        }).collect(Collectors.groupingBy(Tag::getCategory)));
        return ResultUtils.success(tagMap);
    }


    //endregion

    /**
     * 分页查询标签
     */
    @GetMapping("/list/page")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Page<Tag>> listTagsByPage(TagQueryRequest tagQueryRequest) {
        // 1.校验参数
        if(tagQueryRequest == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        // 2.获取请求中的参数
        long current = tagQueryRequest.getCurrent();
        long pageSize = tagQueryRequest.getPageSize();
        String tagName = tagQueryRequest.getTagName();
        String category = tagQueryRequest.getCategory();
        // 3.按需求查询
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        // 分组
        if(StringUtils.isNotBlank(category)){
            queryWrapper.eq("category",category);
        }
        // 标签名 相似搜索
        if(StringUtils.isNotBlank(tagName)){
            queryWrapper.like("tagName",tagName);
        }
        // 默认为 帖子使用数降序排列
        queryWrapper.orderByDesc("postNum");
        // 4.分页
        Page<Tag> tagPage = tagService.page(new Page<>(current, pageSize), queryWrapper);
        // 视图化/脱敏 规定分页查询只需要考虑敏感数据即可
//        List<Tag> tagList = page.getRecords();
//        List<Tag> newTagList = tagList.stream().map(tag -> {
//            Tag newTag = new Tag();
//            newTag.setTagName(tag.getTagName());
//            newTag.setPostNum(tag.getPostNum());
//            newTag.setCategory(tag.getCategory());
//            newTag.setUserId(tag.getUserId());
//            return newTag;
//        }).collect(Collectors.toList());
//        page.setRecords(newTagList);
        return ResultUtils.success(tagPage);
    }
}
