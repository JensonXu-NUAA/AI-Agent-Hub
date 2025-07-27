package cn.tycoding.springai.repository.mapper;

import cn.tycoding.springai.data.entity.CustomChatMessageDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SpringAIChatMemoryMapper extends BaseMapper<CustomChatMessageDO> {
}
