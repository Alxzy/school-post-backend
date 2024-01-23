package com.frn.findlovebackend.service;

import com.frn.findlovebackend.model.entity.Tag;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author Administrator
* @description 针对表【tag(标签)】的数据库操作Service
* @createDate 2023-12-28 17:44:37
*/
public interface TagService extends IService<Tag> {
    /**
     * 添加标签
     * @param tagName 标签名称
     * @param tagCategory 标签分组/种类
     * @return 标签 id
     */
    long addTag(String tagName, String tagCategory, HttpServletRequest request);

    /**
     * 按照 id 删除 标签
     * @param id
     * @return 是否成功
     */
    boolean deleteTagById(long id);

    /**
     * 按照 id 修改 标签
     * @param id 标签id
     * @param tagName 新的标签名称
     * @param tagCategory 新的标签分组
     * @return 是否成功
     */
    boolean updateTagById(long id,String tagName,String tagCategory, HttpServletRequest request);
}
