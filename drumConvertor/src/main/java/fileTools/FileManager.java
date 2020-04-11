package fileTools;

import globalData.globVar;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class FileManager {

    //public File dir;
    public File wrFile;
    public File rdFile;
    public FileOutputStream fos;
    public FileInputStream fis;
    public Writer wrStream;
    public Reader rdStream;
    public boolean EOF;
    //String buf;

    public int createFile2write(String dirName, String fileName) throws IOException {

        File dir = new File(dirName);
        // если объект представляет каталог
        if (dir.isFile()) {
            return 1; //неправильный путь
        }
        if (dir.isDirectory()) {
            wrFile = new File(dir, fileName);
            if (wrFile.isFile()) {
                wrFile.delete();
            }
            boolean created = wrFile.createNewFile();
            if (!created) {
                return 2; // не удалось создать файл
            }
        }
        fos = new FileOutputStream(wrFile, false);
        wrStream = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
        return 0;
    }
    public int openFile4read(String dirName, String fileName) throws IOException {
        return openFile4read(dirName, fileName, "UTF-8");
    }
    public int openFile4read(String dirName, String fileName, String charSer) throws IOException {
        File dir = new File(dirName);
        // если объект представляет каталог
        if (dir.isFile()) {
            return 1; //неправильный путь
        }
        if (dir.isDirectory()) {
            /*/--- контрольный вывод ---
            for (File fileEntry : dir.listFiles()) {
                System.out.println(fileEntry.getName());
            }
            //--- конец контрольного вывода ---/*/
            rdFile = new File(dir, fileName);
            if (!rdFile.isFile()) {
                return 3; // не удалось найти файл
            }
        } else {
            return 1; //неправильный путь 
        }
        fis = new FileInputStream(rdFile);
        rdStream = new InputStreamReader(fis, charSer); //cs);
        EOF = false;
        return 0;
    }
    public void wr(String s) throws IOException {
        wrStream.write(s);
    }
    public String rd() throws IOException {
        String s = "";
        int ch = 0;
        //while (((char)ch) != '\n' && ch >= 0 ) {
        while (true) {
            try {
                ch = rdStream.read();
                if (!(s.isEmpty() && (ch == 10))) {//ch == 13 || 
                    if (((char) ch) == '\n' || ch < 0 || ch == 13 || ch == 10) {
                        break;
                    }
                    s += (char) ch;
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                        EOF = true;
                break;
            }
        }
        if (ch == -1) {
                    EOF = true;
        }
        return s;
    }
    public String rdChar(int q) throws IOException {
        String s = "";
        int ch=0;
        for(int i = 0; i < q; i++) {
            try {
                ch = rdStream.read();
                s += ch;
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                EOF = true;
                break;
            }
        }
        if (ch == -1) EOF = true;
        return s;
    }
    public int CopyFile(String file1Name, String file2Name, String srcDir, String dstDir, String charSet) throws IOException {
        if (openFile4read(srcDir, file1Name, charSet) != 0) {
            return 1;
        }
        if (createFile2write(dstDir, file2Name) != 0) {
            return 1;
        }
        while (!EOF) {
            String buf = rd();
            wr(buf);
            //System.out.println("[" + buf +"]" + EOF);
        }
        rdStream.close();
        wrStream.close();
        return 0;
    }
    public int MoveFile(String fileName, String srcDir, String dstDir) {
        File originalFile = new File(srcDir, fileName);
        File newFile = new File(dstDir, fileName);

        if (originalFile.exists() && !newFile.exists()) {
            if (originalFile.renameTo(newFile)) {
            } else {
                return 1;
            }
        }
        return 0;
    }
    public String rdXmlTag(){
        String s = "";
        int ch = 0;
        while (true){//ch>=0) {
            try {
                ch = rdStream.read();
                if (((char) ch) == '>' || ch<0) break;
                else s += (char) ch;
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                EOF = true;
                break;
            }
        }
        if (ch == -1) EOF = true;
        return s;
    }
    public String rdXmlTagContent(){
        String s = "";
        int ch = 0;
        while (true){//ch>=0) {
            try {
                ch = rdStream.read();
                if (((char) ch) == '<' || ch<0) break;
                else s += (char) ch;
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                EOF = true;
                break;
            }
        }
        if (ch == -1) EOF = true;
        return s;
    }
    
    public String rdSt() throws IOException{
        String s = "";
        while(!EOF){
            s += rdStTillComment();
            if("/".equals(s.substring(s.length()-1))){
                s = s.substring(0, s.length()-1);
                rd();
            }else if("(".equals(s.substring(s.length()-1))){
                rdStComment();
            }else return s.trim();
        }
        return s.trim();
    }
    public String rdStTillComment(){
        String s = "";
        int ch = 0;
        while (true){//ch>=0) {
            try {
                ch = rdStream.read();
                if (((char) ch) == ';' || 
                    ((char) ch) == '/' && "/".equals(s.substring(s.length()-1)) || 
                    ((char) ch) == '*' && "(".equals(s.substring(s.length()-1)) || 
                      ch<0) break;
                else s += (char) ch;
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                EOF = true;
                break;
            }
        }
        if (ch == -1) EOF = true;
        return s;
    }
    public String rdStComment(){
        String s = "";
        int ch = 0;
        while (true){//ch>=0) {
            try {
                ch = rdStream.read();
                if (((char) ch) == ')' && "*".equals(s.substring(s.length()-1)) || 
                      ch<0) break;
                else s += (char) ch;
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                EOF = true;
                break;
            }
        }
        if (ch == -1) EOF = true;
        return s;
    }

}