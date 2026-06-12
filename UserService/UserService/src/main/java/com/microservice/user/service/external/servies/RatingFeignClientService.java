package com.microservice.user.service.external.servies;

import com.microservice.user.service.entities.Rating;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "RATINGSERVICE")
public interface RatingFeignClientService {

    @GetMapping("/ratings/users/{ratingId}")
    Rating[] getRating(@PathVariable("ratingId") String ratingId);
}
