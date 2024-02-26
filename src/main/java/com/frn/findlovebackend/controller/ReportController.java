package com.frn.findlovebackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.frn.findlovebackend.common.BaseResponse;
import com.frn.findlovebackend.common.ErrorCode;
import com.frn.findlovebackend.common.ResultUtils;
import com.frn.findlovebackend.exception.BusinessException;
import com.frn.findlovebackend.model.dto.ReportAddRequest;
import com.frn.findlovebackend.model.entity.Post;
import com.frn.findlovebackend.model.entity.Report;
import com.frn.findlovebackend.model.entity.User;
import com.frn.findlovebackend.model.enums.ReportStatusEnum;
import com.frn.findlovebackend.service.PostService;
import com.frn.findlovebackend.service.ReportService;
import com.frn.findlovebackend.service.UserService;
import javafx.geometry.Pos;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
}
