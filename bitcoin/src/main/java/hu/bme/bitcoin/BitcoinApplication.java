package hu.bme.bitcoin;


import hu.bme.bitcoin.entity.BitcoinService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.Resource;
import java.io.*;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class BitcoinApplication {


    public static void main(String[] args) throws IOException, InterruptedException {
        ConfigurableApplicationContext run = SpringApplication.run(BitcoinApplication.class, args);
        BitcoinService bean = run.getBean(BitcoinService.class);
        bean.queryMemoryPool();
        //bean.initList();
        /**
        for(;;)
        {
            System.out.println("---------------------------------------");
            System.out.println("---------------------------------------");
            bean.execute();
            Thread.sleep(10000);
        }
         **/


    }

}
