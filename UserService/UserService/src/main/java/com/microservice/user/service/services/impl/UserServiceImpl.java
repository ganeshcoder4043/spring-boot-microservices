package com.microservice.user.service.services.impl;

import com.microservice.user.service.entities.Hotel;
import com.microservice.user.service.entities.Rating;
import com.microservice.user.service.entities.User;
import com.microservice.user.service.exception.ResourceNotFoundException;
import com.microservice.user.service.exception.UserNotFoundException;
import com.microservice.user.service.repositories.UserRepository;
import com.microservice.user.service.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public User saveUser(User user) {

        // Generate Unique Id
        String randomUserId = UUID.randomUUID().toString();
        user.setUserId(randomUserId);
        return userRepository.save(user);
    }

    /*@Override
    public User getUserById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

//        http://localhost:8083/ratings/users/66a9b43c-bf55-4c90-8619-a2bd28564dad

        ArrayList<Rating> ratingsOfUsers = restTemplate.getForObject("http://localhost:8083/ratings/users/"+user.getUserId(), ArrayList.class);
        logger.info("{}",ratingsOfUsers);
        user.setRatings(ratingsOfUsers);
        return  user;
    }*/

    @Override
    public User getUserById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

//        http://localhost:8083/ratings/users/66a9b43c-bf55-4c90-8619-a2bd28564dad

//        Rating[] ratingsOfUsers = restTemplate.getForObject("http://localhost:8083/ratings/users/" + user.getUserId(), Rating[].class);\
        Rating[] ratingsOfUsers = restTemplate.getForObject("http://RATINGSERVICE/ratings/users/" + user.getUserId(), Rating[].class);
        logger.info("{}", ratingsOfUsers);

        List<Rating> ratings = Arrays.stream(ratingsOfUsers).toList();

        List<Rating> ratingList = ratings.stream().map(rating -> {
//            ResponseEntity<Hotel> forEntity = restTemplate.getForEntity("http://localhost:8082/hotels/" + rating.getHotelId(), Hotel.class);
            ResponseEntity<Hotel> forEntity = restTemplate.getForEntity("http://HOTELSERVICE/hotels/" + rating.getHotelId(), Hotel.class);
            Hotel hotel = forEntity.getBody();

            logger.info("response status code : {}", forEntity.getStatusCode());
            rating.setHotel(hotel);
            return rating;

        }).collect(Collectors.toList());
        user.setRatings(ratingList);
        return user;
    }


    /* @Override
     public List<User> getAllUsers() {
         return userRepository.findAll();
     }*/

    /*@Override
    public List<User> getAllUsers() {
        List<User> allUsers = userRepository.findAll();

        // Fetch ratings for each user
        for (User user : allUsers) {
            try {
                String url = "http://localhost:8083/ratings/users/" + user.getUserId();
                ArrayList<Rating> ratingsOfUsers = restTemplate.getForObject(url, ArrayList.class);
                user.setRatings(ratingsOfUsers);
            } catch (Exception e) {
                logger.error("Ratings not found for user: {}", user.getUserId());
                user.setRatings(new ArrayList<>()); // Empty list if error
            }
        }

        return allUsers;
    }*/


    @Override
    public List<User> getAllUsers() {
        List<User> allUsers = userRepository.findAll();

        for (User user : allUsers) {
            try {
                // Fetch ratings as array
                String ratingUrl = "http://RATINGSERVICE/ratings/users/" + user.getUserId();
                Rating[] ratingsArray = restTemplate.getForObject(ratingUrl, Rating[].class);

                if (ratingsArray == null) {
                    user.setRatings(new ArrayList<>());
                    continue;
                }

                // Stream: Fetch hotel for each rating
                List<Rating> ratingsWithHotels = Arrays.stream(ratingsArray)
                        .map(rating -> {
                            String hotelUrl = "http://HOTELSERVICE/hotels/" + rating.getHotelId();
                            try {
                                Hotel hotel = restTemplate.getForObject(hotelUrl, Hotel.class);
                                rating.setHotel(hotel);
                                logger.info("Hotel fetched for rating: {}", rating.getHotelId());
                            } catch (Exception e) {
                                logger.error("Hotel not found for hotelId: {}", rating.getHotelId());
                                rating.setHotel(new Hotel());
                            }
                            return rating;
                        })
                        .collect(Collectors.toList());

                user.setRatings(ratingsWithHotels);

            } catch (Exception e) {
                logger.error("Ratings not found for user: {}", user.getUserId());
                user.setRatings(new ArrayList<>());
            }
        }

        return allUsers;
    }


    @Override
    public User updateUser(String userId, User user) {
        User existingUser = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User Not Found & Something Went Wrong!!!"));
        existingUser.setName(user.getName());
        existingUser.setLocation(user.getLocation());
        existingUser.setEmail(user.getEmail());
        existingUser.setAbout(user.getAbout());
        return userRepository.save(existingUser);

    }

    @Override
    public String deleteUserById(String userId) {
        userRepository.deleteById(userId);
        return "Your User " + userId + " Has Been Deleted";
    }
}
