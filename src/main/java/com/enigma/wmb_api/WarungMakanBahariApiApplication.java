package com.enigma.wmb_api;

import com.enigma.wmb_api.util.HashUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class WarungMakanBahariApiApplication {

    public static void main(String[] args) {
		SpringApplication.run(WarungMakanBahariApiApplication.class, args);
    }

}
