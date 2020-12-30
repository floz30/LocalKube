package com.example.demo;

import org.springframework.boot.ApplicationArguments;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    private final LocalKubeConsumingLoader logger;

    public DemoController(ApplicationArguments args){
        this.logger = new LocalKubeConsumingLoader(Integer.parseInt(args.getOptionValues("service.port").get(0)));
    }

    @GetMapping("/")
    public ResponseEntity hello(){
        try{
            logger.insertLog("GET on '/' ");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
        return ResponseEntity.status(HttpStatus.OK).body("Hello world !");
    }
}
