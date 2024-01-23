package com.frn.findlovebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frn.findlovebackend.common.ErrorCode;
import com.frn.findlovebackend.exception.BusinessException;
import com.frn.findlovebackend.model.entity.Tag;
import com.frn.findlovebackend.model.entity.User;
import com.frn.findlovebackend.model.enums.TagCategoryEnum;
import com.frn.findlovebackend.service.TagService;
import com.frn.findlovebackend.mapper.TagMapper;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import static com.frn.findlovebackend.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author Administrator
* @description 针对表【tag(标签)】的数据库操作Service实现
* @createDate 2023-12-28 17:44:37
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

    //region 增删改查

    @Override
    public long addTag(String tagName, String tagCategory, HttpServletRequest request) {
        // 1.校验是否登录 保险起见
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        // 2.校验标签组和标签名称是否存在
        // 标签组
        if(!TagCategoryEnum.contains(tagCategory)){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"标签分组不存在");
        }
        // 标签名称
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("tagName",tagName);
        long count = this.count(queryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"标签名称已存在");
        }
        // 设置登录用户id等属性,封装Tag对象并保存
        Tag tag = new Tag();
        tag.setTagName(tagName);
        tag.setCategory(tagCategory);
        tag.setUserId(currentUser.getId());

        boolean isSuccess = save(tag);
        if(!isSuccess) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "添加失败,数据库发送错误");
        }
        return tag.getId();
    }

    @Override
    public boolean deleteTagById(long id) {
        boolean b = this.removeById(id);
        if(!b){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"标签id不存在或者数据库错误,删除失败");
        }
        return b;
    }

    @Override
    public boolean updateTagById(long id, String tagName, String tagCategory, HttpServletRequest request) {
        // 1.校验是否登录 保险起见
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        // 2.校验标签组和标签名称是否存在
        // 标签组
        if(!TagCategoryEnum.contains(tagCategory)){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"标签分组不存在");
        }
        // 标签名称
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("tagName",tagName);
        long count = this.count(queryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"标签名称已存在");
        }
        // 设置登录用户id等属性,封装Tag对象并保存
        Tag tag = new Tag();
        tag.setId(id);
        tag.setTagName(tagName);
        tag.setCategory(tagCategory);
        tag.setUserId(currentUser.getId());
        boolean b = this.updateById(tag);
        if(!b){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"标签id不存在或者数据库错误,修改失败");
        }
        return b;
    }

    // endregion
}




