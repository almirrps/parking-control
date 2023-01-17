package com.api.parkingcontrol.controllers;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.api.parkingcontrol.LazyBean;
import com.api.parkingcontrol.MyBean;
import com.api.parkingcontrol.dtos.ParkingSpotDto;
import com.api.parkingcontrol.models.ParkingSpotModel;
import com.api.parkingcontrol.services.ParkingSpotService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins="*", maxAge = 3600) //Permite que o controller seja acessado de qualquer fonte
@RequestMapping("/parking-spot") //Define a URI a nivel de classe
//@Scope("singleton")  //Deixa o springboot decidir melhor momento para iniciar a instância
//@Scope("prototype")  //Inicia uma nova instância de Controller toda vez que um método (endpoint) é executado
//@PropertySource("classpath:custom.properties")
public class ParkingSpotController {

    //Primeira forma de injeção de dependência - por meio de um construtor
    /*
    final ParkingSpotService parkingSpotService;

    public ParkingSpotController(ParkingSpotService parkingSpotService) {
        this.parkingSpotService = parkingSpotService;
    }
     */

    //Segunda forma de injeção de dependência - por meio da annotation @Autowired
    @Autowired
    @Qualifier("parkingSpotServiceImpl")  //Apontando o Bean da Service
    //@Qualifier("parkingSpotServiceImplV2") ////Apontando o Bean da Service V2
    private ParkingSpotService parkingSpotService;

    @Autowired
    private MyBean myBean;

    @Autowired
    private LazyBean lazyBean;

    @Value("${app.name}")
    private String appName;

    @Value("${app.host}")
    private String appHost;

    //@Value("${message}")
    //private String message;

    //public ParkingSpotController() {
    //    System.out.println("ParkingSpotController created!!!");
    //}

    @PostMapping  //Definindo um método público post direto na URI da classe
    public ResponseEntity<Object> saveParkingSpot(@RequestBody @Valid ParkingSpotDto  parkingSpotDto) {
        if (parkingSpotService.existsByLicensePlateCar(parkingSpotDto.getLicensePlateCar())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: License Plate Car is already in use!");
        }

        if (parkingSpotService.existsByParkingSpotNumber(parkingSpotDto.getParkingSpotNumber())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot is already in use!");
        }

        if (parkingSpotService.existsByApartmentAndBlock(parkingSpotDto.getApartment(), parkingSpotDto.getBlock())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot already registered for this apartment/block!");
        }

        var parkingSpotModel = new ParkingSpotModel();
        BeanUtils.copyProperties(parkingSpotDto, parkingSpotModel);
        parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotService.save(parkingSpotModel));
    }

    @GetMapping //Definindo um método público get direto na URI da classe para retornar uma listagem
    public ResponseEntity<Page<ParkingSpotModel>> getAllParkingSpots(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        System.out.println("App Name: " + appName);
        System.out.println("App Host: " + appHost);
        //System.out.println(message);
        myBean.method();

        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.findAll(pageable));
    }

    @GetMapping("/{id}") //Definindo método público get direto na URI da classe com parametro ID
    public ResponseEntity<Object> getOneParkingSpot(@PathVariable(value = "id") UUID id) {
        Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
        if (!parkingSpotModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotModelOptional.get());
    }

    @DeleteMapping("/{id}") //Definindo método público delete direto na URI da classe com parametro ID
    public ResponseEntity<Object> deleteParkingSpot(@PathVariable(value = "id") UUID id) {
        Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
        if (!parkingSpotModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found.");
        }
        parkingSpotService.delete(parkingSpotModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Parking Spot deleted successfully.");
    }

    @PutMapping("/{id}") //Definindo método público put direto na URI da classe com parametro ID e Dto
    public ResponseEntity<Object> updateParkingSpot(@PathVariable(value = "id") UUID id,
                                                    @RequestBody @Valid ParkingSpotDto parkingSpotDto) {
        Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
        if (!parkingSpotModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found.");
        }
        var parkingSpotModel = parkingSpotModelOptional.get();

        //Primeira forma de salvar os dados alterados - passa campo a campo
        /*
        parkingSpotModel.setParkingSpotNumber(parkingSpotDto.getParkingSpotNumber());
        parkingSpotModel.setLicensePlateCar(parkingSpotDto.getLicensePlateCar());
        parkingSpotModel.setModelCar(parkingSpotDto.getModelCar());
        parkingSpotModel.setBrandCar(parkingSpotDto.getBrandCar());
        parkingSpotModel.setColorCar(parkingSpotDto.getColorCar());
        parkingSpotModel.setResponsibleName(parkingSpotDto.getResponsibleName());
        parkingSpotModel.setApartment(parkingSpotDto.getApartment());
        parkingSpotModel.setBlock(parkingSpotDto.getBlock());
         */

        //Segunda forma de salvar os dados alterados
        BeanUtils.copyProperties(parkingSpotDto, parkingSpotModel);  //Convertendo Dto para Model pelo BeanUntils
        parkingSpotModel.setId(parkingSpotModelOptional.get().getId()); //Para permanecer o mesmo ID
        parkingSpotModel.setRegistrationDate(parkingSpotModelOptional.get().getRegistrationDate()); //Para permanecer a mesma data de registro

        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.save(parkingSpotModel));
    }

}
