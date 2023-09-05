package com.lamine.path_finder.Entities;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Data
@Document
public class Entity {



    @Id
    String name;

    String roleName;
    List<String>ConnectionIn;
    List<String>ConnectionOut;
    String addresse;
    Localisation localisation;
    String Responsable;

    public Entity(String name, String roleName, List<String> connectionIn, List<String> connectionOut, String addresse, Localisation localisation, String responsable) {

        this.name = name;
        this.roleName = roleName;
        ConnectionIn = connectionIn;
        ConnectionOut = connectionOut;
        this.addresse = addresse;
        this.localisation = localisation;
        Responsable = responsable;
    }

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
}
