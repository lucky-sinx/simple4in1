package test.wordcountdemo;

import simple4in1.WareHouse;
import simple4in1.Worker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class WordCountWorker extends Worker {
    public WareHouse doTask(WareHouse inhouse) {
        System.out.println(Thread.currentThread());
        String filepath = (String) inhouse.get("filepath");
        BufferedReader in = null;
        HashMap<String, Integer> wordcount = new HashMap<String, Integer>();
        try {
            in = new BufferedReader(new FileReader(filepath));
            Scanner sc=new Scanner(in);
            while (sc.hasNextLine()) {
                String str=sc.nextLine();
                for (String s : str.split(" ")) {
                    String curword = s;
                    if (wordcount.containsKey(curword))
                        wordcount.put(curword, wordcount.get(curword) + 1);
                    else
                        wordcount.put(curword, 1);
                }
            }
        } catch (IOException e) {

        }
        System.out.println(wordcount);
        return new WareHouse("word", wordcount);
    }
    public static void main(String[] args) {
        WordCountWorker wk1 = new WordCountWorker();
        wk1.startWorker("192.168.44.1", 8001, "wordcount");
        WordCountWorker wk2 = new WordCountWorker();
        wk2.startWorker("192.168.44.1", 8002, "wordcount");
        WordCountWorker wk3 = new WordCountWorker();
        wk3.startWorker("192.168.44.1", 8003, "wordcount");
    }
}