package test;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import ahocorasick.AhoCorasickAutomaton;
import ahocorasick.count.CountAccumulator;
import ahocorasick.count.CountMatcher;
import ahocorasick.count.CountParallelMatcher;

public class TestAhoCorasick {
  public static String gen(int size) {
    return ThreadLocalRandom.current().ints(0, 36).mapToObj(i -> Integer.toString(i, 36)).limit(size).collect(Collectors.joining());
  }

  public static void main(String[] args) {
    int size = 1000000000;
    int wordsCount = 50000;
    int sizeMin = 3;
    int sizeMax = 15;

    String text = gen(size);
    List<String> words = ThreadLocalRandom.current().ints(sizeMin, sizeMax + 1).mapToObj(i -> gen(i)).limit(wordsCount).collect(Collectors.toList());

    System.out.println("Word ready");

    for (int i = 0; i < 5; i++) {
      Timing.time("Par 4", () -> computeEntriesParallel(text, words, 4));
      Timing.time("Par 8", () -> computeEntriesParallel(text, words, 8));
      Timing.time("Par 16", () -> computeEntriesParallel(text, words, 16));
      Timing.time("Par 32", () -> computeEntriesParallel(text, words, 32));
      Timing.time("Seq", () -> computeEntries(text, words));
    }

  }

  private static long computeEntries(String text, List<String> words) {
    AhoCorasickAutomaton<long[]> automaton2 = new AhoCorasickAutomaton<>(words, new CountAccumulator());
    return automaton2.searchWords(text, new CountMatcher()).getCount();
  }

  private static long computeEntriesParallel(String text, List<String> words, int threads) {
    AhoCorasickAutomaton<long[]> automaton2 = new AhoCorasickAutomaton<>(words, new CountAccumulator());
    return automaton2.searchWordsParallel(text, threads, new CountParallelMatcher()).getCount();
  }
}
