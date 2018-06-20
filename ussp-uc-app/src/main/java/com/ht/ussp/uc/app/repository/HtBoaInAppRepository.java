package com.ht.ussp.uc.app.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ht.ussp.uc.app.domain.HtBoaInApp;
import com.ht.ussp.uc.app.model.BoaInAppInfo;

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
            "IF(RES_PARENT IS NULL, (IF(RES_TYPE='group' or RES_TYPE='view', CONCAT(APP,'_menu'), CONCAT(APP,'_',RES_TYPE))),RES_PARENT) AS RES_PARENT, " +
            "FONT_ICON, " +
            "APP " +
            "FROM HT_BOA_IN_RESOURCE " +
            "WHERE RES_TYPE IN('view','group','module') " +
            "AND DEL_FLAG='0' " +
            "UNION ALL " +
            "SELECT " +
            "CONCAT(APP,'_menu'),'menu','菜单',1,'menuType',APP,NULL,APP  " +
            "FROM HT_BOA_IN_APP " +
            "WHERE STATUS='0' " +
            "AND DEL_FLAG='0' " +
            "UNION ALL " +
            "SELECT " +
            "CONCAT(APP,'_module'),'module','模块',2,'moduleType',APP,NULL,APP " +
            "FROM HT_BOA_IN_APP " +
            "WHERE STATUS='0' " +
            "AND DEL_FLAG='0' " +
            "UNION ALL " +
            "SELECT CONCAT(APP,'_custom'),'custom','自定义权限',3,'customType',APP, NULL,APP " +
            "FROM HT_BOA_IN_APP " +
            "WHERE STATUS='0' AND DEL_FLAG='0' " +
            "UNION ALL " +
            "SELECT " +
            "APP,NAME,NAME_CN,1,'app', '0',NULL,APP " +
            "FROM HT_BOA_IN_APP " +
            "WHERE STATUS='0' " +
            "AND DEL_FLAG='0' " +
            ") A ORDER BY SEQUENCE,RES_NAME_CN ", nativeQuery = true)
    List<Object[]> queryAppAndAuthTree();

    @Query(value = "SELECT * FROM ( " +
            "SELECT " +
            "ROLE_CODE,ROLE_NAME,ROLE_NAME_CN,APP parentAPP,APP " +
            "FROM HT_BOA_IN_ROLE " +
            "WHERE STATUS='0' " +
            "AND DEL_FLAG='0' " +
            "UNION ALL " +
            "SELECT " +
            "APP,NAME,NAME_CN,'0',APP " +
            "FROM HT_BOA_IN_APP " +
            "WHERE STATUS='0' " +
            "AND DEL_FLAG='0' " +
            ") A ORDER BY ROLE_NAME_CN ", nativeQuery = true)
    List<Object[]> queryAppAndRoleTree();


    @Query("SELECT new com.ht.ussp.uc.app.model.BoaInAppInfo(u.app, u.name, u.nameCn,  u.status, u.createOperator, u.createdDatetime, u.updateOperator, u.lastModifiedDatetime,u.delFlag,u.id,u.isOS,u.maxLoginCount,u.tips,u.sysToken) "
            + "FROM HtBoaInApp u    WHERE ( u.app LIKE ?1 OR u.name LIKE ?1 OR u.nameCn LIKE ?1 )  AND  u.delFlag=0  GROUP BY u")
    public Page<BoaInAppInfo> listAllAppByPage(Pageable arg0, String search);

    List<HtBoaInApp> findByApp(String app);

	public List<HtBoaInApp> findByStatus(String status);

}
