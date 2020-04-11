package XMLTools;

import fileTools.FileManager;

/*@author Lev*/
public abstract class xmlContent {
    public abstract String getName();
    public abstract void fprintfSelf(FileManager fm, String pref);
    public abstract boolean isText();
     public abstract int size();
}
