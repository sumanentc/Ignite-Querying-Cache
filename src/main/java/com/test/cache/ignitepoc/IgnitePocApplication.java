package com.test.cache.ignitepoc;

import com.test.cache.ignitepoc.config.EnableIgnite;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableIgnite
public class IgnitePocApplication {

    public static void main(String[] args) {
        SpringApplication.run(IgnitePocApplication.class, args);
    }

}
