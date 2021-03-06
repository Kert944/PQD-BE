package com.pqd.adapters.web.security.jwt;

import com.pqd.adapters.web.authentication.UserProductClaimResponseJson;
import com.pqd.application.domain.claim.ClaimLevel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtil implements Serializable {

    // TODO change Date to LocalDate

    private static final long serialVersionUID = -2550185165626007488L;

    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

    @Value("${jwt.secret}")
    private String secret;

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getIssuedAtDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public List<JwtUserProductClaim> getProductClaimsFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);

        @SuppressWarnings (value="unchecked")
        ArrayList<Object> productClaims = claims.get("product", ArrayList.class);
        return productClaims
                .stream()
                .map(obj -> {
                    if (obj instanceof LinkedHashMap) {

                        @SuppressWarnings (value="unchecked")
                        LinkedHashMap<String, Object> linkedHashMap = (LinkedHashMap<String, Object>) obj;

                        @SuppressWarnings (value="unchecked")
                        String claimLevel = ((LinkedHashMap<String, String>) linkedHashMap.get("claimLevel")).get("value");
                        return JwtUserProductClaim.builder()
                                                  .productId(Long.valueOf((Integer) linkedHashMap.get("productId")))
                                                  .claimLevel(ClaimLevel.builder()
                                                                        .value(claimLevel)
                                                                        .build())
                                                  .build();
                    }
                    return null;
                }).collect(Collectors.toList());
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private Boolean ignoreTokenExpiration(String token) {
        // here you specify tokens, for that the expiration is ignored
        return false;
    }

    public String generateToken(UserDetails userDetails, List<UserProductClaimResponseJson> productClaims) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("product", productClaims);
        return doGenerateToken(claims, userDetails.getUsername());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                   .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                   .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    public Boolean canTokenBeRefreshed(String token) {
        return (!isTokenExpired(token) || ignoreTokenExpiration(token));
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}
