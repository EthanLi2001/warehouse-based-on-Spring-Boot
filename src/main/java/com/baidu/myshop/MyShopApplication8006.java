package com.baidu.myshop;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import javax.swing.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@MapperScan("com.baidu.myshop.dao")

public class MyShopApplication8006 {
    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext application = SpringApplication.run(MyShopApplication8006.class, args);

        Environment env = application.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port");
        System.out.println("\n------------------------------------------------------\n\t" +
                "Application Ruoyi-Geek is running! Access URLs:\n\t" +
                "Local: \t\thttp://localhost:" + port + "/\n\t" +
                "External: \thttp://" +ip + ":" + port +"/\n\t" +
                "Swagger文档: \thttp://" +ip + ":" +port + "/swagger-ui/index.html\n\t" +
                "Knife4j文档: \thttp://" +ip + ":" +port + "/doc.html\n" +
                "---------------------------------------------------------");
    }
}
