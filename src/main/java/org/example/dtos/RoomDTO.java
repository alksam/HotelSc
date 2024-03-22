package org.example.dtos;



import lombok.*;
import org.example.Entity.Room;

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
public class RoomDTO {

    int id;
    int hotelId;
    int number;
    double price;


    public RoomDTO(int hotelId, int number, double price) {

        this.hotelId = hotelId;
        this.number = number;
        this.price = price;
    }

    public RoomDTO(Room room) {
        this.id = room.getId();
        this.number = room.getNumber();
        this.price = room.getPrice();
    }
}
