package com.ht.ussp.uc.app.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ht.ussp.uc.app.domain.HtBoaInResource;
import com.ht.ussp.uc.app.domain.HtBoaInRoleRes;
import com.ht.ussp.uc.app.repository.HtBoaInResourceRepository;
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
}
