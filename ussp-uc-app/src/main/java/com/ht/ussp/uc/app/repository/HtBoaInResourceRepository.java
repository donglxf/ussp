package com.ht.ussp.uc.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ht.ussp.uc.app.domain.HtBoaInResource;
import com.ht.ussp.uc.app.vo.ResVo;

/**
 * 
 * @ClassName: HtBoaInResourceRepository
 * @Description: 资源操作接口
 * @author wim qiuwenwu@hongte.info
 * @date 2018年1月15日 下午2:27:45
 */
public interface HtBoaInResourceRepository extends JpaRepository<HtBoaInResource, Long>{
	//查询资源--管理员  (模块、菜单、按钮、api)
	@Query(value="select new com.ht.ussp.uc.app.vo.ResVo(resCode,resNameCn,sequence,resType,resParent,resContent,fontIcon) from HtBoaInResource where status=0 and app= :app and resType in(:res_type)")
	public List<ResVo> queryResForY(@Param("res_type") List<String> res_type,@Param("app") String app);

	//查询资源--非管理员
	@Query(value="select new com.ht.ussp.uc.app.vo.ResVo(resCode,resNameCn,sequence,resType,resParent,resContent,fontIcon) from  HtBoaInResource  where status=0 and app= :app and resType in(:res_type) and resCode in(:res_code)")
    public List<ResVo> queryResForN(@Param("res_code") List<String> res_code,@Param("res_type") List<String> res_type,@Param("app") String app);
	
}
