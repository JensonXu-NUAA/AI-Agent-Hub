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

package cn.tycoding.langchat.upms.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.lang.Dict;
import cn.tycoding.langchat.common.core.annotation.ApiLog;
import cn.tycoding.langchat.common.core.utils.MybatisUtil;
import cn.tycoding.langchat.common.core.utils.QueryPage;
import cn.tycoding.langchat.common.core.utils.CommonResponse;
import cn.tycoding.langchat.upms.dto.SysRoleDTO;
import cn.tycoding.langchat.upms.entity.SysRole;
import cn.tycoding.langchat.upms.service.SysRoleService;
import cn.tycoding.langchat.upms.utils.AuthUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色表(Role)表控制层
 *
 * @author tycoding
 * @since 2024/4/15
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/upms/role")
public class SysRoleController {

    private final SysRoleService sysRoleService;

    @GetMapping("/list")
    public CommonResponse<List<SysRole>> list(SysRole sysRole) {
        return CommonResponse.ok(sysRoleService.list(new LambdaQueryWrapper<SysRole>()
                .ne(SysRole::getCode, AuthUtil.ADMINISTRATOR)));
    }

    @GetMapping("/page")
    public CommonResponse<Dict> page(SysRole role, QueryPage queryPage) {
        return CommonResponse.ok(MybatisUtil.getData(sysRoleService.page(role, queryPage)));
    }

    @GetMapping("/{id}")
    public CommonResponse<SysRoleDTO> findById(@PathVariable String id) {
        return CommonResponse.ok(sysRoleService.findById(id));
    }

    @PostMapping
    @ApiLog("新增角色")
    @SaCheckPermission("upms:role:add")
    public CommonResponse add(@RequestBody SysRoleDTO sysRole) {
        sysRoleService.add(sysRole);
        return CommonResponse.ok();
    }

    @PutMapping
    @ApiLog("修改角色")
    @SaCheckPermission("upms:role:update")
    public CommonResponse update(@RequestBody SysRoleDTO sysRole) {
        sysRoleService.update(sysRole);
        return CommonResponse.ok();
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除角色")
    @SaCheckPermission("upms:role:delete")
    public CommonResponse delete(@PathVariable String id) {
        sysRoleService.delete(id);
        return CommonResponse.ok();
    }
}
