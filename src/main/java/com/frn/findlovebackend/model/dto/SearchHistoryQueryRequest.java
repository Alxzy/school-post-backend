package com.frn.findlovebackend.model.dto;

import com.frn.findlovebackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author Administrator
 * @version 1.0
 * @date 2024-03-01 20:02
 * 搜索记录查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true) // 使得保证有父类的 equals and hashcode
public class SearchHistoryQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 搜索关键字
     */
    private String word;


    private static final long serialVersionUID = 6966761499813561374L;
}
