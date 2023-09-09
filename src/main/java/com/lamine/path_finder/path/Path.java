package com.lamine.path_finder.path;

import com.lamine.path_finder.Entities.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Data
@AllArgsConstructor
@Document
public class Path {
    @org.springframework.data.annotation.Id
    String Id;
    String departName, departAddress;
    Entity depart;
    String destinationName, destinationAddress;
    Entity destination;

    List<String> visitedEntitiesByName;
    List<String> visitedEntitiesByAddress;
    List<Entity> visitedEntities;

    public Path(String departName, String destinationName, List<String> visitedEntitiesByName) {
        this.departName = departName;
        this.destinationName = destinationName;
        this.visitedEntitiesByName = visitedEntitiesByName;
    }

    public Path(String departName, String departAddress, String destinationName, String destinationAddress, List<String> visitedEntitiesByName, List<String> visitedEntitiesByAddress) {
        this.departName = departName;
        this.departAddress = departAddress;
        this.destinationName = destinationName;
        this.destinationAddress = destinationAddress;
        this.visitedEntitiesByName = visitedEntitiesByName;
        this.visitedEntitiesByAddress = visitedEntitiesByAddress;
    }

    public Path(Entity depart, Entity destination, List<Entity> visitedEntities) {
        this.depart = depart;
        this.destination = destination;
        this.visitedEntities = visitedEntities;
        this.departAddress =depart.getAddresse();
        this.departName =depart.getName();
        this.destinationName=destination.getName();
        this.destinationAddress=destination.getAddresse();
        this.visitedEntitiesByAddress = visitedEntities.stream().map(c->c.getAddresse()).toList();
        this.visitedEntitiesByName = visitedEntities.stream().map(c->c.getName()).toList();
    }

    public Path(List<Entity> visitedEntities) {
        this.depart = visitedEntities.get(0);
        this.destination = visitedEntities.get(visitedEntities.size()-1);;
        this.visitedEntities = visitedEntities;
        this.departAddress =depart.getAddresse();
        this.departName =depart.getName();
        this.destinationName=destination.getName();
        this.destinationAddress=destination.getAddresse();
        this.visitedEntitiesByAddress = visitedEntities.stream().map(c->c.getAddresse()).toList();
        this.visitedEntitiesByName = visitedEntities.stream().map(c->c.getName()).toList();
    }
}
