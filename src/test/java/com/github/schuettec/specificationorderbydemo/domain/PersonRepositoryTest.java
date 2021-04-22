package com.github.schuettec.specificationorderbydemo.domain;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

@DataJpaTest
@Slf4j
public class PersonRepositoryTest {
  @Autowired
  PersonRepository repo;
  @Autowired
  AddressRepository addresses;
  @Autowired
  EntityManager em;

  @BeforeEach
  public void setup() {
    Address a1 = addresses.save(Address.builder().id(0L).city("Dortmund").street("street1").build());
    Address a2 = addresses.save(Address.builder().id(1L).city("Dortmund").street("street2").build());
    Address a3 = addresses.save(Address.builder().id(2L).city("Berlin").street("street3").build());

    repo.save(Person.builder().id(0L).name("Dieter0").address(a1).addresses(asList(a1, a2, a3)).build());
    repo.save(Person.builder().id(1L).name("Dieter1").address(a2).addresses(asList(a1, a2, a3)).build());
    repo.save(Person.builder().id(2L).name("Dieter2").address(a3).build());

    em.flush();
  }

  @Test
  public void duplicates_Simple() {
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
    result.forEach(this::print);
  }

  @Test
  public void duplicates_Simple_Page() {
    Specification<Person> spec = new Specification<Person>() {

      @Override
      public Predicate toPredicate(Root<Person> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        query.distinct(true);
        return criteriaBuilder.and(criteriaBuilder.equal(root.join("address").get("city"), "Dortmund"),
            criteriaBuilder.equal(root.join("addresses").get("city"), "Dortmund"));
      }
    };
    Page<Person> result = repo.findAll(spec, PageRequest.of(0, 50, Sort.by("address.city")));
    assertEquals(2, result.getTotalElements());
    result.forEach(this::print);
  }

  @Test
  public void duplicates() {
    Specification<Person> spec = new Specification<Person>() {

      @Override
      public Predicate toPredicate(Root<Person> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        query.distinct(true);

        List<Order> orderList = new ArrayList<>();

        Fetch<Person, Address> fetch = root.fetch("address");
        Join<Person, Address> join = (Join<Person, Address>) fetch;

        Fetch<Person, Address> fetch2 = root.fetch("addresses");
        Join<Person, Address> join2 = (Join<Person, Address>) fetch2;

        orderList.add(criteriaBuilder.desc(join.get("city")));
        orderList.add(criteriaBuilder.desc(join2.get("city")));

        query.orderBy(orderList);

        return criteriaBuilder.and(criteriaBuilder.equal(join.get("city"), "Dortmund"),
            criteriaBuilder.equal(join2.get("city"), "Dortmund"));
      }
    };

    List<Person> result = repo.findAll(spec);
    assertEquals(2, result.size());
  }

  @Test
  public void duplicatesPage() {
    Specification<Person> spec = new Specification<Person>() {

      @Override
      public Predicate toPredicate(Root<Person> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        query.distinct(true);

        List<Order> orderList = new ArrayList<>();

        Fetch<Person, Address> fetch = root.fetch("address");
        Join<Person, Address> join = (Join<Person, Address>) fetch;

        Fetch<Person, Address> fetch2 = root.fetch("addresses");
        Join<Person, Address> join2 = (Join<Person, Address>) fetch2;

        orderList.add(criteriaBuilder.desc(join.get("city")));
        orderList.add(criteriaBuilder.desc(join2.get("city")));

        query.orderBy(orderList);

        return criteriaBuilder.and(criteriaBuilder.equal(join.get("city"), "Dortmund"),
            criteriaBuilder.equal(join2.get("city"), "Dortmund"));
      }
    };

    Page<Person> result = repo.findAll(spec, PageRequest.of(0, 50));
    assertEquals(2, result.getTotalElements());
    log.info("page: {}", result);
    result.forEach(this::print);
  }

  @Test
  public void duplicatesPage_WithSort() {
    Specification<Person> spec = new Specification<Person>() {

      @Override
      public Predicate toPredicate(Root<Person> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        query.distinct(true);

        List<Order> orderList = new ArrayList<>();
        query.orderBy(orderList);
        if (Long.class != query.getResultType()) {
          Fetch<Person, Address> fetch = root.fetch("address");
          Join<Person, Address> join = (Join<Person, Address>) fetch;

          Fetch<Person, Address> fetch2 = root.fetch("addresses");
          Join<Person, Address> join2 = (Join<Person, Address>) fetch2;

          orderList.add(criteriaBuilder.desc(join.get("city")));
          orderList.add(criteriaBuilder.desc(join2.get("city")));

          return criteriaBuilder.or(criteriaBuilder.equal(join.get("street"), "street1"),
              criteriaBuilder.equal(join2.get("street"), "street1"),
              criteriaBuilder.equal(join2.get("street"), "street2"));
        } else {
          Join<Person, Address> join = root.join("address");
          Join<Person, Address> join2 = root.join("addresses");

          return criteriaBuilder.or(criteriaBuilder.equal(join.get("street"), "street1"),
              criteriaBuilder.equal(join2.get("street"), "street1"),
              criteriaBuilder.equal(join2.get("street"), "street2"));
        }
      }
    };

    Page<Person> result = repo.findAll(spec, PageRequest.of(0, 1));
    assertEquals(2, result.getTotalElements());
    print(result);
    result.forEach(this::print);

    result = repo.findAll(spec, PageRequest.of(1, 1));
    assertEquals(2, result.getTotalElements());
    print(result);
    result.forEach(this::print);
  }

  @Test
  public void duplicatesPage_WithoutFetch_WrongResults() {
    Specification<Person> spec = new Specification<Person>() {

      @Override
      public Predicate toPredicate(Root<Person> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.and(criteriaBuilder.equal(root.join("address").get("city"), "Dortmund"),
            criteriaBuilder.equal(root.join("addresses").get("city"), "Dortmund"));
      }
    };

    Page<Person> result = repo.findAll(spec, PageRequest.of(0, 50, Sort.by("address.city")));
    assertEquals(2, result.getTotalElements());
    log.info("page: {}", result);
    result.forEach(this::print);
  }

  @Test
  public void duplicatesPage_WithoutFetch_WithDistinct() {
    Specification<Person> spec = new Specification<Person>() {

      @Override
      public Predicate toPredicate(Root<Person> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        query.distinct(true);

        return criteriaBuilder.and(criteriaBuilder.equal(root.join("address").get("city"), "Dortmund"),
            criteriaBuilder.equal(root.join("addresses").get("city"), "Dortmund"));
      }
    };

    Page<Person> result = repo.findAll(spec, PageRequest.of(0, 50, Sort.by("address.city")));
    assertEquals(2, result.getTotalElements());
    log.info("page: {}", result);
    result.forEach(this::print);
  }

  @Test
  public void duplicatesPage_WithoutFetch_WithDistinct_WithoutSort_Works() {
    Specification<Person> spec = new Specification<Person>() {

      @Override
      public Predicate toPredicate(Root<Person> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        query.distinct(true);

        return criteriaBuilder.and(criteriaBuilder.equal(root.join("address").get("city"), "Dortmund"),
            criteriaBuilder.equal(root.join("addresses").get("city"), "Dortmund"));
      }
    };

    Page<Person> result = repo.findAll(spec, PageRequest.of(0, 50));
    assertEquals(2, result.getTotalElements());
    log.info("page: {}", result);
    result.forEach(this::print);
  }

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

  private void print(Page<?> page) {
    log.info("page {}", page);
    log.info("getTotalElements {}", page.getTotalElements());
    log.info("getTotalPages {}", page.getTotalPages());
  }

  private void print(Person person) {
    log.info("person {}", person);
  }

}
