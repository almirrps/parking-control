package com.api.parkingcontrol;

import org.springframework.data.repository.cdi.Eager;
import org.springframework.stereotype.Component;

@Component
@Eager  //O SpringBoot cria esta classe de forma anciosa, deixando ela disponível para utilização
public class EagerBean {

    public EagerBean() {
        System.out.println("eagerBean started!");
    }

}
