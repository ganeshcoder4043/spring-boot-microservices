package com.microservice.hotel.controllers;

import com.microservice.hotel.entities.Hotel;
import com.microservice.hotel.services.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/hotel-create")
    public ResponseEntity<Hotel> createHotel(@RequestBody Hotel hotel){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(hotelService.createHotel(hotel));
    }

    @GetMapping("/all-hotels")
    public ResponseEntity<List<Hotel>> getAllHotel(){
        List<Hotel> allHotel = hotelService.getAllHotel();
        if (allHotel.isEmpty()){
            return ResponseEntity.noContent().build(); // No Content
        }
        return ResponseEntity.ok(allHotel);
    }

    @PreAuthorize("hasAuthority('SCOPE_internal')")
    @GetMapping("/{hotelId}")
    public ResponseEntity<Hotel> getHotelById(@PathVariable String hotelId){
        Hotel hotelById = hotelService.getHotelById(hotelId);
        return ResponseEntity.ok(hotelById);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{hotelId}")
    public ResponseEntity<Hotel> updateHotel(@PathVariable String hotelId, @RequestBody Hotel hotel){
        Hotel updateHotel = hotelService.updateHotel(hotelId, hotel);
        return ResponseEntity.ok(updateHotel);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{hotelId}")
    public ResponseEntity<String> deleteHotelById(@PathVariable String hotelId){
        String s = hotelService.deleteHotelById(hotelId);
        return ResponseEntity.ok(s);
    }


}
