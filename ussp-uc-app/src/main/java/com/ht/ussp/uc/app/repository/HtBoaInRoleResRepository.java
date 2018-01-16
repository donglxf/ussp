package com.ht.ussp.uc.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ht.ussp.uc.app.domain.HtBoaInResource;
/**
 * 
 * @ClassName: HtBoaInRoleRes
 * @Description: 资源角色关系接口
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月15日 下午3:08:56
 */
public interface HtBoaInRoleResRepository extends JpaRepository<HtBoaInResource, Long>{
	@Query(value="select RES_CODE from HT_BOA_IN_ROLE_RES where DEL_FLAG=0 and ROLE_CODE in(:role_code)",nativeQuery=true)
	public List<String> queryResByCode(@Param("role_code") List<String> role_code);
}
