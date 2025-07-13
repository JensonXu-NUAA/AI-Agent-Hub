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
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.tycoding.langchat.ai.biz.entity.AigcAppApi;
import cn.tycoding.langchat.ai.biz.service.AigcAppApiService;
import cn.tycoding.langchat.common.core.annotation.ApiLog;
import cn.tycoding.langchat.common.core.utils.MybatisUtil;
import cn.tycoding.langchat.common.core.utils.QueryPage;
import cn.tycoding.langchat.common.core.utils.CommonResponse;
import cn.tycoding.langchat.server.consts.AppConst;
import cn.tycoding.langchat.server.store.AppChannelStore;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/aigc/app/api")
public class AigcAppApiController {

    private final AigcAppApiService appApiService;
    private final AppChannelStore appChannelStore;

    @GetMapping("/create/{id}/{channel}")
    public CommonResponse create(@PathVariable String id, @PathVariable String channel) {
        String uuid = AppConst.PREFIX + IdUtil.simpleUUID();
        appApiService.save(new AigcAppApi()
                .setAppId(id)
                .setApiKey(uuid)
                .setChannel(channel)
                .setCreateTime(new Date()));
        appChannelStore.init();
        return CommonResponse.ok();
    }

    @GetMapping("/list")
    public CommonResponse<List<AigcAppApi>> list(AigcAppApi data) {
        List<AigcAppApi> list = appApiService.list(Wrappers.<AigcAppApi>lambdaQuery()
                .eq(StrUtil.isNotBlank(data.getAppId()), AigcAppApi::getAppId, data.getAppId())
                .eq(StrUtil.isNotBlank(data.getChannel()), AigcAppApi::getChannel, data.getChannel())
                .orderByDesc(AigcAppApi::getCreateTime));
        return CommonResponse.ok(list);
    }

    @GetMapping("/page")
    public CommonResponse<Dict> page(AigcAppApi data, QueryPage queryPage) {
        IPage<AigcAppApi> iPage = appApiService.page(MybatisUtil.wrap(data, queryPage),
                Wrappers.<AigcAppApi>lambdaQuery()
                        .like(StringUtils.isNotEmpty(data.getAppId()), AigcAppApi::getAppId, data.getAppId())
                        .eq(StrUtil.isNotBlank(data.getChannel()), AigcAppApi::getChannel, data.getChannel())
                        .orderByDesc(AigcAppApi::getCreateTime));
        return CommonResponse.ok(MybatisUtil.getData(iPage));
    }

    @GetMapping("/{id}")
    public CommonResponse<AigcAppApi> findById(@PathVariable String id) {
        AigcAppApi api = appApiService.getById(id);
        return CommonResponse.ok(api);
    }

    @PostMapping
    @ApiLog("新增API渠道")
    public CommonResponse add(@RequestBody AigcAppApi data) {
        data.setCreateTime(new Date());
        appApiService.save(data);
        appChannelStore.init();
        return CommonResponse.ok();
    }

    @PutMapping
    @ApiLog("修改API渠道")
    public CommonResponse update(@RequestBody AigcAppApi data) {
        appApiService.updateById(data);
        appChannelStore.init();
        return CommonResponse.ok();
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除API渠道")
    public CommonResponse delete(@PathVariable String id) {
        appApiService.removeById(id);
        appChannelStore.init();
        return CommonResponse.ok();
    }
}