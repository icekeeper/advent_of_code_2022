import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Day01 {
    public static void main(String[] args) throws IOException {
        var input = readFile("input/day01.txt");
        List<Integer> counts = new ArrayList<>();
        int sum = 0;
        for (String line : input) {
            if (line.isEmpty()) {
                counts.add(sum);
                sum = 0;
            } else {
                sum += Integer.parseInt(line);
            }
        }
        counts.add(sum);
        part1(counts);
        part2(counts);
    }

    private static void part2(List<Integer> counts) {
        counts.sort(Collections.reverseOrder());
        System.out.println(counts.stream().limit(3).mapToInt(x -> x).sum());
    }

    private static void part1(List<Integer> counts) {
        System.out.println(counts.stream().mapToInt(x -> x).max().getAsInt());
    }

    private static List<String> readFile(String filePath) throws IOException {
        try (var bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)))) {
            return bufferedReader.lines().collect(Collectors.toList());
        }
    }
}
