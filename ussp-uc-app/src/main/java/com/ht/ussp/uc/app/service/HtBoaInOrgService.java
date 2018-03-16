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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ht.ussp.bean.ExcelBean;
import com.ht.ussp.common.Constants;
import com.ht.ussp.uc.app.domain.DdDept;
import com.ht.ussp.uc.app.domain.DdDeptUser;
import com.ht.ussp.uc.app.domain.HtBoaInOrg;
import com.ht.ussp.uc.app.model.BoaInOrgInfo;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.repository.DdDeptRepository;
import com.ht.ussp.uc.app.repository.DdDeptUserRepository;
import com.ht.ussp.uc.app.repository.HtBoaInOrgRepository;
import com.ht.ussp.util.ExcelUtils;
import com.ht.ussp.util.HttpClientUtil;

@Service
public class HtBoaInOrgService {

	@Autowired
    private HtBoaInOrgRepository htBoaInOrgRepository;
	
	@Autowired
    private DdDeptRepository ddDeptRepository;

	@Autowired
    private DdDeptUserRepository ddDeptUserRepository;
	
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
        return this.htBoaInOrgRepository.saveAndFlush(u);
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

	@Transactional
	public void getDD() {
		String getTokenUrl="https://oapi.dingtalk.com/gettoken?corpid=dingb9594db0ecde853a35c2f4657eb6378f&corpsecret=g445dW-0hpWTXkgJGDjty01q_xulKO9EDlX_XV4AOeZaciJDsEBW14xzXNdFPxF1";
		String getAuthUrl ="https://oapi.dingtalk.com/auth/scopes";
		String getDeptList = "https://oapi.dingtalk.com/department/list";
	    String getDeptUser = "https://oapi.dingtalk.com/user/list";
	    
	    try {

			//1.获取token
			String token = (String) JSONObject.parseObject(HttpClientUtil.getInstance().doGet(getTokenUrl, null)).get("access_token");
			//2.获取授权部门
			String authDeptResBody = HttpClientUtil.getInstance().doGet(getAuthUrl+"?access_token="+token, null);
			JSONObject jsonAuthDept = JSONObject.parseObject(authDeptResBody);
	        JSONArray jsonAuthDepts = jsonAuthDept.getJSONObject("auth_org_scopes").getJSONArray("authed_dept");
	        String authDept = jsonAuthDepts.getString(0);
			//3.获取部门列表
			String listDeptResBody = HttpClientUtil.getInstance().doGet(getDeptList+"?access_token="+token+"&id="+authDept, null);
			JSONObject jsonDeptRes = JSONObject.parseObject(listDeptResBody);
			JSONArray jsonDepts = jsonDeptRes.getJSONArray("department");
			List<DdDept> listDdDept = new ArrayList<DdDept>();
			List<DdDeptUser> listDdDeptUser = new ArrayList<DdDeptUser>();
			if(jsonDepts!=null) {
				for(int i = 0;i<jsonDepts.size();i++) {
					JSONObject depts = (JSONObject) jsonDepts.get(i);
					DdDept ddDept = new DdDept();
					ddDept.setDeptId(depts.getString("id"));
					ddDept.setDeptName(new String(depts.getString("name").getBytes(),"utf-8"));
					ddDept.setParentId(depts.getString("parentid"));
					ddDept.setCreatedDatetime(new Date());
					ddDept.setOrders(depts.getString("order"));
					listDdDept.add(ddDept);
					// ddDeptRepository.saveAndFlush(ddDept);
					//4.获取部门成员列表  ---需要循环调用添加
		    		String listDeptUserResBody = HttpClientUtil.getInstance().doGet(getDeptUser+"?access_token="+token+"&department_id="+depts.get("id"), null);
		    		JSONObject jsonUserRes = JSONObject.parseObject(listDeptUserResBody);
		    		JSONArray jsonDeptUsers = jsonUserRes.getJSONArray("userlist");
		    		if(jsonDeptUsers!=null) {
		    			for(int j=0;j<jsonDeptUsers.size();j++) {
			    			JSONObject deptUsers = (JSONObject) jsonDeptUsers.get(j);
			    			DdDeptUser ddDeptUser = new DdDeptUser();
			    			ddDeptUser.setUserId(deptUsers.getString("userid"));
			    			ddDeptUser.setUserName(new String(deptUsers.getString("name").getBytes(),"utf-8"));
			    			ddDeptUser.setOrgCode(depts.get("id")+"");
			    			ddDeptUser.setEmail(deptUsers.getString("email"));
			    			ddDeptUser.setMobile(deptUsers.getString("mobile"));
			    			ddDeptUser.setJobNumber(deptUsers.getString("jobnumber"));
			    			ddDeptUser.setPosition(deptUsers.getString("position"));
			    			ddDept.setCreatedDatetime(new Date());
			    			listDdDeptUser.add(ddDeptUser); 
			    			//ddDeptUserRepository.saveAndFlush(ddDeptUser);
			    		}
		    		}
				}
				ddDeptRepository.deleteAll();
				ddDeptRepository.save(listDdDept);
				ddDeptUserRepository.deleteAll();
				ddDeptUserRepository.save(listDdDeptUser);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 钉钉与生产数据对照
	 */
	public void getDDOrgDZ() {
		//1 递归获取钉钉orgpath
		List<DdDept> listDdDept = ddDeptRepository.findAll();
		for(DdDept ddDept :listDdDept) {
			if(ddDept!=null) {
				ddDept.setOrgPath(getDDOrgPath(ddDept));
			}
		}
		System.out.println("");
	 	ddDeptRepository.save(listDdDept);
		
		//2递归获取生产数据orgpath
		//3.对比可以对照的保存起来  不可以对照的保存起来
	}
	
	/**
	 * 钉钉与生产数据对照
	 */
	public List<DdDeptUser> getDDUserList() {
		return ddDeptUserRepository.findAll();
	}
	
	public List<DdDept> getDdDeptList() {
		return ddDeptRepository.findAll();
	}

	
	private String getDDOrgPath(DdDept ddDept) {
		try {
			if("58800327".equals(ddDept.getParentId())) {
				return  ddDept.getDeptName();
			} else {
				DdDept ddDeptP = ddDeptRepository.findByDeptId(ddDept.getParentId());
				return getDDOrgPath(ddDeptP)+"/"+ddDept.getDeptName();
			}
		} catch (Exception e) {
			 System.out.println(ddDept);
			 System.out.println(ddDept.getDeptId()+" lrc "+ddDept.getParentId());
		}
		return "";
	}

    public static void main(String[] args) {
    	String getTokenUrl="https://oapi.dingtalk.com/gettoken?corpid=dingb9594db0ecde853a35c2f4657eb6378f&corpsecret=g445dW-0hpWTXkgJGDjty01q_xulKO9EDlX_XV4AOeZaciJDsEBW14xzXNdFPxF1";
    	String getAuthUrl ="https://oapi.dingtalk.com/auth/scopes";
    	String getDeptList = "https://oapi.dingtalk.com/department/list";
    	String getDeptUser = "https://oapi.dingtalk.com/user/list";

    	try {
    		//1.获取token
    		String token = (String) JSONObject.parseObject(HttpClientUtil.getInstance().doGet(getTokenUrl, null)).get("access_token");
    		
    		//2.获取授权部门
    		String authDeptResBody = HttpClientUtil.getInstance().doGet(getAuthUrl+"?access_token="+token, null);
    		JSONObject jsonAuthDept = JSONObject.parseObject(authDeptResBody);
	        JSONArray jsonAuthDepts = jsonAuthDept.getJSONObject("auth_org_scopes").getJSONArray("authed_dept");
	        String authDept = jsonAuthDepts.getString(0);
    		System.out.println(authDept);
    		//3.获取部门列表
    		/*String listDeptResBody = HttpClientUtil.getInstance().doGet(getDeptList+"?access_token="+token+"&id="+authDept, null);
    		System.out.println(listDeptResBody);
    		JSONObject jsonDeptRes = JSONObject.parseObject(listDeptResBody);
    		System.out.println(jsonDeptRes);
    		JSONArray jsonDepts = jsonDeptRes.getJSONArray("department");
    		System.out.println("部门总数："+jsonDepts.size());
    		for(int i = 0;i<jsonDepts.size();i++) {
    			JSONObject depts = (JSONObject) jsonDepts.get(i);
    			//4.获取部门成员列表  ---需要循环调用添加
        		String listDeptUserResBody = HttpClientUtil.getInstance().doGet(getDeptUser+"?access_token="+token+"&department_id="+depts.get("id"), null);
        		System.out.println("id==>> "+depts.get("id")+listDeptUserResBody);
    		}*/
    		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}
