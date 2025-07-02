package com.puenteblanco.pb.services.impl;

import com.puenteblanco.pb.services.interfaces.ReniecService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Service
public class ReniecServiceImpl implements ReniecService {

    private final String API_URL = "https://api.apis.net.pe/v2/reniec/dni?numero=";
    private final String TOKEN = "apis-token-16341.cXDDQG15OxxWuVSjIeGrdYShM5G64PsQ";

    @Override
    public Map<String, Object> consultarPorDni(String dni) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + TOKEN);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                API_URL + dni,
                HttpMethod.GET,
                entity,
                Map.class);

        return response.getBody();
    }
}