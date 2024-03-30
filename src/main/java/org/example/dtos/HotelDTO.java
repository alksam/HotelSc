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
@Builder
public class HotelDTO {

  private   int id;
   private String name;
   private String address;
    private Set<String> rooms;


    public HotelDTO(Hotel hotel) {
        this.id = hotel.getId();
        this.name = hotel.getName();
        this.address = hotel.getAddress();
       this.rooms = hotel.getRoomasStrings();
    }
    public HotelDTO( String name, String address, Set<String> rooms) {

        this.name = name;
        this.address = address;
        this.rooms = rooms;
    }
}
