package ahocorasick.index;

import java.util.ArrayList;
import java.util.List;

import ahocorasick.Accumulator;

public class IntListAccumulator implements Accumulator<List<Integer>> {
  @Override
  public List<Integer> newInstance() {
    return new ArrayList<>();
  }

  @Override
  public void insert(List<String> words, int wordIndex, List<Integer> accumulator) {
    accumulator.add(wordIndex);
  }

  @Override
  public void insert(List<Integer> src, List<Integer> target) {
    target.addAll(src);
  }
}