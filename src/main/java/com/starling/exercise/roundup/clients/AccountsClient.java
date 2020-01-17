package com.starling.exercise.roundup.clients;

import static java.lang.String.format;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

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
  @Value("${authorization.token}")
  private String authorizationToken;

  public Accounts accounts() {

    HttpHeaders headers = new HttpHeaders();
    headers.add(ACCEPT, APPLICATION_JSON_VALUE);
    headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
    headers.add(AUTHORIZATION, format("Bearer %s", authorizationToken));
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
