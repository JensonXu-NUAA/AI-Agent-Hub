package cn.tycoding.langchat.common.repository.mysql.repository;

import cn.tycoding.langchat.common.repository.mysql.mapper.AigcConversationMapperV2;
import cn.tycoding.langchat.common.repository.mysql.entity.AigcConversationDO;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class AigcConversationRepository {

    private final AigcConversationMapperV2 aigcConversationMapper;

    @Autowired
    public AigcConversationRepository(AigcConversationMapperV2 aigcConversationMapper) {
        this.aigcConversationMapper = aigcConversationMapper;
    }

    public List<AigcConversationDO> findByUserId(String userId) {
        return aigcConversationMapper.selectList(
                Wrappers.<AigcConversationDO>lambdaQuery()
                        .eq(AigcConversationDO::getUserId, userId)  // 筛选出 userId 等于传入参数的对话记录
                        .orderByDesc(AigcConversationDO::getCreateTime));  // 按照对话创建时间降序排列，最新创建的对话排在前面
    }

    @Transactional
    public int insert(AigcConversationDO aigcConversationDO) {
        return aigcConversationMapper.insert(aigcConversationDO);
    }
}