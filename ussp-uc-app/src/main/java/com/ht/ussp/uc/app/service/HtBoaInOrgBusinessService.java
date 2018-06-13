package com.ht.ussp.uc.app.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Predicate;

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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ht.ussp.bean.ExcelBean;
import com.ht.ussp.core.PageResult;
import com.ht.ussp.core.ReturnCodeEnum;
import com.ht.ussp.uc.app.domain.HtBoaInBusinessOrg;
import com.ht.ussp.uc.app.model.BoaInOrgInfo;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.repository.HtBoaInOrgBusinessRepository;
import com.ht.ussp.util.ExcelUtils;

@Service
public class HtBoaInOrgBusinessService {

	@Autowired
    private HtBoaInOrgBusinessRepository htBoaInOrgBusinessRepository;
	
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
    		Specification<HtBoaInBusinessOrg> specification = (root, query1, cb) -> {
    			Predicate p1 = cb.like(root.get("businessOrgName").as(String.class), "%" + keyWordSp + "%");
    			Predicate p2 = cb.like(root.get("businessOrgCode").as(String.class), "%" + keyWordSp + "%" );
    			Predicate p3 = cb.equal(root.get("parentOrgCode").as(String.class),   orgCodeSp );
    			// 把Predicate应用到CriteriaQuery中去,因为还可以给CriteriaQuery添加其他的功能，比如排序、分组啥的
    			query1.where(cb.and(cb.or(p1, p2),p3));
    			return query1.getRestriction();
    		};
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
    
    public HtBoaInBusinessOrg getOrgInfoByOrgType(String orgCode, String orgType) {
		return getParentOrgs(orgCode,orgType);
	}

    private HtBoaInBusinessOrg getParentOrgs(String orgCode,String orgType) {
    	HtBoaInBusinessOrg htBoaInBusinessOrg = null;
    	List<HtBoaInBusinessOrg> listHtBoaInOrg = this.htBoaInOrgBusinessRepository.findByBusinessOrgCode(orgCode);
		if(listHtBoaInOrg!=null&&!listHtBoaInOrg.isEmpty()) {
			htBoaInBusinessOrg = listHtBoaInOrg.get(0);
		}
		/*if(htBoaInBusinessOrg!=null && orgType.equals(htBoaInOrg.getOrgType())) {
			return htBoaInBusinessOrg;
		}*/
		if("D01".equals(orgCode)) {
			return htBoaInBusinessOrg;
		} else {
			return  getParentOrgs(htBoaInBusinessOrg.getParentOrgCode(),orgType);
		}
	}

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

    @Transactional
    public void importOrgExcel(InputStream in, MultipartFile file, String userId) {
        try {
            List<List<Object>> listob = ExcelUtils.getBankListByExcel(in, file.getOriginalFilename());
            for (int i = 0; i < listob.size(); i++) {
                List<Object> ob = listob.get(i);
                HtBoaInBusinessOrg u = new HtBoaInBusinessOrg();
                u.setLastModifiedDatetime(new Date());
               // u.setOrgCode(String.valueOf(ob.get(0)));
                //u.setOrgNameCn(String.valueOf(ob.get(1)));
                u.setParentOrgCode(String.valueOf(ob.get(2)));
                u.setCreatedDatetime(new Date());
               // u.setDelFlag(Constants.DEL_0);
                u.setCreateOperator(userId);
                u = add(u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

	public List<HtBoaInBusinessOrg> findByOrgLevel(String orgLevel) {
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
}
