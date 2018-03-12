package com.ht.ussp.uc.app.repository;

import com.ht.ussp.uc.app.domain.HtBoaInRoleRes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author wim qiuwenwu@hongte.info
 * @ClassName: HtBoaInRoleRes
 * @Description: 资源角色关系接口
 * @date 2018年1月15日 下午3:08:56
 */
public interface HtBoaInRoleResRepository extends JpaSpecificationExecutor<HtBoaInRoleRes>, JpaRepository<HtBoaInRoleRes, Long> {
    @Query(value = "select RES_CODE from HT_BOA_IN_ROLE_RES where DEL_FLAG=0 and ROLE_CODE in(:role_code)", nativeQuery = true)
    List<String> queryResByCode(@Param("role_code") List<String> role_code);

    int deleteByRoleCode(String roleCode);

    List<HtBoaInRoleRes> findByResCode(String resCode);

    List<HtBoaInRoleRes>  findByResCodeAndRoleCode(String resCode, String roleCode);

	void deleteById(Long id);
}
