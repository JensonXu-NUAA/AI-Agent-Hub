package cn.tycoding.springai.repository;

import cn.tycoding.springai.data.entity.CustomChatMessageDO;
import cn.tycoding.springai.repository.mapper.SpringAIChatMemoryMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SpringAIChatMemoryRepository {

    private final SpringAIChatMemoryMapper springAIChatMemoryMapper;

    @Autowired
    public SpringAIChatMemoryRepository(SpringAIChatMemoryMapper springAIChatMemoryMapper) {
        this.springAIChatMemoryMapper = springAIChatMemoryMapper;
    }

    public void insert(CustomChatMessageDO customChatMessageDO) {
        springAIChatMemoryMapper.insert(customChatMessageDO);
    }

    public List<CustomChatMessageDO> selectByConversationId(String conversationId) {
        return springAIChatMemoryMapper.selectList(new LambdaQueryWrapper<CustomChatMessageDO>()
                .eq(CustomChatMessageDO::getConversationId, conversationId));
    }
}
