/*
 * Copyright (c) 2024 LangChat. TyCoding All Rights Reserved.
 *
 * Licensed under the GNU Affero General Public License, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gnu.org/licenses/agpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.tycoding.langchat.server.controller;

import cn.hutool.core.lang.Dict;
import cn.tycoding.langchat.ai.biz.mapper.AigcAppMapper;
import cn.tycoding.langchat.ai.biz.mapper.AigcKnowledgeMapper;
import cn.tycoding.langchat.ai.biz.mapper.AigcMessageMapper;
import cn.tycoding.langchat.common.core.utils.CommonResponse;
import cn.tycoding.langchat.upms.mapper.SysUserMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tycoding
 * @since 2024/6/8
 */
@RequestMapping("/aigc/statistic")
@RestController
@AllArgsConstructor
public class AigcStatisticsController {

    private final AigcMessageMapper aigcMessageMapper;
    private final SysUserMapper userMapper;
    private final AigcKnowledgeMapper aigcKnowledgeMapper;
    private final AigcAppMapper aigcAppMapper;

    @GetMapping("/requestBy30")
    public CommonResponse request30Chart() {
        return CommonResponse.ok(aigcMessageMapper.getReqChartBy30());
    }

    @GetMapping("/tokenBy30")
    public CommonResponse token30Chart() {
        return CommonResponse.ok(aigcMessageMapper.getTokenChartBy30());
    }

    @GetMapping("/token")
    public CommonResponse tokenChart() {
        return CommonResponse.ok(aigcMessageMapper.getTokenChart());
    }

    @GetMapping("/request")
    public CommonResponse requestChart() {
        return CommonResponse.ok(aigcMessageMapper.getReqChart());
    }

    @GetMapping("/home")
    public CommonResponse home() {
        Dict reqData = aigcMessageMapper.getCount();
        Dict totalData = aigcMessageMapper.getTotalSum();
        Dict userData = userMapper.getCount();
        Long totalKnowledge = aigcKnowledgeMapper.selectCount(Wrappers.query());
        Long totalPrompt = aigcAppMapper.selectCount(Wrappers.query());
        Dict result = Dict.create();
        result.putAll(reqData);
        result.putAll(totalData);
        result.putAll(userData);
        result.set("totalKnowledge", totalKnowledge.intValue()).set("totalPrompt", totalPrompt.intValue());
        return CommonResponse.ok(result);
    }
}
