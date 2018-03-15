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
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.CorpRoleListRequest;
import com.dingtalk.api.request.CorpRoleSimplelistRequest;
import com.dingtalk.api.response.CorpRoleListResponse;
import com.dingtalk.api.response.CorpRoleSimplelistResponse;
import com.ht.ussp.bean.ExcelBean;
import com.ht.ussp.common.Constants;
import com.ht.ussp.uc.app.domain.DdRole;
import com.ht.ussp.uc.app.domain.DdRoleUser;
import com.ht.ussp.uc.app.domain.HtBoaInOrg;
import com.ht.ussp.uc.app.domain.HtBoaInRole;
import com.ht.ussp.uc.app.model.BoaInRoleInfo;
import com.ht.ussp.uc.app.model.PageConf;
import com.ht.ussp.uc.app.repository.DdRoleRepository;
import com.ht.ussp.uc.app.repository.DdRoleUserRepository;
import com.ht.ussp.uc.app.repository.HtBoaInRoleRepository;
import com.ht.ussp.util.ExcelUtils;
import com.ht.ussp.util.HttpClientUtil;

@Service
public class HtBoaInRoleService {

	@Autowired
    private HtBoaInRoleRepository htBoaInRoleRepository;
	
	@Autowired
    private DdRoleRepository ddRoleRepository;
	
	@Autowired
    private DdRoleUserRepository ddRoleUserRepository;
    
    public HtBoaInRole findById(Long id) {
        return this.htBoaInRoleRepository.findById(id);
    }

    public List<HtBoaInRole> findAll(HtBoaInRole u) {
        ExampleMatcher matcher = ExampleMatcher.matching();
        Example<HtBoaInRole> ex = Example.of(u, matcher);
        return this.htBoaInRoleRepository.findAll(ex);
    }
    
    public Object findAllRoleByAppPage(PageConf pageConf, List<String> apps) {
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
             Page<HtBoaInRole> p = this.htBoaInRoleRepository.findByAppInAndDelFlagAndStatus(pageable,apps,0,"0");
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
		try {
			//1.获取token
			String token = (String) JSONObject.parseObject(HttpClientUtil.getInstance().doGet(getTokenUrl, null)).get("access_token");
	        
	        //2.获取角色列表
			DingTalkClient clientRole = new DefaultDingTalkClient("https://eco.taobao.com/router/rest");
			CorpRoleListRequest reqRole = new CorpRoleListRequest();
			/*reqRole.setSize(100L);
			reqRole.setOffset(0L);*/
			CorpRoleListResponse rspRole = clientRole.execute(reqRole, token);
			JSONObject jsonRspRole = JSONObject.parseObject(rspRole.getBody());
	        JSONArray rspRoleGroup = jsonRspRole.getJSONObject("dingtalk_corp_role_list_response").getJSONObject("result").getJSONObject("list").getJSONArray("role_groups");
	        List<DdRole> listDdRole = new ArrayList<DdRole>();
	        List<DdRoleUser> listDdRoleUser = new ArrayList<DdRoleUser>();
	        for (int i = 0; i < rspRoleGroup.size(); i++) {
	        	JSONObject roleGroup = (JSONObject) rspRoleGroup.get(i);
	        	String roleGroupName=roleGroup.getString("group_name");
	            JSONArray roleArr = roleGroup.getJSONObject("roles").getJSONArray("roles");
	            for (int j = 0; j < roleArr.size(); j++) {
	            	JSONObject role = (JSONObject) roleArr.get(j);
	            	DdRole ddRole = new DdRole();
		        	ddRole.setRoleId(role.getString("id"));
		        	ddRole.setRoleName(role.getString("role_name"));
		        	ddRole.setGroupName(roleGroupName);
		        	ddRole.setCreatedDatetime(new Date());
		        	listDdRole.add(ddRole);
	            	//获取角色的员工列表
	    			DingTalkClient roleUserclient = new DefaultDingTalkClient("https://eco.taobao.com/router/rest");
	    			CorpRoleSimplelistRequest roleUserReq = new CorpRoleSimplelistRequest();
	    			roleUserReq.setRoleId(role.getLong("id"));
	    			/*roleUserReq.setSize(100L);
	    			roleUserReq.setOffset(0L);*/
	    			CorpRoleSimplelistResponse rspRoleUser = roleUserclient.execute(roleUserReq,token);
	    			JSONObject jsonRspRoleUser = JSONObject.parseObject(rspRoleUser.getBody());
	    	        JSONArray rspRoleUserArr = jsonRspRoleUser.getJSONObject("dingtalk_corp_role_simplelist_response").getJSONObject("result").getJSONObject("list").getJSONArray("emp_simple_list");
	    	        if(rspRoleUserArr!=null) {
	    	        	for (int k = 0; k < rspRoleUserArr.size(); k++) {
		    	        	JSONObject roleuser = (JSONObject) rspRoleUserArr.get(k);
		    	        	DdRoleUser ddRoleUser = new DdRoleUser();
		    	        	ddRoleUser.setRoleId(role.getString("id"));
		    	        	ddRoleUser.setUserId(roleuser.getString("userid"));
		    	        	ddRoleUser.setCreatedDatetime(new Date());
			    			listDdRoleUser.add(ddRoleUser);
		    	        }
	    	        }
	            }
	        }
	        ddRoleRepository.save(listDdRole);
	        ddRoleUserRepository.save(listDdRoleUser);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
	  public static void main(String[] args) {
	    	String getTokenUrl="https://oapi.dingtalk.com/gettoken?corpid=dingb9594db0ecde853a35c2f4657eb6378f&corpsecret=g445dW-0hpWTXkgJGDjty01q_xulKO9EDlX_XV4AOeZaciJDsEBW14xzXNdFPxF1";
	    	try {
	    		//1.获取token
	    		String token = (String) JSONObject.parseObject(HttpClientUtil.getInstance().doGet(getTokenUrl, null)).get("access_token");
	    		
	    		//5.获取角色列表
	    		DingTalkClient clientRole = new DefaultDingTalkClient("https://eco.taobao.com/router/rest");
				CorpRoleListRequest reqRole = new CorpRoleListRequest();
			 
				CorpRoleListResponse rspRole = clientRole.execute(reqRole, token);
				JSONObject jsonRspRole = JSONObject.parseObject(rspRole.getBody());
				System.out.println(jsonRspRole);
		        JSONArray rspRoleGroup = jsonRspRole.getJSONObject("dingtalk_corp_role_list_response").getJSONObject("result").getJSONObject("list").getJSONArray("role_groups");
		        System.out.println("角色组总数："+rspRoleGroup.size());
		        int roleCount=0;
		        for (int i = 0; i < rspRoleGroup.size(); i++) {
		        	JSONObject roleGroup = (JSONObject) rspRoleGroup.get(i);
		            JSONArray roleArr = roleGroup.getJSONObject("roles").getJSONArray("roles");
		            for (int j = 0; j < roleArr.size(); j++) {
		            	JSONObject role = (JSONObject) roleArr.get(j);
		            	System.out.println(role);
		            	//获取角色的员工列表
		    			DingTalkClient roleUserclient = new DefaultDingTalkClient("https://eco.taobao.com/router/rest");
		    			CorpRoleSimplelistRequest roleUserReq = new CorpRoleSimplelistRequest();
		    			roleUserReq.setRoleId(role.getLong("id"));
		    		 
		    			CorpRoleSimplelistResponse rspRoleUser = roleUserclient.execute(roleUserReq,token);
		    			System.out.println(rspRoleUser.getBody());
		            	roleCount++;
		            }
		        }
		        System.out.println("角色总数："+roleCount);
				
	    		
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

}
