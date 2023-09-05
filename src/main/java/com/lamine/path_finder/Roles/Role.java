package com.lamine.path_finder.Roles;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document

public class Role {




    @Id
    private  String name;

   private List<ConnectionPolicy> connectionsEnabledIn;
   private List<ConnectionPolicy>connectionsEnabledOut;

    public Role(String name, List<ConnectionPolicy> connectionsEnabledIn, List<ConnectionPolicy> connectionsEnabledOut) {
        this.name = name;
        this.connectionsEnabledIn = connectionsEnabledIn;
        this.connectionsEnabledOut = connectionsEnabledOut;
    }

}
