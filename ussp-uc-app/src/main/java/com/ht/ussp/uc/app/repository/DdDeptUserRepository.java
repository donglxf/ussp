package com.ht.ussp.uc.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.ht.ussp.uc.app.domain.DdUser;
import com.ht.ussp.uc.app.vo.DdUserVo;
 
public interface DdDeptUserRepository  extends JpaRepository<DdUser, Long> ,JpaSpecificationExecutor<DdUser>  {

	@Query("SELECT new com.ht.ussp.uc.app.vo.DdUserVo (u.userId,u.userName, u.deptId, d.deptName, u.email, u.idNo, u.mobile, u.jobNumber, u.position,u.creatDatetime) FROM DdUser u, DdDept d WHERE u.deptId=d.deptId GROUP BY u")
	List<DdUserVo> getDdUserList();
}
