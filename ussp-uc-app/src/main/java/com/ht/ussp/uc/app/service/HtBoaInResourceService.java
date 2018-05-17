package com.ht.ussp.uc.app.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ht.ussp.common.Constants;
import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.ReturnCodeEnum;
import com.ht.ussp.uc.app.domain.HtBoaInPublish;
import com.ht.ussp.uc.app.domain.HtBoaInResource;
import com.ht.ussp.uc.app.domain.HtBoaInRoleRes;
import com.ht.ussp.uc.app.domain.HtBoaInUserApp;
import com.ht.ussp.uc.app.repository.HtBoaInPublishRepository;
import com.ht.ussp.uc.app.repository.HtBoaInResourceRepository;
import com.ht.ussp.uc.app.repository.HtBoaInRoleRepository;
import com.ht.ussp.uc.app.repository.HtBoaInRoleResRepository;
import com.ht.ussp.uc.app.repository.HtBoaInUserAppRepository;
import com.ht.ussp.uc.app.vo.ApiResourceVo;
import com.ht.ussp.uc.app.vo.ResVo;
import com.ht.ussp.uc.app.vo.ResourcePageVo;
import com.ht.ussp.util.ExcelUtils;

/**
 * @author wim qiuwenwu@hongte.info
 * @ClassName: HtBoaInResourceService
 * @Description: 查找资源服务
 * @date 2018年1月15日 下午2:28:11
 */
@Service
public class HtBoaInResourceService {
	@Autowired
	private HtBoaInResourceRepository htBoaInResourceRepository;
	@Autowired
	private HtBoaInUserAppRepository htBoaInUserAppRepository;
	@Autowired
	private HtBoaInRoleRepository htBoaInRoleRepository;
	@Autowired
	private HtBoaInRoleResRepository htBoaInRoleResRepository;
	@Autowired
	private HtBoaInPublishRepository htBoaInPublishRepository;

	public List<ResVo> queryResForN(List<String> res_code, List<String> res_type, String app) {
		return htBoaInResourceRepository.queryResForN(res_code, res_type, app);
	}

	public List<HtBoaInResource> save(List<HtBoaInResource> htBoaInResources) {
		return htBoaInResourceRepository.save(htBoaInResources);
	}

	public HtBoaInResource save(HtBoaInResource htBoaInResource) {
		return htBoaInResourceRepository.save(htBoaInResource);
	}

	public HtBoaInResource getOne(long id) {
		return htBoaInResourceRepository.findOne(id);
	}

	public void delete(long id) {
		htBoaInResourceRepository.delete(id);
	}

	public List<HtBoaInResource> findByResCodeAndApp(String resCode, String app) {
		return htBoaInResourceRepository.findByResCodeAndApp(resCode, app);
	}

	public List<HtBoaInResource> findByResNameCnAndResContentAndAppAndRemark(String resNameCn, String resContent, String app,String remark) {
		return htBoaInResourceRepository.findByResNameCnAndResContentAndAppAndRemark(resNameCn, resContent, app,remark);
	}

	public void delete(List<HtBoaInResource> htBoaInResourceList) {
		htBoaInResourceRepository.delete(htBoaInResourceList);
	}

	public List<HtBoaInResource> getByResParent(String resParent) {
		return htBoaInResourceRepository.findByResParent(resParent);
	}

	/**
	 * 资源管理分页查询<br>
	 *
	 * @param pageable
	 *            分页对象
	 * @param app
	 *            系统编码
	 * @param parentCode
	 *            父资源编码
	 * @param resType
	 *            资源类型
	 * @param keyWord
	 *            关键字
	 * @return 分页结果对象
	 * @author 谭荣巧
	 * @Date 2018/1/22 21:10
	 */
	public PageResult<List<HtBoaInResource>> getPage(Pageable pageable, String app, String parentCode, String resType,
			String keyWord) {
		PageResult result = new PageResult();
		Page<HtBoaInResource> pageData = null;
		if (!StringUtils.isEmpty(app) && !StringUtils.isEmpty(resType)) {
			Specification<HtBoaInResource> specification = (root, query1, cb) -> {
				Predicate where;
				Predicate p5 = cb.equal(root.get("app").as(String.class), app);
				Predicate p6 = null;
				if (StringUtils.isEmpty(parentCode)) {
					if (!"api".equals(resType)) {
						p6 = cb.isNull(root.get("resParent").as(String.class));
					}
				} else {
					Predicate p61 = cb.equal(root.get("resParent").as(String.class), parentCode);
					Predicate p62 = cb.equal(root.get("resCode").as(String.class), parentCode);
					p6 = cb.or(p61, p62);
				}
				String[] resTypes = resType.split(",");
				CriteriaBuilder.In p7 = cb.in(root.get("resType").as(String.class));
				for (int i = 0; i < resTypes.length; i++) {
					p7.value(resTypes[i]);
				}
				if (!StringUtils.isEmpty(keyWord)) {
					Predicate p1 = cb.like(root.get("resNameCn").as(String.class), "%" + keyWord + "%");
					Predicate p2 = cb.like(root.get("resContent").as(String.class), "%" + keyWord + "%");
					Predicate p3 = cb.like(root.get("resCode").as(String.class), "%" + keyWord + "%");
					Predicate p4 = cb.like(root.get("remark").as(String.class), "%" + keyWord + "%");
					Predicate kewPredicate = cb.or(p1, p2, p3, p4);
					if (p6 != null) {
						where = cb.and(p5, p6, p7, kewPredicate);
					} else {
						where = cb.and(p5, p7, kewPredicate);
					}
				} else {
					if (p6 != null) {
						where = cb.and(p5, p6, p7);
					} else {
						where = cb.and(p5, p7);
					}
				}
				query1.where(where);
				return query1.getRestriction();
			};
			Sort sort = pageable.getSort();
			if (sort == null) {
				List<Sort.Order> orders = new ArrayList<>();
				orders.add(new Sort.Order(Sort.Direction.ASC, "resParent"));
				orders.add(new Sort.Order(Sort.Direction.ASC, "sequence"));
				sort = new Sort(orders);
				pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
			}
			pageData = htBoaInResourceRepository.findAll(specification, pageable);
		}
		if (pageData != null) {
			result.count(pageData.getTotalElements()).data(pageData.getContent());
		}
		result.returnCode(ReturnCodeEnum.SUCCESS.getReturnCode()).codeDesc(ReturnCodeEnum.SUCCESS.getCodeDesc());
		return result;
	}

	/**
	 * 加载权限资源<br>
	 *
	 * @param userId
	 *            用户编码
	 * @param app
	 *            系统编码
	 * @param resTypes
	 *            资源类型（多个）（module/view/group/btn/tab/api）
	 * @return 资源集合
	 * @author 谭荣巧
	 * @Date 2018/1/22 21:11
	 */
	public List<ResVo> loadByUserIdAndApp(String userId, String app, String[] resTypes) {
		List<ResVo> resVoList;
		HtBoaInUserApp userApp = htBoaInUserAppRepository.findByUserIdAndApp(userId, app);
		// 判断是否有系统访问权限
		if (userApp == null) {
			return null;
		}
		// 判断是否是管理员
		if ("Y".equals(userApp.getController())) {// 是管理员，则获取该系统的所有资源
			resVoList = htBoaInResourceRepository.queryResForY(Arrays.asList(resTypes), app);
		} else {// 不是管理员，则通过岗位、角色获取资源
			// 整合所有角色
			List<String> roleCodeList = htBoaInRoleRepository.findRoleCodeByUserId(userId);
			// 如果没有角色，则返回空，说明资源为空
			if (roleCodeList == null || roleCodeList.size() == 0) {
				return null;
			}
			// 通过角色代码获取资源代码
			List<String> resCodeList = htBoaInRoleResRepository.queryResByCode(roleCodeList);
			// 如果没有资源，则返回空
			if (resCodeList == null || resCodeList.size() == 0) {
				return null;
			}
			// 通过角色查询资源
			resVoList = htBoaInResourceRepository.queryResForN(resCodeList, Arrays.asList(resTypes), app);
		}
		return resVoList;
	}

	/**
	 * 加载系统所有资源<br>
	 *
	 * @param app
	 *            系统编码
	 * @return 资源列表
	 * @author 谭荣巧
	 * @Date 2018/1/29 21:37
	 */
	public List<HtBoaInResource> getByApp(String app) {
		return htBoaInResourceRepository.findByAppAndStatusInAndDelFlag(app, new String[] { "0", "2" }, 0);
	}

	/**
	 * 加载系统所有资源<br>
	 */
	public List<HtBoaInResource> getAllByApp(String app) {
		return htBoaInResourceRepository.findByApp(app);
	}

	/**
	 * 通过父编码获取资源个数，用于构造资源编号<br>
	 *
	 * @param app
	 *            系统编号
	 * @param resPanrent
	 *            父资源编码
	 * @param type
	 *            资源类型(menu,tab,api,module,btn)
	 * @return
	 * @author 谭荣巧
	 * @Date 2018/1/30 20:02
	 */
	public String createResourceCode(String app, String resPanrent, String type) {
		String[] resType = null;
		String shortType = "";
		switch (type) {
		case "menu":
			resType = new String[] { "view", "group" };
			if (StringUtils.isEmpty(resPanrent)) {
				shortType = "M";
			}
			break;
		case "tab":
			resType = new String[] { "tab" };
			shortType = "T";
			break;
		case "api":
			resType = new String[] { "api" };
			shortType = "A";
			break;
		case "module":
			resType = new String[] { "module" };
			shortType = "MD";
			break;
		case "custom":
			resType = new String[] { "custom" };
			shortType = "C";
			break;
		case "btn":
			resType = new String[] { "btn" };
			shortType = "B";
			break;
		}
		if (resType != null) {
			// 资源编码前缀
			String resCodePrefix = "";
			if (StringUtils.isEmpty(resPanrent)) {
				// 资源编码前缀
				resCodePrefix = String.format("%s_%s", app, shortType);
			} else {
				if ("menu".equals(type)) {
					resCodePrefix = String.format("%s%s", resPanrent, shortType);
				} else {
					resCodePrefix = String.format("%s_%s", resPanrent, shortType);
				}
			}
			String maxResCode = htBoaInResourceRepository.queryMaxResCodeByAppAndParentAndType2(app, ("".equals(resPanrent) ? "NULL" : resPanrent), Arrays.asList(resType), resCodePrefix);
			int index = 0;
			if (!StringUtils.isEmpty(maxResCode)) {
				// 最大资源编码（去除资源编码前缀）
				try {
					index = Integer.parseInt(maxResCode.replace(resCodePrefix, "").replaceAll("[^0-9]", ""));
				} catch (Exception e) {
					return "";
				}
			}
			return String.format("%s%02d", resCodePrefix, (index + 1));
		}
		return "";
	}
	

	public String createMenuCode(String app, String resPanrent) {
		String[] resType = new String[] { "view", "group" };
		String shortType = "";
		String maxResCode = "";
		// 资源编码前缀
		String resCodePrefix = "";
		if (StringUtils.isEmpty(resPanrent)) {
			shortType = "M";
			// 资源编码前缀
			resCodePrefix = String.format("%s_%s", app, shortType);
		} else {
			resCodePrefix = String.format("%s%s", resPanrent, shortType);
		}
		if (StringUtils.isEmpty(resPanrent)) {
			maxResCode = htBoaInResourceRepository.queryMaxMenuCodeByAppAndParentAndType(app, Arrays.asList(resType),
					resCodePrefix + "%");
		} else {
			maxResCode = htBoaInResourceRepository.queryMaxResCodeByAppAndParentAndType(app,
					("".equals(resPanrent) ? "NULL" : resPanrent), Arrays.asList(resType), resCodePrefix + "%");
		}

		int index = 0;
		if (!StringUtils.isEmpty(maxResCode)) {
			// 最大资源编码（去除资源编码前缀）
			try {
				index = Integer.parseInt(maxResCode.replace(resCodePrefix, "").replaceAll("[^0-9]", ""));
			} catch (Exception e) {
				return "";
			}
		}
		return String.format("%s%02d", resCodePrefix, (index + 1));

	}

	/**
	 * API资源绑定父资源，同时绑定父资源对应的角色<br>
	 *
	 * @param app
	 *            系统编码
	 * @param parentCode
	 *            父资源代码
	 * @param resourceList
	 *            api资源集合
	 * @param userId
	 *            操作的用户
	 * @return true 绑定成功，false 绑定失败
	 * @author 谭荣巧
	 * @Date 2018/2/7 21:19
	 */
	@Transactional
	public boolean relevance(String app, String parentCode, List<HtBoaInResource> resourceList, String userId) {
		String resCodePrefix = String.format("%s_A", parentCode);
		int index = 0;
		String code = createResourceCode(app, parentCode, "api");
		if (!StringUtils.isEmpty(code) && code.contains(resCodePrefix)) {
			try {
				index = Integer.parseInt(code.replace(resCodePrefix, ""));
			} catch (Exception e) {
				// 无需处理
			}
		}

		// API资源绑定父资源
		List<HtBoaInResource> newList = new ArrayList<>();
		for (HtBoaInResource resource : resourceList) {
			if (!StringUtils.isEmpty(resource.getResParent())) {
				resource.setId(null);
			}
			index++;
			// 需要重置资源编码
			resource.setResCode(String.format("%s%02d", resCodePrefix, index));
			resource.setResParent(parentCode);
			resource.setApp(app);
			resource.setResType("api");
			resource.setStatus("0");
			resource.setDelFlag(0);
			resource.setSequence(index);
			resource.setCreateOperator(userId);
			resource.setUpdateOperator(userId);
			newList.add(resource);
		}
		save(newList);
		// API资源绑定父资源对应的角色
		bindParentRole(parentCode, newList, userId);
		return true;
	}

	public void bindParentRole(String parentCode, List<HtBoaInResource> newList, String userId) {
		List<HtBoaInRoleRes> roleResList = htBoaInRoleResRepository.findByResCode(parentCode);
		List<HtBoaInRoleRes> newHtBoaInRoleResList = new ArrayList<>();
		HtBoaInRoleRes newHtBoaInRoleRes;
		for (HtBoaInRoleRes htBoaInRoleRes : roleResList) {
			for (HtBoaInResource resource : newList) {
				newHtBoaInRoleRes = new HtBoaInRoleRes();
				newHtBoaInRoleRes.setResCode(resource.getResCode());
				newHtBoaInRoleRes.setRoleCode(htBoaInRoleRes.getRoleCode());
				newHtBoaInRoleRes.setUpdateOperator(userId);
				newHtBoaInRoleRes.setCreateOperator(userId);
				newHtBoaInRoleRes.setDelFlag(0);
				newHtBoaInRoleResList.add(newHtBoaInRoleRes);
			}
		}
		htBoaInRoleResRepository.save(newHtBoaInRoleResList);
	}

	public PageResult loadApiByPage(ResourcePageVo page) {
		Page<ApiResourceVo> pageResult = htBoaInResourceRepository.queryApiByPage(page.getApp(), page.getKeyWord(),
				page.getParentCode(), page.getPageRequest());
		return PageResult.buildSuccess(pageResult.getContent(), pageResult.getTotalElements());
	}

	public void changeApiState(Long id, String status) {
		HtBoaInResource htBoaInResource = htBoaInResourceRepository.findById(id);
		if (htBoaInResource != null) {
			htBoaInResource.setStatus(status);
			htBoaInResourceRepository.saveAndFlush(htBoaInResource);
		}
	}

	public List<HtBoaInResource> findByAppAndResType(String app, String resType) {
		return htBoaInResourceRepository.findByAppAndResType(app, resType);
	}

	@Transactional
	public void importResExcel(InputStream in, MultipartFile file, String userId, String app) {
		try {
			List<List<Object>> listob = ExcelUtils.getBankListByExcel(in, file.getOriginalFilename());
			List<HtBoaInResource> listHtBoaInResources = new ArrayList<HtBoaInResource>();

			for (int i = 0; i < listob.size(); i++) {
				List<Object> ob = listob.get(i);
				if (app.equals(String.valueOf(ob.get(10)))) {
					HtBoaInResource u = new HtBoaInResource();
					u.setResCode(String.valueOf(ob.get(0)));
					u.setResNameCn(String.valueOf(ob.get(1)));
					u.setSequence(Integer.parseInt(String.valueOf(ob.get(2))));
					u.setResType(String.valueOf(ob.get(3)));
					u.setResParent(String.valueOf(ob.get(4)));
					u.setRemark(String.valueOf(ob.get(5)));
					u.setStatus(String.valueOf(ob.get(6)));
					// u.setResIcon(String.valueOf(ob.get(7)));
					u.setFontIcon(String.valueOf(ob.get(8)));
					u.setResContent(String.valueOf(ob.get(9)));
					u.setApp(String.valueOf(ob.get(10)));
					u.setLastModifiedDatetime(new Date());
					u.setCreatedDatetime(new Date());
					u.setDelFlag(Constants.DEL_0);
					u.setCreateOperator(userId);

					List<HtBoaInResource> listHtBoaInResourceResCode = htBoaInResourceRepository
							.findByResCodeAndApp(u.getResCode(), app);
					if (listHtBoaInResourceResCode != null && !listHtBoaInResourceResCode.isEmpty()
							&& listHtBoaInResourceResCode.size() > 0) {
						continue;
					}

					if ("api".equals(u.getResType())) {
						if (!StringUtils.isEmpty(u.getRemark())) {
							int indexOfThrows = u.getRemark().indexOf("throws");
							String rk = u.getRemark();
							if (indexOfThrows > 0) {
								rk = u.getRemark().substring(0, u.getRemark().indexOf("throws"));
							}
							List<HtBoaInResource> listHtBoaInResource = htBoaInResourceRepository
									.findByRemarkLikeAndResTypeAndApp(rk.trim() + "%", "api", app);
							if (listHtBoaInResource != null && !listHtBoaInResource.isEmpty()
									&& listHtBoaInResource.size() > 0) {
								continue;
							}
						}
					}
					listHtBoaInResources.add(u);
				}
			}
			if (!listHtBoaInResources.isEmpty()) {
				save(listHtBoaInResources);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param sourceListHtBoaInResource
	 *            来源数据 (删除，dev删除了的而生产正在使用？？)
	 * @return
	 */
	public Map<String, String> analysisResData(List<HtBoaInResource> sourceListHtBoaInResource) {
		Map<String, String> backMap = new HashMap<String, String>();
		StringBuffer sbf = new StringBuffer(); // 升级脚本
		StringBuffer fallbacksbf = new StringBuffer(); // 回滚脚本
		boolean isAnais = false; // 是否生成了升级脚本 false没有 true生成了脚本
		String enter = "\r\n";
		List<HtBoaInResource> needAdd = new ArrayList<HtBoaInResource>();
		needAdd.addAll(sourceListHtBoaInResource);
		List<HtBoaInResource> needUpdate = new ArrayList<HtBoaInResource>();
		List<HtBoaInResource> needDel = new ArrayList<HtBoaInResource>();

		List<HtBoaInResource> sourceParentRes = new ArrayList<HtBoaInResource>(); // 父节点不为空
		List<HtBoaInResource> sourceParentNullRes = new ArrayList<HtBoaInResource>();// 父节点为空

		List<HtBoaInResource> targetParentRes = new ArrayList<HtBoaInResource>(); // 父节点不为空
		List<HtBoaInResource> targetParentNullRes = new ArrayList<HtBoaInResource>();// 父节点为空

		String app = "";

		if (sourceListHtBoaInResource != null && !sourceListHtBoaInResource.isEmpty()) {
			app = sourceListHtBoaInResource.get(0).getApp();
			String app2 = sourceListHtBoaInResource.get(sourceListHtBoaInResource.size() - 1).getApp();
			if (!StringUtils.isEmpty(app) && !StringUtils.isEmpty(app2)) {
				if (!app.equals(app2)) {
					return null;
				}
			}
			sbf.append(enter+"-- "+app);
			sbf.append("系统升级脚本（HT_BOA_IN_RESOURCE 资源表） " + enter);
			List<HtBoaInResource> targetListHtBoaInResource = htBoaInResourceRepository.findByApp(app);// 目标数据
			needDel.addAll(targetListHtBoaInResource);
			if (targetListHtBoaInResource != null && !targetListHtBoaInResource.isEmpty()) {
				for (HtBoaInResource sourceHtBoaInResource : sourceListHtBoaInResource) {
					if (!StringUtils.isEmpty(sourceHtBoaInResource.getResCode())) {
						if (StringUtils.isEmpty(sourceHtBoaInResource.getResParent())) {// 父节点为空
							sourceParentNullRes.add(sourceHtBoaInResource);
						} else {// 父节点不为空
							sourceParentRes.add(sourceHtBoaInResource);
						}
					}
				}
				for (HtBoaInResource targetHtBoaInResource : targetListHtBoaInResource) {
					if (StringUtils.isEmpty(targetHtBoaInResource.getResParent())) {// 父节点为空
						targetParentNullRes.add(targetHtBoaInResource);
					} else {// 父节点不为空
						targetParentRes.add(targetHtBoaInResource);
					}
				}

				for (HtBoaInResource sourceHtBoaInResource : sourceParentNullRes) {
					List<HtBoaInResource> sameHtBoaInResource = targetParentNullRes.stream() .filter(targetRes -> sourceHtBoaInResource.getResCode().equals(targetRes.getResCode())) .collect(Collectors.toList());
					needUpdate.addAll(sameHtBoaInResource);// 两边都有的数据，则需要更新
					needAdd.removeAll(sameHtBoaInResource);// src库多出的数据，得到目标库需要新增的数据（去除两边都有的）
					if (sameHtBoaInResource != null && !sameHtBoaInResource.isEmpty()) {
						if (sourceHtBoaInResource.getResCode().equals(sameHtBoaInResource.get(0).getResCode())) {
							needAdd.remove(sourceHtBoaInResource);// src库多出的数据，得到目标库需要新增的数据（去除两边都有的）
						}
					}
					needDel.removeAll(sameHtBoaInResource);// 目标库多出的数据，得到目标库需要删除的数据（去除两边都有的）
				}

				for (HtBoaInResource sourceHtBoaInResource : sourceParentRes) {
					List<HtBoaInResource> sameHtBoaInResource = targetParentRes.stream() .filter(targetRes -> sourceHtBoaInResource.getResCode().equals(targetRes.getResCode()) && sourceHtBoaInResource.getResParent().equals(targetRes.getResParent())) .collect(Collectors.toList());
					needUpdate.addAll(sameHtBoaInResource);// 两边都有的数据，则需要更新
					needAdd.removeAll(sameHtBoaInResource);// src库多出的数据，得到目标库需要新增的数据（去除两边都有的）
					if (sameHtBoaInResource != null && !sameHtBoaInResource.isEmpty()) {
						if (sourceHtBoaInResource.getResCode().equals(sameHtBoaInResource.get(0).getResCode())) {
							needAdd.remove(sourceHtBoaInResource);// src库多出的数据，得到目标库需要新增的数据（去除两边都有的）
						}
					}
					needDel.removeAll(sameHtBoaInResource);// 目标库多出的数据，得到目标库需要删除的数据（去除两边都有的）
				}

				if (needDel != null && !needDel.isEmpty()) {
					sbf.append("-- 删除  请确认生产是否需要删除 " + enter);
					fallbacksbf.append("-- 回滚删除 " + enter);
					for (HtBoaInResource delHtBoaInResource : needDel) {
						if ("0".equals((delHtBoaInResource.getDelFlag() + ""))) {
							sbf.append("UPDATE HT_BOA_IN_RESOURCE SET DEL_FLAG='1' WHERE APP='" + app + "' AND RES_CODE='" + delHtBoaInResource.getResCode() + "';" + enter);
							fallbacksbf.append("UPDATE HT_BOA_IN_RESOURCE SET DEL_FLAG='0' WHERE APP='" + app + "' AND RES_CODE='" + delHtBoaInResource.getResCode() + "';" + enter);
							isAnais = true;
						}
					}
				}

				if (needAdd != null && !needAdd.isEmpty()) {
					sbf.append("-- 添加" + enter);
					fallbacksbf.append("-- 回滚添加 " + enter);
					for (HtBoaInResource addHtBoaInResource : needAdd) {
						sbf.append( "DELETE FROM  HT_BOA_IN_RESOURCE WHERE RES_CODE='" + addHtBoaInResource.getResCode() + "' AND RES_PARENT='" + addHtBoaInResource.getResParent() + "'" + " AND APP='" + addHtBoaInResource.getApp() + "'" + ";" + enter);
						sbf.append( "INSERT INTO `HT_BOA_IN_RESOURCE` (`RES_CODE`, `RES_NAME_CN`, `SEQUENCE`, `RES_TYPE`, `RES_PARENT`, `REMARK`, `STATUS`, `RES_ICON`, `FONT_ICON`, `RES_CONTENT`, `APP`, `JPA_VERSION`, `DEL_FLAG`) VALUES (");
						sbf.append("'" + addHtBoaInResource.getResCode() + "',");
						sbf.append("'" + addHtBoaInResource.getResNameCn() + "',");
						sbf.append("'" + addHtBoaInResource.getSequence() + "',");
						sbf.append("'" + addHtBoaInResource.getResType() + "',");
						sbf.append((addHtBoaInResource.getResParent() == null ? null : "'" + addHtBoaInResource.getResParent() + "'") + ",");
						sbf.append("'" + addHtBoaInResource.getRemark() + "',");
						sbf.append("'" + addHtBoaInResource.getStatus() + "',");
						sbf.append((addHtBoaInResource.getResIcon() == null ? null : "'" + addHtBoaInResource.getResIcon() + "'") + ",");
						sbf.append((addHtBoaInResource.getFontIcon() == null ? null : "'" + addHtBoaInResource.getFontIcon() + "'") + ",");
						sbf.append("'" + addHtBoaInResource.getResContent() + "',");
						sbf.append("'" + addHtBoaInResource.getApp() + "',");
						sbf.append("'" + addHtBoaInResource.getJpaVersion() + "',");
						sbf.append("0");
						sbf.append(");" + enter);
						fallbacksbf.append( "DELETE FROM  HT_BOA_IN_RESOURCE WHERE RES_CODE='" + addHtBoaInResource.getResCode() + "' AND RES_PARENT='" + addHtBoaInResource.getResParent() + "'" + " AND APP='" + addHtBoaInResource.getApp() + "'" + ";" + enter);
						isAnais = true;
					}
				}

				if (needUpdate != null && !needUpdate.isEmpty()) {
					sbf.append("-- 更新 " + enter);
					fallbacksbf.append("-- 回滚更新  " + enter);
					for (HtBoaInResource updateHtBoaInResource : needUpdate) { // target
						Optional<HtBoaInResource> optionalHtBoaInResource = null;
						if (StringUtils.isEmpty(updateHtBoaInResource.getResParent())) {// 父节点为空
							optionalHtBoaInResource = sourceParentNullRes.stream().filter( targetRes -> updateHtBoaInResource.getResCode().equals(targetRes.getResCode())) .findFirst();
						} else {// 父节点不为空
							optionalHtBoaInResource = sourceParentRes.stream().filter( targetRes -> updateHtBoaInResource.getResCode().equals(targetRes.getResCode()) && updateHtBoaInResource.getResParent().equals(targetRes.getResParent())) .findFirst();
						}

						HtBoaInResource sourceHtBoaInResource = null;
						if (optionalHtBoaInResource != null && optionalHtBoaInResource.isPresent()) {
							sourceHtBoaInResource = optionalHtBoaInResource.get();
						}
						if (sourceHtBoaInResource != null) {
							boolean isUpdate = false;
							StringBuffer sbfUpdate = new StringBuffer();
							StringBuffer fallBacksbfUpdate = new StringBuffer();
							sbfUpdate.append("UPDATE HT_BOA_IN_RESOURCE SET RES_CODE=RES_CODE ,");
							fallBacksbfUpdate.append("UPDATE HT_BOA_IN_RESOURCE SET RES_CODE=RES_CODE ,");

							if (StringUtils.isEmpty(updateHtBoaInResource.getResNameCn())) {
								if (!StringUtils.isEmpty(sourceHtBoaInResource.getResNameCn())) {
									sbfUpdate.append( " RES_NAME_CN=" + ((sourceHtBoaInResource.getResNameCn() == null ? null : "'" + sourceHtBoaInResource.getResNameCn() + "'")) + ",");
									fallBacksbfUpdate.append( " RES_NAME_CN=" + ((updateHtBoaInResource.getResNameCn() == null ? null : "'" + updateHtBoaInResource.getResNameCn() + "'")) + ",");
									isUpdate = true;
								}
							} else {
								if (!updateHtBoaInResource.getResNameCn() .equals(sourceHtBoaInResource.getResNameCn())) {
									sbfUpdate.append( " RES_NAME_CN=" + ((sourceHtBoaInResource.getResNameCn() == null ? null : "'" + sourceHtBoaInResource.getResNameCn() + "'")) + ",");
									fallBacksbfUpdate.append( " RES_NAME_CN=" + ((updateHtBoaInResource.getResNameCn() == null ? null : "'" + updateHtBoaInResource.getResNameCn() + "'")) + ",");
									isUpdate = true;
								}
							}
							if (StringUtils.isEmpty(updateHtBoaInResource.getSequence())) {
								if (!StringUtils.isEmpty(sourceHtBoaInResource.getSequence())) {
									sbfUpdate.append(" SEQUENCE=" + sourceHtBoaInResource.getSequence() + ",");
									fallBacksbfUpdate.append(" SEQUENCE=" + updateHtBoaInResource.getSequence() + ",");
									isUpdate = true;
								}
							} else {
								if (!(updateHtBoaInResource.getSequence()) .equals((sourceHtBoaInResource.getSequence()))) {
									if (!StringUtils.isEmpty(sourceHtBoaInResource.getSequence())) {
										sbfUpdate.append(" SEQUENCE=" + sourceHtBoaInResource.getSequence() + ",");
										fallBacksbfUpdate .append(" SEQUENCE=" + updateHtBoaInResource.getSequence() + ",");
										isUpdate = true;
									}
								}
							}
							if (StringUtils.isEmpty(updateHtBoaInResource.getResType())) {
								if (!StringUtils.isEmpty(sourceHtBoaInResource.getResType())) {
									sbfUpdate.append(" RES_TYPE=" + ((sourceHtBoaInResource.getResType() == null ? null : "'" + sourceHtBoaInResource.getResType() + "'")) + ",");
									fallBacksbfUpdate .append(" RES_TYPE=" + ((updateHtBoaInResource.getResType() == null ? null : "'" + updateHtBoaInResource.getResType() + "'")) + ",");
									isUpdate = true;
								}
							} else {
								if (!updateHtBoaInResource.getResType().equals(sourceHtBoaInResource.getResType())) {
									sbfUpdate.append(" RES_TYPE=" + ((sourceHtBoaInResource.getResType() == null ? null : "'" + sourceHtBoaInResource.getResType() + "'")) + ",");
									fallBacksbfUpdate .append(" RES_TYPE=" + ((updateHtBoaInResource.getResType() == null ? null : "'" + updateHtBoaInResource.getResType() + "'")) + ",");
									isUpdate = true;
								}
							}
							if (StringUtils.isEmpty(updateHtBoaInResource.getResParent())) {
								if (!StringUtils.isEmpty(sourceHtBoaInResource.getResParent())) {
									sbfUpdate.append( " RES_PARENT=" + ((sourceHtBoaInResource.getResParent() == null ? null : "'" + sourceHtBoaInResource.getResParent() + "'")) + ",");
									fallBacksbfUpdate.append( " RES_PARENT=" + ((updateHtBoaInResource.getResParent() == null ? null : "'" + updateHtBoaInResource.getResParent() + "'")) + ",");
									isUpdate = true;
								}
							} else {
								if (!updateHtBoaInResource.getResParent() .equals(sourceHtBoaInResource.getResParent())) {
									sbfUpdate.append( " RES_PARENT=" + ((sourceHtBoaInResource.getResParent() == null ? null : "'" + sourceHtBoaInResource.getResParent() + "'")) + ",");
									fallBacksbfUpdate.append( " RES_PARENT=" + ((updateHtBoaInResource.getResParent() == null ? null : "'" + updateHtBoaInResource.getResParent() + "'")) + ",");
									isUpdate = true;
								}
							}
							if (StringUtils.isEmpty(updateHtBoaInResource.getRemark())) {
								if (!StringUtils.isEmpty(sourceHtBoaInResource.getRemark())) {
									sbfUpdate.append(" REMARK=" + ((sourceHtBoaInResource.getRemark() == null ? null : "'" + sourceHtBoaInResource.getRemark() + "'")) + ",");
									fallBacksbfUpdate .append(" REMARK=" + ((updateHtBoaInResource.getRemark() == null ? null : "'" + updateHtBoaInResource.getRemark() + "'")) + ",");
									isUpdate = true;
								}
							} else {
								if (!updateHtBoaInResource.getRemark().equals(sourceHtBoaInResource.getRemark())) {
									sbfUpdate.append(" REMARK=" + ((sourceHtBoaInResource.getRemark() == null ? null : "'" + sourceHtBoaInResource.getRemark() + "'")) + ",");
									fallBacksbfUpdate .append(" REMARK=" + ((updateHtBoaInResource.getRemark() == null ? null : "'" + updateHtBoaInResource.getRemark() + "'")) + ",");
									isUpdate = true;
								}
							}
							if (StringUtils.isEmpty(updateHtBoaInResource.getStatus())) {
								if (!StringUtils.isEmpty(sourceHtBoaInResource.getStatus())) {
									sbfUpdate.append(" STATUS=" + ((sourceHtBoaInResource.getStatus() == null ? null : "'" + sourceHtBoaInResource.getStatus() + "'")) + ",");
									fallBacksbfUpdate .append(" STATUS=" + ((updateHtBoaInResource.getStatus() == null ? null : "'" + updateHtBoaInResource.getStatus() + "'")) + ",");
									isUpdate = true;
								}
							} else {
								if (!updateHtBoaInResource.getStatus().equals(sourceHtBoaInResource.getStatus())) {
									sbfUpdate.append(" STATUS=" + ((sourceHtBoaInResource.getStatus() == null ? null : "'" + sourceHtBoaInResource.getStatus() + "'")) + ",");
									fallBacksbfUpdate .append(" STATUS=" + ((updateHtBoaInResource.getStatus() == null ? null : "'" + updateHtBoaInResource.getStatus() + "'")) + ",");
									isUpdate = true;
								}
							}
							if (StringUtils.isEmpty(updateHtBoaInResource.getResIcon())) {
								if (!StringUtils.isEmpty(sourceHtBoaInResource.getResIcon())) {
									sbfUpdate.append(" RES_ICON=" + ((sourceHtBoaInResource.getResIcon() == null ? null : "'" + sourceHtBoaInResource.getResIcon() + "'")) + ",");
									fallBacksbfUpdate .append(" RES_ICON=" + ((updateHtBoaInResource.getResIcon() == null ? null : "'" + updateHtBoaInResource.getResIcon() + "'")) + ",");
									isUpdate = true;
								}
							} else {
								if (!updateHtBoaInResource.getResIcon().equals(sourceHtBoaInResource.getResIcon())) {
									sbfUpdate.append(" RES_ICON=" + ((sourceHtBoaInResource.getResIcon() == null ? null : "'" + sourceHtBoaInResource.getResIcon() + "'")) + ",");
									fallBacksbfUpdate .append(" RES_ICON=" + ((updateHtBoaInResource.getResIcon() == null ? null : "'" + updateHtBoaInResource.getResIcon() + "'")) + ",");
									isUpdate = true;
								}
							}
							if (StringUtils.isEmpty(updateHtBoaInResource.getFontIcon())) {
								if (!StringUtils.isEmpty(sourceHtBoaInResource.getFontIcon())) {
									sbfUpdate .append(" FONT_ICON=" + ((sourceHtBoaInResource.getFontIcon() == null ? null : "'" + sourceHtBoaInResource.getFontIcon() + "'")) + ",");
									fallBacksbfUpdate .append(" FONT_ICON=" + ((updateHtBoaInResource.getFontIcon() == null ? null : "'" + updateHtBoaInResource.getFontIcon() + "'")) + ",");
									isUpdate = true;
								}
							} else {
								if (!updateHtBoaInResource.getFontIcon().equals(sourceHtBoaInResource.getFontIcon())) {
									sbfUpdate .append(" FONT_ICON=" + ((sourceHtBoaInResource.getFontIcon() == null ? null : "'" + sourceHtBoaInResource.getFontIcon() + "'")) + ",");
									fallBacksbfUpdate .append(" FONT_ICON=" + ((updateHtBoaInResource.getFontIcon() == null ? null : "'" + updateHtBoaInResource.getFontIcon() + "'")) + ",");
									isUpdate = true;
								}
							}
							if (StringUtils.isEmpty(updateHtBoaInResource.getResContent())) {
								if (!StringUtils.isEmpty(sourceHtBoaInResource.getResContent())) {
									sbfUpdate .append(" RES_CONTENT=" + ((sourceHtBoaInResource.getResContent() == null ? null : "'" + sourceHtBoaInResource.getResContent() + "'")) + ",");
									fallBacksbfUpdate .append(" RES_CONTENT=" + ((updateHtBoaInResource.getResContent() == null ? null : "'" + updateHtBoaInResource.getResContent() + "'")) + ",");
									isUpdate = true;
								}
							} else {
								if (!updateHtBoaInResource.getResContent() .equals(sourceHtBoaInResource.getResContent())) {
									sbfUpdate .append(" RES_CONTENT=" + ((sourceHtBoaInResource.getResContent() == null ? null : "'" + sourceHtBoaInResource.getResContent() + "'")) + ",");
									fallBacksbfUpdate .append(" RES_CONTENT=" + ((updateHtBoaInResource.getResContent() == null ? null : "'" + updateHtBoaInResource.getResContent() + "'")) + ",");
									isUpdate = true;
								}
							}
							if (StringUtils.isEmpty(updateHtBoaInResource.getDelFlag())) {
								if (!StringUtils.isEmpty(sourceHtBoaInResource.getDelFlag())) {
									sbfUpdate.append(" DEL_FLAG=" + sourceHtBoaInResource.getDelFlag()+ ",");
									fallBacksbfUpdate.append(" DEL_FLAG=" + updateHtBoaInResource.getDelFlag()+ ",");
									isUpdate = true;
								}
							} else {
								if (!(updateHtBoaInResource.getDelFlag()) .equals((sourceHtBoaInResource.getDelFlag()))) {
									if (!StringUtils.isEmpty(sourceHtBoaInResource.getDelFlag())) {
										sbfUpdate.append(" DEL_FLAG=" + sourceHtBoaInResource.getDelFlag()+ ",");
										fallBacksbfUpdate.append(" DEL_FLAG=" + updateHtBoaInResource.getDelFlag()+ ",");
										isUpdate = true;
									}
								}
							}
							sbfUpdate.append(" RES_CODE=RES_CODE ");
							sbfUpdate.append(" WHERE APP='" + app + "' AND RES_CODE='" + updateHtBoaInResource.getResCode() + "'");
							if (StringUtils.isEmpty(updateHtBoaInResource.getResParent())) {
								sbfUpdate.append(";" + enter);
							} else {
								sbfUpdate.append( " AND RES_PARENT='" + updateHtBoaInResource.getResParent() + "';" + enter);
							}
							fallBacksbfUpdate.append(" RES_CODE=RES_CODE ");
							fallBacksbfUpdate.append(" WHERE APP='" + app + "' AND RES_CODE='" + updateHtBoaInResource.getResCode() + "'");
							if (StringUtils.isEmpty(updateHtBoaInResource.getResParent())) {
								fallBacksbfUpdate.append(";" + enter);
							} else {
								fallBacksbfUpdate.append( " AND RES_PARENT=" + (updateHtBoaInResource.getResParent() == null ? null : "'" + updateHtBoaInResource.getResParent() + "'") + ";" + enter);
							}
							if (isUpdate) {
								sbf.append(sbfUpdate);
								fallbacksbf.append(fallBacksbfUpdate);
								isAnais = true;
							}
						}
					}
				}
			} else { // 全量
				if (sourceListHtBoaInResource != null && !sourceListHtBoaInResource.isEmpty()) {
					sbf.append("-- 添加 " + enter);
					fallbacksbf.append("-- 回滚添加 " + enter);
					for (HtBoaInResource addHtBoaInResource : sourceListHtBoaInResource) {
						sbf.append( "INSERT INTO `HT_BOA_IN_RESOURCE` (`RES_CODE`, `RES_NAME_CN`, `SEQUENCE`, `RES_TYPE`, `RES_PARENT`, `REMARK`, `STATUS`, `RES_ICON`, `FONT_ICON`, `RES_CONTENT`, `APP`, `JPA_VERSION`, `DEL_FLAG`) VALUES (");
						sbf.append("'" + addHtBoaInResource.getResCode() + "',");
						sbf.append("'" + addHtBoaInResource.getResNameCn() + "',");
						sbf.append("'" + addHtBoaInResource.getSequence() + "',");
						sbf.append("'" + addHtBoaInResource.getResType() + "',");
						sbf.append("'" + addHtBoaInResource.getResParent() + "',");
						sbf.append("'" + addHtBoaInResource.getRemark() + "',");
						sbf.append("'" + addHtBoaInResource.getStatus() + "',");
						sbf.append((addHtBoaInResource.getResIcon() == null ? null : "'" + addHtBoaInResource.getResIcon() + "'") + ",");
						sbf.append((addHtBoaInResource.getFontIcon() == null ? null : "'" + addHtBoaInResource.getFontIcon() + "'") + ",");
						sbf.append("'" + addHtBoaInResource.getResContent() + "',");
						sbf.append("'" + addHtBoaInResource.getApp() + "',");
						sbf.append("'" + addHtBoaInResource.getJpaVersion() + "',");
						sbf.append("0");
						sbf.append(");" + enter);
						fallbacksbf.append( "DELETE FROM  HT_BOA_IN_RESOURCE WHERE RES_CODE='" + addHtBoaInResource.getResCode() + "' AND RES_PARENT='" + addHtBoaInResource.getResParent() + "'" + " AND APP='" + addHtBoaInResource.getApp() + "'" + ";" + enter);
						isAnais = true;
					}
				}
			}
		}
		if(isAnais) {
			sbf.append("--  ====HT_BOA_IN_RESOURCE 资源以及资源关系表  end ===="+enter);
			fallbacksbf.append("--  ====HT_BOA_IN_RESOURCE 资源以及资源关系表  end ===="+enter);
		}
		backMap.put("resultData", sbf.toString());
		backMap.put("resultDataBack", fallbacksbf.toString());
		backMap.put("isAnais", isAnais+"");
		return backMap;
	}
	
	/**
	 * 保存升级sql
	 * @param isAnais 是否产生了升级脚本
	 * @param resultDataCode 升级版本
	 * @param app 系统
	 * @param sbf 升级sql
	 * @param fallbacksbf 回滚sql
	 */
	public void saveAnaisSql( String resultDataCode,String app,String sbf,String fallbacksbf) {
			HtBoaInPublish htBoaInPublish = new HtBoaInPublish();
			htBoaInPublish.setPublishCode(resultDataCode);
			htBoaInPublish.setPublishFileName(app + "_publish_" + resultDataCode);
			htBoaInPublish.setFallBackFileName(app + "_fallback_" + resultDataCode);
			htBoaInPublish.setPublishSql(sbf);
			htBoaInPublish.setFallBackSql(fallbacksbf);
			htBoaInPublish.setApp(app);
			htBoaInPublish.setCreatedDatetime(new Date());
			htBoaInPublish.setUpdateDatetime(new Date());
			htBoaInPublish.setIsdown("0");
			htBoaInPublishRepository.save(htBoaInPublish);
	}
}
