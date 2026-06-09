package com.microservice.rating.services;

import com.microservice.rating.entities.Rating;

import java.util.List;

public interface RatingService {

    // create
    Rating createRating(Rating rating);

    // getAllRatings
    List<Rating> getAllRatings();

    // getAllRatingByUserId
    List<Rating> getAllRatingByUserId(String userId);

    // getAllRatingByHotelId
    List<Rating> getAllRatingByHotelId(String hotelId);


}
