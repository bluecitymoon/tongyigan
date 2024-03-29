package TongYiGan.com.tongyigan.web.controller.auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.dayatang.querychannel.Page;
import org.openkoala.auth.application.ResourceApplication;
import org.openkoala.auth.application.RoleApplication;
import org.openkoala.auth.application.UserApplication;
import org.openkoala.auth.application.vo.QueryConditionVO;
import org.openkoala.auth.application.vo.QueryItemVO;
import org.openkoala.auth.application.vo.ResourceVO;
import org.openkoala.auth.application.vo.RoleVO;
import org.openkoala.auth.application.vo.UserVO;
import org.openkoala.koala.auth.ss3adapter.ehcache.CacheUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/auth/Role")
public class RoleController extends BaseController {

	@Inject
	private RoleApplication roleApplication;

	@Inject
	private ResourceApplication urlApplication;

	@Inject
	private UserApplication userApplication;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setAutoGrowCollectionLimit(4096);
	}

	@RequestMapping("/list")
	public String list(Long userId, String userAccount, ModelMap modelMap) {
		modelMap.addAttribute("userId", userId);
		modelMap.addAttribute("userAccount", userAccount);
		return "auth/Role-list";
	}

	@ResponseBody
	@RequestMapping("/abolishResource")
	public Map<String, Object> abolishResource(ParamsPojo params, RoleVO roleVO, List<ResourceVO> menus) {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		roleApplication.abolishMenu(roleVO, menus);
		for (ResourceVO mv : menus) {
			CacheUtil.refreshUrlAttributes(mv.getIdentifier());
		}
		dataMap.put("result", "success");
		return dataMap;
	}

	@ResponseBody
	@RequestMapping("/queryUrlResourceByRole")
	public Map<String, Object> queryUrlResourceByRole(ParamsPojo params) {
		RoleVO roleVO = params.getRoleVO();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		List<ResourceVO> all = roleApplication.findResourceByRole(roleVO);
		dataMap.put("data", all);

		return dataMap;

	}

	@ResponseBody
	@RequestMapping("/queryUserByRole")
	public Map<String, Object> queryUserByRole(ParamsPojo params) {
		RoleVO roleVO = params.getRoleVO();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		List<UserVO> all = roleApplication.findUserByRole(roleVO);
		dataMap.put("data", all);
		return dataMap;
	}

	@ResponseBody
	@RequestMapping("/queryMenuByRole")
	public Map<String, Object> queryMenuByRole(ParamsPojo params) {
		RoleVO roleVO = params.getRoleVO();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		List<ResourceVO> all = roleApplication.findMenuByRole(roleVO);
		dataMap.put("data", all);
		return dataMap;
	}

	@ResponseBody
	@RequestMapping("/pageJson")
	public Page pageJson(String page, String pagesize, String userAccount, String roleNameForSearch) {
		int start = Integer.parseInt(page);
		int limit = Integer.parseInt(pagesize);
		QueryConditionVO search = new QueryConditionVO();
		initSearchCondition(search, roleNameForSearch);
		Page<RoleVO> all = null;
		if (userAccount == null || userAccount.isEmpty()) {
			all = roleApplication.pageQueryByRoleCustom(start, limit, search);
		} else {
			all = roleApplication.pageQueryRoleByUseraccount(start, limit, userAccount);
		}

		return all;
	}

	@ResponseBody
	@RequestMapping("/query")
	public Page query(String page, String pagesize, String roleNameForSearch) {
		int start = Integer.parseInt(page);
		int limit = Integer.parseInt(pagesize);
		QueryConditionVO search = new QueryConditionVO();
		initSearchCondition(search, roleNameForSearch);

		Page<RoleVO> all = roleApplication.pageQueryByRoleCustom(start, limit, search);
		return all;
	}

	@ResponseBody
	@RequestMapping("/queryRolesForAssign")
	public Page queryRolesForAssign(int page, int pagesize, String userAccount, String roleNameForSearch) {
		RoleVO roleVO = new RoleVO();
		roleVO.setName(roleNameForSearch);

		Page<RoleVO> all = roleApplication.pageQueryNotAssignRoleByUseraccount(page, pagesize, userAccount, roleVO);

		return all;
	}

	private void initSearchCondition(QueryConditionVO search, String roleNameForSearch) {
		search.setObjectName("Role");
		List<QueryItemVO> searchConditions = new ArrayList<QueryItemVO>();
		if (roleNameForSearch != null) {
			if (!roleNameForSearch.isEmpty()) {
				QueryItemVO queryItem = new QueryItemVO();
				queryItem.setPropName("name");

				queryItem.setPropValue("'%" + roleNameForSearch + "%'");

				queryItem.setOperaType(QueryConditionVO.LIKE);
				searchConditions.add(queryItem);
			}
		}
		search.setItems(searchConditions);
	}

	@ResponseBody
	@RequestMapping("/queryNotAssignRoleByUser")
	public Page queryNotAssignRoleByUser(String page, String pagesize, long userId) {
		int start = Integer.parseInt(page);
		int limit = Integer.parseInt(pagesize);

		UserVO userVoForFind = new UserVO();
		userVoForFind.setId(userId);
		Page<RoleVO> all = userApplication.pageQueryNotAssignRoleByUser(start, limit, userVoForFind);

		return all;
	}

	@ResponseBody
	@RequestMapping("/add")
	public Map<String, Object> add(ParamsPojo params) {
		RoleVO roleVO = params.getRoleVO();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		roleVO.setSerialNumber("test");
		roleApplication.saveRole(roleVO);
		dataMap.put("result", "success");
		return dataMap;
	}

	@ResponseBody
	@RequestMapping("/del")
	public Map<String, Object> del(ParamsPojo params) {
		List<RoleVO> roles = params.getRoles();
		Map<String, Object> dataMap = new HashMap<String, Object>();

		List<Long> roleIds = new ArrayList<Long>();

		for (RoleVO role : roles) {
			roleIds.add(role.getId());
		}

		roleApplication.removeRoles(roleIds.toArray(new Long[] {}));
		dataMap.put("result", "success");
		return dataMap;

	}

	@ResponseBody
	@RequestMapping("/update")
	public Map<String, Object> update(ParamsPojo params) {
		RoleVO roleVO = params.getRoleVO();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		roleApplication.updateRole(roleVO);
		dataMap.put("result", "success");
		return dataMap;
	}

	/**
	 * 为角色分配菜单资源
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/assignMenuResources")
	public Map<String, Object> assignMenuResources(ParamsPojo params) {
		RoleVO roleVO = params.getRoleVO();
		List<ResourceVO> menus = params.getMenus();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		// 原先拥有的菜单资源
		List<ResourceVO> ownMenus = roleApplication.findAllResourceByRole(roleVO);
		// 勾选中的菜单资源
		List<ResourceVO> tmpList = new ArrayList<ResourceVO>(menus);
		List<ResourceVO> addList = new ArrayList<ResourceVO>();
		List<ResourceVO> delList = new ArrayList<ResourceVO>();
		// 得到相到的菜单资源
		menus.retainAll(ownMenus);
		// 去掉相同的菜单资源
		ownMenus.removeAll(menus);
		// 得到被删除的菜单资源
		delList.addAll(ownMenus);
		// 得到被新添加的菜单资源
		tmpList.removeAll(menus);

		addList.addAll(tmpList);
		roleApplication.abolishMenu(roleVO, delList);
		roleApplication.assignMenuResource(roleVO, addList);

		List<Long> refreshResourceIds = new ArrayList<Long>();
		for (ResourceVO mVO : addList) {
			refreshResourceIds.add(mVO.getId());
		}
		for (ResourceVO mVO : delList) {
			refreshResourceIds.add(mVO.getId());
		}
		CacheUtil.refreshUrlAttributes(refreshResourceIds);
		dataMap.put("result", "success");

		return dataMap;
	}

	/**
	 * 为角色分配资源
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/assignResources")
	public Map<String, Object> assignResources(ParamsPojo params) {
		RoleVO roleVO = params.getRoleVO();
		List<ResourceVO> addList = params.getAddList();
		List<ResourceVO> delList = params.getDelList();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		if (addList != null && addList.size() > 0) {
			roleApplication.assignMenuResource(roleVO, addList);
		}
		if (delList != null && delList.size() > 0) {
			roleApplication.abolishMenu(roleVO, delList);
		}
		dataMap.put("result", "success");
		return dataMap;
	}

	/**
	 * 为角色分配URL资源
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/assignUrlResources")
	public Map<String, Object> assignUrlResources(ParamsPojo params) {
		RoleVO roleVO = params.getRoleVO();
		List<ResourceVO> menus = params.getMenus();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		roleApplication.assignMenuResource(roleVO, menus);
		for (ResourceVO mVO : menus) {
			CacheUtil.refreshUrlAttributes(mVO.getIdentifier());
		}
		dataMap.put("result", "success");

		return dataMap;
	}

	/**
	 * 查询某个角色下的所有用户
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/findUsersByRole")
	public Map<String, Object> findUsersByRole(ParamsPojo params) {
		RoleVO roleVO = params.getRoleVO();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("roleUsers", roleApplication.findUserByRole(roleVO));
		dataMap.put("result", "success");
		return dataMap;
	}

	/**
	 * 给角色分配用户
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/assignUsers")
	public Map<String, Object> assignUsers(ParamsPojo params) {
		RoleVO roleVO = params.getRoleVO();
		List<UserVO> users = params.getUsers();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		for (UserVO user : users) {
			roleApplication.assignUser(roleVO, user);
			CacheUtil.refreshUserAttributes(((UserVO) userApplication.getUser(user.getId())).getUserAccount());
		}
		dataMap.put("result", "success");
		return dataMap;
	}

	/**
	 * 返回角色所拥有的资源页面
	 * 
	 * @return
	 */
	@RequestMapping("/resourceList")
	public String resourceList() {
		return "Role-resourceList";
	}

	/**
	 * 删除某个用户下的某个角色
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/removeRoleForUser")
	public Map<String, Object> removeRoleForUser(ParamsPojo params) {
		long userId = params.getUserId();
		List<RoleVO> roles = params.getRoles();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		UserVO user = new UserVO();
		user.setId(userId);
		userApplication.abolishRole(user, roles);
		CacheUtil.refreshUserAttributes(((UserVO) userApplication.getUser(user.getId())).getUserAccount());
		dataMap.put("result", "success");
		return dataMap;
	}

}