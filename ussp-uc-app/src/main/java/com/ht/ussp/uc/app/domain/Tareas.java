package com.ht.ussp.uc.app.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="t_areas")
@NamedQuery(name="Tareas.findAll", query="SELECT h FROM Tareas h")
public class Tareas implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private String id;
	
	@Column(name="parent_id")
	private String parentId;
	
	@Column(name="name")
	private String name;

	@Column(name="level_type")
	private String levelType;
	
}