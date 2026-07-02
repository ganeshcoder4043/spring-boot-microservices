package com.microservice.rating.controllers;

import com.microservice.rating.entities.Rating;
import com.microservice.rating.services.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ratings")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-rating")
    public ResponseEntity<Rating> createRating(@RequestBody Rating rating){
        return ResponseEntity.status(HttpStatus.CREATED).body(ratingService.createRating(rating));
    }

    @GetMapping("/getAllRatings")
    public ResponseEntity<List<Rating>> getAllRatings(){
        return ResponseEntity.ok(ratingService.getAllRatings());
    }

    @PreAuthorize("hasAuthority('SCOPE_internal')")
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<Rating>> getAllRatingByUserId(@PathVariable String userId){
        return ResponseEntity.ok(ratingService.getAllRatingByUserId(userId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/hotels/{hotelId}")
    public ResponseEntity<List<Rating>> getAllRatingByHotelId(@PathVariable String hotelId){
        return ResponseEntity.ok(ratingService.getAllRatingByHotelId(hotelId));
    }

}
