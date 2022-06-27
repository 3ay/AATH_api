package com.hyperledger.AATH.Backchannel.API;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AathBackchannelApiApplicationTests {
	@Value("${credentials}")
	private String credentials_agent;
	@Test
	void contextLoads() {
	}
	@Test
	public void test() {
		Assert.assertEquals(credentials_agent,"Ftp1Lx2Y9uVqM5Q1fzAgy3kngGMWqOsUGylPGc3oYD6a3o5vSxMHC4NKE8+f1HCdo3T+ZJEBZq50BaJy9rSGh18/iBQBSUoI8MAF402kYjQ=");
	}

}
