package com.LoQueHay.project.integrations;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class shopifyClient {

    private static final String SHOPIFY_STORE = "erp-test-store.myshopify.com";
    private static final String ACCESS_TOKEN = "tu_access_token";

    public void getProducts() {
        String url = "https://" + SHOPIFY_STORE + "/admin/api/2025-10/products.json";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Shopify-Access-Token", ACCESS_TOKEN);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, String.class);

        System.out.println(response.getBody());
    }
}
