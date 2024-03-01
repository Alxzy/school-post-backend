package com.frn.findlovebackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.frn.findlovebackend.annotation.AuthCheck;
import com.frn.findlovebackend.common.BaseResponse;
import com.frn.findlovebackend.common.DeleteRequest;
import com.frn.findlovebackend.common.ErrorCode;
import com.frn.findlovebackend.common.ResultUtils;
import com.frn.findlovebackend.constant.CommonConstant;
import com.frn.findlovebackend.exception.BussinessException;
import com.frn.findlovebackend.model.dto.ReportAddRequest;
import com.frn.findlovebackend.model.dto.ReportQueryRequest;
import com.frn.findlovebackend.model.dto.ReportUpdateRequest;
import com.frn.findlovebackend.model.dto.SearchHistoryQueryRequest;
import com.frn.findlovebackend.model.entity.Post;
import com.frn.findlovebackend.model.entity.Report;
import com.frn.findlovebackend.model.entity.SearchHistory;
import com.frn.findlovebackend.model.entity.User;
import com.frn.findlovebackend.model.enums.ReportStatusEnum;
import com.frn.findlovebackend.service.PostService;
import com.frn.findlovebackend.service.ReportService;
import com.frn.findlovebackend.service.SearchHistoryService;
import com.frn.findlovebackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @date 2024-02-26 21:47
 * 搜索记录模块接口
 */
@RestController
@Slf4j
@RequestMapping("/search_history")
public class SearchHistoryController {
    @Resource
    SearchHistoryService searchHistoryService;

    @Resource
    UserService userService;

    //region 增删改查

    /**
     * 搜索记录添加请求
     * @param searchHistoryAdd 搜索记录对象
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> searchHistoryAdd(@RequestBody SearchHistory searchHistoryAdd){
        // 1.非参数校验
        // 1.1 请求体是否为空
        if(searchHistoryAdd == null){
            throw new BussinessException(ErrorCode.PARAM_ERROR);
        }
        // 1.2 获取参数
        String word = searchHistoryAdd.getWord();
        // 2.校验并保存
        long result = searchHistoryService.addSearchHistory(word);
        // 3.返回
        return ResultUtils.success(result);
    }

    /**
     * 删除搜索记录接口
     * @param request
     * @return
     */
    @DeleteMapping("/delete")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> deleteSearchHistory(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        // 1.参数校验
        // 1.1 非空
        if (deleteRequest == null || request == null) {
            throw new BussinessException(ErrorCode.PARAM_ERROR);
        }
        // 1.2 业务校验 对应id的帖子是否存在
        Long searchHistoryId = deleteRequest.getId();
        SearchHistory searchHistory = searchHistoryService.getById(searchHistoryId);
        if (searchHistory == null) {
            throw new BussinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 2.删除
        boolean result = searchHistoryService.removeById(searchHistoryId);
        if(!result){
            throw new BussinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 3.返回
        return ResultUtils.success(result);
    }



    /**
     * 分页获取列表
     * @param searchHistoryQueryRequest
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<SearchHistory>> listSearchHistoryByPage(SearchHistoryQueryRequest searchHistoryQueryRequest){
        // 1.参数校验
        if (searchHistoryQueryRequest == null) {
            throw new BussinessException(ErrorCode.PARAM_ERROR);
        }
        // 2.填充查询对象
        SearchHistory searchHistoryQuery = new SearchHistory();
        BeanUtils.copyProperties(searchHistoryQueryRequest, searchHistoryQuery);

        //获取参数
        long current = searchHistoryQueryRequest.getCurrent();
        long pageSize = searchHistoryQueryRequest.getPageSize();
        String sortField = searchHistoryQueryRequest.getSortField();
        String sortOrder = searchHistoryQueryRequest.getSortOrder();
        String word = searchHistoryQueryRequest.getWord();
        // 2.1content 支持模糊搜索
        searchHistoryQuery.setWord(null);
        // 2.2 业务 限制爬虫
        if (pageSize > 50) {
            throw new BussinessException(ErrorCode.PARAM_ERROR);
        }

        // 3.查询
        QueryWrapper<SearchHistory> queryWrapper = new QueryWrapper<>(searchHistoryQuery);
        queryWrapper.like(StringUtils.isNotBlank(word),"word",word);
        // 默认 按照 数量 从大到小
        queryWrapper.orderBy(StringUtils.isAnyBlank(sortField),sortOrder.equals(CommonConstant.SORT_ORDER_DESC),"num");
        Page<SearchHistory> page = searchHistoryService.page(new Page<>(current, pageSize), queryWrapper);

        // 4.返回
        return ResultUtils.success(page);
    }

    //endregion
}
