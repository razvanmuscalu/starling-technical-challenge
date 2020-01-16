package com.starling.exercise.roundup.clients;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.starling.exercise.roundup.clients.model.Accounts;
import com.starling.exercise.roundup.exception.HttpClientServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AccountsClient {

  private final RestTemplate restTemplate;
  @Value("${accounts.url}")
  private String accountsUrl;

  public Accounts accounts() {

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", "application/json");
    headers.add("Content-Type", "application/json");
    headers.add("Authorization", "Bearer mock_token");
    HttpEntity<?> httpEntity = new HttpEntity<>(headers);

    try {
      ResponseEntity<Accounts> response = restTemplate.exchange(accountsUrl, GET, httpEntity, Accounts.class);

      return response.getBody();
    } catch (HttpClientErrorException ex) {
      throw new HttpClientServiceException("Failed to call Accounts API correctly", INTERNAL_SERVER_ERROR);
    } catch (HttpServerErrorException ex) {
      throw new HttpClientServiceException("Accounts API failed to fulfill the request", BAD_GATEWAY);
    } catch (Exception e) {
      throw new HttpClientServiceException("Something unrecoverable has happened", INTERNAL_SERVER_ERROR);
    }
  }
}
