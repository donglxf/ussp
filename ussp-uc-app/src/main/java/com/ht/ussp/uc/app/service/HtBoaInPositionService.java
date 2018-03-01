package com.ht.ussp.uc.app.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.ht.ussp.uc.app.domain.HtBoaInPosition;
import com.ht.ussp.uc.app.model.BoaInPositionInfo;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.repository.HtBoaInPositionRepository;
import com.ht.ussp.util.ExcelUtils;

@Service
public class HtBoaInPositionService {

    @Autowired
    private HtBoaInPositionRepository htBoaInPositionRepository;

    public HtBoaInPosition findById(Long id) {
        return this.htBoaInPositionRepository.findById(id);
    }

    public List<HtBoaInPosition> findAll(HtBoaInPosition u) {
        ExampleMatcher matcher = ExampleMatcher.matching();
        Example<HtBoaInPosition> ex = Example.of(u, matcher);
        return this.htBoaInPositionRepository.findAll(ex);
    }
    
    public List<HtBoaInPosition> findByPositionCodeIn(Set<String> positionCodes) {
        return this.htBoaInPositionRepository.findByPositionCodeIn(positionCodes);
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
            pageable = new PageRequest(pageConf.getPage(), pageConf.getSize(),  sort);
        String search = "";
        String orgPath = "";
        if (query != null && query.size() > 0 && query.get("orgCode") != null) {
        	orgPath =  query.get("orgCode") ;
        }
        if (query != null && query.size() > 0 && query.get("keyWord") != null) {
        	search = "%" +query.get("keyWord")+ "%";
        }
        
        if (null == search || 0 == search.trim().length())
            search = "%%";
    
        
        if (null != pageable) {
            Page<BoaInPositionInfo> p = this.htBoaInPositionRepository.listPositionByPageWeb(pageable, search,orgPath);
            return p;
        } else {
            List<BoaInPositionInfo> p = this.htBoaInPositionRepository.listPositionInfo(search);
          
            return p;
        }
    }

    public HtBoaInPosition add(HtBoaInPosition u) {
        return this.htBoaInPositionRepository.saveAndFlush(u);
    }

    public HtBoaInPosition update(HtBoaInPosition u) {
        return this.htBoaInPositionRepository.save(u);
    }
    
    public void delete(long id) {
        this.htBoaInPositionRepository.delete(id);;
    }

	public List<HtBoaInPosition> findByPositionCode(String positionCode) {
		return this.htBoaInPositionRepository.findByPositionCode(positionCode);
	}

	public XSSFWorkbook exportPositionExcel() {
		XSSFWorkbook book = null;
		try {
			List<HtBoaInPosition> listHtBoaInApp = this.htBoaInPositionRepository.findAll();
			List<ExcelBean> ems = new ArrayList<>();
			Map<Integer, List<ExcelBean>> map = new LinkedHashMap<>();
			ems.add(new ExcelBean("机构编码", "orgCode", 0));
			ems.add(new ExcelBean("机构名称", "orgNameCn", 0));
			ems.add(new ExcelBean("父机构编码", "parentOrgCode", 0));
			ems.add(new ExcelBean("排序", "sequence", 0));
			ems.add(new ExcelBean("状态", "delFlag", 0));
			map.put(0, ems);
			book = ExcelUtils.createExcelFile(HtBoaInOrg.class, listHtBoaInApp, map, "机构信息");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return book;
	}

	@Transactional
	public void importPositionExcel(InputStream in, MultipartFile file, String userId) {
		try {
			List<List<Object>> listob = ExcelUtils.getBankListByExcel(in,file.getOriginalFilename());    
		    for (int i = 0; i < listob.size(); i++) {    
		            List<Object> ob = listob.get(i);    
		            HtBoaInPosition u = new HtBoaInPosition();
		            u.setPositionCode(String.valueOf(ob.get(0)));
		    		u.setPositionNameCn(String.valueOf(ob.get(1)));
		    		u.setParentOrgCode(String.valueOf(ob.get(2)));
		    		u.setSequence(Integer.parseInt(String.valueOf(ob.get(3))));
		    		
		    		u.setRootOrgCode("HT");
		            u.setStatus(Constants.STATUS_0);
		            u.setLastModifiedDatetime(new Date());
		            u.setCreatedDatetime(new Date());
		            u.setDelFlag(Constants.DEL_0);
		            u.setCreateOperator(userId);
		            u = add(u);
		        }    
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
