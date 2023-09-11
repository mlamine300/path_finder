package com.lamine.path_finder.Entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Data
@Document
@AllArgsConstructor
public class Entity {



    @Id
    String name;

    String roleName;
    List<String>ConnectionIn;
    List<String>ConnectionOut;
    String addresse;
    Localisation localisation;
    String Responsable;



    public Entity(String name, String roleName) {
        this.name = name;
        this.roleName = roleName;
    }

    public Entity() {
    }

    public Entity(String name, String roleName, String addresse) {
        this.name = name;
        this.roleName = roleName;
        this.addresse = addresse;
    }

    public Entity(Entity ent) {


        this.name = ent.name;
        this.roleName = ent.roleName;
        ConnectionIn = List.copyOf(ent.getConnectionIn());
        ConnectionOut = List.copyOf(ent.getConnectionOut());
        this.addresse = ent.addresse;
        this.localisation = ent.localisation;
        Responsable = ent.getResponsable();
    }
}
