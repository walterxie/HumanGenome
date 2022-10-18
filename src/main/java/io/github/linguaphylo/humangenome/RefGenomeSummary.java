package io.github.linguaphylo.humangenome;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Nucleotide Frequency of Human Reference Genome
 *
 * @author Walter Xie
 */
public class RefGenomeSummary {

    final static String fileNm = System.getProperty("user.home") +
            "/WorkSpace/HumanGenome/data/GRCh38_latest_genomic.fna";

    static int[][] counts; // columns A C G T N -?
    final static int N_COL = 6;
    static String[] names;

    public RefGenomeSummary() {
        final int N = 25;
        counts = new int[N][N_COL];
        names = new String[N];
    }

    private static void countNucleotideFrequency(int[] freqs, String line) {
        assert freqs.length == N_COL;

        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == 'A' || line.charAt(i) == 'a') {
                freqs[0] += 1;
            } else if (line.charAt(i) == 'C' || line.charAt(i) == 'c') {
                freqs[1] += 1;
            } else if (line.charAt(i) == 'G' || line.charAt(i) == 'g') {
                freqs[2] += 1;
            } else if (line.charAt(i) == 'T' || line.charAt(i) == 't') {
                freqs[3] += 1;
            } else if (line.charAt(i) == 'N' || line.charAt(i) == 'n') {
                freqs[4] += 1;
            } else {
                freqs[5] += 1;
                // Y R B W ...
//                System.out.println("char " + i + " = " + line.charAt(i));
            }
        }

    }

    private static boolean isLabel(String line) {
        return line.startsWith(">");
    }

    private static void writeSummary() throws IOException {
        PrintWriter printWriter = new PrintWriter(
                new FileWriter("HumanRefNucFreqs.txt"));

        printWriter.println("Label\tA\tC\tG\tT\tN\t?");

        for (int i = 0; i < counts.length; i++) {
            if (i < names.length)
                printWriter.print(names[i]);
//            else
//                printWriter.print("Total length");
            for (int j = 0; j < counts[0].length; j++) {
                printWriter.print("\t" + counts[i][j]);
            }
            printWriter.println();
        }
        printWriter.close();
    }

    public static void main(String[] args) throws IOException {

        RefGenomeSummary refGenomeSummary = new RefGenomeSummary();

        final BufferedReader reader = Files.newBufferedReader(Path.of(fileNm));
        String line;
        int seq = -1; // ++ first
        boolean save = false;
        while ((line = reader.readLine()) != null) {
            if (isLabel(line)) {
                // only take Primary Assembly
                if (line.contains("NC_0000")) {
                    save = true;
                    seq++;
                    names[seq] = line.substring(1);
                    System.out.println("Counting " + names[seq]);
                } else if (line.contains("NT_187395.1")) {
                    break; // just after NC_000024.10 Homo sapiens chromosome Y, GRCh38.p14 Primary Assembly
                } else {
                    save = false;
                }
            } else if (save) {
                countNucleotideFrequency(counts[seq], line);
            }

        }
        reader.close();

        System.out.println(Arrays.deepToString(counts));

        writeSummary();

    }

}
