package com.lamine.path_finder.Roles;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/role")
@AllArgsConstructor
public class RoleController {
RoleService roleService;

    @GetMapping(path = "get_all_roles_by")
    public List<String>getAllRoles(@RequestParam String role){
        return roleService.getNameOfAcceptableRolesByName(role);
    }
    @GetMapping(path = "get_all_roles_out")
    public List<String>getAllRolesOut(@RequestParam String role){
        return roleService.getNameOfAcceptableRolesByName(role);
    }

    @GetMapping(path = "get_all_roles_in")
    public List<String>getAllRolesIn(@RequestParam String role){
        return roleService.getNameOfAcceptableRolesInByName(role);
    }
    @GetMapping(path = "get_all_roles")
    public List<Role>getAllRoles(){
        return roleService.getAllRoles();
    }

    @PostMapping(path = "add_role")
    public void AddRole(@RequestBody Role role){
        roleService.AddRole(role);
    }

    @PostMapping(path = "update_role")
    public void UpdateRole(@RequestBody Role role,@RequestParam String name){
        roleService.updateRole(name,role);
    }

    @PostMapping(path = "rename_role")
    public void UpdateRole(@RequestParam String old_name,@RequestParam String new_name ){
roleService.renameRole(old_name,new_name);
    }

}
