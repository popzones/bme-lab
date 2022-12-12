package hu.bme.bitcoin;

import com.alibaba.fastjson.JSONObject;
import hu.bme.bitcoin.utils.ShellUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;

@SpringBootTest
class BitcoinApplicationTests {


    @Test
    void contextLoads() throws IOException {
        String hash="000000000000000000029732109150958e4ceba006d5ce81a3802c83a01bf9a5";
        System.out.println(hash);
        String[] shell={"/bin/sh", "-c", "cd /home/ubuntu/bitcoin_source/bitcoin-23.0/src;./bitcoin-cli getblock"+" \""+hash+"\" 2"};

        StringBuffer buffer = ShellUtils.executeShell(shell);
        JSONObject jsonObject = JSONObject.parseObject(String.valueOf(buffer));
        System.out.println(jsonObject);





    }

}
