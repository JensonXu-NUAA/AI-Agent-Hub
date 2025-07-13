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

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;

import cn.hutool.core.util.StrUtil;
import cn.tycoding.langchat.ai.biz.component.ProviderRefreshEvent;
import cn.tycoding.langchat.ai.biz.entity.AigcModel;
import cn.tycoding.langchat.ai.biz.service.AigcModelService;
import cn.tycoding.langchat.common.core.annotation.ApiLog;
import cn.tycoding.langchat.common.core.component.SpringContextHolder;
import cn.tycoding.langchat.common.core.utils.MybatisUtil;
import cn.tycoding.langchat.common.core.utils.QueryPage;
import cn.tycoding.langchat.common.core.utils.CommonResponse;
import cn.tycoding.langchat.common.repository.mysql.entity.AigcModelDO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author tycoding
 * @since 2024/4/15
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/aigc/model")
public class AigcModelController {

    private final AigcModelService modelService;
    private final SpringContextHolder contextHolder;

    @SaIgnore
    @GetMapping("/getModels")
    public CommonResponse<List<AigcModelDO>> getModels() {
        return CommonResponse.ok(modelService.getChatModels());
    }

    @GetMapping("/list")
    public CommonResponse<List<AigcModel>> list(AigcModel data) {
        return CommonResponse.ok(modelService.list(data));
    }

    @GetMapping("/page")
    public CommonResponse list(AigcModel data, QueryPage queryPage) {
        Page<AigcModel> iPage = modelService.page(data, queryPage);
        return CommonResponse.ok(MybatisUtil.getData(iPage));
    }

    @GetMapping("/{id}")
    public CommonResponse<AigcModel> findById(@PathVariable String id) {
        return CommonResponse.ok(modelService.selectById(id));
    }

    @PostMapping
    @ApiLog("添加模型")
    @SaCheckPermission("aigc:model:add")
    public CommonResponse add(@RequestBody AigcModel data) {
        if (StrUtil.isNotBlank(data.getApiKey()) && data.getApiKey().contains("*")) {
            data.setApiKey(null);
        }
        if (StrUtil.isNotBlank(data.getSecretKey()) && data.getSecretKey().contains("*")) {
            data.setSecretKey(null);
        }
        modelService.save(data);
        SpringContextHolder.publishEvent(new ProviderRefreshEvent(data));
        return CommonResponse.ok();
    }

    @PutMapping
    @ApiLog("修改模型")
    @SaCheckPermission("aigc:model:update")
    public CommonResponse update(@RequestBody AigcModel data) {
        if (StrUtil.isNotBlank(data.getApiKey()) && data.getApiKey().contains("*")) {
            data.setApiKey(null);
        }
        if (StrUtil.isNotBlank(data.getSecretKey()) && data.getSecretKey().contains("*")) {
            data.setSecretKey(null);
        }
        modelService.updateById(data);
        SpringContextHolder.publishEvent(new ProviderRefreshEvent(data));
        return CommonResponse.ok();
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除模型")
    @SaCheckPermission("aigc:model:delete")
    public CommonResponse delete(@PathVariable String id) {
        modelService.removeById(id);

        // Delete dynamically registered beans, according to ID
        contextHolder.unregisterBean(id);
        return CommonResponse.ok();
    }
}

