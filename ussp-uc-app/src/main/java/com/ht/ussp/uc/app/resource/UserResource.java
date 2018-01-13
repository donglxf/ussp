package com.ht.ussp.uc.app.resource;

import java.util.ArrayList;
import java.util.List;

import com.ht.ussp.core.PageResult;
import com.ht.ussp.uc.app.vo.Page;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import com.ht.ussp.uc.app.domain.HtBoaInLogin;
import com.ht.ussp.uc.app.domain.HtBoaInUser;
import com.ht.ussp.uc.app.domain.HtBoaInUserApp;
import com.ht.ussp.uc.app.model.ResponseModal;
import com.ht.ussp.uc.app.model.SysStatus;
import com.ht.ussp.uc.app.service.HtBoaInLoginService;
import com.ht.ussp.uc.app.service.HtBoaInPositionRoleService;
import com.ht.ussp.uc.app.service.HtBoaInPositionUserService;
import com.ht.ussp.uc.app.service.HtBoaInUserAppService;
import com.ht.ussp.uc.app.service.HtBoaInUserRoleService;
import com.ht.ussp.uc.app.service.HtBoaInUserService;
import com.ht.ussp.uc.app.util.BeanUtils;
import com.ht.ussp.uc.app.util.LogicUtil;
import com.ht.ussp.uc.app.vo.UserVo;

import io.swagger.annotations.ApiOperation;

/**
 * 
 * @ClassName: UserResource
 * @Description: TODO
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月8日 下午8:13:27
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/member")
@Data
@Log4j2
public class UserResource {

	@Autowired
	private HtBoaInUserService htBoaInUserService;

	@Autowired
	private HtBoaInUserAppService htBoaInUserAppService;

	@Autowired
	private HtBoaInLoginService htBoaInLoginService;

	@Autowired
	private HtBoaInUserRoleService htBoaInUserRoleService;

	@Autowired
	private HtBoaInPositionUserService htBoaInPositionUserService;

	@Autowired
	private HtBoaInPositionRoleService htBoaInPositionRoleService;

	/**
	 *
	 * @Title: validateUser
	 * @Description: 验证用户有效性
	 * @return ResponseModal @throws
	 */

	@GetMapping("/validateUser")
	@ApiOperation(value = "验证用户")
	public ResponseModal validateUser(@RequestParam(value = "app", required = true) String app,
			@RequestParam(value = "userName", required = true) String userName) {
		ResponseModal rm = new ResponseModal();
		UserVo userVo = new UserVo();
		// 查找用户
		HtBoaInUser htBoaInUser = htBoaInUserService.findByUserName(userName);
		if (LogicUtil.isNull(htBoaInUser) || LogicUtil.isNullOrEmpty(htBoaInUser.getUserId())) {
			rm.setSysStatus((SysStatus.USER_NOT_FOUND));
			return rm;
		} else if (htBoaInUser.getDelFlag() == 1) {
            log.info("该用户已被删除！");
			rm.setSysStatus(SysStatus.USER_HAS_DELETED);
		} else {
			BeanUtils.deepCopy(htBoaInUser, userVo);
		}

		// 验证用户与系统是否匹配
		HtBoaInUserApp htBoaInUserApp = htBoaInUserAppService.findUserAndAppInfo(htBoaInUser.getUserId());
		if (LogicUtil.isNull(htBoaInUserApp) || LogicUtil.isNullOrEmpty(htBoaInUserApp.getApp())) {
			rm.setSysStatus(SysStatus.USER_NOT_RELATE_APP);
			return rm;
		} else if (app.equals(htBoaInUserApp.getApp())) {
            log.info("用户与系统匹配正确！");
			BeanUtils.deepCopy(htBoaInUserApp, userVo);
		} else {
            log.info("用户来源不正确！");
			rm.setSysStatus(SysStatus.USER_NOT_MATCH_APP);
			return rm;
		}
		// 获取用户登录信息
		HtBoaInLogin htBoaInLogin = htBoaInLoginService.findByUserId(htBoaInUser.getUserId());
		if (LogicUtil.isNotNull((htBoaInUserApp))) {
			BeanUtils.deepCopy(htBoaInLogin, userVo);
		}
		rm.setSysStatus(SysStatus.SUCCESS);
		rm.setResult(userVo);
		return rm;
	}

	/**
	 *
	 * @Title: getRoleCodes
	 * @Description: 获取用户角色编码
	 * @return ResponseModal
	 * @throws
	 */
	@GetMapping("/getRoleCodes")
	@ApiOperation(value = "获取用户角色编码")
	public ResponseModal getRoleCodes(@RequestParam(value = "userId", required = true) String userId) {
		ResponseModal rm = new ResponseModal();
		List<String> roleCodes = new ArrayList<>();
		// 查找当前用户的角色编码
		List<String> userRoleCodes = htBoaInUserRoleService.queryRoleCodes(userId);
		if (!userRoleCodes.isEmpty()) {
			userRoleCodes.forEach(userRoleCode -> {
				roleCodes.add(userRoleCode);
			});
		}

		// 查找当前用户岗位编码
		List<String> positionCodes = htBoaInPositionUserService.queryRoleCodes(userId);
		// 通过岗位编码查用关联的角色编码
		if (positionCodes != null && positionCodes.size() > 0) {
			List<String> userRoleCodesByPosition = htBoaInPositionRoleService.queryRoleCodesByPosition(positionCodes);
			if (!userRoleCodesByPosition.isEmpty()) {
				userRoleCodesByPosition.forEach(userRoleCode -> {
					roleCodes.add(userRoleCode);
				});
			}
		}

		if (roleCodes.isEmpty()) {
			rm.setSysStatus(SysStatus.NO_ROLE);
			return rm;
		} else {
			rm.setSysStatus(SysStatus.SUCCESS);
			rm.setResult(roleCodes);
			return rm;
		}

	}
    /**
     * 用户信息分页查询<br>
     *
     * @param page 分页参数对象
     * @return 结果对象
     * @author 谭荣巧
     * @Date 2018/1/12 9:01
     */
    @ApiOperation(value = "用户信息分页查询")
    @PostMapping(value = "/loadListByPage")
    public PageResult<HtBoaInUser> loadListByPage(Page page) {
        return htBoaInUserService.getUserListPage(new PageRequest(page.getPage(), page.getLimit()), page.getKeyWord(), page.getQuery());
    }
}
