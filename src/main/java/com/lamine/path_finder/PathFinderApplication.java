package com.lamine.path_finder;

import com.lamine.path_finder.Entities.Entity;
import com.lamine.path_finder.Entities.EntityRepository;
import com.lamine.path_finder.Roles.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.lamine.path_finder.Roles.ConnectionType.*;

@SpringBootApplication

public class PathFinderApplication {


	private static List<Role> roles;

	public static void main(String[] args) {

		SpringApplication.run(PathFinderApplication.class, args);
	}

//	@Bean
//	static CommandLineRunner runner(RoleRepository roleRepository, MongoTemplate mon) {
//		return args -> {
//			List<Role> roles = getRoles();
//			addRoles(roleRepository,roles);
//
//		};


//	}






	@Bean
	static CommandLineRunner runner2(EntityRepository entityRepository,RoleRepository roleRepository, MongoTemplate mon) {
		return args -> {
//			roles.stream().map(r->r.getName()).forEach(System.out::println);
//			addHubs(entityRepository, roles.get(0).getId());
//			addCentreDeTree(entityRepository, roles.get(1).getId());
//			addCentreStation(entityRepository, roles.get(2).getId());
//			addWilaya(entityRepository, roles.get(3).getId());
			roleRepository.saveAll(getRoles());
			String commune=roleRepository.findAll().stream().filter(r->r.getName().equals("commune")).distinct().toList().get(0).getName();
			List<Entity>lst=Communes.getAllCommunes.apply(commune);
			entityRepository.saveAll(lst);




		};
	}

	private static List<Role> getRoles() {
		ConnectionPolicy cp1 = new ConnectionPolicy("hub", ONE_TO_MANY);
		ConnectionPolicy cp2 = new ConnectionPolicy("centre_de_tre", ONE_TO_MANY);

		Role r = new Role("hub", List.of(cp1, cp2), List.of(cp1, cp2));
		ConnectionPolicy cp3 = new ConnectionPolicy("hub", ONE_TO_ONE);
		ConnectionPolicy cp4 = new ConnectionPolicy("centre_de_tre", ONE_TO_MANY);
		ConnectionPolicy cp5 = new ConnectionPolicy("station", ONE_TO_MANY);
		Role r2 = new Role("centre_de_tre", List.of(cp4, cp5), List.of(cp3, cp4));

		ConnectionPolicy fromWilayaTostation = new ConnectionPolicy("wilaya", ONE_TO_MANY);
		ConnectionPolicy fromStationToCentreDetre = new ConnectionPolicy("centre_de_tre", ONE_TO_ONE);
		Role r3 = new Role("station", List.of(fromWilayaTostation), List.of(fromStationToCentreDetre));

		ConnectionPolicy cp8 = new ConnectionPolicy("station", ONE_TO_ONE);
		ConnectionPolicy cp9 = new ConnectionPolicy("commune", ONE_TO_ONE);
		Role r4 = new Role("commune", List.of(cp8,cp9), List.of(cp8,cp9));
		return List.of(r, r2, r3, r4);
	}

	private static void addRoles(RoleRepository roleRepository, List<Role> lst) {

		List<String> names = lst.stream().map(r -> r.getName()).toList();
		//List<Role>lst=oldMethod(names,mon);
		//roleRepository.saveAll(lst);
	}

	private static void addHubs(EntityRepository entityRepository, String h) {

		entityRepository.save(new Entity("centre", h, null, null, "oued smar", null, "aymen"));
		entityRepository.save(new Entity("est", h, null, null, "setif", null, "imad"));
		entityRepository.save(new Entity("ouest", h, null, null, "oran", null, "taki"));

	}

	private static void addCentreDeTree(EntityRepository entityRepository, String ct) {
		for (int i = 1; i <= 5; i++) {
			entityRepository.insert(new Entity("centre bis :" + i, ct, null, null, "alger place " + i, null, "responsabel centre " + i));
			entityRepository.insert(new Entity("est bis :" + i, ct, null, null, "est place " + i, null, "responsabel est " + i));
			entityRepository.insert(new Entity("ouest bis :" + i, ct, null, null, "ouest place " + i, null, "responsabel ouest " + i));

		}


	}

	private static void addCentreStation(EntityRepository entityRepository, String st) {
		for (int i = 1; i <= 30; i++) {
			entityRepository.insert(new Entity("station :" + i, st, null, null, "station place " + i, null, "responsabel station " + i));

		}

	}





}

