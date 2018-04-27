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

}
