package com.frn.findlovebackend.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @version 1.0
 * @date 2023-12-28 17:36
 * 标签分组枚举类
 */
public enum TagCategoryEnum {
    EDUCATION("学历"),
    PLACE("地点"),
    JOB("职业"),
    LOVE_EXP("感情经历"),
    HOBBY("爱好");

    private final String value;

    TagCategoryEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    // 查询是否包含
    public static boolean contains(String testValue) {
        for (TagCategoryEnum c : TagCategoryEnum.values()) {
            if (c.getValue().equals(testValue)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取值列表
     * @return
     */
    public static List<String> getValues(){
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }
}
