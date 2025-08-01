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
import cn.tycoding.langchat.common.core.exception.ServiceException;
import cn.tycoding.langchat.common.core.properties.AuthProps;
import cn.tycoding.langchat.common.core.utils.MybatisUtil;
import cn.tycoding.langchat.common.core.utils.QueryPage;
import cn.tycoding.langchat.common.core.utils.CommonResponse;
import cn.tycoding.langchat.upms.dto.UserInfo;
import cn.tycoding.langchat.upms.entity.SysUser;
import cn.tycoding.langchat.upms.service.SysUserService;
import cn.tycoding.langchat.upms.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户表(User)表控制层
 *
 * @author tycoding
 * @since 2024/4/15
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/upms/user")
public class SysUserController {

    private final SysUserService sysUserService;
    private final AuthProps authProps;

    @GetMapping("/checkName")
    public CommonResponse<Boolean> checkName(UserInfo sysUser) {
        return CommonResponse.ok(sysUserService.checkName(sysUser));
    }

    @GetMapping("/list")
    public CommonResponse<List<SysUser>> list(SysUser sysUser) {
        return CommonResponse.ok(sysUserService.list(sysUser));
    }

    @GetMapping("/page")
    public CommonResponse<Dict> page(UserInfo user, QueryPage queryPage) {
        return CommonResponse.ok(MybatisUtil.getData(sysUserService.page(user, queryPage)));
    }

    @GetMapping("/{id}")
    public CommonResponse<UserInfo> findById(@PathVariable String id) {
        return CommonResponse.ok(sysUserService.findById(id));
    }

    @PostMapping
    @ApiLog("新增用户")
    @SaCheckPermission("upms:user:add")
    public CommonResponse<SysUser> add(@RequestBody UserInfo user) {
        user.setPassword(AuthUtil.encode(authProps.getSaltKey(), user.getPassword()));
        sysUserService.add(user);
        return CommonResponse.ok();
    }

    @PutMapping
    @ApiLog("修改用户")
    @SaCheckPermission("upms:user:update")
    public CommonResponse update(@RequestBody UserInfo user) {
        sysUserService.update(user);
        return CommonResponse.ok();
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除用户")
    @SaCheckPermission("upms:user:delete")
    public CommonResponse delete(@PathVariable String id) {
        SysUser user = sysUserService.getById(id);
        if (user != null) {
            sysUserService.delete(id, user.getUsername());
        }
        return CommonResponse.ok();
    }

    @PutMapping("/resetPass")
    @ApiLog("重置密码")
    @SaCheckPermission("upms:user:reset")
    public CommonResponse resetPass(@RequestBody UserInfo data) {
        SysUser user = sysUserService.getById(data.getId());
        if (user != null) {
            sysUserService.reset(data.getId(), data.getPassword(), user.getUsername());
        }
        return CommonResponse.ok();
    }

    @PutMapping("/updatePass")
    @ApiLog("修改密码")
    @SaCheckPermission("upms:user:updatePass")
    public CommonResponse updatePass(@RequestBody UserInfo data) {
        SysUser user = sysUserService.getById(data.getId());
        if (user == null || !AuthUtil.decrypt(authProps.getSaltKey(), user.getPassword()).equals(data.getPassword())) {
            throw new ServiceException("Old password entered incorrectly, please re-enter");
        }
        user.setPassword(AuthUtil.encode(authProps.getSaltKey(), data.getPassword()));
        return CommonResponse.ok();
    }
}
