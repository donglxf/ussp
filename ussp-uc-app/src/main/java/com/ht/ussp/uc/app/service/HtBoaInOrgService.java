package com.ht.ussp.uc.app.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.multipart.MultipartFile;

import com.ht.ussp.bean.ExcelBean;
import com.ht.ussp.common.Constants;
import com.ht.ussp.uc.app.domain.HtBoaInOrg;
import com.ht.ussp.uc.app.model.BoaInOrgInfo;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.repository.HtBoaInOrgRepository;
import com.ht.ussp.util.ExcelUtils;

@Service
public class HtBoaInOrgService {

	@Autowired
    private HtBoaInOrgRepository htBoaInOrgRepository;
	
    public HtBoaInOrg findById(Long id) {
        return this.htBoaInOrgRepository.findById(id);
    }

    public List<HtBoaInOrg> findAll(HtBoaInOrg u) {
        ExampleMatcher matcher = ExampleMatcher.matching();
        Example<HtBoaInOrg> ex = Example.of(u, matcher);
        return this.htBoaInOrgRepository.findAll(ex);
    }

    public List<HtBoaInOrg> findAll() {
        return this.htBoaInOrgRepository.findAll();
    }

    public List<HtBoaInOrg> add(List<HtBoaInOrg> orgList) {
        return this.htBoaInOrgRepository.save(orgList);
    }

    public HtBoaInOrg add(HtBoaInOrg u) {
        return this.htBoaInOrgRepository.save(u);
    }

    public HtBoaInOrg update(HtBoaInOrg u) {
        return this.htBoaInOrgRepository.save(u);
    }

    public Object findAllByPage(PageConf pageConf, Map<String, String> query) {
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
        String search = "";
        String orgPath = "";
        if (query != null && query.size() > 0 && query.get("orgCode") != null) {
            orgPath = query.get("orgCode");
        }
        if (query != null && query.size() > 0 && query.get("keyWord") != null) {
            search = "%" + query.get("keyWord") + "%";
        }

        if (null == search || 0 == search.trim().length())
            search = "%%";


        if (null != pageable) {
            Page<BoaInOrgInfo> p = this.htBoaInOrgRepository.lisOrgByPageWeb(pageable, search, orgPath);
            return p;
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
    public List<HtBoaInOrg> getOrgTreeList(String parentOrgCode) {
        HtBoaInOrg queryOrg = new HtBoaInOrg();
        queryOrg.setParentOrgCode(parentOrgCode);
        //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                // 忽略 id 和 createTime 字段。
                .withIgnorePaths("id", "createdDatetime", "orgPath", "dataSource","jpaVersion", "sequence")
                // 忽略为空字段。
                .withIgnoreNullValues();
        //创建实例
        Example<HtBoaInOrg> ex = Example.of(queryOrg, matcher);
        return htBoaInOrgRepository.findAll(ex, new Sort(Sort.Direction.ASC, "sequence"));
    }

    public List<HtBoaInOrg> findByOrgCode(String orgCode) {
        return this.htBoaInOrgRepository.findByOrgCode(orgCode);
    }

    public List<HtBoaInOrg> findByParentOrgCode(String parentOrgCode) {
        return this.htBoaInOrgRepository.findByParentOrgCode(parentOrgCode);
    }
    
    public HtBoaInOrg getOrgInfoByOrgType(String orgCode, String orgType) {
		return getParentOrgs(orgCode,orgType);
	}

    private HtBoaInOrg getParentOrgs(String orgCode,String orgType) {
    	HtBoaInOrg htBoaInOrg = null;
    	List<HtBoaInOrg> listHtBoaInOrg = this.htBoaInOrgRepository.findByOrgCode(orgCode);
		if(listHtBoaInOrg!=null&&!listHtBoaInOrg.isEmpty()) {
			htBoaInOrg = listHtBoaInOrg.get(0);
		}
		if(htBoaInOrg!=null && orgType.equals(htBoaInOrg.getOrgType())) {
			return htBoaInOrg;
		}
		if("D01".equals(orgCode)) {
			return htBoaInOrg;
		} else {
			return  getParentOrgs(htBoaInOrg.getParentOrgCode(),orgType);
		}
	}

    public List<HtBoaInOrg> getOrgListByTime(String startTime, String endTime) {
		return this.htBoaInOrgRepository.getByLastModifiedDatetime(startTime,endTime);
	}

    public XSSFWorkbook exportOrgExcel() {
        XSSFWorkbook book = null;
        try {
            List<HtBoaInOrg> listHtBoaInOrg = this.htBoaInOrgRepository.findAll();
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
    public void importOrgExcel(InputStream in, MultipartFile file, String userId) {
        try {
            List<List<Object>> listob = ExcelUtils.getBankListByExcel(in, file.getOriginalFilename());
            for (int i = 0; i < listob.size(); i++) {
                List<Object> ob = listob.get(i);
                HtBoaInOrg u = new HtBoaInOrg();
                u.setLastModifiedDatetime(new Date());
                u.setOrgCode(String.valueOf(ob.get(0)));
                u.setOrgNameCn(String.valueOf(ob.get(1)));
                u.setParentOrgCode(String.valueOf(ob.get(2)));
                u.setCreatedDatetime(new Date());
                u.setDelFlag(Constants.DEL_0);
                u.setCreateOperator(userId);
                u = add(u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public String getMaxOrgCode(String parentOrgCode) {
		return  this.htBoaInOrgRepository.getMaxOrgCode(parentOrgCode);
	}
}
