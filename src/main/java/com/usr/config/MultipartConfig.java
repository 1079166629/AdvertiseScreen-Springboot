package com.usr.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.MultipartConfigElement;
import java.io.File;

@Configuration
@PropertySource("classpath:application.yml")
@Controller
@Repository
public class MultipartConfig {
    @Value("${locationWin}")
    private String tempDir;

    @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        File tmpDirFile = new File(tempDir);
        String systemName = System.getProperty("os.name");
        // 判断文件夹是否存在
        if (!tmpDirFile.exists()) {
            //创建文件夹
            tmpDirFile.mkdirs();
        }
        if(!StringUtils.isBlank(systemName) && systemName.toLowerCase().contains("linux")){
            factory.setLocation(tempDir);
        }
        else factory.setLocation("D/");

        return factory.createMultipartConfig();
    }
}
