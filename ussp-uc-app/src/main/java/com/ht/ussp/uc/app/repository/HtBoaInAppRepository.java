package com.ht.ussp.uc.app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ht.ussp.uc.app.domain.HtBoaInApp;
import com.ht.ussp.uc.app.model.BoaInAppInfo;
import com.ht.ussp.uc.app.model.BoaInRoleInfo;

import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author wim qiuwenwu@hongte.info
 * @ClassName: HtBoaInAppRepository
 * @Description: 系统表持久层
 * @date 2018年1月8日 下午8:19:33
 */
public interface HtBoaInAppRepository extends JpaRepository<HtBoaInApp, Long> {

    public HtBoaInApp findById(Long id);

    @Query(value = "SELECT * FROM ( " +
            "SELECT " +
            "RES_CODE,RES_NAME,RES_NAME_CN,SEQUENCE,RES_TYPE, " +
            "IF(RES_PARENT IS NULL, (IF(RES_TYPE='group', CONCAT(APP,'_menu'), CONCAT(APP,'_',RES_TYPE))),RES_PARENT) AS RES_PARENT, " +
            "FONT_ICON, " +
            "APP " +
            "FROM HT_BOA_IN_RESOURCE " +
            "WHERE RES_TYPE IN('view','group','module') AND STATUS='0' " +
            "UNION ALL " +
            "SELECT " +
            "CONCAT(APP,'_menu'),'menu','菜单',1,'menuType',APP,NULL,APP  " +
            "FROM HT_BOA_IN_APP " +
            "WHERE STATUS='0' " +
            "UNION ALL " +
            "SELECT " +
            "CONCAT(APP,'_module'),'module','模块',2,'moduleType',APP,NULL,APP " +
            "FROM HT_BOA_IN_APP " +
            "WHERE STATUS='0' " +
            "UNION ALL " +
            "SELECT " +
            "APP,NAME,NAME_CN,1,'app', '0',NULL,APP " +
            "FROM HT_BOA_IN_APP " +
            "WHERE STATUS='0' " +
            ") A ORDER BY SEQUENCE,RES_NAME_CN ", nativeQuery = true)
    List<Object[]> queryAppAndAuthTree();
    
    
    
	@Query("SELECT new com.ht.ussp.uc.app.model.BoaInAppInfo(u.app, u.name, u.nameCn,  u.status, u.createOperator, u.createdDatetime, u.updateOperator, u.lastModifiedDatetime,u.delFlag,u.id) "
			+ "FROM HtBoaInApp u    WHERE ( u.app LIKE ?1 OR u.name LIKE ?1 OR u.nameCn LIKE ?1 )  AND  u.delFlag=0  GROUP BY u")
	public Page<BoaInAppInfo> listAllAppByPage(Pageable arg0, String search);
    

}
