package org.example.Controller;


import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import org.example.DAO.RoomDAO;
import org.example.Entity.Room;
import org.example.dtos.RoomDTO;

public class RoomController {

    public static Handler create(RoomDAO dao) {
        return ctx -> {
            Room room = ctx.bodyAsClass(Room.class);

            if (room != null) {
                Room createdRoom = dao.create(room);

                RoomDTO createdRoomDTO = RoomDTO.builder()
                        .id(createdRoom.getId())
                        .number(createdRoom.getNumber())
                        .price(createdRoom.getPrice()).build();

                ctx.json(createdRoomDTO);
            } else {
                ctx.status(500).result("Couldn't create the room with the given data.");
            }
        };
    }

    public static Handler delete(RoomDAO dao) {
        return ctx -> {
            int roomId = Integer.parseInt(ctx.pathParam("id"));
            Room foundRoom = dao.getById(roomId);

            if (foundRoom != null) {
                RoomDTO roomDTO = RoomDTO.builder()
                        .id(foundRoom.getId())
                        .number(foundRoom.getNumber())
                        .price(foundRoom.getPrice())
                        .hotelId(foundRoom.getHotel().getId()).build();

                dao.delete(roomId);
                ctx.status(200).json(roomDTO);
            } else {
                ctx.status(404).result("Room not found.");
            }
        };
    }


    public static Handler getRoom(RoomDAO dao) {
        return ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if (dao.getById(id) != null) {
                Room foundRoom = dao.getById(id);
                RoomDTO roomDTO = RoomDTO.builder()
                        .id(foundRoom.getId())
                        .number(foundRoom.getNumber())
                        .price(foundRoom.getPrice())
                        .hotelId(foundRoom.getHotel().getId())
                        .build();
                ctx.status(HttpStatus.OK).json(roomDTO);
            } else {
                ctx.status(404).result("The id you are looking for does not exist.");
            }
        };
    }

    public static Handler getAll(RoomDAO dao) {
        return ctx -> {
            if (dao.getAll().isEmpty()) {
                ctx.status(404).result("No rooms available.");
            } else {
                ctx.json(dao.getAll());
            }
        };
    }

    public static Handler update(RoomDAO roomDAO) {
        return ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            RoomDTO updatedRoomDTO = ctx.bodyAsClass(RoomDTO.class);

            // Fetch the hotel from the database
            Room foundRoom = roomDAO.getById(id);

            if (foundRoom != null) {
                foundRoom.setNumber(updatedRoomDTO.getNumber());
                foundRoom.setPrice(updatedRoomDTO.getPrice());

                // Save the updated hotel to the database
                roomDAO.update(foundRoom);
                updatedRoomDTO.setId(id);

                // Return the updated hotelDTO object as JSON response
                ctx.json(updatedRoomDTO);
            } else {
                // Hotel with the provided ID not found
                ctx.status(404);
                ctx.result("Room not found.");
            }
        };
    }
}

