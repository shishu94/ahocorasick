package ahocorasick.count;

import java.util.List;

import ahocorasick.Accumulator;

public class CountAccumulator implements Accumulator<long[]> {
  @Override
  public long[] newInstance() {
    return new long[1];
  }

  @Override
  public void insert(List<String> words, int wordIndex, long[] accumulator) {
    accumulator[0]++;
  }

  @Override
  public void insert(long[] src, long[] target) {
    target[0] += src[0];
  }
}
