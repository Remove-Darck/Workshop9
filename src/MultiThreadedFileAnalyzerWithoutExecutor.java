import fileanalys.FileAnalysisResult;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MultiThreadedFileAnalyzerWithoutExecutor {
    public static void main(String[] args) throws InterruptedException {
        String directoryPath = "Multi_threading_workshop_9";

        File folder = new File(directoryPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files == null || files.length == 0) {
            System.out.println("No .txt files found in the directory.");
            return;
        }

        List<Thread> threads = new ArrayList<>();
        List<FileAnalysisResult> results = Collections.synchronizedList(new ArrayList<>());

        for (File file : files) {
            Thread thread = new Thread(() -> {
                try {
                    results.add(analyzeFile(file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // جمع‌بندی نتایج مشابه روش قبل
        Set<String> uniqueWords = new HashSet<>();
        String longestWord = "";
        String shortestWord = null;
        int totalWords = 0;
        int totalLength = 0;

        for (FileAnalysisResult result : results) {
            uniqueWords.addAll(result.getUniqueWords());
            totalWords += result.getWordCount();
            totalLength += result.getTotalWordLength();

            if (result.getLongestWord().length() > longestWord.length()) {
                longestWord = result.getLongestWord();
            }
            if (shortestWord == null || result.getShortestWord().length() < shortestWord.length()) {
                shortestWord = result.getShortestWord();
            }
        }

        System.out.println("\nResults:");
        System.out.println("Total unique words: " + uniqueWords.size());
        System.out.println("Longest word: " + longestWord + " (Length: " + longestWord.length() + ")");
        System.out.println("Shortest word: " + shortestWord + " (Length: " + shortestWord.length() + ")");
        System.out.println("Average word length: " + (totalWords > 0 ? (double) totalLength / totalWords : 0));
    }

    private static FileAnalysisResult analyzeFile(File file) throws IOException {
        // مشابه متد قبل
        return new FileAnalysisResult(new HashSet<>(), 0, "", "", 0);
    }
}
