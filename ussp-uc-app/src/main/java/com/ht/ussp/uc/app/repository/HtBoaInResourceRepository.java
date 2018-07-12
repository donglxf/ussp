package com.ht.ussp.uc.app.repository;

import java.util.List;

import com.ht.ussp.uc.app.domain.HtBoaInUser;
import com.ht.ussp.uc.app.vo.ApiResourceVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ht.ussp.uc.app.domain.HtBoaInResource;
import com.ht.ussp.uc.app.vo.ResVo;
import org.springframework.stereotype.Repository;

/**
 * @author wim qiuwenwu@hongte.info
 * @ClassName: HtBoaInResourceRepository
 * @Description: 资源操作接口
 * @date 2018年1月15日 下午2:27:45
 */
@Repository
public interface HtBoaInResourceRepository extends JpaSpecificationExecutor<HtBoaInResource>, JpaRepository<HtBoaInResource, Long> {
    //查询资源--管理员  (模块、菜单、按钮、api)
    @Query(value = "select new com.ht.ussp.uc.app.vo.ResVo(resCode,resNameCn,sequence,resType,resParent,resContent,fontIcon,status,ruleNum,ruleNumName) from HtBoaInResource where status in ('0','2') and app= :app and resType in(:res_type) order by resParent,sequence")
    List<ResVo> queryResForY(@Param("res_type") List<String> res_type, @Param("app") String app);

    //查询资源--非管理员
    @Query(value = "select new com.ht.ussp.uc.app.vo.ResVo(resCode,resNameCn,sequence,resType,resParent,resContent,fontIcon,status,ruleNum,ruleNumName) from  HtBoaInResource  where status in ('0','2') and app= :app and resType in(:res_type) and resCode in(:res_code) order by resParent,sequence")
    List<ResVo> queryResForN(@Param("res_code") List<String> res_code, @Param("res_type") List<String> res_type, @Param("app") String app);

    List<HtBoaInResource> findByResParent(String resPanrent);

    List<HtBoaInResource> findByAppAndStatusInAndDelFlag(String app, String[] status, int delFlag);

    @Query(value = "SELECT MAX(res_code) FROM HT_BOA_IN_RESOURCE WHERE APP=?1  AND ((?2='NULL' AND RES_PARENT IS NULL) OR (RES_PARENT=?2)) AND RES_TYPE in(?3) AND RES_CODE like ?4", nativeQuery = true)
    String queryMaxResCodeByAppAndParentAndType(String app, String resPanrent, List<String> resTypes, String resCodePrefix);
    
    @Query(value = "SELECT CONCAT(?4,MAX(CONVERT(REPLACE(res_code,?4,''),SIGNED))) FROM HT_BOA_IN_RESOURCE WHERE APP=?1  AND ((?2='NULL' AND RES_PARENT IS NULL) OR (RES_PARENT=?2)) AND RES_TYPE in(?3) AND RES_CODE like concat(?4,'%')", nativeQuery = true)
    String queryMaxResCodeByAppAndParentAndType2(String app, String resPanrent, List<String> resTypes, String resCodePrefix);
    
    
    @Query(value = "SELECT res_code FROM HT_BOA_IN_RESOURCE WHERE APP=?1  AND ((?2='NULL' AND RES_PARENT IS NULL) OR (RES_PARENT=?2)) AND RES_TYPE in(?3) AND RES_CODE like ?4", nativeQuery = true)
    List<String> queryResCodeListByAppAndParentAndType(String app, String resPanrent, List<String> resTypes, String resCodePrefix);
    
    @Query(value = "SELECT MAX(res_code) FROM HT_BOA_IN_RESOURCE WHERE APP=?1  AND RES_TYPE in(?2) AND RES_CODE like ?3 AND RES_PARENT IS NULL", nativeQuery = true)
    String queryMaxMenuCodeByAppAndParentAndType(String app,   List<String> resTypes, String resCodePrefix);

    @Query(value = "SELECT new com.ht.ussp.uc.app.vo.ApiResourceVo(id,resNameCn,resContent,remark,resParent,resCode)" +
            "FROM HtBoaInResource " +
            "WHERE app=?1 AND resType='api' " +
            "AND resContent IS NOT NULL AND remark IS NOT NULL " +
            "AND CONCAT(resContent,remark) NOT IN( " +
            "SELECT CONCAT(resContent,remark) " +
            "FROM HtBoaInResource\n" +
            "WHERE app=?1 AND resType='api'  " +
            "AND resContent IS NOT NULL AND remark IS NOT NULL " +
            "AND resParent=?3) " +
            "AND (resCode LIKE %?2% OR resNameCn LIKE %?2% OR resContent LIKE %?2% OR remark LIKE %?2% OR ?2 is null OR ?2='') " +
            "GROUP BY resNameCn,resContent,remark " +
            "ORDER BY resNameCn "
            , countQuery = "SELECT COUNT(id) " +
            "FROM HtBoaInResource " +
            "WHERE app=?1 AND resType='api' " +
            "AND resContent IS NOT NULL AND remark IS NOT NULL " +
            "AND CONCAT(resContent,remark) NOT IN( " +
            "SELECT CONCAT(resContent,remark) " +
            "FROM HtBoaInResource\n" +
            "WHERE app=?1 AND resType='api' " +
            "AND resContent IS NOT NULL AND remark IS NOT NULL " +
            "AND resParent=?3) " +
            "AND (resCode LIKE %?2% OR resNameCn LIKE %?2% OR resContent LIKE %?2% OR remark LIKE %?2% OR ?2 is null OR ?2='') " +
            "GROUP BY resNameCn,resContent,remark "
    )
    Page<ApiResourceVo> queryApiByPage(String app, String keyWord, String resParent, Pageable pageable);

	HtBoaInResource findById(Long id);

	List<HtBoaInResource> findByResCodeAndApp(String resCode,String app);

	List<HtBoaInResource> findByAppAndResType(String app, String resType);
	
	List<HtBoaInResource> findByRemarkLikeAndResTypeAndApp(String remark, String resType,String app);
	
	List<HtBoaInResource> findByApp(String app);

	List<HtBoaInResource> findByResNameCnAndResContentAndAppAndRemark(String resNameCn, String resContent, String app,String remark);

	List<HtBoaInResource> findByResCode(String resCode);
}
