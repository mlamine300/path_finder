package com.lamine.path_finder.path;

import com.lamine.path_finder.Entities.Entity;
import com.lamine.path_finder.Entities.EntityDto;
import com.lamine.path_finder.Entities.EntityService;
import com.lamine.path_finder.Roles.ConnectionType;
import com.lamine.path_finder.Roles.Role;
import com.lamine.path_finder.Roles.RoleService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.BiFunction;


@Service
@Data
public class PathService {
        @Autowired
    private EntityService entityService;
        @Autowired
    RoleService roleService;
    private static final ChronoUnit TIME_TO_REINITIALIZE_UNIT = ChronoUnit.MINUTES;//should be 1 day
    private static final int TIME_TO_REINITIALIZE=10;//should be 1 day

    List<Entity>AllEntity=null;
    Hashtable<String,List<Entity>>listHashtable=null;
    LocalDateTime lastInit=LocalDateTime.now();
    List<String>RoleHearchie;


    public List<String> getNamesOfVisitedEntities
            (String communeDepart, String communeDestination) {
        init();
        Entity depart=findEntityFromListByName.apply(communeDepart,AllEntity);
        Entity destination=findEntityFromListByName.apply(communeDestination,AllEntity);
        List<Entity>visitedEntites=getVisitedEntities(depart,destination);
        if (visitedEntites==null)return List.of();
        return visitedEntites.stream().map(e->e.getName()).toList();


    }

    public Path findThePath(String communeDepart, String communeDestination) {

        init();
        Entity departEntity= findEntityFromListByName.apply(communeDepart,AllEntity);
        Entity destinationEntity= findEntityFromListByName.apply(communeDestination,AllEntity);
        
        return new Path(getVisitedEntities(departEntity,destinationEntity));


    }

    public List<EntityDto> getInformationOfVisitedEntities(String communeDepart, String communeDestination) {
        init();
        Entity depart=findEntityFromListByName.apply(communeDepart,AllEntity);
        Entity destination=findEntityFromListByName.apply(communeDestination,AllEntity);
        List<Entity>visitedEntites=getVisitedEntities(depart,destination);
        if (visitedEntites==null)return List.of();
        return visitedEntites.stream().map(e->EntityDto.fromEntity(e)).toList();
    }



    /***
     *
     *
     *
     * */

    private void init() {
        if(AllEntity==null||AllEntity.size()==0
                ||RoleHearchie==null||RoleHearchie.size()==0
                ||lastInit.until(LocalDateTime.now(),TIME_TO_REINITIALIZE_UNIT)>TIME_TO_REINITIALIZE){

            AllEntity=entityService.getAllEntities();
            listHashtable=new Hashtable<>();
            List<String>roles=AllEntity.stream().map(e->e.getRoleName()).distinct().toList();
            roles.stream().forEach(r->{
                listHashtable.put(r,
                        AllEntity.stream().filter(
                                ae->ae.getRoleName().equals(r)).toList());
            });

            setRoleHeachie(roleService.getAllRoles());
            lastInit=LocalDateTime.now();
            System.out.println("All entities/Roles has been Initialized at :"+lastInit);
        }

    }

    public static BiFunction<String,List<Entity>,Entity> findEntityFromListByName =(s, lst)->{
        for (Entity e:lst){
            if(e.getName().equals(s))return e;
            
        }

        //throw new NoSuchElementException();
        System.out.println(s+" is null");
        return null;
    };


    private List<Entity> getVisitedEntities(Entity depart, Entity destination) {

       ArrayList<Entity>departSemiPath= findPathToTheTopFromDepart(depart);//from depart to the top of hiarchie
       ArrayList<Entity>destinationSemiPath= findPathToTheTopFromDestination(destination);//from destination to the top of hiarchie

       if (isPathsConnectable(departSemiPath,destinationSemiPath)){
           //check if a graph is a tree (there is no cycle/ all entites are connected)

           if(isSemiPathesHaveTheSameTop(departSemiPath,destinationSemiPath)){//for entities with the same top 'to distinct'
                destinationSemiPath.remove(0);
}
           Collections.reverse(destinationSemiPath);
         departSemiPath.addAll(destinationSemiPath);

          return departSemiPath;
       }
       else{//if(a graph is not a tree it should send an error the our delivery network is wrong
           manifestDefaillanceInSystem(departSemiPath,destinationSemiPath);
           return null;

       }



    }

    private void manifestDefaillanceInSystem(ArrayList<Entity> departSemiPath, ArrayList<Entity> destinationSemiPath) {
   System.out.println("there is a probleme in our network!!!!!\n"+departSemiPath.stream()
           .map(e->e.getName()+" -> ")+
           destinationSemiPath.stream().map(e->e.getName()+" -> "));//to edite later

    }



    private boolean isSemiPathesHaveTheSameTop(ArrayList<Entity> departSemiPath, ArrayList<Entity> destinationSemiPath) {
   return destinationSemiPath.get(destinationSemiPath.size()-1).getName().equals(
           departSemiPath.get(departSemiPath.size()-1).getName());
    }

    private boolean isPathsConnectable(ArrayList<Entity> departSemiPath, ArrayList<Entity> destinationSemiPath) {
  return departSemiPath.get(departSemiPath.size()-1).
          getConnectionOut().contains(
                  destinationSemiPath.get(destinationSemiPath.size()-1).getName())&&

          destinationSemiPath.get(destinationSemiPath.size()-1).
                  getConnectionIn().contains(
                          departSemiPath.get(departSemiPath.size()-1).getName());

    }

    private ArrayList<Entity> findPathToTheTopFromDestination(Entity destination) {
       Entity nextDepart=new Entity(destination);
        ArrayList<Entity> DestinationChemin=new ArrayList<>();
        DestinationChemin.add(nextDepart);
        while (nextDepart.getConnectionIn()!=null&&
                getPosibleConnectionsIn(nextDepart)!=null
                &&getPosibleConnectionsIn(nextDepart).size()==1){

            nextDepart= getPosibleConnectionsIn(nextDepart).get(0);
            DestinationChemin.add(nextDepart);
            System.out.println(nextDepart.getName()+" Added");
        }
   return DestinationChemin;
    }

    private ArrayList<Entity> findPathToTheTopFromDepart(Entity depart) {
        Entity nextDepart=new Entity(depart);
        ArrayList<Entity> DepartChemin=new ArrayList<>();
        DepartChemin.add(nextDepart);
        while (nextDepart.getConnectionOut()!=null&&
                getPosibleConnectionsOut(nextDepart)!=null
                &&getPosibleConnectionsOut(nextDepart).size()==1){

            nextDepart= getPosibleConnectionsOut(nextDepart).get(0);
            DepartChemin.add(nextDepart);

        }
    return DepartChemin;}

    private  List<Entity>getPosibleConnectionsIn(Entity entity) {
        List<String>conectionIn=entity.getConnectionIn();
        String r=entity.getRoleName();
        String rPlusOne=r;
        for(String rs:RoleHearchie){
            if(rs.equals(r))break;
            rPlusOne=rs;
        }
        //System.out.println(rPlusOne);
        List<Entity>rts=listHashtable.get(rPlusOne);
        if(rts!=null)
            rts=rts.stream().filter(c->conectionIn.contains(c.getName())).toList();
        if(rts==null)List.of();
        return rts;
    }
    private  List<Entity>getPosibleConnectionsOut(Entity entity) {
        List<String>conectionOut=entity.getConnectionOut();
        String r=entity.getRoleName();
        String rPlusOne=r;
        for(String rs:RoleHearchie){
            if(rs.equals(r))break;
            rPlusOne=rs;
        }
        //System.out.println(rPlusOne);
        List<Entity>rts=listHashtable.get(rPlusOne);
        if(rts!=null)
            rts=rts.stream().filter(c->conectionOut.contains(c.getName())).toList();
        if(rts==null)List.of();
        return rts;
    }


    private void setRoleHeachie(List<Role>roles){
        roles.sort(new Comparator<Role>() {
            @Override
            public int compare(Role o1, Role o2) {
                return IsthisRoleGreaterThenThis(o1,o2);
            }
        });

        RoleHearchie=List.copyOf(roles.stream().map(role -> role.getName()).toList());

    }

    private int IsthisRoleGreaterThenThis(Role a,Role b){
        ConnectionType aTob,bToa;
        aTob= roleService.getConnectionTypeBetweenTwoRolesUsingName(a.getName(),b.getName());
        bToa=roleService.getConnectionTypeBetweenTwoRolesUsingName(b.getName(),a.getName());
         if(aTob.equals(ConnectionType.ONE_TO_ONE)&&
                bToa.equals(ConnectionType.ONE_TO_MANY))
            return 0;

         return 1;


    }


   
}
