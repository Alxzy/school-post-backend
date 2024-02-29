package com.frn.findlovebackend.job;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.frn.findlovebackend.model.entity.Post;
import com.frn.findlovebackend.model.entity.Tag;
import com.frn.findlovebackend.service.PostService;
import com.frn.findlovebackend.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 * @date 2024-02-29 16:51
 * 定时任务,定期同步各个标签在帖子中的使用数
 */
@Component
@Slf4j
public class SyncTagPostNumJob {

    @Resource
    PostService postService;

    @Resource
    TagService tagService;

    /**
     * 定时任务,每隔 12 小时执行一次
     */
    @Scheduled(fixedDelay = 12 * 60 * 60 * 1000)
    public void doStart() {
        log.info("SyncTagPostNumJob start");
        // 1.准备工作
        // 获取所有的帖子
        List<Post> postList = postService.list();
        // 存储标签使用数
        HashMap<String, Integer> tagNameCountMap = new HashMap<>();
        // 2.遍历并计算
        postList.forEach(post -> {
            increase(tagNameCountMap,post.getPlace());
            increase(tagNameCountMap,post.getJob());
            increase(tagNameCountMap,post.getEducation());
            increase(tagNameCountMap,post.getHobby());
            increase(tagNameCountMap,post.getLoveExp());
        });
        // 3.将结果写入到数据库(修改操作)
        for (Map.Entry<String, Integer> tagNameCountEntry : tagNameCountMap.entrySet()) {
            UpdateWrapper<Tag> tagUpdateWrapper = new UpdateWrapper<>();
            tagUpdateWrapper.eq("tagName",tagNameCountEntry.getKey())
                    .set("postNum",tagNameCountEntry.getValue());
            tagService.update(tagUpdateWrapper);
        }

        log.info("SyncTagPostNumJob end");
    }

    /**
     * 值 + 1
     *
     * @param map
     * @param key
     */
    private void increase(Map<String, Integer> map, String key) {
        map.put(key, map.getOrDefault(key, 0) + 1);
    }

}
