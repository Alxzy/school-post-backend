package com.frn.findlovebackend.model.dto;

import com.frn.findlovebackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Administrator
 * @version 1.0
 * @date 2024-01-10 17:35
 * 用户查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true) // 使得保证有父类的 equals and hashcode
public class UserQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 性别: 0 男 / 1女
     */
    private Integer gender;

    /**
     * 用户角色：user / admin
     */
    private String userRole;


    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = -5484630200614682537L;
}
