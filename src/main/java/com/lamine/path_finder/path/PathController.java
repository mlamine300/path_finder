package com.lamine.path_finder.path;

import com.lamine.path_finder.Entities.EntityDto;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/paths")
@AllArgsConstructor
public class PathController {
    PathService pathService;

    @GetMapping(path = "get_path")
    public Path getPathByIdName(@RequestParam String communeDepart, @RequestParam String communeDestination){

       return pathService.findThePath(communeDepart,communeDestination);

    }

    @GetMapping(path = "get_path_by_name")
    public List<String> getVisitedEntitiesIdName(@RequestParam String communeDepart, @RequestParam String communeDestination){

        return pathService.getNamesOfVisitedEntities(communeDepart,communeDestination);

    }

    @GetMapping(path = "s")
    public List<EntityDto> getVisitedEntitiesInformation(@RequestParam String communeDepart, @RequestParam String communeDestination){

        return pathService.getInformationOfVisitedEntities(communeDepart,communeDestination);

    }


}
