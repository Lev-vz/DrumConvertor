package XMLTools;

import Algorithm.token;
import StringTools.StrTools;
import fileTools.FileManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*@author lev*/
public final class nodeXML implements Comparable<nodeXML> {
    String tagName = "";
    Map<String, String> attr;
    ArrayList<xmlContent> content;
    String specSimbol = "";
    boolean autoClose = false;
    //String nonProcData = "";
    //ArrayList<token> tail;
    //int tailInd=0;
    public nodeXML(FileManager fm) throws IOException{
        attr = new HashMap();
        content = new ArrayList();
        while(!fm.EOF){
            String tag = fm.rdXmlTag();
            if(fm.EOF) break;
            ArrayList<token> t = StrTools.strToTokXML(tag);
            if(t.isEmpty()) break;
            if("<".equals(t.get(0).tok)) t.remove(0);
            nodeContent nc = new nodeContent(fm,t,tag);
            content.add(nc);
            //int x = readNodeXML(fm, t, tag);
        }
    }
    public nodeXML(FileManager fm, ArrayList<token> t, String text) throws IOException{
        attr = new HashMap();
        content = new ArrayList();
        int x = readNodeXML(fm, t, text);
    }
    public int readNodeXML(FileManager fm, ArrayList<token> t, String text) throws IOException{
        if("?".equals(t.get(0).tok)){
            if(!"?".equals(t.get(t.size()-1).tok) || t.size()<3) return -1;
            specSimbol = "?";
            autoClose = true;
            t.remove(t.size()-1);
            t.remove(0);
            tagName = t.get(0).tok;
            t.remove(0);
            int x = readAttributes(t);
            return x;
        }else if("!".equals(t.get(0).tok)){
            if(t.size()>1 && "DOCTYPE".equals(t.get(1).tok)){
                specSimbol = "!";
                tagName = "DOCTYPE";
                int x = readDOCTYPE(t, fm);
                return x;
            }else if(t.size()>2 && "CDATA".equals(t.get(2).tok)){
                specSimbol = "!";
                tagName = "CDATA";
                int x = readCDATA(t, fm);                
                return x;
            }else if(t.size()>1 && "--".equals(t.get(2).tok.substring(0, 2))){
                specSimbol = "!";
                tagName = "Comment";
                int x = readComment(t, fm, text);                
                return x;
            }else return -2;
        }
        tagName = t.get(0).tok;
        if("/".equals(t.get(t.size()-1).tok)){
            autoClose = true;
            t.remove(t.size()-1);
        }
        t.remove(0);
        int x = readAttributes(t);
        if(x != 0) return x;
        x = readContent(fm);
        return x;
    }
    int readContent(FileManager fm) throws IOException{
        while(!fm.EOF){
            String cntnt = fm.rdXmlTagContent().trim();
            if(!cntnt.isEmpty()) content.add(new textContent(cntnt));
            String nextTag = fm.rdXmlTag();
            ArrayList<token> t = StrTools.strToTokXML(nextTag);
            if(t.size()>1 && "/".equals(t.get(0).tok)){
                if(t.size()==2 && tagName.equals(t.get(1).tok)) return 0;
                return -100;
            }else{
                nodeContent nc = new nodeContent(fm,t,nextTag);
                content.add(nc);
            }
        }
        return 0;
    }
    public nodeXML(String Name, boolean ac){
        //this.tail = new ArrayList<>();
        tagName = Name;
        //autoClose = ac;
        content = new ArrayList<>();
        attr = new HashMap<>();
    }
    public nodeXML(String Name, boolean ac, ArrayList<String[]> attrList){
        //this.tail = new ArrayList<>();
        tagName = Name;
        //autoClose = ac;
        content = new ArrayList<>();
        attr = new HashMap<>();
        attrList.forEach((al) -> {attr.put(al[0], al[1]);});
    }
    @Override
    public int compareTo(nodeXML nx) {
        return this.tagName.compareTo(nx.tagName);
    }   
    int readAttributes(ArrayList<token> t){
        for(int i = 0; i*3 < t.size()-2; i++){
            if(!"=".equals(t.get(i*3+1).tok)) return -1;
            attr.put(t.get(i*3).tok, t.get(i*3+2).tok);
        }
        return 0;
    }
    //Ещё не сделано
    int readDOCTYPE(ArrayList<token> t, FileManager fm){
        for(int i = 0; i*3 < t.size()-1; i++){
            if(!"=".equals(t.get(i*3+2).tok)) return -1;
            attr.put(t.get(i*3+1).tok, t.get(i*3+3).tok);
        }
        return 0;
    }
    //Ещё не сделано
    int readCDATA(ArrayList<token> t, FileManager fm){
        for(int i = 0; i*3 < t.size()-1; i++){
            if(!"=".equals(t.get(i*3+2).tok)) return -1;
            attr.put(t.get(i*3+1).tok, t.get(i*3+3).tok);
        }
        return 0;
    }
    int readComment(ArrayList<token> t, FileManager fm, String text){
        String tmp = text;
        while(!"--".equals(tmp.substring(text.length()-2)) && !fm.EOF){
            tmp += fm.rdXmlTag();
        }
        content.add(new textContent(tmp));
        return 0;
    }  
    public void fprintSelf(FileManager fm, String pref) throws IOException{
        boolean in1str = false;
        String dopPref = "\t";
        if(tagName.isEmpty()){
            dopPref = "";
            //tmp = "";
        }else{
            fm.wr(pref + "<" + specSimbol + tagName);
            for(String key : attr.keySet()) fm.wr(" " + key + "=\"" + attr.get(key) + "\"");
            if("?".equals(specSimbol)) fm.wr("?");
            else if(autoClose) fm.wr("/");
            in1str = content.isEmpty() || content.size() == 1 && content.get(0).isText() && content.get(0).size()<100;
            if(in1str) fm.wr(">");
            else fm.wr(">\n");
        }
        
        if(in1str) for(xmlContent xc: content) xc.fprintfSelf(fm, "");
        else for(xmlContent xc: content) xc.fprintfSelf(fm, pref + dopPref);
        if(!tagName.isEmpty()){
            if(!autoClose)
                if(in1str)fm.wr("</" + tagName + ">");
                else fm.wr(pref + "</" + tagName + ">");
            fm.wr("\n");
        }
    }
}
//    int indexOf(String nx){
//        for(int i=0; i < content.size(); i++) if(nx.equals(content.get(i).tagName)) return i;
//        return -1;
//    }
    /*
    nodeXML getNode(String nodeName) {
        for(int i=0; i < nodeList.size(); i++) if(nodeName.equals(nodeList.get(i).tagName)) return nodeList.get(i);
        return null;
    }
    nodeXML getNode(String nodeName, String attrName) {
        for(int i=0; i < nodeList.size(); i++) 
            if(nodeName.equals(nodeList.get(i).tagName) && 
               attrName.equals(nodeList.get(i).attr.get("Name"))) return nodeList.get(i);
        return null;
    }*/
