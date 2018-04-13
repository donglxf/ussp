package com.ht.ussp.uc.app.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ht.ussp.uc.app.vo.AppAndRoleVo;
import com.ht.ussp.util.ExcelUtils;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.ht.ussp.uc.app.domain.HtBoaInApp;
import com.ht.ussp.uc.app.domain.HtBoaInOrg;
import com.ht.ussp.uc.app.model.BoaInAppInfo;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.repository.HtBoaInAppRepository;
import com.ht.ussp.uc.app.vo.AppAndResourceVo;

@Service
public class HtBoaInAppService {
    @Autowired
    private HtBoaInAppRepository htBoaInAppRepository;

    public HtBoaInApp findById(Long id) {
        return htBoaInAppRepository.findById(id);
    }
    public List<HtBoaInApp> findAllApp(String status) {
		return htBoaInAppRepository.findByStatus(status);
	}
    /**
     * 加载系统与权限资源的组合树数据<br>
     *
     * @author 谭荣巧
     * @Date 2018/1/17 15:30
     */
    public List<AppAndResourceVo> loadAppAndResourceVoList() {
        List<Object[]> list = htBoaInAppRepository.queryAppAndAuthTree();
        List<AppAndResourceVo> aaaList = new ArrayList<>();
        AppAndResourceVo aaa;
        for (Object[] objects : list) {
            aaa = new AppAndResourceVo(objects[0], objects[1], objects[2], objects[3], objects[4], objects[5], objects[6], objects[7]);
            aaaList.add(aaa);
        }
        return aaaList;
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

        String orgPath = "";
        if (query != null && query.size() > 0 && query.get("orgCode") != null) {
            orgPath = "%" + query.get("orgCode") + "%";
        }

        String search = pageConf.getSearch();
        if (null == search || 0 == search.trim().length())
            search = "%%";
        else
            search = "%" + search + "%";
        if (null != pageable) {
            Page<BoaInAppInfo> p = this.htBoaInAppRepository.listAllAppByPage(pageable, search);
            return p;
        }
        return null;
    }

    public HtBoaInApp add(HtBoaInApp u) {
        return this.htBoaInAppRepository.saveAndFlush(u);
    }

    public HtBoaInApp update(HtBoaInApp u) {
        return this.htBoaInAppRepository.save(u);
    }

    public void delete(long id) {
        this.htBoaInAppRepository.delete(id);
    }

    public List<HtBoaInApp> findByAppCode(String appCode) {
        return this.htBoaInAppRepository.findByApp(appCode);
    }

    /**
     * 加载系统与角色的组合树数据<br>
     *
     * @author 谭荣巧
     * @Date 2018/1/25 15:51
     */
    public List<AppAndRoleVo> loadAppAndRoleVoList() {
        List<Object[]> list = htBoaInAppRepository.queryAppAndRoleTree();
        List<AppAndRoleVo> arList = new ArrayList<>();
        AppAndRoleVo aaa;
        for (Object[] objects : list) {
            aaa = new AppAndRoleVo(objects[0], objects[1], objects[2], objects[3], objects[4]);
            arList.add(aaa);
        }
        return arList;
    }

	public XSSFWorkbook exportAppExcel() {
		XSSFWorkbook book = null;
		try {
			List<HtBoaInApp> listHtBoaInApp = this.htBoaInAppRepository.findAll();
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
	public void importAppExcel(InputStream in, MultipartFile file, String userId) {
		try {
			List<List<Object>> listob = ExcelUtils.getBankListByExcel(in,file.getOriginalFilename());    
		    for (int i = 0; i < listob.size(); i++) {    
		            List<Object> ob = listob.get(i);    
		            HtBoaInApp u = new HtBoaInApp();
		            u.setApp(String.valueOf(ob.get(0)));
		            u.setNameCn(String.valueOf(ob.get(1)));
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
