package Java_Threads;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Moamn Mahmoud
 */
public class WordStatistics implements Runnable {

    private File mainFolder;
    private String longestWordInDirectory = "";
    private String shortestWordInDirectory = "zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz";
    private List<File> innerFilesResult = new ArrayList<>();
    private DefaultTableModel DF;
    private int row = 0;
    
    public WordStatistics(File mainFolder,DefaultTableModel DF, boolean subFiles) {
        this.mainFolder = mainFolder;
        this.DF = DF;

        if (mainFolder.isDirectory() && mainFolder != null) {
            if (subFiles) {
                getFilesWithSubfolders(2, mainFolder);

            } else {
                getFiles();

            }
            DF.setRowCount(innerFilesResult.size());
        }
    }

    public void getFiles() {
        for (File f : this.mainFolder.listFiles()) {
            if (f.isFile()) {
                this.innerFilesResult.add(f);
            }
        }
    }

    public void getFilesWithSubfolders(int level, File folder) {
        File[] innerFiles = folder.listFiles();
        if (level < 0 || innerFiles == null) {
            return;
        }

        for (File f : innerFiles) {
            if (f.isFile() && isTxtFile(f)) {
                this.innerFilesResult.add(f);
            } else if (f.isDirectory()) {
                getFilesWithSubfolders(--level, f);
            }
        }
        return;
    }

    public File[] getInnerFilesResult() {
        return ((File[]) this.innerFilesResult.toArray(new File[this.innerFilesResult.size()]));
    }

    public boolean isTxtFile(File file) {
        String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length());
        return extension.equals("txt") ? true : false;
    }

    public int countFileWords(File file, int threadRow, int column ) {
        int wordCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                // Split the line into words using whitespace a
                String[] words = line.split("\\s+");
                wordCount += words.length;
                try {
                        Thread.sleep(250);
                } catch (InterruptedException ex) {
                    Logger.getLogger(WordStatistics.class.getName()).log(Level.SEVERE, null, ex);
                    }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        DF.setValueAt(wordCount, threadRow, column);
        return wordCount;
    }

    public String findLongestWord(File file,int threadRow,int column) {
        String longestWordInFile = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line into words using whitespace 
                String[] words = line.split("\\s+");
                for (String w : words) {
                    if (this.longestWordInDirectory == null) {
                        this.longestWordInDirectory = w;
                    }
                    if (w.length() > longestWordInFile.length()) {
                        longestWordInFile = w;
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(WordStatistics.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
            if (longestWordInFile.length() >= this.longestWordInDirectory.length()) {
                this.longestWordInDirectory = longestWordInFile;
            }
        } catch (IOException e) {
        }
        DF.setValueAt(longestWordInFile, threadRow, column);
        return longestWordInFile;

    }

    public String findShortestWord(File file, int threadRow, int column) {
        String shortestWordInFile = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line into words using whitespace
                String[] words = line.split("\\s+");
                for (String w : words) {
                    if (this.shortestWordInDirectory == null) {
                        this.shortestWordInDirectory = w;
                    }
                    if (shortestWordInFile.length() == 0) {
                        shortestWordInFile = w;
                    }
                    if (w.length() < shortestWordInFile.length()) {
                        shortestWordInFile = w;
                        try {
                            Thread.sleep(500);
                        }catch (InterruptedException ex) {
                            Logger.getLogger(WordStatistics.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
            if (shortestWordInFile.length() <= this.shortestWordInDirectory.length() && shortestWordInFile.length() != 0) {
                this.shortestWordInDirectory = shortestWordInFile;
            }
        } catch (IOException e) {
        }
        DF.setValueAt(shortestWordInFile, threadRow, column);
        return shortestWordInFile;
    }

    public int countWordOccurrences(File file, String word, int threadRow, int column) {
        int wordCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line into words using whitespace 
                String[] words = line.split("\\s+");
                for (String w : words) {
                    if (w.equalsIgnoreCase(word)) {
                        wordCount++;
                        try {
                            Thread.sleep(500);
                        }catch (InterruptedException ex) {
                            Logger.getLogger(WordStatistics.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                }
            }
        } catch (IOException e) {
        }
        DF.setValueAt(wordCount, threadRow, column);
        return wordCount;
    }
    
    public String getShortestWordInDirectory () {
        return this.shortestWordInDirectory;
    }
    
    public String getLongestWordInDirectory () {
        return this.longestWordInDirectory;
    }
    public synchronized File pop(){
        if(!this.innerFilesResult.isEmpty()) 
            return this.innerFilesResult.remove(0);
        return null;
    }
    private synchronized int rowNumber(){
        row++;
        return row-1;
    }
            
    @Override
    public void run() {
        while(!this.innerFilesResult.isEmpty()){
            int threadRow = rowNumber();
            File threadFile = pop();
            DF.setValueAt(threadFile.getName(), threadRow, 0);
            countFileWords(threadFile, threadRow, 1);
            countWordOccurrences(threadFile,"is",threadRow,2);
            countWordOccurrences(threadFile,"are",threadRow,3);
            countWordOccurrences(threadFile,"you",threadRow,4);
            findLongestWord(threadFile,threadRow,5);
            findShortestWord(threadFile,threadRow,6);
        }
    }

}
