package com.lamine.path_finder.Entities;


import com.lamine.path_finder.Roles.ConnectionType;
import com.lamine.path_finder.Roles.RoleService;
import com.mongodb.annotations.NotThreadSafe;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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






    public ResponseEntity<Object> addEntity(Entity entity) {
        if(entity==null||entity.getName()==null||
                entity.getName()==null||
                entity.getAddresse()==null){

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("invalide information provided")
                    ;

        }
        if(getEntityByName(entity.getName())!=null){
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(String.format("this %s (%s)already existe",entity.roleName,entity.name));


        }
        entityRepository.insert(entity);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(String.format("(%s) has been added into %s liste ",entity.name,entity.roleName));
    }

    public ResponseEntity<Object> deleteEntity(String name){
        if (entityRepository.findById(name)==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).
                    body("no such Entity with the name :"+name+" in database, nothing was deleted");

        }
        entityRepository.deleteById(name);
        return ResponseEntity.status(HttpStatus.OK).
                body("Entity "+name +" has been deleted !");
    }
    public ResponseEntity updateEntity(String name,Entity entity){
        if(!entityRepository.findEntityByName(name).isPresent()){
           // throw new IllegalArgumentException(String.format("this  %s does not exist",name));
  return ResponseEntity.status(HttpStatus.NOT_FOUND).
                    body("no such Entity with the name :"+name+" in database, nothing was deleted");
        }
        else if(entity==null||entity.getName()==null||
                entity.getRoleName()==null||
                entity.getAddresse()==null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                    body("please make sure that Entity name && Entity Role && Entity " +
                            "address are all mentioned in the request");
        else{


            entity.setName(name);
            mongoTemplate.save(entity);
            return ResponseEntity.status(HttpStatus.OK).
                    body(String.format("%s has been edited successfully!",name));



        }
    }

    public ResponseEntity<Object> addConnection(String from_name, String to_name) {
    Optional<Entity> from=entityRepository.findById(from_name);
        Optional<Entity> to=entityRepository.findById(to_name);
        if(from.isPresent()&& to.isPresent()){
            Entity Efrom=from.get();
            List<String>lst1=Efrom.getConnectionOut();
            if(lst1==null){
                lst1=List.of(to_name);
                Efrom.setConnectionOut(lst1);
            }
            else lst1.add(to_name);

            mongoTemplate.save(Efrom);

            Entity ETo=to.get();
            List<String>lst2=ETo.getConnectionIn();
            if(lst2==null){
                lst2=List.of(from_name);
                ETo.setConnectionIn(lst2);
            }
            else lst2.add(from_name);

            mongoTemplate.save(ETo);

            return ResponseEntity.status(HttpStatus.OK).
                    body(String.format("connection between %s and %s has been Added successfully",from_name,to_name));
        }

        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).
                    body(String.format("%s or %s not found please make sure you entred the right names",from_name,to_name));

        }










    }

    public ResponseEntity<Object> addToListConnection(String fromName, List<String> tolist) {

        Optional<Entity> from=entityRepository.findById(fromName);
        ArrayList<String> notFound=new ArrayList<>();
        if (!from.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).
                    body(String.format("%s not found , make sure you entered the right name",fromName));
        }

            Entity Efrom=from.get();
            List<String>lst=Efrom.getConnectionOut();
            if(lst==null){
                lst=List.copyOf(tolist);
                Efrom.setConnectionOut(lst);
            }
            else lst.addAll(tolist);

            for(String to:tolist){
                if(!entityRepository.findById(to).isPresent()){
                    notFound.add(to);
                    continue;
                }
                Entity toEntity=entityRepository.findById(to).get();
                List<String>Tolst=toEntity.getConnectionIn();
                if(Tolst==null){
                    Tolst=List.of(fromName);
                    toEntity.setConnectionIn(Tolst);
                }else if(!Tolst.contains(fromName)) toEntity.getConnectionIn().add(fromName);
                mongoTemplate.save(toEntity);
            }
            mongoTemplate.save(Efrom);
            String body=String.format("%d connection has been added to %s successfully",tolist.size(),fromName);
        if (notFound.size()>0){
            body=String.format("%d connection has been added to %s successfully\n" +
                    "%d connection cannot be added because we counld not found them in our database \n connection failed:\n",tolist.size()-notFound
                    .size(),fromName,notFound.size());
            for(String n:notFound)body+=n+"\n";
        }
        return ResponseEntity.status(HttpStatus.OK).body(body);

    }

    public ResponseEntity<Object> addFromListConnection(String toName, List<String> fromlist) {

        Optional<Entity> to=entityRepository.findById(toName);
        ArrayList<String>notFound=new ArrayList<>();
        if(!to.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).
                    body(String.format("%s not found , make sure you entered the right name",toName));

        }
            Entity Eto=to.get();
            List<String>lst=Eto.getConnectionIn();
            if(lst==null){
                lst=List.copyOf(fromlist);
                Eto.setConnectionIn(lst);
            }

            else lst.addAll(fromlist);

            for(String from:fromlist){
                if(!entityRepository.findById(from).isPresent()) {
                    notFound.add(from);
                    continue;
                }
                Entity fromEntity=entityRepository.findById(from).get();
                List<String>Fromlst=fromEntity.getConnectionOut();
                if(Fromlst==null){
                    Fromlst=List.of(toName);
                    fromEntity.setConnectionOut(Fromlst);
                }else if(!Fromlst.contains(toName))fromEntity.getConnectionOut().add(toName);
                mongoTemplate.save(fromEntity);
            }
            mongoTemplate.save(Eto);
            String body=String.format("%d connection has been added to %s successfully",fromlist.size(),toName);
            if (notFound.size()>0){
                body=String.format("%d connection has been added to %s successfully\n" +
                        "%d connection cannot be added because we counld not found them in our database \n connection failed:\n",fromlist.size()-notFound
                        .size(),toName,notFound.size());
                for(String n:notFound)body+=n+"\n";
            }
            return ResponseEntity.status(HttpStatus.OK).body(body);
        }


}
