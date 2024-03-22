package org.example.dtos;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
public class TokenDTO {

    private  String token;
    private String username;
}
