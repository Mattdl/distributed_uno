package app_server.service;

import io.jsonwebtoken.impl.crypto.MacProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stub_RMI.client_appserver.LoginStub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;

import io.jsonwebtoken.*;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

public class LoginService extends UnicastRemoteObject implements LoginStub {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameService.class.getName());

    private final long TTL = 24 * 60 * 60 * 1000; //24h Time to live
    private String apiSecret;

    public LoginService() throws RemoteException {
        apiSecret = generateApiSecret(50);
    }

    @Override
    public boolean ping() throws RemoteException {
        return true;
    }


    @Override
    public String getLoginToken(String username, String password) throws RemoteException {

        //TODO check login with database


        //Get password in plain text from user, hash with salt from database, check with hash from database
        //return null if failed login

        //TODO generate token
        String token = createJWT(username, null, username, TTL);

        return token;
    }

    @Override
    public boolean loginWithToken(String token) throws RemoteException {
        return validateJWT(token);
    }

    private String createJWT(String id, String issuer, String subject, long ttlMillis) {

        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(apiSecret);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId(id)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(issuer)
                .signWith(signatureAlgorithm, signingKey);

        //if it has been specified, let's add the expiration
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    /**
     * Read and validate JWT token
     *
     * @param jwt
     */
    private boolean validateJWT(String jwt) {
        LOGGER.info("Validating JWT, jwt = {}", jwt);

        //This line will throw an exception if it is not a signed JWS (as expected)
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(apiSecret))
                    .parseClaimsJws(jwt).getBody();

            return claims.getExpiration().after(new Date());

        } catch (SignatureException e) {
            return false;
        }
    }

    private String generateApiSecret(int length) {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[length];
        random.nextBytes(bytes);
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        String token = encoder.encodeToString(bytes);
        return token;
    }
}
