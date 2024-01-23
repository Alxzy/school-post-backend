-- 创建库
create database if not exists lovefinder;

-- 切换库
use lovefinder;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userName     varchar(256)                           null comment '用户昵称',
    userAccount  varchar(256)                           not null comment '账号',
    userAvatar   varchar(1024)                          null comment '用户头像',
    gender       tinyint                                null comment '性别',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user / admin',
    userPassword varchar(512)                           not null comment '密码',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    constraint uni_userAccount
    unique (userAccount)
    ) comment '用户';

-- 标签表
create table if not exists tag
(
    id bigint auto_increment comment 'id' primary key,
    category     varchar(256)                           null comment '分类',
    tagName      varchar(256)                           not null comment '标签名称',
    postNum      int             default 0              not null comment '帖子使用标签次数',
    userId       bigint                                 not null comment '创建用户 id',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    constraint uni_tagName
        unique (tagName)
) comment '标签';