package ahocorasick.count;

import ahocorasick.ParallelMatcher;

public class CountParallelMatcher implements ParallelMatcher<CountMatcher> {
  @Override
  public CountMatcher threadMatcher() {
    return new CountMatcher();
  }

  @Override
  public CountMatcher merge(CountMatcher m0, CountMatcher m1) {
    m0.addCount(m1.getCount());
    return m0;
  }
}