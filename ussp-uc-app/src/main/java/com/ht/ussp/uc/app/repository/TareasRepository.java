package com.ht.ussp.uc.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ht.ussp.uc.app.domain.Tareas;

 
@Repository
public interface TareasRepository extends  JpaRepository<Tareas, Long>,JpaSpecificationExecutor<Tareas>{

	List<Tareas> findByParentId(String parentId);
	
	List<Tareas> findByLevelType(String levelType);
}
