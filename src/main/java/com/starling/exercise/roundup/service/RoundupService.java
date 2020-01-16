package com.starling.exercise.roundup.service;

import com.starling.exercise.roundup.clients.AccountsClient;
import com.starling.exercise.roundup.clients.TransactionFeedClient;
import com.starling.exercise.roundup.clients.model.Accounts;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoundupService {

  private final AccountsClient accountsClient;
  private final TransactionFeedClient transactionFeedClient;

  public void roundup(UUID accountUid, UUID savingsGoalUid, OffsetDateTime minTransactionTimestamp,
      OffsetDateTime maxTransactionTimestamp) {

    final Accounts accounts = accountsClient.accounts();
    final UUID categoryUid = accounts.getAccounts().get(0).getDefaultCategory();
    transactionFeedClient.transactionFeed(accountUid, categoryUid, minTransactionTimestamp, maxTransactionTimestamp);
  }
}
