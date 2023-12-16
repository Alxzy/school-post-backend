package com.frn.findlovebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com/frn/findlovebackend/mapper")
public class FindloveBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FindloveBackendApplication.class, args);
    }

}
