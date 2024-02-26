package com.frn.findlovebackend.model.dto;
import lombok.Data;
import java.io.Serializable;

/**
 * @author Administrator
 * @version 1.0
 * @date 2024-01-10 16:07
 * 管理员 添加用户请求
 */
@Data
public class UserAddRequest  implements Serializable {


    private static final long serialVersionUID = -2079854746085213428L;
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
     * 密码
     */
    private String userPassword;

}
