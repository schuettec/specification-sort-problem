package com.github.schuettec.specificationorderbydemo.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
public class Person {

	@Id
	private Long id;
	private String name;
	@ManyToOne
	private Address address;

	@ManyToMany
	private List<Address> addresses;

}
