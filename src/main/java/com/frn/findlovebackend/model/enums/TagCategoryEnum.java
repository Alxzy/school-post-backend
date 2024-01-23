package com.frn.findlovebackend.model.enums;

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

    TagCategoryEnum(String vale) {
        this.value = vale;
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
}
