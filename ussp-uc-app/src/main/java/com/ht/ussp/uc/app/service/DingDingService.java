package com.ht.ussp.uc.app.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ht.ussp.uc.app.domain.DdDept;
import com.ht.ussp.uc.app.domain.DdDeptOperator;
import com.ht.ussp.uc.app.domain.DdUser;
import com.ht.ussp.uc.app.domain.HtBoaInPwdHist;
import com.ht.ussp.uc.app.repository.DdDeptOperatorRepository;
import com.ht.ussp.uc.app.repository.DdDeptRepository;
import com.ht.ussp.uc.app.repository.DdDeptUserRepository;
import com.ht.ussp.util.HttpClientUtil;

@Service
public class DingDingService {

	
	@Autowired
    private DdDeptRepository ddDeptRepository;

	@Autowired
    private DdDeptUserRepository ddDeptUserRepository;
	
	@Autowired
    private DdDeptOperatorRepository ddDeptOperatorRepository;
	

	/**
	 * 钉钉与生产数据对照
	 */
	public List<DdUser> getDDUserList() {
		return ddDeptUserRepository.findAll();
	}
	
	public List<DdDept> getDdDeptList() {
		return ddDeptRepository.findAll();
	}
	
	public List<DdDept> findDDByParentId(String parentId) {
		return ddDeptRepository.findByParentId(parentId);
	}
	
	public DdDept findByDDParentOrgCode(String parentId) {
		return ddDeptRepository.findByDeptId(parentId);
	}
	
	@Transactional
	public boolean getDD() {
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
			DdDept ddDeptTop = new DdDept();
			ddDeptTop.setDeptId(authDept);
			ddDeptTop.setDeptName("鸿特信息");
			ddDeptTop.setCreatDatetime(new Date());
			ddDeptTop.setLevel(1);
			listDdDept.add(ddDeptTop);
			List<DdUser> listDdDeptUser = new ArrayList<DdUser>();
			if(jsonDepts!=null) {
				for(int i = 0;i<jsonDepts.size();i++) {
					JSONObject depts = (JSONObject) jsonDepts.get(i);
					DdDept ddDept = new DdDept();
					ddDept.setDeptId(depts.getString("id"));
					ddDept.setDeptName(new String(depts.getString("name").getBytes(),"utf-8"));
					ddDept.setParentId(depts.getString("parentid"));
					ddDept.setCreatDatetime(new Date());
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
			    			DdUser DdUser = new DdUser();
			    			DdUser.setUserId(deptUsers.getString("userid"));
			    			DdUser.setUserName(new String(deptUsers.getString("name").getBytes(),"utf-8"));
			    			DdUser.setDeptId(depts.get("id")+"");
			    			DdUser.setEmail(deptUsers.getString("email"));
			    			DdUser.setMobile(deptUsers.getString("mobile"));
			    			DdUser.setJobNumber(deptUsers.getString("jobnumber"));
			    			DdUser.setPosition(deptUsers.getString("position"));
			    			DdUser.setCreatDatetime(new Date());
			    			listDdDeptUser.add(DdUser); 
			    			//ddDeptUserRepository.saveAndFlush(DdUser);
			    		}
		    		}
				}
				ddDeptRepository.deleteAll();
				ddDeptRepository.save(listDdDept);
				ddDeptUserRepository.deleteAll();
				ddDeptUserRepository.save(listDdDeptUser);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	    return false;
	}
	
	public DdDept findByDeptId(String deptId) {
		return ddDeptRepository.findByDeptId(deptId);
	}
	
	public List<DdDept> saveOrUpdateDDDpet(List<DdDept> updateDDOrgList) {
		return ddDeptRepository.save(updateDDOrgList);
	}
	
	public List<DdDeptOperator>  saveDeptOperator(List<DdDeptOperator> ddDeptOperatorList) {
		ddDeptOperatorRepository.deleteAll();
		return ddDeptOperatorRepository.save(ddDeptOperatorList);
	}
	
	public List<DdDeptOperator>  updateDeptOperator(List<DdDeptOperator> ddDeptOperatorList) {
		return ddDeptOperatorRepository.save(ddDeptOperatorList);
	}
	
	public DdDeptOperator saveDeptOperator(DdDeptOperator ddDeptOperator) {
		return ddDeptOperatorRepository.saveAndFlush(ddDeptOperator);
	}
	
	public List<DdDeptOperator> getDdDeptOperatorOrderByLevel(DdDeptOperator u) {
        ExampleMatcher matcher = ExampleMatcher.matching();
        Example<DdDeptOperator> ex = Example.of(u, matcher);
		return ddDeptOperatorRepository.findAll(ex, new Sort(new Order("level")));
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
    		String listDeptResBody = HttpClientUtil.getInstance().doGet(getDeptList+"?access_token="+token+"&id="+authDept, null);
    		System.out.println(listDeptResBody);
    		JSONObject jsonDeptRes = JSONObject.parseObject(listDeptResBody);
    		System.out.println(jsonDeptRes);
    		JSONArray jsonDepts = jsonDeptRes.getJSONArray("department");
    		/*System.out.println("部门总数："+jsonDepts.size());
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
