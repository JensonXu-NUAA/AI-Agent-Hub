package cn.tycoding.langchat.common.repository.mysql.mapper;

import cn.tycoding.langchat.common.repository.mysql.entity.AigcMessageDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AigcMessageMapperV2 extends BaseMapper<AigcMessageDO> {
}
