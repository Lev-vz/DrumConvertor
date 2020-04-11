package Algorithm;

import fileTools.FileManager;
import fileTools.svgTools;
import globalData.globVar;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*@author lev*/
public class simpleOperand extends operand{
    String o;
    public simpleOperand(String O, String op){
        o = O; 
        opa = op;
        /*
        if(opa.equals("NOT")) {
            isNot = true;
        }*/
    }
    @Override
    public String getSelf(){
        String inv = "";
        if(isNot) inv = "!";
        return inv + o;// + "{" + opa + "}";
    }
    public void setSelf(String O){
        o = O;
    }
    @Override
    public float CalcW() {
        return (o.length()+2) * globVar.simbolWidth/100;
    }
    @Override
    public float CalcH() {
        return globVar.simbolHeight/100 + globVar.vertGap/100;
    }

    @Override
    public void drawSelf(float x, float y, float h, String id, FileManager fm) {
            float w = (o.length() + 3) * globVar.simbolWidth/100;
        try {
            fm.wr(svgTools.drawSvgTextRect((x-w),(y+h/2-globVar.simbolHeight/100/2), w, globVar.simbolHeight/100,id,o));
        } catch (IOException ex) {
            Logger.getLogger(simpleOperand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public formula getFormula() {
        return null;
    }

    @Override
    public void optimization() {};
}
