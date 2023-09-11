package com.lamine.path_finder;

import com.lamine.path_finder.Entities.Entity;
import com.lamine.path_finder.Entities.EntityRepository;
import com.lamine.path_finder.Entities.EntityService;
import com.lamine.path_finder.Entities.Localisation;
import com.lamine.path_finder.Roles.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;


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
	static CommandLineRunner runner2(EntityRepository entityRepository, RoleRepository roleRepository, MongoTemplate mon, EntityService entityService) {
		return args -> {

			roleRepository.saveAll(getRoles());
			String commune=roleRepository.findAll().stream().filter(r->r.getName().equals("commune")).distinct().toList().get(0).getName();
			List<Entity>lst=Communes.getAllCommunes.apply(commune);
			entityRepository.saveAll(lst);
			String wilayaRole=roleRepository.findAll().stream().filter(r->r.getName().equals("station")).distinct().toList().get(0).getName();
			List<Entity>wilayaLst=Communes.getAllWilaya.apply(wilayaRole);
			entityRepository.saveAll(wilayaLst);

			System.out.println("dbs are set");
			connectCommuneToStation(lst,wilayaLst,entityService);
			String centre_de_treRole=roleRepository.findAll().stream().filter(r->r.getName().equals("centre_de_tre")).distinct().toList().get(0).getName();
			List<Entity>centreDeTree=Communes.GetAllRegion.apply(centre_de_treRole);
			entityRepository.saveAll(centreDeTree);

			connectStationToCentreDetree(centreDeTree,entityService);

			System.out.println("connection are set");
			new Entity("","",null,null,"",new Localisation(0f,0f),"");


		};
	}

	private static void connectStationToCentreDetree( List<Entity> centreDeTree, EntityService entityService) {
	centreDeTree.forEach(ct->{
		ct.getConnectionOut().forEach(s->{

					entityService.addConnection(s,ct.getName());
			entityService.addConnection(ct.getName(),s);
					System.out.println(String.format("connection between %s and %s",s,ct.getName()));


		});
	});

	}


	private static void connectCommuneToStation(List<Entity> commune, List<Entity> wilayaLst,EntityService entityService) {
		wilayaLst.stream().map(e->e.getName()).toList().forEach(s->{
			commune.forEach(c->{
				if(c.getName().contains("("+s.replace("La wilaya de ","")+")")){
					entityService.addConnection(c.getName(),s);
					entityService.addConnection(s,c.getName());
					System.out.println(String.format("connection between %s and %s",s,c.getName()));
				}
			});
		});



	}

	private static List<Role> getRoles() {
		ConnectionPolicy cp1 = new ConnectionPolicy("hub", ONE_TO_MANY);
		ConnectionPolicy cp2 = new ConnectionPolicy("centre_de_tre", ONE_TO_MANY);
		Role r = new Role("hub", List.of(cp1, cp2), List.of(cp1, cp2));


		ConnectionPolicy cp3 = new ConnectionPolicy("hub", ONE_TO_ONE);
		ConnectionPolicy cp4 = new ConnectionPolicy("centre_de_tre", ONE_TO_MANY);
		ConnectionPolicy cp5 = new ConnectionPolicy("station", ONE_TO_MANY);
		Role r2 = new Role("centre_de_tre", List.of(cp3,cp4, cp5), List.of(cp3,cp4, cp5));

		ConnectionPolicy fromCommuneTostation = new ConnectionPolicy("commune", ONE_TO_MANY);
		ConnectionPolicy fromStationToCentreDetre = new ConnectionPolicy("centre_de_tre", ONE_TO_ONE);
		Role r3 = new Role("station", List.of(fromCommuneTostation,fromCommuneTostation), List.of(fromStationToCentreDetre,fromCommuneTostation));

		ConnectionPolicy cp8 = new ConnectionPolicy("station", ONE_TO_ONE);
		ConnectionPolicy cp9 = new ConnectionPolicy("commune", ONE_TO_ONE);
		Role r4 = new Role("commune", List.of(cp8,cp9), List.of(cp8,cp9));
		return List.of(r, r2, r3, r4);
	}
	private static void AddStations(){

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


