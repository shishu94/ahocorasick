package ahocorasick;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.stream.IntStream;

/**
 * @author Dos Santos Leonel
 * @param <T>
 *          accumulator for matching states
 */
public class AhoCorasickAutomaton<T> {
  private final List<String> words;
  private static final int maxChars = 256;
  private int maxSize;

  private int minChar = Integer.MAX_VALUE;
  private int maxChar = 0;

  private T[] out;
  private int[][] g;
  private int[] f;

  private final Accumulator<T> acc;

  public AhoCorasickAutomaton(List<String> wordsToFind, Accumulator<T> acc) {
    this.words = Collections.unmodifiableList(new ArrayList<>(wordsToFind));
    this.acc = acc;

    buildAutomaton();
  }

  /**
   * Perform the matching in a fully sequential way
   * 
   * @param text
   * @param matchConsumer
   */
  public <E extends Matcher<T>> E searchWords(String text, E matchConsumer) {
    // Initialize current state
    int currentState = 0;

    // Traverse the text through the built machine to find all the matchings
    for (int i = 0; i < text.length(); ++i) {
      currentState = findNextState(currentState, text.charAt(i));
      matchConsumer.match(words, out[currentState], i);
    }
    return matchConsumer;
  }

  /**
   * Perform the matching in parallel over chunks of the original string.<br>
   * The implementation takes care of the overlaps between chunks <br>
   * 
   * @param text
   * @param threads
   *          if threads is < 1 we run the
   * @param matchConsumer
   */
  public <E extends Matcher<T>> E searchWordsParallel(String text, int threads, ParallelMatcher<E> matchConsumer) {
    if (threads < 1) return searchWords(text, matchConsumer.threadMatcher());

    int slice = (text.length() / threads) + 1;
    return IntStream.range(0, threads)
                    .parallel()
                    //perform the matching in this thread
                    .mapToObj(t -> {
                      E matcher = matchConsumer.threadMatcher();
                      int start = t * slice;
                      int end = Math.min((t + 1) * slice, text.length());
                      // Initialize current state
                      int currentState = 0;
                      // Traverse the text through the built machine to find all the
                      // matchings

                      // each thread has the responsibility of counting occurrences in his
                      // range of ends,
                      // but it may need to initialize its internal start before
                      for (int i = Math.max(0, start - maxSize); i < start; i++) {
                        currentState = findNextState(currentState, text.charAt(i));
                      }

                      // then we procede to our part
                      for (int i = start; i < end; ++i) {
                        currentState = findNextState(currentState, text.charAt(i));
                        matcher.match(words, out[currentState], i);
                      }
                      return matcher;
                    })
                    //reduce the result to a single consumer
                    .reduce(matchConsumer::merge)
                    //create a default in cas of no thread, should not happen. 
                    .orElseGet(matchConsumer::threadMatcher);
  }

  // Returns the next state the machine will transition to using goto
  // and failure functions.
  // currentState - The current state of the machine. Must be between
  // 0 and the number of states - 1, inclusive.
  // nextInput - The next character that enters into the machine.
  private int findNextState(int currentState, char ch) {
    // If goto is not defined, use failure function
    if (g[currentState][ch] == -1) {
      int answer = f[currentState];
      while (g[answer][ch] == -1)
        answer = f[answer];
      g[currentState][ch] = g[answer][ch];
    }

    return g[currentState][ch];// in parallel this may be concurrent, but since any
    // thread must compute the same value, it does not
    // matter
  }

  @SuppressWarnings("unchecked")
  private void buildAutomaton() {
    int states = 1;

    {
      List<T> out = new ArrayList<>();
      List<int[]> g = new ArrayList<>();
      List<Integer> f = new ArrayList<>();

      out.add(acc.newInstance());
      g.add(buildCharArray());

      f.add(-1);

      // build the regular trie
      for (int w = 0; w < words.size(); w++) {
        String word = words.get(w);
        maxSize = Math.max(maxSize, word.length());
        int currentState = 0;
        for (int i = 0; i < word.length(); i++) {
          int ch = word.charAt(i);
          minChar = Math.min(minChar, ch);
          maxChar = Math.max(maxChar, ch);
          if (g.get(currentState)[ch] == -1) {
            g.get(currentState)[ch] = states;
            out.add(acc.newInstance());
            g.add(buildCharArray());
            f.add(-1);
            states++;
          }

          currentState = g.get(currentState)[ch];
        }

        acc.insert(words, w, out.get(currentState));
      }
      this.out = (T[]) out.toArray();
      this.f = f.stream().mapToInt(Integer::intValue).toArray();
      this.g = g.toArray(new int[0][]);
    }

    // For all characters which don't have an edge from
    // root (or state 0) in Trie, add a goto edge to state
    // 0 itself
    for (int ch = 0; ch < maxChars; ++ch)
      if (g[0][ch] == -1)
        g[0][ch] = 0;

    // build failure
    Queue<Integer> q = new ArrayDeque<>();

    // Iterate over every possible input
    for (int ch = minChar; ch < maxChar + 1; ++ch) {
      if (g[0][ch] != 0) {
        f[g[0][ch]] = 0;
        q.add(g[0][ch]);
      }
    }

    while (!q.isEmpty()) {
      // Remove the front state from queue
      int state = q.poll();

      // For the removed state, find failure function for
      // all those characters for which goto function is
      // not defined.
      for (int ch = minChar; ch < maxChar + 1; ++ch) {
        // If goto function is defined for character 'ch'
        // and 'state'
        if (g[state][ch] != -1) {
          // Find failure state of removed state
          int failure = f[state];

          // Find the deepest node labeled by proper
          // suffix of string from root to current
          // state.
          while (g[failure][ch] == -1)
            failure = f[failure];

          int compress = f[state];
          while (g[compress][ch] == -1) {
            g[compress][ch] = failure;
            compress = f[compress];
          }

          failure = g[failure][ch];
          f[g[state][ch]] = failure;

          // Merge output values
          acc.insert(out[failure], out[g[state][ch]]);

          // Insert the next level node (of Trie) in Queue
          q.add(g[state][ch]);
        }
      }
    }
  }

  //  private int[][] transpose(List<int[]> matrix) {
  //    int[][] ng = new int[maxChars][matrix.size()];
  //    for (int i = 0; i < matrix.size(); i++) {
  //      int[] ar = matrix.get(i);
  //      for (int j = 0; j < maxChars; j++) {
  //        ng[j][i] = ar[j];
  //      }
  //    }
  //    return ng;
  //  }

  private static int[] buildCharArray() {
    int[] tos = new int[maxChars];
    Arrays.fill(tos, -1);
    return tos;
  }

}
