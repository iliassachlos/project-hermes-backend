package org.example.user.utilities;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtTokenUtil {
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public static String generateToken(String userId) {
        // Set expiration to 72 hours (3 days)
        Date expirationTime = new Date(System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000);

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(expirationTime)
                .signWith(SECRET_KEY)
                .compact();
    }
}
