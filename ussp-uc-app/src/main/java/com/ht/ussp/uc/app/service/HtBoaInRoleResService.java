package com.ht.ussp.uc.app.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ht.ussp.uc.app.domain.HtBoaInResource;
import com.ht.ussp.uc.app.domain.HtBoaInRole;
import com.ht.ussp.uc.app.domain.HtBoaInRoleRes;
import com.ht.ussp.uc.app.repository.HtBoaInResourceRepository;
import com.ht.ussp.uc.app.repository.HtBoaInRoleRepository;
import com.ht.ussp.uc.app.repository.HtBoaInRoleResRepository;
import com.ht.ussp.uc.app.vo.RoleAndResVo;

/**
 * @author wim qiuwenwu@hongte.info
 * @ClassName: HtBoaInRoleResService
 * @Description: 资源编码关系服务
 * @date 2018年1月15日 下午3:29:24
 */
@Service
public class HtBoaInRoleResService {

    @Autowired
    private HtBoaInRoleResRepository htBoaInRoleResRepository;
    @Autowired
    private HtBoaInResourceRepository htBoaInResourceRepository;
    @Autowired
    private HtBoaInRoleRepository htBoaInRoleRepository;

    /**
     * @return List<String>
     * @throws
     * @Title: queryResByCode
     * @Description: 通过角色编码查询资源编码
     */
    public List<String> queryResByCode(String... role_code) {
        return htBoaInRoleResRepository.queryResByCode(Arrays.asList(role_code));
    }

    /**
     * 保存角色与资源的关系<br>
     *
     * @param roleAndResVo 角色与资源的关系数据集
     * @param userId       创建人ID
     * @return true 保存成功
     * @author 谭荣巧
     * @Date 2018/1/30 15:16
     */
    @Transactional
    public boolean saveRoleAndRes(RoleAndResVo roleAndResVo, String userId) {
        String roleCode = roleAndResVo.getRoleCode();
        String[] resCodes = roleAndResVo.getResCodes();
        //先删除已有的角色与资源的关系
        htBoaInRoleResRepository.deleteByRoleCode(roleCode);
        //重新保存角色与资源的关系
        List<HtBoaInResource> allList = htBoaInResourceRepository.findByAppAndStatusInAndDelFlag(roleAndResVo.getApp(), new String[]{"0", "2"}, 0);
        List<HtBoaInRoleRes> htBoaInRoleRess = new ArrayList<>();
        HtBoaInRoleRes rr;
        for (String resCode : resCodes) {
            if (StringUtils.isEmpty(resCode)) {
                continue;
            }
            rr = new HtBoaInRoleRes();
            rr.setResCode(resCode);
            rr.setRoleCode(roleCode);
            rr.setDelFlag(0);
            rr.setCreateOperator(userId);
            rr.setUpdateOperator(userId);
            htBoaInRoleRess.add(rr);
            //
            List<HtBoaInResource> apiList = allList.stream().filter(res -> "api".equals(res.getResType()) && resCode.equals(res.getResParent())).collect(Collectors.toList());
            for (HtBoaInResource resource : apiList) {
                rr = new HtBoaInRoleRes();
                rr.setResCode(resource.getResCode());
                rr.setRoleCode(roleCode);
                rr.setDelFlag(0);
                rr.setCreateOperator(userId);
                rr.setUpdateOperator(userId);
                htBoaInRoleRess.add(rr);
            }
        }
        htBoaInRoleResRepository.save(htBoaInRoleRess);
        return true;
    }

    public void delete(HtBoaInRoleRes u) {
        this.htBoaInRoleResRepository.delete(u);
    }

    public void deleteByRoleCode(String roleCode) {
        this.htBoaInRoleResRepository.deleteByRoleCode(roleCode);
    }
    
    public void save(HtBoaInRoleRes u) {
        this.htBoaInRoleResRepository.save(u);
    }
    
    public List<HtBoaInRoleRes> findByResCodeAndRoleCode(String resCode,String roleCode) {
       return this.htBoaInRoleResRepository.findByResCodeAndRoleCode(resCode,roleCode);
    }

	public void deleteById(Long id) {
		this.htBoaInRoleResRepository.deleteById(id);
	}

	public List<HtBoaInRoleRes> getAllByApp(String app) {
		return htBoaInRoleResRepository.getAllByApp(app);
	}

	public Map<String, String> analysisRoleResData(List<HtBoaInRoleRes> sourceListHtBoaInRoleRes) {

		StringBuffer sbf = new StringBuffer(); // 升级脚本
		StringBuffer fallbacksbf = new StringBuffer(); // 回滚脚本
		boolean isAnais = false; // 是否生成了升级脚本 false没有 true生成了脚本
		String enter = "\r\n";
		String app = "";
		List<HtBoaInRoleRes> needAdd = new ArrayList<HtBoaInRoleRes>();
		needAdd.addAll(sourceListHtBoaInRoleRes);
		List<HtBoaInRoleRes> needUpdate = new ArrayList<HtBoaInRoleRes>();
		List<HtBoaInRoleRes> needDel = new ArrayList<HtBoaInRoleRes>();
		
		if (sourceListHtBoaInRoleRes != null && !sourceListHtBoaInRoleRes.isEmpty()) {
			String roleCode = sourceListHtBoaInRoleRes.get(0).getRoleCode();
			List<HtBoaInRole> listHtBoaInRole = htBoaInRoleRepository.findByRoleCode(roleCode);
			if(listHtBoaInRole==null || listHtBoaInRole.isEmpty()) {
				return null;
			}
			app = listHtBoaInRole.get(0).getApp();
			
			sbf.append(enter+"-- "+app);
			sbf.append("系统升级脚本 (HT_BOA_IN_ROLE_RES 角色资源表 )" + enter);
			List<HtBoaInRoleRes> targetListHtBoaInRoleRes = htBoaInRoleResRepository.getAllByApp(app);// 目标数据
			needDel.addAll(targetListHtBoaInRoleRes);
			if (targetListHtBoaInRoleRes != null && !targetListHtBoaInRoleRes.isEmpty()) {
				for (HtBoaInRoleRes sourceHtBoaInRoleRes : sourceListHtBoaInRoleRes) {
					List<HtBoaInRoleRes> sameHtBoaInRole = targetListHtBoaInRoleRes.stream().filter(targetRole -> sourceHtBoaInRoleRes.getRoleCode().equals(targetRole.getRoleCode()) && sourceHtBoaInRoleRes.getResCode().equals(targetRole.getResCode()) ).collect(Collectors.toList());
					needUpdate.addAll(sameHtBoaInRole);// 两边都有的数据，则需要更新
					needAdd.removeAll(sameHtBoaInRole);// src库多出的数据，得到目标库需要新增的数据（去除两边都有的）
					if (sameHtBoaInRole != null && !sameHtBoaInRole.isEmpty()) {
						if (sourceHtBoaInRoleRes.getRoleCode().equals(sameHtBoaInRole.get(0).getRoleCode())&&sourceHtBoaInRoleRes.getResCode().equals(sameHtBoaInRole.get(0).getResCode())) {
							needAdd.remove(sourceHtBoaInRoleRes);// src库多出的数据，得到目标库需要新增的数据（去除两边都有的）
						}
					}
					needDel.removeAll(sameHtBoaInRole);// 目标库多出的数据，得到目标库需要删除的数据（去除两边都有的）
				}
				
				if (needDel != null && !needDel.isEmpty()) {
					sbf.append("-- 需要删除的数据  请确认生产是否需要删除 " + enter);
					fallbacksbf.append("-- 回滚删除的数据 " + enter);
					for (HtBoaInRoleRes delHtBoaInRoleRes : needDel) {
						if ("0".equals((delHtBoaInRoleRes.getDelFlag() + ""))) {
							sbf.append("UPDATE HT_BOA_IN_ROLE_RES SET DEL_FLAG='1' WHERE RES_CODE='" + delHtBoaInRoleRes.getRoleCode() + "' AND ROLE_CODE='" + delHtBoaInRoleRes.getRoleCode() + "';" + enter);
							fallbacksbf.append("UPDATE HT_BOA_IN_ROLE_RES SET DEL_FLAG='1' WHERE RES_CODE='" + delHtBoaInRoleRes.getRoleCode() + "' AND ROLE_CODE='" + delHtBoaInRoleRes.getRoleCode() + "';" + enter);
							isAnais = true;
						}
					}
				}

				if (needAdd != null && !needAdd.isEmpty()) {
					sbf.append("-- 需要添加的数据 " + enter);
					fallbacksbf.append("-- 回滚添加的数据 " + enter);
					for (HtBoaInRoleRes addHtBoaInRoleRes : needAdd) {
						sbf.append( "INSERT INTO  `HT_BOA_IN_ROLE_RES` (  `RES_CODE`, `ROLE_CODE`,  `DEL_FLAG` ) VALUES (");
						sbf.append("'" + addHtBoaInRoleRes.getRoleCode() + "',");
						sbf.append("'" + addHtBoaInRoleRes.getResCode() + "',");
						sbf.append("'" + addHtBoaInRoleRes.getDelFlag() + "'");
						sbf.append(");" + enter);
						fallbacksbf.append( "DELETE FROM  HT_BOA_IN_ROLE_RES WHERE ROLE_CODE='" + addHtBoaInRoleRes.getRoleCode() + "' AND RES_CODE='" + addHtBoaInRoleRes.getResCode() + "';" + enter);
						isAnais = true;
					}
				}
				
			}else { //全量添加
				for (HtBoaInRoleRes addHtBoaInRoleRes : sourceListHtBoaInRoleRes) {
					sbf.append( "INSERT INTO  `HT_BOA_IN_ROLE_RES` (  `RES_CODE`, `ROLE_CODE`,  `DEL_FLAG` ) VALUES (");
					sbf.append("'" + addHtBoaInRoleRes.getRoleCode() + "',");
					sbf.append("'" + addHtBoaInRoleRes.getResCode() + "',");
					sbf.append("'" + addHtBoaInRoleRes.getDelFlag() + "'");
					sbf.append(");" + enter);
					fallbacksbf.append( "DELETE FROM  HT_BOA_IN_ROLE_RES WHERE ROLE_CODE='" + addHtBoaInRoleRes.getRoleCode() + "' AND RES_CODE='" + addHtBoaInRoleRes.getResCode() + "';" + enter);
					isAnais = true;
				}
			}
		}
		if(isAnais) {
			sbf.append("--  ====HT_BOA_IN_ROLE_RES 角色资源表  end ===="+enter);
			fallbacksbf.append("--  ====HT_BOA_IN_ROLE_RES 角色资源表  end ===="+enter);
		}
		Map<String, String> backMap = new HashMap<String, String>();
		backMap.put("resultData", sbf.toString());
		backMap.put("resultDataBack", fallbacksbf.toString());
		backMap.put("isAnais", isAnais+"");
		return backMap;
	
	}

}
