package com.sporty.aviationapiwrapper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AviationApiWrapperApplication {

  public static void main(String[] args) {
    SpringApplication.run(AviationApiWrapperApplication.class, args);
  }

}
