package com.hosting.rest.api.controllers.Accomodation;

import com.hosting.rest.api.models.Accomodation.AccomodationModel;
import com.hosting.rest.api.services.Accomodation.AccomodationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/accomodations")
public class AccomodationController{

    @Autowired
    private AccomodationServiceImpl accomodationService;

    @GetMapping("all")
    public List<AccomodationModel> getAllAccomodations(){
        return accomodationService.getAllAccomodations();
    }
}
