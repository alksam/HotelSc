package org.example.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "hotel")
@Setter
@Getter
@NoArgsConstructor
@NamedQuery(name = "Hotel.findAll", query = "SELECT h FROM Hotel h")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;
    @Column(name = "name")
    String name;
    @Column(name = "address")
    String address;
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    HotelType type;
    @OneToMany(mappedBy = "hotel", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    Set<Room> rooms = new HashSet<>();
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    public Hotel( String name, String address , HotelType type) {
        this.name = name;
        this.address = address;
        this.type = type;

    }

    public void addUser(User user) {
        this.user = user;
        user.addHotel(this);
    }
    public void addRoom(Room room) {
        rooms.add(room);
        room.setHotel(this);
    }

  

    public Set<String> getRoomasStrings() {
        if (rooms.isEmpty()) {
            return null;
        }
        Set<String> roomStrings = new HashSet<>();
        rooms.forEach((room) -> {
            roomStrings.add(room.getNumber() + " " + room.getPrice());
        });
        return roomStrings;
    }

    public enum HotelType {
        STANDARD, BUDGET, LUXURY
    }
}
