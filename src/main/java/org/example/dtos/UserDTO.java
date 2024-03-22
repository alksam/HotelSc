package org.example.dtos;

import lombok.*;
import org.example.Entity.User;


import java.util.Set;

@Getter
@NoArgsConstructor
@Setter
@AllArgsConstructor
@ToString
public class UserDTO {
    private String username;
    private String password;

    private Set<String> roles;

    public UserDTO(User user){ {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.roles = user.getRolesAsStrings();
    }
    }
    public UserDTO(String username, Set<String> roles) {
        this.username = username;
        this.roles = roles;

    }


}

