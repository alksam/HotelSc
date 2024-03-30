package org.example.Entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;



@Getter
@Setter
@NoArgsConstructor
@Table(name = "room")
@Entity
@NamedQuery(name = "Room.findAll", query = "SELECT r FROM Room r")

public class Room {
    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id")
    int id;
    @Column(name = "number")
    int number;
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    RoomType type;
    @Column(name = "price")
    double price;
   @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "hotel_id")
    Hotel hotel;


    public Room(  int number, double price, RoomType type) {
        this.number = number;
        this.price = price;
        this.type = type;
    }

  //  public void setHotel(Hotel hotel) {
   //     this.hotel = hotel;
   // }

    public enum RoomType {
        SINGLE, DOUBLE,SUITE
    }
}
