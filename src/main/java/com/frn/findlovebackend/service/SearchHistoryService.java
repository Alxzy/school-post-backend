package com.frn.findlovebackend.service;

import com.frn.findlovebackend.model.entity.SearchHistory;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【search_history(搜索记录)】的数据库操作Service
* @createDate 2024-03-01 17:19:30
*/
public interface SearchHistoryService extends IService<SearchHistory> {

    long addSearchHistory(String word);

    long addSearchHistoryIntern(String word);
}
