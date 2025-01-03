import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class MultiThreadedFileAnalyzer {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String directoryPath = "Multi_threading_workshop_9"; // پوشه‌ای که فایل‌ها در آن قرار دارند

        File folder = new File(directoryPath);
        File[] files = folder.listFiles((dir,name) ->name.endsWith(".txt"));
        if (files == null || files.length == 0) {
            System.out.println("No .txt files found in the directory.");
            return;
        }

        ExecutorService executor = Executors.newFixedThreadPool(files.length);

        List<Future<FileAnalysisResult>> futures = new ArrayList<>();
        for (File file : files) {
            futures.add(executor.submit(() -> analyzeFile(file)));
        }

        Set<String> uniqueWords = new HashSet<>();
        String longestWord = "";
        String shortestWord = null;
        int totalWords = 0;
        int totalLength = 0;

        for (Future<FileAnalysisResult> future : futures) {
            FileAnalysisResult result = future.get();
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

        executor.shutdown();

        System.out.println("\nResults:");
        System.out.println("Total unique words: " + uniqueWords.size());
        System.out.println("Longest word: " + longestWord + " (Length: " + longestWord.length() + ")");
        System.out.println("Shortest word: " + shortestWord + " (Length: " + shortestWord.length() + ")");
        System.out.println("Average word length: " + (totalWords > 0 ? (double) totalLength / totalWords : 0));
    }

    private static FileAnalysisResult analyzeFile(File file) throws IOException {
        List<String> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] splitWords = line.split("\\s+");
                words.addAll(Arrays.asList(splitWords));
            }
        }

        Set<String> uniqueWords = new HashSet<>(words);
        String longestWord = words.stream().max(Comparator.comparingInt(String::length)).orElse("");
        String shortestWord = words.stream().min(Comparator.comparingInt(String::length)).orElse("");
        int totalLength = words.stream().mapToInt(String::length).sum();

        return new FileAnalysisResult(uniqueWords, words.size(), longestWord, shortestWord, totalLength);
    }
}

class FileAnalysisResult {
    private final Set<String> uniqueWords;
    private final int wordCount;
    private final String longestWord;
    private final String shortestWord;
    private final int totalWordLength;

    public FileAnalysisResult(Set<String> uniqueWords, int wordCount, String longestWord, String shortestWord, int totalWordLength) {
        this.uniqueWords = uniqueWords;
        this.wordCount = wordCount;
        this.longestWord = longestWord;
        this.shortestWord = shortestWord;
        this.totalWordLength = totalWordLength;
    }

    public Set<String> getUniqueWords() {
        return uniqueWords;
    }

    public int getWordCount() {
        return wordCount;
    }

    public String getLongestWord() {
        return longestWord;
    }

    public String getShortestWord() {
        return shortestWord;
    }

    public int getTotalWordLength() {
        return totalWordLength;
    }
}
