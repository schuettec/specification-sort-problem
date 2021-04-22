package com.github.schuettec.specificationorderbydemo.domain;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

@DataJpaTest
public class PersonRepositoryTest {
	@Autowired
	PersonRepository repo;
	@Autowired
	AddressRepository addresses;
	@Autowired
	EntityManager em;

	/**
	 * Get some test data into database.
	 */
	@BeforeEach
	public void setup() {
		Address a1 = addresses.save(Address.builder().id(0L).city("Dortmund").street("street1").build());
		Address a2 = addresses.save(Address.builder().id(1L).city("Dortmund").street("street2").build());
		Address a3 = addresses.save(Address.builder().id(2L).city("Berlin").street("street3").build());

		repo.save(Person.builder().id(0L).name("Dieter0").address(a1).addresses(asList(a1, a2)).build());
		repo.save(Person.builder().id(1L).name("Dieter1").address(a2).addresses(asList(a1)).build());
		repo.save(Person.builder().id(2L).name("Dieter2").address(a3).build());

		em.flush();
	}

	/**
	 * This test shows that we need to use distinct when filtering, because
	 * otherwise 1-M relations lead to duplicates when joining. Together with
	 * distinct and sorting, the error occurs.
	 */
	@Test
	public void duplicates() {
		Specification<Person> spec = new Specification<Person>() {

			@Override
			public Predicate toPredicate(Root<Person> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				query.distinct(true);
				return criteriaBuilder.and(criteriaBuilder.equal(root.join("address").get("city"), "Dortmund"),
						criteriaBuilder.equal(root.join("addresses").get("city"), "Dortmund"));
			}
		};
		List<Person> result = repo.findAll(spec, Sort.by("address.city"));
		assertEquals(2, result.size());
	}

	/**
	 * This test shows the isolated problem when using distinct together with
	 * sorting. No duplicates are provoked here, but the combination of order by and
	 * distinct is enough to reproduce the error.
	 */
	@Test
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
