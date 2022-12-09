import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Day02 {
    public static void main(String[] args) throws IOException {
        var input = readFile("input/day02.txt");
        part1(input);
        part2(input);
    }

    private static void part1(List<String> input) {
        int result = input.stream().mapToInt(line -> play(line.charAt(0) - 'A', line.charAt(2) - 'X') +
                (line.charAt(2) - 'X' + 1)).sum();
        System.out.println(result);
    }

    private static void part2(List<String> input) {
        int result = input.stream().mapToInt(line -> play2(line.charAt(0) - 'A', line.charAt(2)) +
                (line.charAt(2) - 'X') * 3).sum();
        System.out.println(result);
    }

    static int play(int p1, int p2) {
        if (p1 == p2) {
            return 3;
        } else if ((p1 + 1) % 3 == p2) {
            return 6;
        } else {
            return 0;
        }
    }

    static int play2(int p1, char c) {
        return switch (c) {
            case 'X' -> (p1 + 2) % 3;
            case 'Y' -> p1;
            default -> (p1 + 1) % 3;
        } + 1;
    }

    private static List<String> readFile(String filePath) throws IOException {
        try (var bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)))) {
            return bufferedReader.lines().collect(Collectors.toList());
        }
    }
}
