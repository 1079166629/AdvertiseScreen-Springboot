package com.usr.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Repository;

import javax.servlet.MultipartConfigElement;
import java.io.File;

@Configuration
@Repository
public class MultipartConfig {
    @Value("${locationWin:/tmp/tomcat_upload}")
    private String tempDir;

    @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        File tmpDirFile = new File(tempDir);
        // 判断文件夹是否存在
        if (!tmpDirFile.exists()) {
            //创建文件夹
            tmpDirFile.mkdirs();
        }
        factory.setLocation(tempDir);
        return factory.createMultipartConfig();
    }
}
