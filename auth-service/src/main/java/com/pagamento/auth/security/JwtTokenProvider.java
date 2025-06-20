package com.pagamento.auth.security;

import org.bouncycastle.jcajce.BCFKSLoadStoreParameter.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
/**
 * 
 * The import org.bouncycastle cannot be resolved
 * The import org.springframework cannot be resolved
 * The import org.springframework cannot be resolved
 * 
 * 
 * 
 * 
 * 
 * 
 * ***?
 */
import java.util.Date;

@Component
/**
 * 
 * The import org.springframework cannot be resolved
 * 
 * **/



public class JwtTokenProvider {

    @Value("${jwt.secret}")
    /**
     * 
     * Value cannot be resolved to a type
     * 
     * **/
    
    
    private String secret;

    @Value("${jwt.expiration-ms}")
    /**
     * 
     * Value cannot be resolved to a type
     * 
     * 
     * **/
    
    private long expirationMs;

    public String generateToken1(String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
        		/**
        		 * Jwts cannot be resolved
        		 * 
        		 * 
        		 * 
        		 * **/
        		
        		/**
        		 * 
        		 * Jwts cannot be resolved
        		 * 
        		 * 
        		 * **/
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(SignatureAlgorithm.HS512, secret)
                
                /**
                 * 
                 * 
                 * HS512 cannot be resolved or is not a field
                 * 
                 * */
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            /**
             * 
             * 
             * Jwts cannot be resolved
             * 
             * **/
            
            
            return true;
        } catch (JwtException | IllegalArgumentException e) {
        	
        	/*
        	 * 
        	 * Multiple markers at this line
	- No exception of type Object can be thrown; an exception type must be a subclass of Throwable
	- JwtException cannot be resolved to a type
        	 * 
        	 * 
        	 * ***/
        	
        	
            return false;
        }
    }

	public String generateToken(String username) {
		// TODO Auto-generated method stub
		return null;
	}
}
