package cn.tycoding.langchat.common.repository.mysql.repository;

import cn.tycoding.langchat.common.repository.mysql.entity.AigcModelDO;
import cn.tycoding.langchat.common.repository.mysql.mapper.AigcModelMapperV2;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AigcModelRepository {

    private final AigcModelMapperV2 aigcModelMapper;

    @Autowired
    public AigcModelRepository(AigcModelMapperV2 aigcModelMapper) {
        this.aigcModelMapper = aigcModelMapper;
    }

    public List<AigcModelDO> getChatModels() {
        return aigcModelMapper.selectList(Wrappers.<AigcModelDO>lambdaQuery()
                .eq(AigcModelDO::getType, "CHAT"));
    }
}
