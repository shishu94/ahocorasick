package ahocorasick;

import java.util.List;

public interface Accumulator<T> {
  /**
   * @return a new instance of an accumulator
   */
  T newInstance();

  /**
   * trigger a insertion of the word (given by index in the list) in the accumulator
   * 
   * @param words
   * @param wordIndex
   * @param accumulator
   */
  void insert(List<String> words, int wordIndex, T accumulator);

  /**
   * Add the content of src into dest
   * 
   * @param src
   * @param target
   */
  void insert(T src, T dest);
}
