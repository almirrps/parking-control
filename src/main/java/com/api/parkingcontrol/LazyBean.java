package com.api.parkingcontrol;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy //O SpringBoot cria esta classe somente quando solicitada (injetada - ver classe ParkingSpotController)
public class LazyBean {

    public LazyBean() {
        System.out.println("LazyBean started!");
    }

}
