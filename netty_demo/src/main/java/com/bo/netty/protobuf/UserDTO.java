package com.bo.netty.protobuf;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

/**
 * @Author: gpb
 * @Date: 2023/4/19 14:29
 * @Description:
 */
@Data
public class UserDTO {

    private UserPOJO.user user;

    private ChannelHandlerContext ctx;

}
