package com.ht.ussp.uc.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ht.ussp.uc.app.domain.HtBoaInPositionUser;

/**
 * 
 * @ClassName: HtBoaInPositionUserRepository
 * @Description: 用户岗位持久层
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月11日 上午10:22:45
 */
public interface HtBoaInPositionUserRepository extends JpaRepository<HtBoaInPositionUser, Long> {
	
	@Query(value="select POSITION_CODE from HT_BOA_IN_POSITION_USER where USER_ID= :userId",nativeQuery=true)
	public List<String> queryPositionCodes(@Param("userId") String userId);
	
	 public HtBoaInPositionUser findById(Long id);
}
