package test.wordcountdemo;

import simple4in1.WareHouse;
import simple4in1.Worker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class WordCountWorker {
    public static void main(String[] args) {
        WordCountWorkerLocal wk1 = new WordCountWorkerLocal();
        wk1.startWorker("192.168.44.1", 8001, "wordcount");
        WordCountWorkerLocal wk2 = new WordCountWorkerLocal();
        wk1.startWorker("192.168.44.1", 8002, "wordcount");
        WordCountWorkerLocal wk3 = new WordCountWorkerLocal();
        wk1.startWorker("192.168.44.1", 8003, "wordcount");
    }
}

class WordCountWorkerLocal extends Worker {
    public WareHouse doTask(WareHouse inhouse) {
        System.out.println(Thread.currentThread());
        String filepath = (String) inhouse.get("filepath");
        BufferedReader in = null;
        String str;
        HashMap<String, Integer> wordcount = new HashMap<String, Integer>();
        try {
            in = new BufferedReader(new FileReader(filepath));
            while ((str = in.readLine()) != null) {
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
}