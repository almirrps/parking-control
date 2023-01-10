package com.api.parkingcontrol.repositories;

import com.api.parkingcontrol.models.ParkingSpotModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

@Repository   //Coloquei essa anotation por questão de clareza, mas ela já é embutida no JpaRepository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpotModel, UUID> {

}
