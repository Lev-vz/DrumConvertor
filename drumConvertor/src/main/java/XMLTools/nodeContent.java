package XMLTools;

import Algorithm.token;
import fileTools.FileManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/*@author Lev*/
public class nodeContent extends xmlContent{
    nodeXML node;
    public nodeContent(FileManager fm, ArrayList<token> fs, String text) throws IOException{
        node = new nodeXML(fm, fs, text);
    }
    @Override
    public String getName() {
        return node.tagName;
    }

    @Override
    public void fprintfSelf(FileManager fm, String pref) {
        try {
            node.fprintSelf(fm, pref);
        } catch (IOException ex) {
            Logger.getLogger(nodeContent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean isText() {
        return false;
    }

    @Override
    public int size() {
        return node.content.size();
    }
}
