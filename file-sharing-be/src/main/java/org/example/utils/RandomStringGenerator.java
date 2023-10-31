package org.example.utils;

import org.bson.types.ObjectId;

import java.math.BigInteger;

public class RandomStringGenerator {
    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public String generateRandomBase62String() {
        ObjectId objectId = new ObjectId();
        return base62Encode(objectId.toByteArray());
    }

    private String base62Encode(byte[] input) {
        StringBuilder base62 = new StringBuilder();

        // Convert the string to a big integer
        BigInteger bigInteger = new BigInteger(1, input);

        while (bigInteger.compareTo(BigInteger.ZERO) > 0) {
            int remainder = bigInteger.mod(BigInteger.valueOf(62)).intValue();
            base62.append(BASE62.charAt(remainder));
            bigInteger = bigInteger.divide(BigInteger.valueOf(62));
        }

        return base62.reverse().toString();
    }
}
