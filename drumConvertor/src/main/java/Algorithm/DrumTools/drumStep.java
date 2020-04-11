package Algorithm.DrumTools;

import Algorithm.formula;
import fileTools.FileManager;
import fileTools.svgTools;
import globalData.globVar;
import java.io.IOException;
import java.util.ArrayList;
//import java.util.ArrayList;

/*@author lev*/
public class drumStep {
    public formula f = null;
    public boolean timer = false;
    public String timerOp = "";
    float xConnect;
    float yConnect;
    int qOuts = 0;
    float yLastOut = 0;
    
    public drumStep(){};
    public drumStep(formula F, boolean t){
        f = F;
        timer = t;
    }
    public drumStep(boolean t){
        timer = t;
    }
    public drumStep(formula F){
        f = F;
    }
    public float drawSelf(float x, float y, int numStep, String drumName, ArrayList<drumOut> outs, FileManager fm) throws IOException{
        int FONT_SIZE = 24;
        float sW = globVar.simbolWidth/100;
        float sH = globVar.simbolHeight/100;
        float drumNodeH = sH*FONT_SIZE/14;
        float xStepConnect = x - sW*2*FONT_SIZE/14; //Смещение овала с номером шага от оси барабана
        //int lbw = globVar.logicBlockWidth;
        fm.wr(svgTools.drawSvgTextRect(xStepConnect, y, 4*sW*FONT_SIZE/14, drumNodeH, drumNodeH/2, 
                                               drumName+"Step"+numStep, ""+numStep, FONT_SIZE, true));
        xConnect = xStepConnect + svgTools.wSVG;
        yConnect = y + drumNodeH/2;
        y += drumNodeH;
        
        float outH = 0;
        for(drumOut o: outs){
            if(o.isOnStep(numStep)){
                if(o.steps.size()>1) outH += (o.steps.size())*sH*2/3;
                else outH += sH*4/3;
            }
        }
        float h = (outH > drumNodeH)? outH : drumNodeH;
        if(f!= null){
            float vertGap = globVar.vertGap/100;
            float formulaH = f.CalcH();
            if(timer) formulaH += globVar.logicBlockWidth + vertGap;
            if(h < formulaH - drumNodeH*2/3) h = formulaH - drumNodeH*2/3;
            if(timer){
                // левый, верхний, ширина, высота, х, у точки коннекта, id, text
                float xOR = x-globVar.logicBlockWidth*2;
                float yOR = y + h/2-globVar.logicBlockWidth*3/4;
                float wOR = globVar.logicBlockWidth;
                float hOR = globVar.logicBlockWidth*3/2;
                String idOR = drumName+numStep+"T_OR";
                fm.wr(svgTools.drawSvgTextRect(xOR, yOR, wOR, hOR, idOR, "OR"));
                
                float xT = x-5*sW-globVar.logicBlockWidth*3;
                float yT = y + h/2 - formulaH/2;
                float wT = 5*globVar.simbolWidth/100;
                float hT = globVar.logicBlockWidth;
                String idT = drumName+numStep+"T"+timer;
                fm.wr(svgTools.drawSvgTimerRect(xT, yT, wT, hT, xStepConnect, yConnect, idT, "T"+timer));
            //    public static String drawSvgPath(int x0, int y0, int x2, int y2, int offset, String id1, String id2, boolean inv){
                fm.wr(svgTools.drawSvgPath(xT + 5*sW, yT + globVar.logicBlockWidth/2, xOR, yOR + 4, 50, idT, idOR, false));

                xT = x-globVar.logicBlockWidth*3;
                yT = yT + hT + vertGap;
                hT = formulaH - globVar.logicBlockWidth - vertGap;
                idT = drumName+numStep+"F";
                f.drawSelf(xT, yT, hT, idT, fm);
                fm.wr(svgTools.drawSvgPath(xT, yT + hT/2, xOR, yOR + 11, 50, idT, idOR, false));
            }else{
                f.drawSelf((x-globVar.logicBlockWidth), (y+h/2 - formulaH/2), formulaH, drumName+numStep+"F", fm);
            }    
        }else if(timer){
            fm.wr(svgTools.drawSvgTimerRect(x-5*sW-globVar.logicBlockWidth, y + h/2 - globVar.logicBlockWidth/2, 
                           5*globVar.simbolWidth/100, globVar.logicBlockWidth, xStepConnect, yConnect, 
                           drumName+numStep+"T"+timer, "T"+timer));
        }else{
            System.out.println("Нет условия перехода из шага " + numStep + " последовательности " + drumName);
            return -1;
        }
        fm.wr(svgTools.drawSvgDrumPath(x-globVar.logicBlockWidth,y,x,y+h));
        return y+h;
    }
}
