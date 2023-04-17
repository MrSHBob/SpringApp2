package com.app.springapp2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class SpringApp2ApplicationTests {

    @Autowired
    ApplicationContext context;

    @Test
    void contextLoads() {
        String appName = context.getApplicationName();
        System.out.println();
    }

}
