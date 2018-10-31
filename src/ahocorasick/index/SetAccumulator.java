package ahocorasick.index;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ahocorasick.Accumulator;

public class SetAccumulator implements Accumulator<Set<Integer>> {
  @Override
  public Set<Integer> newInstance() {
    return new HashSet<>();
  }

  @Override
  public void insert(List<String> words, int wordIndex, Set<Integer> accumulator) {
    accumulator.add(wordIndex);
  }

  @Override
  public void insert(Set<Integer> src, Set<Integer> target) {
    target.addAll(src);
  }
}
