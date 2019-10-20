package com.example.securedapi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class TestEncoding {

  @Test
  public void testBCrypEncoding() {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2B, 12);
    String [] passwords = new String[] { "changeit", "paz$w0rD", "s3<Re7" };
    for (String password: passwords) {
      long l1 = System.currentTimeMillis();
      String encoded = encoder.encode(password);
      long l2 = System.currentTimeMillis();
      long l3 = (l2 - l1);
      System.out.println("Encoded: " + encoded + " - " + l3 + " ms");
    }
  }
}
