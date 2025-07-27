package cn.tycoding.springai.data.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("ai_chat_message_persistence")
public class CustomChatMessageDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private String userId;

    @TableField("chat_id")
    private String chatId;

    @TableField("type")
    private String type;

    @TableField("conversation_id")
    private String conversationId;

    @TableField("message")
    private String message;

    @TableField("model_name")
    private String modelName;

    @TableField("create_time")
    private Date createTime;
}
