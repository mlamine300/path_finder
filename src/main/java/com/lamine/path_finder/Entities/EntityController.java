package com.lamine.path_finder.Entities;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/entity")
@AllArgsConstructor
public class EntityController {
    EntityService entityService;
    @PostMapping(path = "add_entity")//working
    public ResponseEntity<Object> AddEntity(@RequestBody Entity entity){
      return   entityService.addEntity(entity);
    }
    @PostMapping(path = "delete_entity")//working
    public ResponseEntity<Object> DeleteEntity(@RequestParam String name){
       return entityService.deleteEntity(name);
    }

    @PostMapping(path = "update_entity")//working
    public ResponseEntity<Object> UpdateEntity(@RequestParam String name,@RequestBody Entity entity){
     return    entityService.updateEntity(name,entity);
    }

    @PostMapping(path = "add_coonection")//working
    public ResponseEntity<Object> AddConnection(@RequestParam String fromName,@RequestParam String toName){
       return entityService.addConnection(fromName,toName);
    }
    @PostMapping(path = "link_entityTo")//0%
    public ResponseEntity<Object> LinkEntityTo(@RequestParam String fromName,@RequestBody List<String> tolist){
       return entityService.addToListConnection(fromName,tolist);
    }

    @PostMapping(path = "link_entityFrom")//0%
    public ResponseEntity<Object> LinkEntityFrom(@RequestParam String toName,@RequestBody List<String> tolist){
       return entityService.addFromListConnection(toName,tolist);
    }
    @GetMapping(path = "get_entity")//working

    public Entity getentity(@RequestParam String name){
        return entityService.getEntityByName(name);
    }

    @GetMapping(path = "get_all_entity")//working

    public List<Entity>getAllEntity(){
        return entityService.getAllEntities();
    }
    @GetMapping(path = "entity_connectable_to")//50%

    public List<Entity>getConnectableEntityTo(@RequestParam String roleName){
        return entityService.getAllEntitesTo(roleName);
    }

    @GetMapping(path = "entity_connectable_to_not_taken")//50%

    public List<Entity>getConnectableEntityToNotTaken(@RequestParam String roleName){
        return entityService.getAllEntitesToNotTaken(roleName);

    }

    @GetMapping(path = "entity_connectable_from")//50%

    public List<Entity>getConnectableEntityFrom(@RequestParam String roleName){
        return entityService.getAllEntitesFrom(roleName);
    }

    @GetMapping(path = "entity_connectable_from_not_taken")//50%

    public List<Entity>getConnectableEntityFromNotTaken(@RequestParam String roleName){
        return entityService.getAllEntitesFromNotTaken(roleName);

    }

    @GetMapping(path = "entity_by_responsable")//0%

    public List<Entity>getEntitiesByResponsable(@RequestParam String responsable){
        return entityService.getAllEntitiesWithResponsable(responsable);

    }


    //getAllEntitiesWithResponsable
}
