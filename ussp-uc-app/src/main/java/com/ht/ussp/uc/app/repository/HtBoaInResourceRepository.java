package com.ht.ussp.uc.app.repository;

import java.util.List;

import com.ht.ussp.uc.app.domain.HtBoaInUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ht.ussp.uc.app.domain.HtBoaInResource;
import com.ht.ussp.uc.app.vo.ResVo;

/**
 * @author wim qiuwenwu@hongte.info
 * @ClassName: HtBoaInResourceRepository
 * @Description: 资源操作接口
 * @date 2018年1月15日 下午2:27:45
 */
public interface HtBoaInResourceRepository extends JpaSpecificationExecutor<HtBoaInResource>, JpaRepository<HtBoaInResource, Long> {
    //查询资源--管理员  (模块、菜单、按钮、api)
    @Query(value = "select new com.ht.ussp.uc.app.vo.ResVo(resCode,resNameCn,sequence,resType,resParent,resContent,fontIcon) from HtBoaInResource where status=0 and app= :app and resType in(:res_type) order by resParent,sequence")
    public List<ResVo> queryResForY(@Param("res_type") List<String> res_type, @Param("app") String app);

    //查询资源--非管理员
    @Query(value = "select new com.ht.ussp.uc.app.vo.ResVo(resCode,resNameCn,sequence,resType,resParent,resContent,fontIcon) from  HtBoaInResource  where status=0 and app= :app and resType in(:res_type) and resCode in(:res_code) order by resParent,sequence")
    public List<ResVo> queryResForN(@Param("res_code") List<String> res_code, @Param("res_type") List<String> res_type, @Param("app") String app);

    List<HtBoaInResource> findByResParent(String resPanrent);

    List<HtBoaInResource> findByAppAndStatusAndDelFlag(String app, String status, int delFlag);

    List<HtBoaInResource> findByAppAndResParentAndResTypeIn(String app, String resPanrent, List<String> resTypes);

    @Query(value = "SELECT MAX(res_code) FROM HT_BOA_IN_RESOURCE WHERE APP=?1  AND ((?2='NULL' AND RES_PARENT IS NULL) OR (RES_PARENT=?2)) AND RES_TYPE in(?3) ", nativeQuery = true)
    String queryMaxResCodeByAppAndParentAndType(String app, String resPanrent, List<String> resTypes);
}
