package com.ht.ussp.uc.app.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.criteria.Predicate;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.ht.ussp.bean.ExcelBean;
import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.Result;
import com.ht.ussp.core.ReturnCodeEnum;
import com.ht.ussp.uc.app.domain.HtBoaInBusinessOrg;
import com.ht.ussp.uc.app.domain.HtBoaInContrast;
import com.ht.ussp.uc.app.domain.TbDepartment;
import com.ht.ussp.uc.app.model.BoaInOrgInfo;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.repository.HtBoaInContrastRepository;
import com.ht.ussp.uc.app.repository.HtBoaInOrgBusinessRepository;
import com.ht.ussp.uc.app.repository.TbDepartmentRepository;
import com.ht.ussp.util.ExcelUtils;

@Service
public class HtBoaInOrgBusinessService {

	@Autowired
    private HtBoaInOrgBusinessRepository htBoaInOrgBusinessRepository;
	
	@Autowired
    private TbDepartmentRepository tbDepartmentRepository;
	
	@Autowired
    private HtBoaInContrastRepository htBoaInContrastRepository;
	
	
	
    public HtBoaInBusinessOrg findById(Long id) {
        return this.htBoaInOrgBusinessRepository.findById(id);
    }

    public List<HtBoaInBusinessOrg> findAll(HtBoaInBusinessOrg u) {
        ExampleMatcher matcher = ExampleMatcher.matching();
        Example<HtBoaInBusinessOrg> ex = Example.of(u, matcher);
        return this.htBoaInOrgBusinessRepository.findAll(ex);
    }

    public List<HtBoaInBusinessOrg> findAll() {
        return this.htBoaInOrgBusinessRepository.findAll();
    }

    public List<HtBoaInBusinessOrg> add(List<HtBoaInBusinessOrg> orgList) {
        return this.htBoaInOrgBusinessRepository.save(orgList);
    }

    public HtBoaInBusinessOrg add(HtBoaInBusinessOrg u) {
        return this.htBoaInOrgBusinessRepository.save(u);
    }

    public HtBoaInBusinessOrg update(HtBoaInBusinessOrg u) {
        return this.htBoaInOrgBusinessRepository.save(u);
    }

    public PageResult<List<HtBoaInBusinessOrg>> findAllByPage(PageConf pageConf, Map<String, String> query) {
        Sort sort = null;
        PageRequest pageable = null;
        List<Order> orders = new ArrayList<Order>();
        orders.add(new Order(Direction.ASC,"orgLevel"));
        sort = new Sort(orders);
        if (null != pageConf.getPage() && null != pageConf.getSize())
            pageable = new PageRequest(pageConf.getPage(), pageConf.getSize(), sort);
        
        String orgCode = "";
        if (query != null && query.size() > 0 && query.get("businessOrgCode") != null) {
        	orgCode = query.get("businessOrgCode");
        }
        
        String keyWord = "";
        if (query != null && query.size() > 0 && query.get("keyWord") != null) {
        	keyWord = query.get("keyWord");
        }

        String orgCodeSp = orgCode;
        String keyWordSp = keyWord == null?"":keyWord;
        if (null != pageable) {
            PageResult result = new PageResult();
    		Specification<HtBoaInBusinessOrg> specification = null;
    		
    		if(StringUtils.isNotEmpty(orgCodeSp)) {
    			specification = (root, query1, cb) -> {
        			Predicate p1 = cb.like(root.get("businessOrgName").as(String.class), "%" + keyWordSp + "%");
        			Predicate p2 = cb.like(root.get("businessOrgCode").as(String.class), "%" + keyWordSp + "%" );
        			Predicate p3 = cb.equal(root.get("parentOrgCode").as(String.class),   orgCodeSp );
        			// 把Predicate应用到CriteriaQuery中去,因为还可以给CriteriaQuery添加其他的功能，比如排序、分组啥的
        			query1.where(cb.and(cb.or(p1, p2),p3));
        			return query1.getRestriction();
    			};
    		}else {
    			specification = (root, query1, cb) -> {
        			Predicate p1 = cb.like(root.get("businessOrgName").as(String.class), "%" + keyWordSp + "%");
        			Predicate p2 = cb.like(root.get("businessOrgCode").as(String.class), "%" + keyWordSp + "%" );
        			// 把Predicate应用到CriteriaQuery中去,因为还可以给CriteriaQuery添加其他的功能，比如排序、分组啥的
        			query1.where(cb.or(p1, p2));
        			return query1.getRestriction();
    			};
    		}
    		Page<HtBoaInBusinessOrg> pageData = htBoaInOrgBusinessRepository.findAll(specification, pageable);
    		if (pageData != null) {
    			result.count(pageData.getTotalElements()).data(pageData.getContent());
    		}
    		result.returnCode(ReturnCodeEnum.SUCCESS.getReturnCode()).codeDesc(ReturnCodeEnum.SUCCESS.getCodeDesc());
    		return result;
        }
        return null;
    }

    /**
     * 根据父组织机构代码查询组织机构，并转化成Tree<br>
     *
     * @param parentOrgCode
     * @return Listst<Tree>
     * @author 谭荣巧
     * @Date 2018/1/13 10:52
     */
    public List<HtBoaInBusinessOrg> getOrgTreeList(String parentOrgCode) {
        HtBoaInBusinessOrg queryOrg = new HtBoaInBusinessOrg();
        queryOrg.setParentOrgCode(parentOrgCode);
        //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                // 忽略 id 和 createTime 字段。
                .withIgnorePaths("id", "createdDatetime", "orgPath", "dataSource","jpaVersion", "sequence")
                // 忽略为空字段。
                .withIgnoreNullValues();
        //创建实例
        Example<HtBoaInBusinessOrg> ex = Example.of(queryOrg, matcher);
        return htBoaInOrgBusinessRepository.findAll(ex, new Sort(Sort.Direction.ASC, "sequence"));
    }

    public List<HtBoaInBusinessOrg> findByOrgCode(String orgCode) {
        return this.htBoaInOrgBusinessRepository.findByBusinessOrgCode(orgCode);
    }

    public List<HtBoaInBusinessOrg> findByParentOrgCode(String parentOrgCode) {
        return this.htBoaInOrgBusinessRepository.findByParentOrgCode(parentOrgCode);
    }
    
    /*public HtBoaInBusinessOrg getOrgInfoByOrgType(String orgCode, String orgType) {
		return getParentOrgs(orgCode,orgType);
	}

    private HtBoaInBusinessOrg getParentOrgs(String orgCode,String orgType) {
    	HtBoaInBusinessOrg htBoaInBusinessOrg = null;
    	List<HtBoaInBusinessOrg> listHtBoaInOrg = this.htBoaInOrgBusinessRepository.findByBusinessOrgCode(orgCode);
		if(listHtBoaInOrg!=null&&!listHtBoaInOrg.isEmpty()) {
			htBoaInBusinessOrg = listHtBoaInOrg.get(0);
		}
		if(htBoaInBusinessOrg!=null && orgType.equals(htBoaInOrg.getOrgType())) {
			return htBoaInBusinessOrg;
		}
		if("D01".equals(orgCode)) {
			return htBoaInBusinessOrg;
		} else {
			return  getParentOrgs(htBoaInBusinessOrg.getParentOrgCode(),orgType);
		}
	}*/

    public List<HtBoaInBusinessOrg> getOrgListByTime(String startTime, String endTime) {
		return this.htBoaInOrgBusinessRepository.getByLastModifiedDatetime(startTime,endTime);
	}

    public XSSFWorkbook exportOrgExcel() {
        XSSFWorkbook book = null;
        try {
            List<HtBoaInBusinessOrg> listHtBoaInOrg = this.htBoaInOrgBusinessRepository.findAll();
            List<ExcelBean> ems = new ArrayList<>();
            Map<Integer, List<ExcelBean>> map = new LinkedHashMap<>();
            ems.add(new ExcelBean("机构编码", "orgCode", 0));
            ems.add(new ExcelBean("机构名称", "orgNameCn", 0));
            ems.add(new ExcelBean("父机构编码", "parentOrgCode", 0));
            ems.add(new ExcelBean("排序", "sequence", 0));
            ems.add(new ExcelBean("状态", "delFlag", 0));
            map.put(0, ems);
            book = ExcelUtils.createExcelFile(HtBoaInBusinessOrg.class, listHtBoaInOrg, map, "机构信息");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return book;
    }

	public String getMaxOrgCode(String parentOrgCode) {
		return  this.htBoaInOrgBusinessRepository.getMaxOrgCode(parentOrgCode);
	}

	/**
	 * 获取所有下级机构
	 * @param orgCode
	 * @return
	 */
	public List<HtBoaInBusinessOrg> getAllSubOrgInfo(String orgCode) {
		List<HtBoaInBusinessOrg> listHtBoaInOrg = new ArrayList<HtBoaInBusinessOrg>();
		getAllSubOrg(listHtBoaInOrg,orgCode);
		return listHtBoaInOrg;
	}
	private List<HtBoaInBusinessOrg> getAllSubOrg(List<HtBoaInBusinessOrg> listHtBoaInOrg,String orgCode) {
		List<HtBoaInBusinessOrg> listSubHtBoaInOrg = htBoaInOrgBusinessRepository.findByParentOrgCode(orgCode);//获取下级机构
		if(listSubHtBoaInOrg!=null&&!listSubHtBoaInOrg.isEmpty()) {
			listHtBoaInOrg.addAll(listSubHtBoaInOrg);
			for(HtBoaInBusinessOrg o :listSubHtBoaInOrg) {
				return getAllSubOrg(listHtBoaInOrg,o.getBusinessOrgCode());
			}
		} 
		return listHtBoaInOrg;
	}

	public List<HtBoaInBusinessOrg> findByOrgLevel(Integer orgLevel) {
		return htBoaInOrgBusinessRepository.findByOrgLevel(orgLevel);
	}

	public List<HtBoaInBusinessOrg> findByParentOrgCodeAndOrgLevel(String busiOrgCode, Integer orgLevel) {
		return htBoaInOrgBusinessRepository.findByParentOrgCodeAndOrgLevel(busiOrgCode,orgLevel);
	}

	/**
	 * 获取所有下级机构
	 * @param orgCode
	 * @return
	 */
	public List<BoaInOrgInfo> getSubOrgInfo(String orgCode) {
		List<BoaInOrgInfo> listHtBoaInOrg = new ArrayList<BoaInOrgInfo>();
		getSubOrg(listHtBoaInOrg,orgCode);
		return listHtBoaInOrg;
	}
	
	public List<BoaInOrgInfo> findAllSubOrgInfo(String keyword) {
		List<BoaInOrgInfo> listHtBoaInOrg = htBoaInOrgBusinessRepository.listOrg("%"+keyword+"%");//获取下级机构
		return listHtBoaInOrg;
	}
	
	private List<BoaInOrgInfo> getSubOrg(List<BoaInOrgInfo> listHtBoaInOrg,String orgCode) {
		List<BoaInOrgInfo> listSubHtBoaInOrg = htBoaInOrgBusinessRepository.listOrgByParentOrgCode(orgCode);//获取下级机构
		if(listSubHtBoaInOrg!=null&&!listSubHtBoaInOrg.isEmpty()) {
			listHtBoaInOrg.addAll(listSubHtBoaInOrg);
			for(BoaInOrgInfo o :listSubHtBoaInOrg) {
				return getSubOrg(listHtBoaInOrg, o.getOrgCode());
			}
		} 
		return listHtBoaInOrg;
	}

	public void delete(HtBoaInBusinessOrg u) {
		htBoaInOrgBusinessRepository.delete(u);
	}
	
	private String getBmOrgPath(String bmDeptId,List<TbDepartment> listTbDepartment) {
		TbDepartment tbDepartment = null;
		if(listTbDepartment!=null&&!listTbDepartment.isEmpty()&&StringUtils.isNotEmpty(bmDeptId)) {
		  Optional<TbDepartment> tbDepartmentOptional = listTbDepartment.stream().filter(dept -> bmDeptId.equals(dept.getDeptId())).findFirst();
		  if(tbDepartmentOptional!=null&&tbDepartmentOptional.isPresent()) {
			  tbDepartment = tbDepartmentOptional.get();
		  }
		}
		if(tbDepartment!=null) {
			try {
				if(tbDepartment.getDeptId().equals(tbDepartment.getParentOrgCode())) {
					return  tbDepartment.getDeptId();
				} else {
					TbDepartment tbDepartmentP = null;// findByDeptId(ddDept.getParentId());
					if(listTbDepartment!=null&&!listTbDepartment.isEmpty()&&StringUtils.isNotEmpty(tbDepartment.getParentOrgCode())) {
						  String parentDeptId = tbDepartment.getParentOrgCode();
						  Optional<TbDepartment> tbDepartmentPOptional = listTbDepartment.stream().filter(dept -> parentDeptId.equals(dept.getDeptId())).findFirst();
						  if(tbDepartmentPOptional!=null&&tbDepartmentPOptional.isPresent()) {
							  tbDepartmentP = tbDepartmentPOptional.get();
						  }
					}
					if(tbDepartmentP!=null) {
						return getBmOrgPath(tbDepartmentP.getDeptId(),listTbDepartment)+"/"+tbDepartment.getDeptId();
					}
				}
			} catch (Exception e) {
				 
			}
		}
		return "";
	} 
	
	public Result convertBmOrg( ) {
		try {
			Sort sort = new Sort(new Order(Direction.ASC, "orgLevel"));
			List<TbDepartment> listTbDepartment = tbDepartmentRepository.findAll(sort);
			List<TbDepartment> listTbDepartmentDeal = new ArrayList<TbDepartment>();
				for(TbDepartment tbDepartment :listTbDepartment) {
					String path = getBmOrgPath(tbDepartment.getDeptId(),listTbDepartment);
					Integer level = 0;
					if (path != null) {
						// 根据指定的字符构建正则
						Pattern pattern = Pattern.compile("/");
						// 构建字符串和正则的匹配
						Matcher matcher = pattern.matcher(path);
						int count = 0;
						// 循环依次往下匹配
						while (matcher.find()) { // 如果匹配,则数量+1
							count++;
						}
						level = count + 2;
					}
					if (StringUtils.isEmpty(tbDepartment.getParentOrgCode())) {
						level = 1;
					}
					tbDepartment.setBusinessRegion(level+"");
					listTbDepartmentDeal.add(tbDepartment);
				}
				if(listTbDepartmentDeal!=null&&!listTbDepartmentDeal.isEmpty()) {
					Collections.sort(listTbDepartmentDeal, (a, b) -> a.getBusinessRegion().compareTo(b.getBusinessRegion())); //升序排序
					//按等级进行保存
					for(TbDepartment tbDepartment :listTbDepartmentDeal) {
						HtBoaInBusinessOrg htBoaInBusinessOrgNew = null;
						String newOrgCode ="";
						String newParentOrgCode ="";
						if(tbDepartment.getDeptId().equals(tbDepartment.getParentOrgCode())) {//顶级机构
							newOrgCode = "BD01";
							newParentOrgCode = newOrgCode;
						}else {
							HtBoaInContrast htBoaInContrastP  = null;
							List<HtBoaInContrast> listHtBoaInContrastP = htBoaInContrastRepository.findByBmBusinessIdAndType(tbDepartment.getParentOrgCode(), "10");
				            if(listHtBoaInContrastP!=null&&!listHtBoaInContrastP.isEmpty()) {
				            	htBoaInContrastP = listHtBoaInContrastP.get(0);
				            }
				            if(htBoaInContrastP!=null) {
				            	List<HtBoaInBusinessOrg> listP = htBoaInOrgBusinessRepository.findByBusinessOrgCode(htBoaInContrastP.getUcBusinessId());
				            	HtBoaInBusinessOrg htBoaInBusinessOrgP = null;
				            	if(listP!=null&&!listP.isEmpty()) {
				            		htBoaInBusinessOrgP = listP.get(0);
				            	}
				            	if(htBoaInBusinessOrgP!=null) {
				            		newParentOrgCode = htBoaInBusinessOrgP.getBusinessOrgCode();
				            		List<HtBoaInBusinessOrg> listSub =  htBoaInOrgBusinessRepository.findByParentOrgCode(htBoaInContrastP.getUcBusinessId());
					            	if(listSub!=null&&!listSub.isEmpty()) {
					            		newOrgCode = String.format("%s%02d", htBoaInBusinessOrgP.getBusinessOrgCode(), listSub.size()+1);
					            	}else {
					            		newOrgCode = String.format("%s%02d", htBoaInBusinessOrgP.getBusinessOrgCode(),  1);
					            	}
				            	}
				            } 
						}
						List<HtBoaInContrast> listHtBoaInContrastBm = htBoaInContrastRepository.findByBmBusinessIdAndType(tbDepartment.getDeptId(), "10");
			            if(listHtBoaInContrastBm!=null&&!listHtBoaInContrastBm.isEmpty()) {
			            	continue;//已经转换的则不再转换
			            }
						if(StringUtils.isEmpty(newOrgCode)) {
							newOrgCode = "BD01";
						}
						List<HtBoaInBusinessOrg> listNew = htBoaInOrgBusinessRepository.findByBusinessOrgCode(newOrgCode);
		            	if(listNew!=null&&!listNew.isEmpty()) {
		            		htBoaInBusinessOrgNew = listNew.get(0);
		            	} 
						if(htBoaInBusinessOrgNew==null) {
							htBoaInBusinessOrgNew = new HtBoaInBusinessOrg();
							htBoaInBusinessOrgNew.setBusinessOrgCode(newOrgCode);
						}
						htBoaInBusinessOrgNew.setParentOrgCode(newParentOrgCode);
						htBoaInBusinessOrgNew.setBusinessOrgName(tbDepartment.getDeptName());
						htBoaInBusinessOrgNew.setBusinessGroup(tbDepartment.getBusinessGroup());
						htBoaInBusinessOrgNew.setBranchCode(tbDepartment.getBranchCode());
						htBoaInBusinessOrgNew.setDistrictCode(tbDepartment.getDistrictCode());
						htBoaInBusinessOrgNew.setFinanceCode(tbDepartment.getFinanceCode());
						htBoaInBusinessOrgNew.setApprovalCode(tbDepartment.getApprovalCode());
						htBoaInBusinessOrgNew.setActivityCode(tbDepartment.getActivityCode());
						htBoaInBusinessOrgNew.setBmOrgCode(tbDepartment.getDeptId());
						htBoaInBusinessOrgNew.setOrgLevel(tbDepartment.getOrgLevel());
						htBoaInBusinessOrgNew.setStatus(0);
						htBoaInBusinessOrgNew.setProvince(tbDepartment.getProvince());
						htBoaInBusinessOrgNew.setCity(tbDepartment.getCity());
						htBoaInBusinessOrgNew.setCounty(tbDepartment.getCounty());
						htBoaInBusinessOrgNew.setDataSource(3);
						htBoaInBusinessOrgNew.setCreatedDatetime(new Date());
						htBoaInBusinessOrgNew.setUpdateDatetime(new Date());
						htBoaInBusinessOrgNew.setJpaVersion(0);
						htBoaInBusinessOrgNew = htBoaInOrgBusinessRepository.save(htBoaInBusinessOrgNew);
						
						//添加机构与钉钉的关联
						HtBoaInContrast htBoaInContrast = null;
		                List<HtBoaInContrast> listHtBoaInContrast = htBoaInContrastRepository.findByUcBusinessIdAndType(newOrgCode, "10");
		            	if(listHtBoaInContrast!=null&&!listHtBoaInContrast.isEmpty()) {
		            		htBoaInContrast = listHtBoaInContrast.get(0);
		            	} 
		                if(htBoaInContrast==null) {
		                	htBoaInContrast = new HtBoaInContrast();
		                	htBoaInContrast.setType("10");
		                    htBoaInContrast.setUcBusinessId(newOrgCode);
		                    htBoaInContrast.setBmBusinessId(tbDepartment.getDeptId());
		                    htBoaInContrast.setContrast("信贷自动对照");
		                    htBoaInContrast.setContrastDatetime(new Date());
		                }else {
		                	htBoaInContrast.setUcBusinessId(newOrgCode);
		                    htBoaInContrast.setBmBusinessId(tbDepartment.getDeptId());
		                    htBoaInContrast.setContrastDatetime(new Date());
		                }
		                htBoaInContrastRepository.save(htBoaInContrast);
					}

				}
				System.out.println("转换完成");
	    	return Result.buildSuccess();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.buildFail("9999", e.getMessage());
		}
	}

	public void convertBmBranch() {
		//List<HtBoaInContrast> listHtBoaInContrast = htBoaInContrastRepository.findByType("10");
		List<HtBoaInBusinessOrg> listHtBoaInBusinessOrg = htBoaInOrgBusinessRepository.findAll();
		List<HtBoaInBusinessOrg> listHtBoaInBusinessOrgUpdate = new ArrayList<HtBoaInBusinessOrg>();
		for(HtBoaInBusinessOrg htBoaInBusinessOrg : listHtBoaInBusinessOrg) {
			HtBoaInContrast htBoaInContrastBranch = null;
			HtBoaInContrast htBoaInContrastDispatch = null;
			HtBoaInContrast htBoaInContrastApprove = null;
			HtBoaInContrast htBoaInContrastActivi = null;
			HtBoaInContrast htBoaInContrastFinance = null;
			if(StringUtils.isNotEmpty(htBoaInBusinessOrg.getDistrictCode())) { //片区
				List<HtBoaInContrast> list = htBoaInContrastRepository.findByBmBusinessIdAndType(htBoaInBusinessOrg.getDistrictCode(), "10");
				if(list!=null&&!list.isEmpty()) {
					htBoaInContrastDispatch = list.get(0);
				}
				if(htBoaInContrastDispatch!=null) {
					htBoaInBusinessOrg.setDistrictCode(htBoaInContrastDispatch.getUcBusinessId());
				}
			}
			if(StringUtils.isNotEmpty(htBoaInBusinessOrg.getBranchCode())) {//分公司
				List<HtBoaInContrast> list = htBoaInContrastRepository.findByBmBusinessIdAndType(htBoaInBusinessOrg.getBranchCode(), "10");
				if(list!=null&&!list.isEmpty()) {
					htBoaInContrastBranch = list.get(0);
				}
				if(htBoaInContrastBranch!=null) {
					htBoaInBusinessOrg.setBranchCode(htBoaInContrastBranch.getUcBusinessId());
				}
			}
			if(StringUtils.isNotEmpty(htBoaInBusinessOrg.getActivityCode())) {//流程中心
				List<HtBoaInContrast> list = htBoaInContrastRepository.findByBmBusinessIdAndType(htBoaInBusinessOrg.getActivityCode(), "10");
				if(list!=null&&!list.isEmpty()) {
					htBoaInContrastActivi = list.get(0);
				}
				if(htBoaInContrastActivi!=null) {
					htBoaInBusinessOrg.setActivityCode(htBoaInContrastActivi.getUcBusinessId());
				}
			}
			if(StringUtils.isNotEmpty(htBoaInBusinessOrg.getApprovalCode())) {//审批中心
				List<HtBoaInContrast> list = htBoaInContrastRepository.findByBmBusinessIdAndType(htBoaInBusinessOrg.getApprovalCode(), "10");
				if(list!=null&&!list.isEmpty()) {
					htBoaInContrastApprove = list.get(0);
				}
				if(htBoaInContrastApprove!=null) {
					htBoaInBusinessOrg.setApprovalCode(htBoaInContrastApprove.getUcBusinessId());
				}
			}
			if(StringUtils.isNotEmpty(htBoaInBusinessOrg.getFinanceCode())) {//财务中心
				List<HtBoaInContrast> list = htBoaInContrastRepository.findByBmBusinessIdAndType(htBoaInBusinessOrg.getFinanceCode(), "10");
				if(list!=null&&!list.isEmpty()) {
					htBoaInContrastFinance = list.get(0);
				}
				if(htBoaInContrastFinance!=null) {
					htBoaInBusinessOrg.setFinanceCode(htBoaInContrastFinance.getUcBusinessId());
				}
			}
			listHtBoaInBusinessOrgUpdate.add(htBoaInBusinessOrg);
		}
		htBoaInOrgBusinessRepository.save(listHtBoaInBusinessOrgUpdate);
		System.out.println("转换完成");
	}
	
	public void convertBmBranchAll() {
		List<HtBoaInBusinessOrg> listHtBoaInBusinessOrg = htBoaInOrgBusinessRepository.findAll();
		List<HtBoaInBusinessOrg> listHtBoaInBusinessOrgUpdate = new ArrayList<HtBoaInBusinessOrg>();
		for(HtBoaInBusinessOrg htBoaInBusinessOrg : listHtBoaInBusinessOrg) {
			if(htBoaInBusinessOrg.getOrgLevel()>40) { //片区以下的需要挂靠片区
				HtBoaInBusinessOrg dispatch = getOrgInfoByOrgType(htBoaInBusinessOrg.getBusinessOrgCode(),"40"); //分公司 小组 部门 需要找到对应的所属片区
				if(dispatch!=null) {
					htBoaInBusinessOrg.setDistrictCode(dispatch.getBusinessOrgCode());
				}
			}
			if(htBoaInBusinessOrg.getOrgLevel()==60) { //如果是分公司下的分公司则取上级分公司
            	if(StringUtils.isNotEmpty(htBoaInBusinessOrg.getParentOrgCode())) {
            		HtBoaInBusinessOrg branchP = getOrgInfoByOrgType(htBoaInBusinessOrg.getParentOrgCode(),"60"); //  小组 部门 需要找到对应的所属分公司
    				if(branchP!=null) {
    					if(branchP.getOrgLevel()==60) {
    						htBoaInBusinessOrg.setBranchCode(branchP.getBusinessOrgCode());
    					}
    				}
            	}
			}
            if(htBoaInBusinessOrg.getOrgLevel()>60) { //公司以下的还需要挂靠到分公司
            	HtBoaInBusinessOrg branch = getOrgInfoByOrgType(htBoaInBusinessOrg.getBusinessOrgCode(),"60"); //  小组 部门 需要找到对应的所属分公司
				if(branch!=null) {
					htBoaInBusinessOrg.setBranchCode(branch.getBusinessOrgCode());
				}
			}
            
			listHtBoaInBusinessOrgUpdate.add(htBoaInBusinessOrg);
		}
		htBoaInOrgBusinessRepository.save(listHtBoaInBusinessOrgUpdate);
		System.out.println("转换完成");
	}
	
	public HtBoaInBusinessOrg getOrgInfoByOrgType(String orgCode, String orgType) {
		return getParentOrgs(orgCode,orgType);
	}

    private HtBoaInBusinessOrg getParentOrgs(String orgCode,String orgType) {
    	HtBoaInBusinessOrg htBoaInBusinessOrg = null;
    	List<HtBoaInBusinessOrg>  listHtBoaInBusinessOrg = this.htBoaInOrgBusinessRepository.findByBusinessOrgCode(orgCode);
		if(listHtBoaInBusinessOrg!=null&&!listHtBoaInBusinessOrg.isEmpty()) {
			htBoaInBusinessOrg = listHtBoaInBusinessOrg.get(0);
		}
		if(htBoaInBusinessOrg!=null && orgType.equals(htBoaInBusinessOrg.getOrgLevel()+"")) {
			return htBoaInBusinessOrg;
		}
		if("BD01".equals(orgCode)) {
			return htBoaInBusinessOrg;
		} else {
			return  getParentOrgs(htBoaInBusinessOrg.getParentOrgCode(),orgType);
		}
	}
 
}
