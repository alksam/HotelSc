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
    @OneToMany(mappedBy = "hotel", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    Set<Room> rooms = new HashSet<>();
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    public Hotel( String name, String address) {
        this.name = name;
        this.address = address;

    }

    public void addUser(User user) {
        this.user = user;
        user.addHotel(this);
    }
    public void addRoom(Room room) {
        rooms.add(room);
        room.setHotel(this);
    }
}
