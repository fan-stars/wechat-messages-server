package cn.fanstars.application;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目的启动类
 */
@MapperScan("${fan.info.base-package}.module.mapper")
@SpringBootApplication(scanBasePackages = {"${fan.info.base-package}.application", "${fan.info.base-package}.module"})
public class FanApplication {

    public static void main(String[] args) {
        SpringApplication.run(FanApplication.class, args);
    }

}
