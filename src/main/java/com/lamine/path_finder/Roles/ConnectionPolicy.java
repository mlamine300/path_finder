package com.lamine.path_finder.Roles;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConnectionPolicy {
    String  RoleName;

    ConnectionType type;
}
