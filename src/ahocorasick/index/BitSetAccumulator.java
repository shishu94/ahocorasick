package ahocorasick.index;

import java.util.BitSet;
import java.util.List;

import ahocorasick.Accumulator;

public class BitSetAccumulator implements Accumulator<BitSet> {
  @Override
  public BitSet newInstance() {
    return new BitSet();
  }

  @Override
  public void insert(List<String> words, int wordIndex, BitSet accumulator) {
    accumulator.set(wordIndex);
  }

  @Override
  public void insert(BitSet src, BitSet target) {
    target.or(src);
  }
}
