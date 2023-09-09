package com.lamine.path_finder.Entities;

import lombok.*;

import java.util.function.Function;

@Builder
@AllArgsConstructor
@Data
public class EntityDto {
    String NameId;
    String Address;

    String Responsable;
    Localisation Localisation;

    public static EntityDto fromEntity(Entity entity){
return EntityDto.builder().NameId(entity.getName())
        .Address(entity.getAddresse()).
        Responsable(entity.getResponsable())
        .Localisation(entity.getLocalisation()).build();
    }

}
