package com.lamine.path_finder.Roles;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;

@Service
@AllArgsConstructor
public class RoleService {
RoleRepository roleRepository;
@Autowired
MongoTemplate mongoTemplate;
    static BiPredicate<List<ConnectionPolicy>,String>connectionHasThisRole=(lst,name)-> {

        return lst!=null&&lst.stream().filter(l->l.RoleName.equals(name)).toList().size()>0;
    };

    public List<Role> getAllRoles() {
       return roleRepository.findAll();
    }

    public List<String> getNameOfAcceptableRolesByName(String name) {
         return  Optional.of(roleRepository.findAll()
                 .stream().filter(role -> {
                     return connectionHasThisRole.test(role.getConnectionsEnabledIn(),name)||connectionHasThisRole.test(role.getConnectionsEnabledOut(),name);
                 }).map(role ->  role.getName()).toList()).orElse(new ArrayList<String>());

    }


    public List<String> getNameOfAcceptableRolesOutByName(String name) {
        return Optional.of(roleRepository.findAll()
                .stream().filter(role -> {
                    return connectionHasThisRole.test(role.getConnectionsEnabledOut(),name);
                }).map(role ->  role.getName()).toList()).orElse(List.of(""));
    }

    public List<String> getNameOfAcceptableRolesInByName(String name) {
        return Optional.of(roleRepository.findAll()
                .stream().filter(role -> {
                    return connectionHasThisRole.test(role.getConnectionsEnabledIn(),name);
                }).map(role ->  role.getName()).toList()).orElse(List.of(""));
    }

    public List<Role> getAllRolesByName(String name) {
        return  Optional.of(roleRepository.findAll()
                .stream().filter(role -> {
                    return connectionHasThisRole.test(role.getConnectionsEnabledIn(),name)||connectionHasThisRole.test(role.getConnectionsEnabledOut(),name);
                }).toList()).get();

    }

    public List<Role> getAllRolesOutByName(String name) {
        return Optional.of(roleRepository.findAll()
                .stream().filter(role -> {
                    return connectionHasThisRole.test(role.getConnectionsEnabledOut(),name);
                }).toList()).get();
    }

    public List<Role> getAllRolesInByName(String name) {
        return Optional.of(roleRepository.findAll()
                .stream().filter(role -> {
                    return connectionHasThisRole.test(role.getConnectionsEnabledIn(),name);
                }).toList()).get();
    }

    public String getRoleNameById(String id){
      return   roleRepository.findById(id).orElse(new Role("null",null,null)).getName();
    }
    public List<ConnectionPolicy>getConnectionsPolicyToById(String id){
        return   roleRepository.findById(id).orElse(new Role("null",new ArrayList<ConnectionPolicy>(),new ArrayList<ConnectionPolicy>())).getConnectionsEnabledOut();

    }

    public void AddRole(Role role) {
        if(!roleRepository.findRoleByName(role.getName()).isEmpty()){
            throw new IllegalArgumentException(String.format(" Role %s already exist",role.getName()));

        }else{
            roleRepository.save(role);

        }

    }

    public void updateRole(String name,Role role){
        if(roleRepository.findRoleByName(role.getName()).isEmpty()){
            throw new IllegalArgumentException(String.format(" Role %s does not exist",role.getName()));

        }else{
            Query query=new Query().addCriteria(
                    Criteria.where("name").is(name)
            );
            Update update=new Update().
                    set("connectionsEnabledIn",role.getConnectionsEnabledIn()).
                    set("connectionsEnabledOut",role.getConnectionsEnabledOut());
           mongoTemplate.upsert(query,update,Role.class);

        }
    }

    public void renameRole(String oldName, String newName) {
        Query query=new Query().addCriteria(
                Criteria.where("name").is(oldName)
        );
        Role role1=mongoTemplate.findOne(query,Role.class);
        if(role1==null)return;
        role1.setName(newName);
        mongoTemplate.save(role1);
        List<Role>all=roleRepository.findAll();
        for (Role r:all){
            if(r.getConnectionsEnabledIn()!=null)
            for(ConnectionPolicy cp:r.getConnectionsEnabledIn()){
                if(cp.RoleName!=null&&cp.RoleName.equals(oldName))cp.setRoleName(newName);
            }
            if(r.getConnectionsEnabledOut()!=null)
            for(ConnectionPolicy cp:r.getConnectionsEnabledOut()){
                if(cp.RoleName!=null&&cp.RoleName.equals(oldName))cp.setRoleName(newName);
            }
            mongoTemplate.save(r);
        }


    }

    public ConnectionType getConnectionTypeBetweenTwoRolesUsingName(String fromName,String ToName){
      return   roleRepository.findRoleByName(fromName).get().getConnectionsEnabledOut().stream()
                .filter(cp->cp.getRoleName().equals(ToName)).toList().get(0).getType();
    }

    public ConnectionType getConnectionTypeBetweenTwoRolesUsingId(String fromId,String ToId){
        String Toname=roleRepository.findById(ToId).get().getName();
        return   roleRepository.findById(fromId).get().getConnectionsEnabledOut().stream()
                .filter(cp->cp.getRoleName().equals(Toname)).toList().get(0).getType();
    }


}
