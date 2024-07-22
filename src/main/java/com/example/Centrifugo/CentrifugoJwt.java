package com.example.Centrifugo;


import com.example.Centrifugo.config.CentrifugoConfiguration;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.Principal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CentrifugoJwt {

    private  final CentrifugoConfiguration centrifugo;

    public String createJWT(String userId) {


        final String CENTRIFUGO_SECRET = centrifugo.getSecret();

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMills = System.currentTimeMillis();
        Date currentDate = new Date(nowMills);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, + 1);
        Date futureDate = cal.getTime();

        Key signifyKey = new SecretKeySpec(CENTRIFUGO_SECRET.getBytes(),
                          signatureAlgorithm.getJcaName());

        JwtBuilder builder = Jwts.builder()
                .setIssuer("CentrifugoWeb")
                .setAudience("Centrifugo")
                .setIssuedAt(currentDate)
                .setExpiration(futureDate)
                .setSubject(userId)
                .signWith(signifyKey,signatureAlgorithm);

        return builder.compact();

    }
}
