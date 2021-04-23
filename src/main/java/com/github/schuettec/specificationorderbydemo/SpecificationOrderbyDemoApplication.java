package com.github.schuettec.specificationorderbydemo;

import static java.util.Arrays.asList;


import com.github.schuettec.specificationorderbydemo.domain.Address;
import com.github.schuettec.specificationorderbydemo.domain.AddressRepository;
import com.github.schuettec.specificationorderbydemo.domain.Person;
import com.github.schuettec.specificationorderbydemo.domain.PersonRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
	PersonRepository personRepository;

	@Autowired
	AddressRepository addressRepository;

	@PostConstruct
	public void setUp() {

		/*Address a1 = addressRepository.save(Address.builder().id(0L).city("Dortmund").street("street1").build());
		Address a2 = addressRepository.save(Address.builder().id(1L).city("Dortmund").street("street2").build());
		Address a3 = addressRepository.save(Address.builder().id(2L).city("Berlin").street("street3").build());

		List<Address> addresses = new ArrayList<>(Arrays.asList(a1, a2, a3));

		for (int i = 0; i < 10; i++) {
			addresses.add(addressRepository.save(Address.builder().id(2L)
					.city("City " + i).street("Street " + i).build()));
		}

		personRepository.save(Person.builder().id(0L).name("Dieter0").address(a1).addresses(asList(a1, a2, a3)).build());
		personRepository.save(Person.builder().id(1L).name("Dieter1").address(a2).addresses(asList(a1, a2, a3)).build());
		personRepository.save(Person.builder().id(2L).name("Dieter2").address(a3).build());

		for (int i = 0; i < 50; i++) {
			int random = (int) Math.random() * 10;

			Person person = Person.builder().id(2L).name("Dieter2").build();
			for (int x = 0; x < random; x++) {
				person.getAddresses().add(addresses.get((int) (Math.random() * addresses.size())));
			}

			personRepository.save(person);
		}*/
	}

}
