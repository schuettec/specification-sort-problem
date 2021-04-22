package com.github.schuettec.specificationorderbydemo.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Address {
	@Id
	private Long id;
	private String street;
	private String city;
}
