package com.api.parkingcontrol.controllers;

import com.api.parkingcontrol.services.ParkingSpotService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins="*", maxAge = 3600) //Permite que o controller seja acessado de qualquer fonte
@RequestMapping("/parking-spot") //Define a URI a nivel de classe
public class ParkingSpotController {

    //Criando a injeção de dependência, por meio de um construtor,
    //da classe service para a classe Controller
    final ParkingSpotService parkingSpotService;

    public ParkingSpotController(ParkingSpotService parkingSpotService) {
        this.parkingSpotService = parkingSpotService;
    }


}
