package com.frn.findlovebackend.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Administrator
 * @version 1.0
 * @date 2024-01-10 16:07
 * 修改举报请求
 */
@Data
public class ReportUpdateRequest implements Serializable {

    private static final long serialVersionUID = 5149430148584223484L;
    /**
     * id
     */
    private long id;


    /**
     * 举报内容
     */
    private String content;

    /**
     * 状态（0-未处理, 1-已处理）
     */
    private Integer status;


}
