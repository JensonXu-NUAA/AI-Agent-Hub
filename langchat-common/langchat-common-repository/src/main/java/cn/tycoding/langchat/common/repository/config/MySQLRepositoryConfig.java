package cn.tycoding.langchat.common.repository.config;


import cn.tycoding.langchat.common.repository.mysql.mapper.AigcConversationMapperV2;
import cn.tycoding.langchat.common.repository.mysql.mapper.AigcModelMapperV2;
import cn.tycoding.langchat.common.repository.mysql.repository.AigcConversationRepository;

import cn.tycoding.langchat.common.repository.mysql.repository.AigcModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MySQL数据库配置
 *
 */
@Configuration
public class MySQLRepositoryConfig {

    private final AigcConversationMapperV2 aigcConversationMapper;
    private final AigcModelMapperV2 aigcModelMapper;

    @Autowired
    public MySQLRepositoryConfig(AigcConversationMapperV2 aigcConversationMapper, AigcModelMapperV2  aigcModelMapper) {
        this.aigcConversationMapper = aigcConversationMapper;
        this.aigcModelMapper = aigcModelMapper;
    }

    @Bean
    public AigcConversationRepository aigcConversationRepository() {
        return new AigcConversationRepository(aigcConversationMapper);
    }

    @Bean
    public AigcModelRepository aigcModelRepository() {
        return new AigcModelRepository(aigcModelMapper);
    }
}
