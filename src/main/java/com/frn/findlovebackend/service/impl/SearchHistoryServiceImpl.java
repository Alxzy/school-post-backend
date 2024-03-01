package com.frn.findlovebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frn.findlovebackend.common.ErrorCode;
import com.frn.findlovebackend.exception.BussinessException;
import com.frn.findlovebackend.model.entity.SearchHistory;
import com.frn.findlovebackend.service.SearchHistoryService;
import com.frn.findlovebackend.mapper.SearchHistoryMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【search_history(搜索记录)】的数据库操作Service实现
* @createDate 2024-03-01 17:19:30
*/
@Service
public class SearchHistoryServiceImpl extends ServiceImpl<SearchHistoryMapper, SearchHistory>
    implements SearchHistoryService{

    @Override
    public long addSearchHistory(String word) {
        // 1.非空校验
        if(StringUtils.isAnyBlank(word)){
            // 不报错且不执行后续逻辑
            return 0;
        }
        // 2.业务校验 100长度内
        if(word.length() > 100){
            throw new BussinessException(ErrorCode.PARAM_ERROR,"关键词长度过长");
        }
        // 3.执行后续操作
        synchronized (word.intern()){
            return addSearchHistoryIntern(word);
        }
    }

    @Override
    public long addSearchHistoryIntern(String word) {
        // 3.获取当前关键词个数
        QueryWrapper<SearchHistory> searchHistoryQueryWrapper = new QueryWrapper<>();
        searchHistoryQueryWrapper.eq("word",word);
        long count = this.count(searchHistoryQueryWrapper);
        SearchHistory searchHistory = new SearchHistory();
        // 1.如果未添加
        if(count == 0){// 填充对象,添加
            searchHistory = new SearchHistory();
            searchHistory.setNum(1);
            searchHistory.setWord(word);
            this.save(searchHistory);
        }else{// 已经有当前关键词,修改 num + 1
            searchHistory = this.getOne(searchHistoryQueryWrapper);
            searchHistory.setNum( searchHistory.getNum() + 1);
            this.updateById(searchHistory);
        }

        return searchHistory.getId();
    }
}




