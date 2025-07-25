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
import cn.tycoding.langchat.ai.biz.entity.AigcDocs;
import cn.tycoding.langchat.ai.biz.mapper.AigcDocsMapper;
import cn.tycoding.langchat.common.core.annotation.ApiLog;
import cn.tycoding.langchat.common.core.utils.MybatisUtil;
import cn.tycoding.langchat.common.core.utils.QueryPage;
import cn.tycoding.langchat.common.core.utils.CommonResponse;
import cn.tycoding.langchat.server.service.EmbeddingService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author tycoding
 * @since 2024/4/15
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/aigc/docs")
public class AigcDocsController {

    private final AigcDocsMapper docsMapper;
    private final EmbeddingService embeddingService;

    @GetMapping("/list")
    public CommonResponse<List<AigcDocs>> list(AigcDocs data) {
        return CommonResponse.ok(docsMapper.selectList(Wrappers.<AigcDocs>lambdaQuery().orderByDesc(AigcDocs::getCreateTime)));
    }

    @GetMapping("/page")
    public CommonResponse list(AigcDocs data, QueryPage queryPage) {
        Page<AigcDocs> page = new Page<>(queryPage.getPage(), queryPage.getLimit());
        return CommonResponse.ok(MybatisUtil.getData(docsMapper.selectPage(page, Wrappers.<AigcDocs>lambdaQuery()
                .eq(data.getKnowledgeId() != null, AigcDocs::getKnowledgeId, data.getKnowledgeId())
                .eq(data.getSliceStatus() != null, AigcDocs::getSliceStatus, data.getSliceStatus())
                .orderByDesc(AigcDocs::getCreateTime)
        )));
    }

    @GetMapping("/{id}")
    public CommonResponse<AigcDocs> findById(@PathVariable String id) {
        return CommonResponse.ok(docsMapper.selectById(id));
    }

    @PostMapping
    @ApiLog("新增文档")
    @SaCheckPermission("aigc:docs:add")
    public CommonResponse add(@RequestBody AigcDocs data) {
        docsMapper.insert(data);
        return CommonResponse.ok();
    }

    @PutMapping
    @ApiLog("修改文档")
    @SaCheckPermission("aigc:docs:update")
    public CommonResponse update(@RequestBody AigcDocs data) {
        docsMapper.updateById(data);
        return CommonResponse.ok();
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除文档")
    @SaCheckPermission("aigc:docs:delete")
    @Transactional
    public CommonResponse delete(@PathVariable String id) {
        // 删除切面数据
        embeddingService.clearDocSlices(id);

        // 删除文档
        docsMapper.deleteById(id);
        return CommonResponse.ok();
    }
}

