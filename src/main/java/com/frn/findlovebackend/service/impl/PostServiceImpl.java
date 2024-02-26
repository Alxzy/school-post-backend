package com.frn.findlovebackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frn.findlovebackend.common.ErrorCode;
import com.frn.findlovebackend.exception.BusinessException;
import com.frn.findlovebackend.model.entity.Post;
import com.frn.findlovebackend.model.enums.PostGenderEnum;
import com.frn.findlovebackend.model.enums.PostReviewStatusEnum;
import com.frn.findlovebackend.service.PostService;
import com.frn.findlovebackend.mapper.PostMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【post(帖子)】的数据库操作Service实现
* @createDate 2024-01-29 17:16:24
*/
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post>
    implements PostService{

    @Override
    public void validPost(Post post, boolean add) {
        if (post == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        Integer age = post.getAge();
        Integer gender = post.getGender();
        String content = post.getContent();
        String education = post.getEducation();
        String hobby = post.getHobby();
        String place = post.getPlace();
        String loveExp = post.getLoveExp();
        String job = post.getJob();
        Integer reviewStatus = post.getReviewStatus();
        if(add){
            // 创建时参数不能为空
            if(StringUtils.isAnyBlank(content,job,hobby,education,place,loveExp) || ObjectUtils.anyNull(age,gender)){
                throw new BusinessException(ErrorCode.PARAM_ERROR);
            }
        }
        // 内容不能过长
        if(StringUtils.isNotBlank(content) && content.length() > 8192){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"内容过长");
        }
        // reviewStatus 必须为空或者合法
        if(reviewStatus != null && !PostReviewStatusEnum.getValues().contains(reviewStatus)){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        // 年龄 >= 18 <= 100
        if(age != null && (age < 18 || age > 100)){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"年龄不符合要求");
        }
        // 性别 必须合法
        if(gender != null && !PostGenderEnum.getValues().contains(gender)){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"性别不符合要求");
        }
    }
}




