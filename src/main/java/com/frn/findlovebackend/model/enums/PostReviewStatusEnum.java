package com.frn.findlovebackend.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @version 1.0
 * @date 2024-02-04 15:18
 * 帖子审核状态枚举类
 */
public enum PostReviewStatusEnum {
    REVIEWING("待审核",0),
    PASS("通过",1),
    REJECT("拒绝",2);

    private final String content;

    private final int value;

    PostReviewStatusEnum(String content, int value) {
        this.content = content;
        this.value = value;
    }

    public String getContent() {
        return content;
    }

    public int getValue() {
        return value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getValues(){
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }
}
