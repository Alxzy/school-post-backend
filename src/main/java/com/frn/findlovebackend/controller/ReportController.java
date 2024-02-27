package com.frn.findlovebackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.frn.findlovebackend.annotation.AuthCheck;
import com.frn.findlovebackend.common.BaseResponse;
import com.frn.findlovebackend.common.DeleteRequest;
import com.frn.findlovebackend.common.ErrorCode;
import com.frn.findlovebackend.common.ResultUtils;
import com.frn.findlovebackend.constant.CommonConstant;
import com.frn.findlovebackend.exception.BusinessException;
import com.frn.findlovebackend.model.dto.*;
import com.frn.findlovebackend.model.entity.Post;
import com.frn.findlovebackend.model.entity.Report;
import com.frn.findlovebackend.model.entity.User;
import com.frn.findlovebackend.model.enums.ReportStatusEnum;
import com.frn.findlovebackend.service.PostService;
import com.frn.findlovebackend.service.ReportService;
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
 * 举报模块接口
 */
@RestController
@Slf4j
@RequestMapping("/report")
public class ReportController {
    @Resource
    ReportService reportService;

    @Resource
    UserService userService;

    @Resource
    PostService postService;

    //region 增删改查

    /**
     * 举报添加请求
     * @param reportAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> reportAdd(@RequestBody ReportAddRequest reportAddRequest, HttpServletRequest request){
        // 1.非参数校验
        // 1.1 请求体是否为空
        if(reportAddRequest == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        // 1.2 获取参数
        Long reportedPostId = reportAddRequest.getReportedPostId();
        String content = reportAddRequest.getContent();
        // 2.权限校验
        // 2.1 必须登录
        User loginUser = userService.getLoginUser(request);
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NO_AUTO,"未登录");
        }
        Long userId = loginUser.getId();
        // 3.参数校验
        // 3.1举报内容不能过长
        if(StringUtils.isNotBlank(content) && content.length() > 8192){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"内容过长");
        }
        // 3.2举报帖子的id必须存在
        Post post = postService.getById(reportedPostId);
        if(post == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"举报对象不存在");
        }
        Long reportedUserId = post.getUserId();

        // 4.创建对象,填充参数并保存到数据库
        Report report = new Report();
        report.setReportedPostId(reportedPostId);
        report.setContent(content);
        report.setReportedUserId(reportedUserId);
        report.setUserId(userId);
        report.setStatus(ReportStatusEnum.DEFAULT.getValue());

        boolean result = reportService.save(report);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 5.返回
        return ResultUtils.success(report.getId());
    }

    /**
     * 删除举报接口
     * @param request
     * @return
     */
    @DeleteMapping("/delete")
    public BaseResponse<Boolean> deleteReport(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        // 1.参数校验
        // 1.1 非空
        if (deleteRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        // 1.2 业务校验 对应id的帖子是否存在
        Long reportId = deleteRequest.getId();
        Report report = reportService.getById(reportId);
        if (report == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 1.3 权限校验 仅本人和管理员可以删除
        User currentLoginUser = userService.getLoginUser(request);
        if (!userService.isAdmin(request) && !report.getUserId().equals(currentLoginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTO);
        }
        // 2.删除
        boolean result = reportService.removeById(reportId);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 3.返回
        return ResultUtils.success(result);
    }

    @PutMapping("/update")
    public BaseResponse<Boolean> updateReportById(@RequestBody ReportUpdateRequest reportUpdateRequest, HttpServletRequest request) {
        // 1.参数校验
        // 1.1 非空
        if (request == null || reportUpdateRequest == null || reportUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        // 1.2 业务校验
        // 实体存在
        long reportId = reportUpdateRequest.getId();
        Report oldReport = reportService.getById(reportId);
        if (oldReport == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 1.2 内容不能过长
        Report report = new Report();
        BeanUtils.copyProperties(reportUpdateRequest,report);
        String content = report.getContent();
        if(StringUtils.isNotBlank(content) && content.length() > 8192){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"内容过长");
        }
        // 1.2 状态必须被包含
        Integer status = report.getStatus();
        if(status == null){// 不能为空
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        if(status != null && !ReportStatusEnum.getValues().contains(status)){// 必须被包含
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        // 1.3 权限校验
        User currentLoginUser = userService.getLoginUser(request);
        if (!userService.isAdmin(request) && !oldReport.getUserId().equals(currentLoginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTO);
        }
        // 只有管理员可以修改 状态
        if (!oldReport.getStatus().equals(status) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTO);
        }

        // 2.按照举报id修改
        // 这里解释一下为什么可以，mybatisplus的默认更新策略是非 null
        boolean result = reportService.updateById(report);
        return ResultUtils.success(result);

    }

    /**
     * 根据 id 获取举报
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Report> getReportById(Long id) {
        // 参数校验
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        Report report = reportService.getById(id);
        if(report == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(report);
    }

    @GetMapping("/list")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<List<Report>> listReport(ReportQueryRequest reportQueryRequest) {
        // 1.参数校验
        if (reportQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        // 2.填充查询对象
        Report reportQuery = new Report();
        BeanUtils.copyProperties(reportQueryRequest, reportQuery);
        QueryWrapper<Report> reportQueryWrapper = new QueryWrapper<>(reportQuery);
        // 3.查询
        List<Report> list = reportService.list(reportQueryWrapper);
        // 4.返回
        return ResultUtils.success(list);
    }

    /**
     * 分页获取列表
     * @param reportQueryRequest
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<Report>> listReportByPage(ReportQueryRequest reportQueryRequest) {
        // 1.参数校验
        if (reportQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        // 2.填充查询对象
        Report reportQuery = new Report();
        BeanUtils.copyProperties(reportQueryRequest, reportQuery);

        //获取参数
        long current = reportQueryRequest.getCurrent();
        long pageSize = reportQueryRequest.getPageSize();
        String sortField = reportQueryRequest.getSortField();
        String sortOrder = reportQueryRequest.getSortOrder();
        String content = reportQueryRequest.getContent();
        // 2.1content 支持模糊搜索
        reportQuery.setContent(null);
        // 2.2 业务 限制爬虫
        if (pageSize > 50) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        // 3.查询
        QueryWrapper<Report> queryWrapper = new QueryWrapper<>(reportQuery);
        queryWrapper.like(StringUtils.isNotBlank(content),"content",content);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),sortOrder.equals(CommonConstant.SORT_ORDER_ASC),sortField);
        Page<Report> page = reportService.page(new Page<>(current, pageSize), queryWrapper);

        // 4.返回
        return ResultUtils.success(page);
    }

    //endregion
}
