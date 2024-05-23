package com.hasanalmunawr.booknetwork;

import com.hasanalmunawr.booknetwork.entity.RoleEntity;
import com.hasanalmunawr.booknetwork.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableAsync
@SpringBootApplication
public class BookNetworkApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookNetworkApiApplication.class, args);
	}


	@Bean
	public CommandLineRunner runner(RoleRepository roleRepository) {
		return args -> {
			if (roleRepository.findByName("USER").isEmpty()) {
				roleRepository.save(
						RoleEntity.builder()
								.name("USER").build()
				);
			}
		};
	}

}
