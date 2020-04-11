package XMLTools;

import fileTools.FileManager;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*@author Lev*/
public class textContent extends xmlContent{
    String text;
    public textContent(String c){
        text = c;
    }
    @Override
    public String getName() {
        return "";
    }
    @Override
    public void fprintfSelf(FileManager fm, String pref) {
        try {
            fm.wr(pref + text);
        } catch (IOException ex) {
            Logger.getLogger(textContent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public boolean isText() {
        return true;
    }
    @Override
    public int size() {
        return text.length();
    }
}
