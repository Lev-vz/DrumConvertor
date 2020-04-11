package Algorithm.DrumTools;

import fileTools.FileManager;
import fileTools.svgTools;
import globalData.globVar;
import java.io.IOException;
import java.util.ArrayList;


/*@author Lev*/
class drumOut {
    String name;
    ArrayList<Integer> steps = new ArrayList<>();
    ArrayList<Integer> hle = new ArrayList<>();
    drumOut(String n){
        name = n;
    }
    drumOut(String n, int s, int HLE){
        name = n;
        steps.add(s);
        hle.add(HLE);
    }
    void addStep(int s, int HLE){
        steps.add(s);
        hle.add(HLE);
    }
    public void drawSelf(float x, ArrayList<drumStep> drumSteps, String drumName, int numOut, FileManager fm) throws IOException{
        //System.out.print("name " + name + ", step=");//-------- check out ---------
        float sH = globVar.simbolHeight/100;
        float sW = globVar.simbolWidth/100;
        drumStep step = drumSteps.get(steps.get(0)-1);
        int qStep = steps.size();
        if(qStep==1){
            //drawSvgTextRect(int x, int y, int w, int h, int r, String id, String t, int fontSize, boolean sentred)
            fm.wr(svgTools.drawSvgTextRect(x,step.yConnect + step.yLastOut - sH/2, 0, 0, sH/2, drumName + "Out" + numOut,
                                                    name, 14, false));
            step.yLastOut += svgTools.hSVG + globVar.vertGap/100;
            fm.wr(svgTools.drawSvgPath(step.xConnect, step.yConnect, x, svgTools.ySVG + svgTools.hSVG/2, 30, 
                    drumName + "Step" + steps.get(0), drumName + "Out" + numOut, false));
       }else{
            //drawSvgTextRect(int x, int y, int w, int h, String id, String t)
            fm.wr(svgTools.drawSvgTextRect(x, step.yConnect + step.yLastOut - sH/2, globVar.logicBlockWidth, 
                    (qStep+1)*sH*2/3, "", "OR"));
            //System.out.println("x=" + svgTools.xSVG + ", y=" + svgTools.ySVG);//------------------ check out ------------------
            step.yLastOut += svgTools.hSVG + globVar.vertGap/100;
            fm.wr(svgTools.drawSvgTextRect(x + globVar.logicBlockWidth*2,svgTools.ySVG + svgTools.hSVG/2, sW*12, 0, sH/2, 
                    drumName + "Out" + numOut, name, 14, false, true));
            fm.wr(svgTools.drawSvgPath(svgTools.xSVG - globVar.logicBlockWidth, svgTools.ySVG + svgTools.hSVG/2, 
                                                svgTools.xSVG, svgTools.ySVG + svgTools.hSVG/2, 30,"","", false));
            fm.wr(svgTools.drawSvgPath(step.xConnect, step.yConnect, x, svgTools.ySVG + sH/4, 30, 
                    drumName + "Step" + steps.get(0), drumName + "Out" + numOut, false));
            //System.out.println("x=" + svgTools.xSVG + ", y=" + svgTools.ySVG);//------------------ check out ------------------
            float y1 = svgTools.ySVG + sH/5;
            for(int i = 1; i < qStep; i++){
                y1 += sH*4/5;
                fm.wr(svgTools.drawSvgTextRect(x-10,y1,0,sH*10/14,sH*5/14, "", ""+(i+1), 11, true, true));
                //globVar.fm.wr(svgTools.drawSvgPath(drumSteps.get(steps.get(i)-1).xConnect, drumSteps.get(steps.get(i)-1).yConnect, 
                fm.wr(svgTools.drawSvgPath(svgTools.xSVG + svgTools.wSVG, y1, x, y1, 30, 
                        drumName + "Step" + steps.get(i), drumName + "Out" + numOut, false));
            //drawSvgTextRect(int x, int y, int w, int h, int r, String id, String t, int fontSize, boolean sentred)
            //System.out.println("x=" + svgTools.xSVG + ", y=" + svgTools.ySVG);//------------------ check out ------------------
            }
        }
    }
    public boolean isOnStep(int step){
        if(steps.get(0)==step) return true;
        return false;
    }
}
