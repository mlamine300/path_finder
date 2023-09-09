package com.lamine.path_finder.Entities;


import com.lamine.path_finder.Roles.ConnectionType;
import com.lamine.path_finder.Roles.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;


@AllArgsConstructor
@Service
public class EntityService {

    EntityRepository entityRepository;
    RoleService roleService;
    MongoTemplate mongoTemplate;
    public List<Entity>getAllEntities(){
      return   entityRepository.findAll();
    }
    public List<Entity>getAllEntitiesWithResponsable(String responsable){
        return   entityRepository.findAll().stream().filter((en)->en.getResponsable().equals(responsable)).toList();
    }


public List<Entity>getAllEntitesTo(String roleName){
       // String roleName=entityRepository.findById(entityId).get().roleName;

    List<String>RoleAccepted=roleService.getNameOfAcceptableRolesOutByName(roleName);



    return  entityRepository.findAll().stream().filter(
            e->RoleAccepted.contains(e.getRoleName())).toList();
}

    public List<Entity>getAllEntitesFrom(String roleName){
       // String roleName=entityRepository.findById(entityId).get().roleName;
        List<String>RoleAccepted=roleService.getNameOfAcceptableRolesInByName(roleName);



        return   entityRepository.findAll().stream().filter(
                e->RoleAccepted.contains(e.getRoleName())).toList();
    }

    public List<Entity>getAllEntitesToNotTaken(String roleName){


        List<String>RoleAccepted=roleService.getNameOfAcceptableRolesOutByName(roleName);


        Predicate<Entity> acceptableAndNotTaken=e->{
Boolean b=RoleAccepted.contains(e.getRoleName())&&
        ((e.getConnectionIn()==null||e.getConnectionIn().size()==0)||
                roleService.getConnectionTypeBetweenTwoRolesUsingName(  e.getRoleName(),roleName
                      ).equals(ConnectionType.ONE_TO_MANY));

//            System.out.println(roleName+" - "+e.getName()+" - "+e.getRoleName()+" - is "+b+" because \n"
//            +"first = "+(e.getConnectionIn()==null||e.getConnectionIn().size()==0)+" \nor\n second ="+roleService.getConnectionTypeBetweenTwoRolesUsingName(
//                    e.getRoleName(),roleName).equals(ConnectionType.ONE_TO_MANY));
//            System.out.println(roleName+" - "+e.getName()+" - "+e.getRoleName());
            return   b;

        };
        return   entityRepository.findAll().stream().filter(
                acceptableAndNotTaken).toList();
    }

    public List<Entity>getAllEntitesFromNotTaken(String roleName){
       // String roleName=entityRepository.findById(entityId).get().roleName;

        List<String>RoleAccepted=roleService.getNameOfAcceptableRolesInByName(roleName);


        Predicate<Entity> acceptableAndNotTaken=e->{

            Boolean b=RoleAccepted.contains(e.getRoleName())&&
                    ((e.getConnectionOut()==null||e.getConnectionOut().size()==0)||
                            roleService.getConnectionTypeBetweenTwoRolesUsingName(
                                    e.getRoleName(),roleName).equals(ConnectionType.ONE_TO_MANY));
//System.out.println(roleName+" - "+e.getName()+" - "+e.getRoleName()+" - is "+b);
            return  b ;

        };
        return   entityRepository.findAll().stream().filter(
                acceptableAndNotTaken).toList();
    }
   public Entity getEntityByName(String id){
       return Optional.of(  entityRepository.findById(id)).get().orElse(null);
   }






    public void addEntity(Entity entity) {
        if(getEntityByName(entity.getName())!=null){

            return;

        }
        entityRepository.insert(entity);
    }

    public boolean deleteEntity(String id){
        entityRepository.deleteById(id);
        return true;//update it
    }
    public void updateEntity(String name,Entity entity){
        if(!entityRepository.findEntityByName(name).isPresent()){
            throw new IllegalArgumentException(String.format("this  %s does not exist",name));

        }else{


            entity.setName(name);
            mongoTemplate.save(entity);



        }
    }

    public void addConnection(String from_name, String to_name) {
    Optional<Entity> from=entityRepository.findById(from_name);
    if(from.isPresent()){
       Entity Efrom=from.get();
        List<String>lst=Efrom.getConnectionOut();
        if(lst==null){
            lst=List.of(to_name);
            Efrom.setConnectionOut(lst);
        }
        else lst.add(to_name);

        mongoTemplate.save(Efrom);
    }

        Optional<Entity> to=entityRepository.findById(to_name);
        if(to.isPresent()){
            Entity ETo=to.get();
            List<String>lst=ETo.getConnectionIn();
            if(lst==null){
                lst=List.of(from_name);
                ETo.setConnectionIn(lst);
            }
            else lst.add(from_name);

            mongoTemplate.save(ETo);
        }


    }

    public void addToListConnection(String fromName, List<String> tolist) {

        Optional<Entity> from=entityRepository.findById(fromName);
        if(from.isPresent()){
            Entity Efrom=from.get();
            List<String>lst=Efrom.getConnectionOut();
            if(lst==null){
                lst=List.copyOf(tolist);
                Efrom.setConnectionOut(lst);
            }
            else lst.addAll(tolist);

            for(String to:tolist){
                if(!entityRepository.findById(to).isPresent())continue;
                Entity toEntity=entityRepository.findById(to).get();
                List<String>Tolst=toEntity.getConnectionIn();
                if(Tolst==null){
                    Tolst=List.of(fromName);
                    toEntity.setConnectionIn(Tolst);
                }else if(!Tolst.contains(fromName)) toEntity.getConnectionIn().add(fromName);
                mongoTemplate.save(toEntity);
            }
            mongoTemplate.save(Efrom);
        }
    }

    public void addFromListConnection(String toName, List<String> fromlist) {

        Optional<Entity> to=entityRepository.findById(toName);
        if(to.isPresent()){
            Entity Eto=to.get();
            List<String>lst=Eto.getConnectionIn();
            if(lst==null){
                lst=List.copyOf(fromlist);
                Eto.setConnectionIn(lst);
            }
            else lst.addAll(fromlist);

            for(String from:fromlist){
                if(!entityRepository.findById(from).isPresent())continue;
                Entity fromEntity=entityRepository.findById(from).get();
                List<String>Fromlst=fromEntity.getConnectionOut();
                if(Fromlst==null){
                    Fromlst=List.of(toName);
                    fromEntity.setConnectionOut(Fromlst);
                }else if(!Fromlst.contains(toName))fromEntity.getConnectionOut().add(toName);
                mongoTemplate.save(fromEntity);
            }
            mongoTemplate.save(Eto);
        }
    }
}
