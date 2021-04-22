package com.github.schuettec.specificationorderbydemo.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;

@Data
@Entity
public class Person {

	@Id
	private Long id;
	private String name;
	@ManyToOne
	private Address address;
}
