package com.ht.ussp.uc.app.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ht.ussp.bean.ExcelBean;
import com.ht.ussp.common.Constants;
import com.ht.ussp.uc.app.domain.HtBoaInOrg;
import com.ht.ussp.uc.app.domain.HtBoaInRole;
import com.ht.ussp.uc.app.model.BoaInRoleInfo;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.repository.HtBoaInRoleRepository;
import com.ht.ussp.util.ExcelUtils;

@Service
public class HtBoaInRoleService {

	@Autowired
    private HtBoaInRoleRepository htBoaInRoleRepository;
	
    public HtBoaInRole findById(Long id) {
        return this.htBoaInRoleRepository.findById(id);
    }

    public List<HtBoaInRole> findAll(HtBoaInRole u) {
        ExampleMatcher matcher = ExampleMatcher.matching();
        Example<HtBoaInRole> ex = Example.of(u, matcher);
        return this.htBoaInRoleRepository.findAll(ex);
    }
    
    public Object findAllRoleByAppPage(PageConf pageConf, List<String> apps,String keyWord) {
    	 Sort sort = null;
         Pageable pageable = null;
         List<Order> orders = new ArrayList<Order>();
         if (null != pageConf.getSortNames()) {
             for (int i = 0; i < pageConf.getSortNames().size(); i++) {
                 orders.add(new Order(pageConf.getSortOrders().get(i), pageConf.getSortNames().get(i)));
             }
             sort = new Sort(orders);
         }
         if (null != pageConf.getPage() && null != pageConf.getSize())
             pageable = new PageRequest(pageConf.getPage(), pageConf.getSize(), sort);

         if (null != pageable) {
             Page<HtBoaInRole> p = this.htBoaInRoleRepository.findByAppInAndDelFlagAndStatusAndRoleNameCnLike(pageable,apps,0,"0",keyWord);
             return p;
         } 
         return null;
	}

    public Object findAllByPage(PageConf pageConf,Map<String, String> query) {
        Sort sort = null;
        Pageable pageable = null;
        List<Order> orders = new ArrayList<Order>();
        if (null != pageConf.getSortNames()) {
            for (int i = 0; i < pageConf.getSortNames().size(); i++) {
                orders.add(new Order(pageConf.getSortOrders().get(i), pageConf.getSortNames().get(i)));
            }
            sort = new Sort(orders);
        }
        if (null != pageConf.getPage() && null != pageConf.getSize())
            pageable = new PageRequest(pageConf.getPage(), pageConf.getSize(), sort);
        
        String app = "";
        if (query != null && query.size() > 0 && query.get("app") != null) {
        	app = "%" +query.get("app")+ "%";
        }else {
        	app = "%%";
        } 
        
        String search = pageConf.getSearch();
        if (null == search || 0 == search.trim().length())
            search = "%%";
        else
            search = "%" + search + "%";
        if (null != pageable) {
            Page<BoaInRoleInfo> p = this.htBoaInRoleRepository.listRoleInfoByPageWeb(pageable, search ,app);
            return p;
        } else {
            List<BoaInRoleInfo> p = this.htBoaInRoleRepository.listRoleInfo(search);
            for (BoaInRoleInfo u : p) {
                u.setUsers(this.htBoaInRoleRepository.listHtBoaInUser(u.getRoleCode()));
                u.setPositions(this.htBoaInRoleRepository.listHtBoaInPosition(u.getRoleCode()));
            }
            return p;
        }
    }

    public Object loadListRoleByPage(PageConf pageConf,Map<String, String> query) {
        Sort sort = null;
        Pageable pageable = null;
        List<Order> orders = new ArrayList<Order>();
        if (null != pageConf.getSortNames()) {
            for (int i = 0; i < pageConf.getSortNames().size(); i++) {
                orders.add(new Order(pageConf.getSortOrders().get(i), pageConf.getSortNames().get(i)));
            }
            sort = new Sort(orders);
        }
        if (null != pageConf.getPage() && null != pageConf.getSize())
            pageable = new PageRequest(pageConf.getPage(), pageConf.getSize(), sort);
        
        String app = "";
        if (query != null && query.size() > 0 && query.get("app") != null &&(!"".equals(query.get("app")))) {
        	app = "%" +query.get("app")+ "%";
        } else {
        	app = "%%";
        }
        
        String search = pageConf.getSearch();
        if (null == search || 0 == search.trim().length())
            search = "%%";
        else
            search = "%" + search + "%";
        if (null != pageable) {
            Page<BoaInRoleInfo> p = this.htBoaInRoleRepository.listRoleInfoByPageWeb(pageable, search ,app);
            for (BoaInRoleInfo u : p.getContent()) {
                u.setUsers(this.htBoaInRoleRepository.listHtBoaInUser(u.getRoleCode()));
                u.setPositions(this.htBoaInRoleRepository.listHtBoaInPosition(u.getRoleCode()));
            }
            return p;
        } else {
            List<BoaInRoleInfo> p = this.htBoaInRoleRepository.listRoleInfo(search);
            for (BoaInRoleInfo u : p) {
                u.setUsers(this.htBoaInRoleRepository.listHtBoaInUser(u.getRoleCode()));
                u.setPositions(this.htBoaInRoleRepository.listHtBoaInPosition(u.getRoleCode()));
            }
            return p;
        }
    }
    
    public HtBoaInRole add(HtBoaInRole u) {
        return this.htBoaInRoleRepository.saveAndFlush(u);
    }

    public HtBoaInRole update(HtBoaInRole u) {
        return this.htBoaInRoleRepository.save(u);
    }
    
    public void delete(long id) {
        this.htBoaInRoleRepository.delete(id);
    }

	public List<HtBoaInRole> findByRoleCode(String roleCode) {
		return this.htBoaInRoleRepository.findByRoleCode(roleCode);
	}

	public XSSFWorkbook exportRoleExcel() {
		XSSFWorkbook book = null;
		try {
			List<HtBoaInRole> listHtBoaInOrg = this.htBoaInRoleRepository.findAll();
			List<ExcelBean> ems = new ArrayList<>();
			Map<Integer, List<ExcelBean>> map = new LinkedHashMap<>();
			ems.add(new ExcelBean("机构编码", "orgCode", 0));
			ems.add(new ExcelBean("机构名称", "orgNameCn", 0));
			ems.add(new ExcelBean("父机构编码", "parentOrgCode", 0));
			ems.add(new ExcelBean("排序", "sequence", 0));
			ems.add(new ExcelBean("状态", "delFlag", 0));
			map.put(0, ems);
			book = ExcelUtils.createExcelFile(HtBoaInOrg.class, listHtBoaInOrg, map, "机构信息");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return book;
	}

	@Transactional
	public void importRoleExcel(InputStream in, MultipartFile file, String userId) {
		try {
			List<List<Object>> listob = ExcelUtils.getBankListByExcel(in, file.getOriginalFilename());
			for (int i = 0; i < listob.size(); i++) {
				List<Object> ob = listob.get(i);
				HtBoaInRole u = new HtBoaInRole();
				u.setRoleCode(String.valueOf(ob.get(0)));
		        u.setRoleNameCn(String.valueOf(ob.get(1)));
		        u.setApp(String.valueOf(ob.get(2)));
				u.setLastModifiedDatetime(new Date());
				u.setCreatedDatetime(new Date());
				u.setStatus(Constants.STATUS_0);
				u.setDelFlag(Constants.DEL_0);
				u.setCreateOperator(userId);
				u = add(u);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public List<HtBoaInRole> getAllByApp(String app) {
		return htBoaInRoleRepository.findByApp(app);
	}

	public Map<String, String> analysisRoleData(List<HtBoaInRole> sourceListHtBoaInRole) {
		StringBuffer sbf = new StringBuffer(); // 升级脚本
		StringBuffer fallbacksbf = new StringBuffer(); // 回滚脚本
		boolean isAnais = false; // 是否生成了升级脚本 false没有 true生成了脚本
		String enter = "\r\n";
		String app = "";
		List<HtBoaInRole> needAdd = new ArrayList<HtBoaInRole>();
		needAdd.addAll(sourceListHtBoaInRole);
		List<HtBoaInRole> needUpdate = new ArrayList<HtBoaInRole>();
		List<HtBoaInRole> needDel = new ArrayList<HtBoaInRole>();
		
		if (sourceListHtBoaInRole != null && !sourceListHtBoaInRole.isEmpty()) {
			app = sourceListHtBoaInRole.get(0).getApp();
			sbf.append(enter+"-- "+app);
			sbf.append("系统升级脚本 （HT_BOA_IN_ROLE 角色表 ）" + enter);
			List<HtBoaInRole> targetListHtBoaInRole = htBoaInRoleRepository.findByApp(app);// 目标数据
			needDel.addAll(targetListHtBoaInRole);
			if (targetListHtBoaInRole != null && !targetListHtBoaInRole.isEmpty()) {
				for (HtBoaInRole sourceHtBoaInRole : sourceListHtBoaInRole) {
					List<HtBoaInRole> sameHtBoaInRole = targetListHtBoaInRole.stream() .filter(targetRole -> sourceHtBoaInRole.getRoleCode().equals(targetRole.getRoleCode())) .collect(Collectors.toList());
					needUpdate.addAll(sameHtBoaInRole);// 两边都有的数据，则需要更新
					needAdd.removeAll(sameHtBoaInRole);// src库多出的数据，得到目标库需要新增的数据（去除两边都有的）
					if (sameHtBoaInRole != null && !sameHtBoaInRole.isEmpty()) {
						if (sourceHtBoaInRole.getRoleCode().equals(sameHtBoaInRole.get(0).getRoleCode())) {
							needAdd.remove(sourceHtBoaInRole);// src库多出的数据，得到目标库需要新增的数据（去除两边都有的）
						}
					}
					needDel.removeAll(sameHtBoaInRole);// 目标库多出的数据，得到目标库需要删除的数据（去除两边都有的）
				}
				
				if (needDel != null && !needDel.isEmpty()) {
					sbf.append("-- 删除  请确认生产是否需要删除 " + enter);
					fallbacksbf.append("-- 回滚删除 " + enter);
					for (HtBoaInRole delHtBoaInRole : needDel) {
						if ("0".equals((delHtBoaInRole.getDelFlag() + ""))) {
							sbf.append("UPDATE HT_BOA_IN_ROLE SET DEL_FLAG='1' WHERE APP='" + app + "' AND ROLE_CODE='" + delHtBoaInRole.getRoleCode() + "';" + enter);
							fallbacksbf.append("UPDATE HT_BOA_IN_ROLE SET DEL_FLAG='0' WHERE APP='" + app + "' AND ROLE_CODE='" + delHtBoaInRole.getRoleCode() + "';" + enter);
							isAnais = true;
						}
					}
				}

				if (needAdd != null && !needAdd.isEmpty()) {
					sbf.append("-- 添加 " + enter);
					fallbacksbf.append("-- 回滚添加 " + enter);
					for (HtBoaInRole addHtBoaInRole : needAdd) {
						sbf.append( "DELETE FROM  HT_BOA_IN_ROLE WHERE ROLE_CODE='" + addHtBoaInRole.getRoleCode() + "' AND APP='" + addHtBoaInRole.getApp() + "' AND ROLE_NAME_CN='"+addHtBoaInRole.getRoleNameCn()+ "';" + enter);
						sbf.append( "INSERT INTO  `HT_BOA_IN_ROLE` (`ROLE_CODE`, `ROLE_NAME_CN`, `STATUS`, `APP`, `DEL_FLAG` ) VALUES (");
						sbf.append("'" + addHtBoaInRole.getRoleCode() + "',");
						sbf.append("'" + addHtBoaInRole.getRoleNameCn() + "',");
						sbf.append("'" + addHtBoaInRole.getStatus() + "',");
						sbf.append("'" + addHtBoaInRole.getApp() + "',");
						sbf.append("'" + addHtBoaInRole.getDelFlag() + "'");
						sbf.append(");" + enter);
						fallbacksbf.append( "DELETE FROM  HT_BOA_IN_ROLE WHERE ROLE_CODE='" + addHtBoaInRole.getRoleCode() + "' AND APP='" + addHtBoaInRole.getApp() + "' AND ROLE_NAME_CN='"+addHtBoaInRole.getRoleNameCn()+ "';" + enter);
						isAnais = true;
					}
				}

				if (needUpdate != null && !needUpdate.isEmpty()) {
					sbf.append("-- 更新 " + enter);
					fallbacksbf.append("-- 回滚更新 " + enter);
					for (HtBoaInRole updateHtBoaInRole : needUpdate) { // target
						Optional<HtBoaInRole> optionalHtBoaInRole = null;
						if (!StringUtils.isEmpty(updateHtBoaInRole.getRoleCode())) {// 父节点为空
							optionalHtBoaInRole = sourceListHtBoaInRole.stream().filter( targetRole -> updateHtBoaInRole.getRoleCode().equals(targetRole.getRoleCode())).findFirst();
						}  
						HtBoaInRole sourceHtBoaInRole = null;
						if (optionalHtBoaInRole != null && optionalHtBoaInRole.isPresent()) {
							sourceHtBoaInRole = optionalHtBoaInRole.get();
						}
						if (sourceHtBoaInRole != null) {
							boolean isUpdate = false;
							StringBuffer sbfUpdate = new StringBuffer();
							StringBuffer fallBacksbfUpdate = new StringBuffer();
							sbfUpdate.append("UPDATE HT_BOA_IN_ROLE SET ROLE_CODE=ROLE_CODE ,");
							fallBacksbfUpdate.append("UPDATE HT_BOA_IN_ROLE SET ROLE_CODE=ROLE_CODE ,");
							if (!StringUtils.isEmpty(sourceHtBoaInRole.getRoleNameCn())) {
								if(!sourceHtBoaInRole.getRoleNameCn().equals(updateHtBoaInRole.getRoleNameCn())) {
									sbfUpdate.append( " ROLE_NAME_CN=" + ((sourceHtBoaInRole.getRoleNameCn() == null ? null : "'" + sourceHtBoaInRole.getRoleNameCn() + "'")) + ",");
									fallBacksbfUpdate.append(" ROLE_NAME_CN=" + ((updateHtBoaInRole.getRoleNameCn() == null ? null : "'" + updateHtBoaInRole.getRoleNameCn() + "'")) + ",");
									isUpdate = true;
								}
							} 
							sbfUpdate.append(" ROLE_CODE=ROLE_CODE ");
							fallBacksbfUpdate.append(" ROLE_CODE=ROLE_CODE ");
							sbfUpdate.append(" WHERE ROLE_CODE='" + updateHtBoaInRole.getRoleCode() + "';"+ enter);
							fallBacksbfUpdate.append(" WHERE ROLE_CODE='" + updateHtBoaInRole.getRoleCode() + "';"+ enter);
							if (isUpdate) {
								sbf.append(sbfUpdate);
								fallbacksbf.append(fallBacksbfUpdate);
								isAnais = true;
							}
						}
					}
				}
				
			}else { //全量添加
				for (HtBoaInRole addHtBoaInRole : sourceListHtBoaInRole) {
					sbf.append( "INSERT INTO  `HT_BOA_IN_ROLE` (`ROLE_CODE`, `ROLE_NAME_CN`, `STATUS`, `APP`, `DEL_FLAG` ) VALUES (");
					sbf.append("'" + addHtBoaInRole.getRoleCode() + "',");
					sbf.append("'" + addHtBoaInRole.getRoleNameCn() + "',");
					sbf.append("'" + addHtBoaInRole.getStatus() + "',");
					sbf.append("'" + addHtBoaInRole.getApp() + "',");
					sbf.append("'" + addHtBoaInRole.getDelFlag() + "'");
					sbf.append(");" + enter);
					fallbacksbf.append( "DELETE FROM  HT_BOA_IN_ROLE WHERE ROLE_CODE='" + addHtBoaInRole.getRoleCode() + "' AND APP='" + addHtBoaInRole.getApp() + "' AND ROLE_NAME_CN='"+addHtBoaInRole.getRoleNameCn()+ "';" + enter);
					isAnais = true;
				}
			}
		}
		if(isAnais) {
			sbf.append("--  ====HT_BOA_IN_ROLE 角色表  end ===="+enter);
			fallbacksbf.append("--  ====HT_BOA_IN_ROLE 角色表  end ===="+enter);
		}
		Map<String, String> backMap = new HashMap<String, String>();
		backMap.put("resultData", sbf.toString());
		backMap.put("resultDataBack", fallbacksbf.toString());
		backMap.put("isAnais", isAnais+"");
		return backMap;
	}

}
