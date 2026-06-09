package com.microservice.hotel.services.impl;

import com.microservice.hotel.entities.Hotel;
import com.microservice.hotel.exception.UserNotFoundException;
import com.microservice.hotel.repositories.HotelRepository;
import com.microservice.hotel.services.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class HotelServiceImpl implements HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    @Override
    public Hotel createHotel(Hotel hotel) {
        String randomHotelId = UUID.randomUUID().toString();
        hotel.setId(randomHotelId);
        return hotelRepository.save(hotel);
    }

    @Override
    public List<Hotel> getAllHotel() {
        return hotelRepository.findAll();
    }

    @Override
    public Hotel getHotelById(String hotelId) {
        return hotelRepository.findById(hotelId).orElseThrow(() -> new UserNotFoundException(hotelId));
    }

    @Override
    public Hotel updateHotel(String hotelId, Hotel hotel) {
        Hotel existingHotel = hotelRepository.findById(hotelId).orElseThrow(() -> new UserNotFoundException(hotelId));
        existingHotel.setName(hotel.getName());
        existingHotel.setLocation(hotel.getLocation());
        existingHotel.setAbout(hotel.getAbout());
        return hotelRepository.save(existingHotel);
    }

    @Override
    public String deleteHotelById(String hotelId) {
        hotelRepository.deleteById(hotelId);
        return "Your User "+hotelId+" Has Been Deleted";
    }
}
