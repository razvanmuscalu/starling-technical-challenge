package com.starling.exercise.roundup.service;

import static com.starling.exercise.roundup.service.RoundupFunction.roundup;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.starling.exercise.roundup.clients.model.Amount;
import com.starling.exercise.roundup.clients.model.TransactionFeedItems;
import com.starling.exercise.roundup.clients.model.TransactionFeedItems.TransactionFeedItem;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RoundupFunctionTest {

  static Stream<Arguments> roundupTestCaseInputProvider() {
    return Stream.of(
        // amount lower than 100 pence
        of(List.of(12), 88),
        // multiple amounts lower than 100 pence
        of(List.of(12, 24), 164),
        // roundup of exact amount is 0
        of(List.of(100), 0),
        // roundup of exact amount is 0
        of(List.of(1000), 0),
        // roundup of 0 is 0
        of(List.of(0), 0),
        // filter out negative amounts
        of(List.of(-1), 0),
        // filter out multiple negative amounts
        of(List.of(-1, 12, -2, 24, -8), 164),
        // amount higher than 100 pence
        of(List.of(112), 88),
        // multiple amounts higher than 100 pence
        of(List.of(112, 124), 164)
    );
  }

  @ParameterizedTest
  @DisplayName("should sum the remainder amount for all feed items")
  @MethodSource("roundupTestCaseInputProvider")
  void shouldRoundup(List<Integer> amounts, Integer result) {
    final List<TransactionFeedItem> feedItemList = amounts.stream()
        .map(amount -> TransactionFeedItem.builder().amount(Amount.builder().minorUnits(amount).build()).build())
        .collect(toList());

    final TransactionFeedItems feedItems = TransactionFeedItems.builder().feedItems(feedItemList).build();

    assertThat(roundup(feedItems).apply().getMinorUnits()).isEqualTo(result);
  }

}
