package com.homework;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

  public static void main(String[] args) throws InterruptedException, ExecutionException {
    String[] texts = new String[25];
    final ExecutorService threadPool = Executors.newFixedThreadPool(25);
    ArrayList<Callable<Integer>> threads = new ArrayList();
    for (int i = 0; i < texts.length; i++) {
      texts[i] = generateText("aab", 30_000);
    }

    long startTs = System.currentTimeMillis(); // start time
    for (String text : texts) {
      Callable<Integer> thread = () -> {
        int maxSize = 0;
        for (int i = 0; i < text.length(); i++) {
          for (int j = 0; j < text.length(); j++) {
            if (i >= j) {
              continue;
            }
            boolean bFound = false;
            for (int k = i; k < j; k++) {
              if (text.charAt(k) == 'b') {
                bFound = true;
                break;
              }
            }
            if (!bFound && maxSize < j - i) {
              maxSize = j - i;
            }
          }
        }
        System.out.println(text.substring(0, 100) + " -> " + maxSize);
        return maxSize;
      };
      threads.add(thread);
    }

    final List<Future<Integer>> tasksFuture = threadPool.invokeAll(threads);
    ArrayList<Integer> results = new ArrayList<>();
    for (Future<Integer> future : tasksFuture) {
      results.add(future.get());
    }
    threadPool.shutdown();
    long endTs = System.currentTimeMillis(); // end time
    System.out.println("Максимальный интервал " + results.stream().max(Integer::compare).get());
    System.out.println("Time: " + (endTs - startTs) + "ms");
  }

  public static String generateText(String letters, int length) {
    Random random = new Random();
    StringBuilder text = new StringBuilder();
    for (int i = 0; i < length; i++) {
      text.append(letters.charAt(random.nextInt(letters.length())));
    }
    return text.toString();
  }
}
