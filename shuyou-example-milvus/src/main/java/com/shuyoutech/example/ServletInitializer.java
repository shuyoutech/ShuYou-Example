package com.shuyoutech.example;

import com.alibaba.fastjson2.JSON;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.service.rbac.request.UpdatePasswordReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-13 20:30
 **/
@Slf4j
@RestController
@RequiredArgsConstructor
@ComponentScan(basePackages = "com.shuyoutech")
@SpringBootApplication
public class ServletInitializer extends SpringBootServletInitializer {

    @GetMapping("/")
    public String hello() {
        List<String> users = milvusClientV2.listUsers();
        log.info("users: {}", JSON.toJSONString(users));

        UpdatePasswordReq passwordReq = UpdatePasswordReq.builder().userName("root").password("Milvus").newPassword("ShuYou_20250711").build();
        milvusClientV2.updatePassword(passwordReq);
        return "Hello, ShuYou Example Milvus";
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ServletInitializer.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(ServletInitializer.class, args);
    }

    private final MilvusClientV2 milvusClientV2;

}
