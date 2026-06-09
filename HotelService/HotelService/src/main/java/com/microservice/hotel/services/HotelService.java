package com.microservice.hotel.services;

import com.microservice.hotel.entities.Hotel;

import java.util.List;

public interface HotelService {

    // createHotel
    Hotel createHotel(Hotel hotel);

    // getAllHotel
    List<Hotel> getAllHotel();

    // getHotelById
    Hotel getHotelById(String hotelId);

    // updateHotel
    Hotel updateHotel(String hotelId, Hotel hotel);

    // deleteHotelById
    String deleteHotelById(String hotelId);



}
