package org.example.dtos;


import lombok.*;
import org.example.Entity.Hotel;
import org.example.Entity.Room;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class HotelDTO {

    int id;
    String name;
    String address;
     Set<Room> rooms;




}
