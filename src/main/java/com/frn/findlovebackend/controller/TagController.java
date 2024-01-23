package com.frn.findlovebackend.controller;

import com.frn.findlovebackend.annotation.AuthCheck;
import com.frn.findlovebackend.common.BaseResponse;
import com.frn.findlovebackend.common.DeleteRequest;
import com.frn.findlovebackend.common.ErrorCode;
import com.frn.findlovebackend.common.ResultUtils;
import com.frn.findlovebackend.exception.BusinessException;
import com.frn.findlovebackend.model.entity.Tag;
import com.frn.findlovebackend.service.TagService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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

    @PostMapping("/add")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Long> addTag(@RequestBody Tag tag, HttpServletRequest request){
        // 校验参数
        if(tag == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        String tagName = tag.getTagName();
        String category = tag.getCategory();
        // 校验
        if(StringUtils.isAnyBlank(tagName,category)){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        long tagId = tagService.addTag(tagName, category,request);
        return ResultUtils.success(tagId);
    }

    @DeleteMapping("/delete")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> deleteTagById(@RequestBody DeleteRequest deleteRequest){
        // 校验参数
        if(deleteRequest == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        Long id = deleteRequest.getId();
        if(id == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        boolean isSuccess = tagService.deleteTagById(id);
        return ResultUtils.success(isSuccess);
    }

    @PutMapping("/update")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> updateTag(@RequestBody Tag tag, HttpServletRequest request){
        // 校验参数
        if(tag == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        String tagName = tag.getTagName();
        String category = tag.getCategory();
        Long id = tag.getId();
        // 校验
        if(StringUtils.isAnyBlank(tagName,category) || id == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        boolean isSuccess = tagService.updateTagById(id, tagName, category, request);
        return ResultUtils.success(isSuccess);
    }
}
