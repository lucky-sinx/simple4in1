package test.wordcountdemo;

import simple4in1.Contractor;
import simple4in1.WareHouse;
import simple4in1.WorkerLocal;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class WordCountContractor extends Contractor {
    public static void main(String[] args)  throws RemoteException {
        WordCountContractor contractor = new WordCountContractor();
        long begin = (new Date()).getTime();
        String dataPath = "./src/test/wordcountdemo/";
        WareHouse result = contractor.giveTask(new WareHouse("filepath", new String[]{
                new String(dataPath + "data1.txt"),
                new String(dataPath + "data2.txt"),
                new String(dataPath + "data3.txt"),
        }));//"D:\\demo\\parallel\\a\\three.txt"
        long end = (new Date()).getTime();
        System.out.println("time:" + (end - begin) / 1000 + "s");
        System.out.println("result:" + result);
        System.exit(0);
    }
    @Override
    public WareHouse giveTask(WareHouse wareHouse) throws RemoteException {
        String[] filepath = (String[]) wareHouse.get("filepath");
        WorkerLocal[] wks = getWaitingWorkers("wordcount");
        WareHouse[] hmarr = new WareHouse[3];
        HashMap<String, Integer> wordcount = new HashMap<String, Integer>();
        for (int j = 0; j < 3; ) {
            for (int i = 0; i < 3; i++) {
                if (hmarr[i] == null) {
                    hmarr[i] = wks[i].doTask(new WareHouse("filepath", filepath[i]));
                } else if (hmarr[i].isReady() && hmarr[i].isMark()) {
                    hmarr[i].setMark(false);
                    j++;
                }
            }
        }
        for (int i = 0; i < 3; i++) {
            HashMap<String, Integer> wordhm = (HashMap<String, Integer>) hmarr[i].get("word");
            for (Iterator<String> iter = wordhm.keySet().iterator(); iter.hasNext(); ) {
                String curword = iter.next();
                if (wordcount.containsKey(curword))
                    wordcount.put(curword, wordcount.get(curword) + wordhm.get(curword));
                else
                    wordcount.put(curword, wordhm.get(curword));
            }
        }
//        System.out.println(wordcount);
        return new WareHouse("word", wordcount);
    }

//    public static void main(String[] args) {
//        MyCT3 a = new MyCT3();
//        long begin = (new Date()).getTime();
//        WareHouse result = a.giveTask(new WareHouse("filepath", new String[]{
//                new String("D:\\code\\java\\fourinone-2.05.28\\fourinone-2.05.28\\指南和demo\\WordCount\\data1.txt"),
//                new String("D:\\code\\java\\fourinone-2.05.28\\fourinone-2.05.28\\指南和demo\\WordCount\\data2.txt"),
//                new String("D:\\code\\java\\fourinone-2.05.28\\fourinone-2.05.28\\指南和demo\\WordCount\\data3.txt"),
//        }));//"D:\\demo\\parallel\\a\\three.txt"
//        long end = (new Date()).getTime();
//        System.out.println("time:" + (end - begin) / 1000 + "s");
//        System.out.println("result:" + result);
//        System.exit(0);
//    }
}
