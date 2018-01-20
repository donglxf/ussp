package com.ht.ussp.uc.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ht.ussp.uc.app.domain.HtBoaInApp;
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
            ") A ORDER BY SEQUENCE,RES_NAME ", nativeQuery = true)
    List<Object[]> queryAppAndAuthTree();

}
