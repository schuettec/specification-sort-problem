package com.github.schuettec.specificationorderbydemo.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.github.springtestdbunit.annotation.DatabaseSetup;

@DataJpaTest
public class PersonRepositoryTest {
	@Autowired
	PersonRepository repo;

	@Test
	@DatabaseSetup("PersonRepositoryTest.xml")
	public void crashes() {
		Specification<Person> spec = new Specification<Person>() {

			@Override
			public Predicate toPredicate(Root<Person> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				query.distinct(true);
				return criteriaBuilder.equal(root.join("address").get("city"), "Dortmund");
			}
		};
		List<Person> result = repo.findAll(spec, Sort.by("address.city"));
		assertEquals(2, result);
	}

}
