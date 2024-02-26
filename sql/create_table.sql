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
    id         bigint auto_increment comment 'id' primary key,
    category   varchar(256)                       null comment '分类',
    tagName    varchar(256)                       not null comment '标签名称',
    postNum    int      default 0                 not null comment '帖子使用标签次数',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    constraint uni_tagName
        unique (tagName)
) comment '标签';

-- 帖子表
create table if not exists post
(
    id            bigint auto_increment comment 'id' primary key,
    age           int                                null comment '年龄',
    gender        tinyint  default 0                 not null comment '性别（0-男, 1-女）',
    education     varchar(512)                       null comment '学历',
    place         varchar(512)                       null comment '地点',
    job           varchar(512)                       null comment '职业',
    hobby         varchar(512)                       null comment '爱好',
    loveExp       varchar(512)                       null comment '感情经历',
    content       text                               null comment '内容（自我介绍）',
    photo         varchar(1024)                      null comment '照片地址',
    contact       varchar(512)                       null comment '联系方式',
    reviewStatus  int      default 0                 not null comment '状态（0-待审核, 1-通过, 2-拒绝）',
    reviewMessage varchar(512)                       null comment '审核信息',
    viewNum       int      default 0                 not null comment '浏览数',
    thumbNum      int      default 0                 not null comment '点赞数',
    userId        bigint                             not null comment '创建用户id',
    createTime    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete      tinyint  default 0                 not null comment '是否删除'
) comment '帖子';

-- 帖子点赞表
create table if not exists post_thumb
(
    id         bigint auto_increment comment 'id' primary key,
    postId     bigint                             not null comment '帖子 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '帖子点赞记录';

-- 举报表
create table if not exists report
(
    id             bigint auto_increment comment 'id' primary key,
    userId         bigint                             not null comment '创建用户id',
    reportedUserId bigint                             not null comment '被举报用户id',
    reportedPostId bigint                             not null comment '被举报帖子id',
    content        text                               null comment '举报内容',
    status         int      default 0                 not null comment '状态（0-未处理, 1-已处理）',
    createTime     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint  default 0                 not null comment '是否删除'
) comment '举报';