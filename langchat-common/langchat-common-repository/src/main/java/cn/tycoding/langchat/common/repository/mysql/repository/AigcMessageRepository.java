package cn.tycoding.langchat.common.repository.mysql.repository;

import cn.tycoding.langchat.common.repository.mysql.entity.AigcMessageDO;
import cn.tycoding.langchat.common.repository.mysql.mapper.AigcMessageMapperV2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AigcMessageRepository {

    private final AigcMessageMapperV2 aigcMessageMapper;

    @Autowired
    public AigcMessageRepository(AigcMessageMapperV2 aigcMessageMapper) {
        this.aigcMessageMapper = aigcMessageMapper;
    }

    public int insert(AigcMessageDO aigcMessageDO) {
        return aigcMessageMapper.insert(aigcMessageDO);
    }
}
