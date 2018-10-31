package ahocorasick.count;

import java.util.List;

import ahocorasick.Matcher;

public class CountMatcher implements Matcher<long[]> {
  private long count = 0;

  @Override
  public void match(List<String> words, long[] wordsAccumulator, int matchEndIndex) {
    count += wordsAccumulator[0];
  }

  public void addCount(long count) {
    this.count += count;
  }

  public long getCount() {
    return count;
  }
}
