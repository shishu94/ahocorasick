package ahocorasick;

public interface ParallelMatcher<E extends Matcher<?>> {
  /**
   * @return a single matcher that will be used non concurrently
   */
  E threadMatcher();

  /**
   * Merge the two results. May use any one of the incoming instance or a new one to contain the final result.
   * 
   * @param m0
   * @param m1
   * @return the merged container
   */
  E merge(E m0, E m1);
}
