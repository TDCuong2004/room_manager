package com.example.server.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    private final String SECRET = "mysecretkeymysecretkeymysecretkey123456";

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            System.out.println("JWT hết hạn");
        } catch (UnsupportedJwtException e) {
            System.out.println("JWT không hỗ trợ");
        } catch (MalformedJwtException e) {
            System.out.println("JWT sai format");
        } catch (SignatureException e) {
            System.out.println("Sai chữ ký JWT");
        } catch (IllegalArgumentException e) {
            System.out.println("JWT rỗng");
        }
        return null; // 🔥 cực kỳ quan trọng
    }
}