package StringTools;

import Algorithm.token;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrTools {
    //Нарезает строку по пробелам. последовательные пробелы рассматриваются как 1 пробел
    public static ArrayList<String> sliceString(String tail, int max){
        ArrayList<String> out = new ArrayList<>();
        if(tail.length() <= max){
            out.add(tail);
            return out;
        }
        
        tail = tail.trim();
        ArrayList<String> toks = new ArrayList<>();
        Pattern p1 = Pattern.compile("\\S+");
        Matcher match = p1.matcher(tail);
        int s;
        int e;
        while (match.find()){
            s = match.start();
            e = match.end();
            toks.add(tail.substring(s, e));
            //System.out.println(toks.get(toks.size()-1));//----------------- контрольный вывод
        }
        String tmp = "";
        for(int i = 0; i < toks.size(); i++){
            if(tmp.length() + toks.get(i).length() <= max){
                if(tmp.isEmpty()) tmp = toks.get(i);
                else tmp += " " + toks.get(i);
            }else{
                if(tmp.length()>0){
                    out.add(tmp);
                    if(toks.get(i).length() <= max) tmp = toks.get(i);
                    else{
                        out.add(toks.get(i).substring(0, max - 1)+"-");
                        tmp = toks.get(i).substring(max-1);
                    }
                }else{
                    out.add(toks.get(i).substring(0, max - 1)+"-");
                    tmp = toks.get(i).substring(max-1);
                }
            }
        }
        out.add(tmp);
        return out;
    }
    //Преобразует строку в список токенов языков программирования.
    public static ArrayList<token> strToTokArr(String tail){
        tail = tail.trim();
        tail = tail.replaceAll("\\[\\s+", "[");
        tail = tail.replaceAll("\\s+\\]", "]");

        ArrayList<token> quots = new ArrayList<>();
        strSplit(tail, "\"", quots);
        if(quots.size()%2 != 0){
            System.out.print("(Неправильно расставлены кавычки");
            return null;
        }
        ArrayList<token> toks = new ArrayList<>();
        if(quots.isEmpty()){
            strSplitForST(tail, toks,0);
        }else{
            int start = 0;
            for(int i=0; i< quots.size()/2;i++){
                int end = quots.get(i*2).start;
                String tail2 = tail.substring(start, end);
                strSplitForST(tail2, toks, start);
                start = quots.get(i*2+1).start + 1;
                toks.add(new token(end, tail.substring(end+1, start-1), 1000));
            }
            if(start <= tail.length()-1){
                String tail3 = tail.substring(start, tail.length()-1);
                strSplitForST(tail3, toks, start);
            }
        }

        Collections.sort(toks);

        return toks;
    }
    public static ArrayList<token> strToTokXML(String tail){
        ArrayList<token> quots = new ArrayList<>();
        strSplit(tail, "\"", quots);
        if(quots.size()%2 != 0){
            System.out.print("(Неправильно расставлены кавычки");
            return null;
        }
        ArrayList<token> toks = new ArrayList<>();
        if(quots.isEmpty()){
            strSplitForXML(tail, toks,0);
        }else{
            int start = 0;
            for(int i=0; i< quots.size()/2;i++){
                int end = quots.get(i*2).start;
                String tail2 = tail.substring(start, end);
                strSplitForXML(tail2, toks, start);
                start = quots.get(i*2+1).start + 1;
                toks.add(new token(end, tail.substring(end+1, start-1), 1000));
            }
            if(start < tail.length()){
                String tail3 = tail.substring(start);
                strSplitForXML(tail3, toks, start);
            }
        }
        Collections.sort(toks);
        //System.out.println("");//------------------------ контрольный вывод
        return toks;
    }
    //Нарезает строки на токены согласно правилам языка ST и ему подобных
    static void strSplitForST(String tail, ArrayList<token> toks, int start){
            if(tail.isEmpty()) return;
            strSplit(tail, "[\\wА-Яа-яЁё\\.\\[\\]]+", toks, start);
            strSplit(tail, "[\\+\\*\\^\\-!:/<>=%,]+", toks, start);
            strSplit(tail, "[\\(\\)]", toks, start);
    }
    static void strSplitForXML(String tail, ArrayList<token> toks, int start){
            if(tail.isEmpty()) return;
            strSplit(tail, "[\\wА-Яа-яЁё\\.:\\-]+", toks, start);
            strSplit(tail, "[\\+\\*\\^/=%,]+", toks, start);
            strSplit(tail, "[\\(\\)\\[\\]\\?!<>]", toks, start);
    }
    //Нарезает строеу на токены согласно регулярному выражению
    public static void strSplit(String inStr, String pattern, ArrayList<token> toks){
        strSplit(inStr, pattern, toks, 0);
    }
    //Нарезает строеу на токены согласно регулярному выражению и даёт им смещение относительно переменной start
    public static void strSplit(String inStr, String pattern, ArrayList<token> toks, int start){
        Pattern p1 = Pattern.compile(pattern);
        //System.out.println(inStr);//------------------------ контрольный вывод
        Matcher match = p1.matcher(inStr);
        while (match.find()){
            int s = match.start();
            int e = match.end();
            toks.add(new token(s+start, inStr.substring(s, e).trim()));
            //System.out.println(toks.get(toks.size()-1).start + ") " + toks.get(toks.size()-1).tok);//----------------- контрольный вывод
        }
    }
    public static int searchSequence(ArrayList<token> toks, ArrayList<token> srch){
        return searchSequence(toks, srch, 0, -1);
    }
    public static int searchSequence(ArrayList<token> toks, ArrayList<token> srch, int start){
        return searchSequence(toks, srch, start, -1);
    }
    //Ищет совпадение последовательностей токенов без различия регистра
    public static int searchSequence(ArrayList<token> toks, ArrayList<token> srch, int start, int end){
        int x = 0;
        int qt = toks.size();
        if(end > 0 && end+1 < qt) qt = end+1;
        int qs = srch.size();
        if(qs > qt) return -1;
        int i;
        for(i = start; i < qt-qs; i++){
            int j;
            for(j = 0; j < qs; j++) if(!toks.get(i+j).tok.equalsIgnoreCase(srch.get(j).tok)) break;
            if(j==qs) break;
        }
        if(i < qt-qs) return i;
        return -1;
    }
}