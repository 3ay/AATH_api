package com.sirius.aath.backchannel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AathBackchannelApplicationTests {

    @Value("${env.credentials}")
    private String credentialsAgent;
    @Test
    void contextLoads() {
    }

    @Test
    public void test() {
        Assertions.assertEquals(credentialsAgent,
                "Ftp1Lx2Y9uVqM5Q1fzAgy3kngGMWqOsUGylPGc3oYD6a3o5vSxMHC4NKE8+f1HCdo3T+ZJEBZq50BaJy9rSGh18/iBQBSUoI8MAF402kYjQ=");
    }

}
