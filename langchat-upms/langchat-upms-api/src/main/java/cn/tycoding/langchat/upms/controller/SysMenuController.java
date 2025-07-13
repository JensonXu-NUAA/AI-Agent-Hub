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
import cn.tycoding.langchat.common.core.annotation.ApiLog;
import cn.tycoding.langchat.common.core.utils.CommonResponse;
import cn.tycoding.langchat.upms.dto.MenuTree;
import cn.tycoding.langchat.upms.entity.SysMenu;
import cn.tycoding.langchat.upms.service.SysMenuService;
import cn.tycoding.langchat.upms.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单表(Menu)表控制层
 *
 * @author tycoding
 * @since 2024/4/15
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/upms/menu")
public class SysMenuController {

    private final SysMenuService sysMenuService;

    @GetMapping("/tree")
    public CommonResponse<List<MenuTree<SysMenu>>> tree(SysMenu sysMenu) {
        return CommonResponse.ok(sysMenuService.tree(sysMenu));
    }

    @GetMapping("/build")
    public CommonResponse<List<MenuTree<SysMenu>>> build() {
        return CommonResponse.ok(sysMenuService.build(AuthUtil.getUserId()));
    }

    @GetMapping("/list")
    public CommonResponse<List<SysMenu>> list(SysMenu sysMenu) {
        return CommonResponse.ok(sysMenuService.list(sysMenu));
    }

    @GetMapping("/{id}")
    public CommonResponse<SysMenu> findById(@PathVariable String id) {
        return CommonResponse.ok(sysMenuService.getById(id));
    }

    @PostMapping
    @ApiLog("新增菜单")
    @SaCheckPermission("upms:menu:add")
    public CommonResponse add(@RequestBody SysMenu sysMenu) {
        sysMenuService.add(sysMenu);
        return CommonResponse.ok();
    }

    @PutMapping
    @ApiLog("修改菜单")
    @SaCheckPermission("upms:menu:update")
    public CommonResponse update(@RequestBody SysMenu sysMenu) {
        sysMenuService.update(sysMenu);
        return CommonResponse.ok();
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除菜单")
    @SaCheckPermission("upms:menu:delete")
    public CommonResponse delete(@PathVariable String id) {
        sysMenuService.delete(id);
        return CommonResponse.ok();
    }
}
