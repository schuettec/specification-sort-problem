package com.github.schuettec.specificationorderbydemo;

import static java.util.Arrays.asList;


import com.github.schuettec.specificationorderbydemo.domain.Address;
import com.github.schuettec.specificationorderbydemo.domain.AddressRepository;
import com.github.schuettec.specificationorderbydemo.domain.Person;
import com.github.schuettec.specificationorderbydemo.domain.PersonRepository;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;

@EnableJpaRepositories
@SpringBootApplication
@Component
public class SpecificationOrderbyDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpecificationOrderbyDemoApplication.class, args);
	}

	@Autowired
	PersonRepository repo;

	@Autowired
	AddressRepository addresses;

	@PostConstruct
	public void setUp() {
		Address a1 = addresses.save(Address.builder().id(0L).city("Dortmund").street("street1").build());
		Address a2 = addresses.save(Address.builder().id(1L).city("Dortmund").street("street2").build());
		Address a3 = addresses.save(Address.builder().id(2L).city("Berlin").street("street3").build());

		repo.save(Person.builder().id(0L).name("Dieter0").address(a1).addresses(asList(a1, a2, a3)).build());
		repo.save(Person.builder().id(1L).name("Dieter1").address(a2).addresses(asList(a1, a2, a3)).build());
		repo.save(Person.builder().id(2L).name("Dieter2").address(a3).build());
	}

}
