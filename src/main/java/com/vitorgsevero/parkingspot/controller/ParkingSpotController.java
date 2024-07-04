package com.vitorgsevero.parkingspot.controller;

import com.vitorgsevero.parkingspot.dto.ParkingSpotDTO;
import com.vitorgsevero.parkingspot.model.ParkingSpot;
import com.vitorgsevero.parkingspot.service.ParkingSpotService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/parking-spot")
public class ParkingSpotController {

    final ParkingSpotService service;

    public ParkingSpotController(ParkingSpotService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody @Valid ParkingSpotDTO parkingSpotDTO){

        if(service.existsByLicensePlateCar(parkingSpotDTO.getVehicleLicensePlate())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: License Plate Car is already in use! Car parked.");
        }

        if(service.existsByParkingSpotNumber(parkingSpotDTO.getParkingSpotNumber())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking spot is already in use! There is a car parked.");
        }

        if(service.existsByApartmentAndBlock(parkingSpotDTO.getApartment(), parkingSpotDTO.getApartmentBlock())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking spot already registered for this apartment and block! You or someone parked using your permission.");
        }

        var parkingSpot = new ParkingSpot();
        BeanUtils.copyProperties(parkingSpotDTO, parkingSpot);
        parkingSpot.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.save(parkingSpot));
    }

}
