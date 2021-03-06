package com.starling.exercise.roundup.clients;

import static java.lang.String.format;
import static java.time.Duration.ofMillis;
import static java.util.UUID.randomUUID;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.GATEWAY_TIMEOUT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

import com.starling.exercise.roundup.clients.model.Amount;
import com.starling.exercise.roundup.clients.model.SavingsGoalTransfer;
import com.starling.exercise.roundup.exception.HttpClientServiceException;
import com.starling.exercise.roundup.web.model.StarlingOperation;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
public class SavingsGoalClient {

  private final RestTemplate restTemplate;
  @Value("${savings-goal.add-money.url}")
  private String savingsGoalUrl;
  @Value("${authorization.token}")
  private String authorizationToken;

  public SavingsGoalClient(RestTemplateBuilder restTemplateBuilder,
      @Value("${rest.template.timeout.ms}") Integer timeout) {
    restTemplate = restTemplateBuilder
        .setConnectTimeout(ofMillis(timeout))
        .setReadTimeout(ofMillis(timeout))
        .build();
  }

  public StarlingOperation addMoney(UUID accountUid, UUID savingsGoalUid, Amount amount) {

    final HttpHeaders headers = new HttpHeaders();
    headers.add(ACCEPT, APPLICATION_JSON_VALUE);
    headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
    headers.add(AUTHORIZATION, format("Bearer %s", authorizationToken));

    final SavingsGoalTransfer transferRequest = SavingsGoalTransfer.builder().amount(amount).build();

    final HttpEntity<SavingsGoalTransfer> httpEntity = new HttpEntity<>(transferRequest, headers);

    final Map<String, String> urlParams = new HashMap<>();
    urlParams.put("accountUid", accountUid.toString());
    urlParams.put("savingsGoalUid", savingsGoalUid.toString());
    urlParams.put("transferUid", randomUUID().toString());

    final String url = fromHttpUrl(savingsGoalUrl).buildAndExpand(urlParams).toUriString();

    try {
      final ResponseEntity<StarlingOperation> response = restTemplate
          .exchange(url, PUT, httpEntity, StarlingOperation.class);

      return response.getBody();
    } catch (HttpClientErrorException ex) {
      throw new HttpClientServiceException("Failed to call Savings Goal API correctly", INTERNAL_SERVER_ERROR);
    } catch (HttpServerErrorException ex) {
      throw new HttpClientServiceException("Savings Goal API failed to fulfill the request", BAD_GATEWAY);
    } catch (ResourceAccessException ex) {
      throw new HttpClientServiceException("Savings Goal API timed out", GATEWAY_TIMEOUT);
    } catch (Exception e) {
      throw new HttpClientServiceException("Something unrecoverable has happened", INTERNAL_SERVER_ERROR);
    }
  }
}
