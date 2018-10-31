package ahocorasick;

import java.util.List;

@FunctionalInterface
public interface Matcher<T> {
  /**
   * @param accumulator
   *          the accumulator that contains all the words that here
   * @param matchEndIndex
   *          the index of the string that all the matches end
   */
  void match(List<String> words, T wordsAccumulator, int matchEndIndex);
}
