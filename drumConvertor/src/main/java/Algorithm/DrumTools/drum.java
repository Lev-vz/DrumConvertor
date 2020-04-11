package Algorithm.DrumTools;

import Algorithm.formula;
import Algorithm.token;
import StringTools.StrTools;
import fileTools.FileManager;
import fileTools.svgTools;
//import static StringTools.StrTools.strToTokArr;
import globalData.globVar;
import java.io.IOException;
//import java.text.ParseException;
import java.util.ArrayList;

public final class drum {
    String name = "";
    String rusName = "";

    int qStep = 0;
    //int qTimer = 1;
    
    ArrayList<drumStep> drumSteps = new ArrayList<>();
    ArrayList<drumOut> outs = new ArrayList<>();

    
    public drum(FileManager fm) throws IOException{
        String buf = "";
        int x = -1;
        while(!fm.EOF && x < 0){
            buf = fm.rd();
            //System.out.println(buf +"\n");//------------------- check out ----------
            x = buf.indexOf("Drum:");
        }
        //--- Ищем русское название барабана ---
        int y = buf.indexOf(":");
        rusName = buf.substring(y+1).trim();
        //--- Читаем условия переходов барабана ----------
        int step = 1;
        while(!fm.EOF){
            buf = fm.rdSt();
            //System.out.println(buf +"\n");//------------------- check out ----------
            if(buf.indexOf("DRM_Processing") >=0) break;
            if("".equals(name)){
                x = buf.indexOf("Drum.");
                y = buf.indexOf(".cond");
                if(x < 0 || y < 0){
                    fm.wr(buf +" - не условие перехода барабана");
                    return;
                }
                name = buf.substring(x+5, y);
            }
            y = readDrumStep(buf,step);
            if(y<0){
                fm.wr("Не удалось прочитать шаги барабана \""+rusName+ "\" ("+ name +")\n");
                return;
            }
            step++;
        }
        //--- Читаем выходы барабана ----------
        while(!fm.EOF){
            buf = fm.rdSt();
            //System.out.println(buf +"\n");//------------------- check out ----------
            if(buf.indexOf("End_drum") >=0) break;
            y = readDrumOut(buf);
            if(y<0){
                fm.wr("Не удалось прочитать выходы барабана \""+rusName+ "\" ("+ name +")\n");
                return;
            }
        }
    }
    public void drawSelf(float startY, FileManager fm) throws IOException{
        float W = 0;
        for(int i=0;i<drumSteps.size();i++){
            float w = 30;
            if(drumSteps.get(i).f!= null) w = drumSteps.get(i).f.CalcW();
            if(W < w) W = w; 
        }
        W+=50;
        float y = startY;
        float drumNodeH = 10;
        float sW = globVar.simbolWidth/100;
        float drumPathX = W+8*sW*24/14/2 + 2;
        fm.wr("<g id=\"Drum_"+name+"\" title=\"Последовательность '"+rusName+"'\">\n");
        fm.wr(svgTools.drawSvgTextRect(W, y, 8*sW*24/14+2, drumNodeH, drumNodeH/2, "", "НАЧАЛО", 24, true));
        y+= drumNodeH + globVar.vertGap/100;
        String tmp = "Drum."+name+".enable";
        fm.wr(svgTools.drawSvgTextRect(W-(tmp.length()+3) * globVar.simbolWidth/100, y, 0,0,
                                                 //(tmp.length()+2) * globVar.simbolWidth/100, 
                                                 //globVar.simbolHeight/100,
                                                 "", tmp));
        y = startY+drumNodeH + globVar.simbolHeight/100 + globVar.vertGap/100*2;
        fm.wr(svgTools.drawSvgDrumPath(W,startY+drumNodeH,drumPathX,y));
        int i;
                                                   //(int x, int y, int numStep, String drumName, ArrayList<drumOut> outs)
        for(i=0;i<drumSteps.size();i++)y = drumSteps.get(i).drawSelf(drumPathX, y, i+1, rusName, outs, fm);
        
        fm.wr(svgTools.drawSvgTextRect(drumPathX-4*sW*24/14, y, 8*sW*24/14, drumNodeH, drumNodeH/2, "", "КОНЕЦ", 24, true));
        //fm.wr(svgTools.drawSvgTextRect(drumPathX-3*globVar.simbolWidth/2*24/14/100, y, 
         //                                      3*globVar.simbolWidth*24/14/100, drumNodeH, drumNodeH/2, "", ""+(i+1), 24, true));
        for(i=0; i< outs.size(); i++)
            outs.get(i).drawSelf(drumPathX+20, drumSteps, name, i, fm);
        fm.wr("</g>\n");
    }
    int readDrumStep(String stepText, int step){   
        ArrayList<token> dt = StrTools.strToTokArr(stepText);
        if(dt.size() < 3) return -1; //Недостаточно лексем в условии перехода
        int x = dt.get(0).tok.indexOf("]");
        if(x<4) return -2; //Нет индекса в условии перехода
        int thisStep;
        try{
            thisStep = Integer.parseInt(dt.get(0).tok.substring(x-1, x));
        }catch (NumberFormatException nfe){
          System.out.println("NumberFormatException: " + nfe.getMessage());
          return -2;
        }
        if(thisStep != step) return -3;//Неправильный номер шага
        if(!("Drum."+name+".cond[" + thisStep + "]").equals(dt.get(0).tok) || !":=".equals(dt.get(1).tok)) return -3; //Неправильно написана пременная условия перехода
        drumStep ds = new drumStep();
        int start = 2;
        if(dt.size() >= 5 && ("Drum."+name+".timer[" + thisStep + "]").equals(dt.get(2).tok)){
            if(!">=".equals(dt.get(3).tok) || !("Drum."+name+".limTimer[" + thisStep + "]").equals(dt.get(4).tok)) return -4;//Неправильный таймер
            ds.timer = true;
            start = 6;
        }
        if(dt.size() > start){
            if(ds.timer) ds.timerOp = dt.get(5).tok;//  
            String stepFormula = "";
            for(int i=start; i < dt.size(); i++) stepFormula += dt.get(i).tok + " ";
            ds.f = new formula(stepFormula);
        }
        //System.out.println("["+ stepFormula +"]"); //------------ контрольный вывод
        drumSteps.add(ds);
        return 0;
    }
    int readDrumOut(String outText){
        ArrayList<token> dt = StrTools.strToTokArr(outText);
        if(dt.size() < 5) return -1; //Недостаточно лексем
        drumOut dro = new drumOut(dt.get(0).tok);
        if(!":=".equals(dt.get(1).tok)) return -3; //Ошибка в формуле выхода барабана
        int start = 2;
        int hle = 0;
        while(start < dt.size()-2){
            if(!("Drum."+name+".state").equals(dt.get(start).tok)) return -3; //Ошибка в формуле выхода барабана
            if     ( "=".equals(dt.get(start+1).tok) && hle <= 0) hle = 0;
            else if(">=".equals(dt.get(start+1).tok) && hle <= 0) hle = 1;
            else if("<=".equals(dt.get(start+1).tok) && hle == 1) hle = -1;
            else return -4; //Неправильный символ сравнения в формуле выхода барабана. Может быть только "=", ">=" , "<="
            int thisStep;
            try{
                thisStep = Integer.parseInt(dt.get(start +2).tok);
            }catch (NumberFormatException nfe){
              System.out.println("NumberFormatException: " + nfe.getMessage());
              return -2; //Неправильно описан выход
            }
            dro.addStep(thisStep, hle);
            start += 4;
        }
        outs.add(dro);
        return 0;
    }
}
/*
    String readDrumText(FileManager fm) throws IOException{
        if(fm.EOF) return "";
        String buf;// = "";
        String drumStr = "";
        int x = -1;
        while(!fm.EOF && x < 0){
            buf = fm.rd().trim();
            x = buf.indexOf("end_drum");
            int y = buf.indexOf("//");
            drumStr += (y < 0)? buf + " " : buf.substring(0, y) + " ";
        }
        //if(x<0) return "";
       //System.out.println(drumStr +"\n"); //------------ контрольный вывод
        return drumStr;
    }
    int readDrumOuts(ArrayList<token> dt, int x, FileManager fm) {
        //int x = -1;
        if("ELSE".equalsIgnoreCase(dt.get(x).tok)) return 0;
        //ArrayList<token> srch = new ArrayList<>();
        //----- Проверяем, а не конец ли списка выходов ------
        //System.out.println("name " + dt.get(x).tok + ", step=" + dt.get(x+4).tok);//-------- check out ---------
        if(!(":=".equalsIgnoreCase(dt.get(x+1).tok)) ||
           !(("Drum."+name+".state").equalsIgnoreCase(dt.get(x+2).tok)) ||
           !("=".equalsIgnoreCase(dt.get(x+3).tok))){
            System.out.println("Неправильный шаг в выходе барабана " + name + "\n");
            return 0;
        }
        int step;
        try{
            step = Integer.parseInt(dt.get(x+4).tok);
        }catch(NumberFormatException e){
            System.out.println("Неправильный шаг в выходе барабана " + name + "\n");
            return -1;
        }
        if(step>0 && step <= drumSteps.size()){
            drumSteps.get(step-1).qOuts++;
            outs.add(new drumOut(dt.get(x).tok, step));
        }else{
            System.out.println("Неправильный шаг в выходе барабана " + name + "\n");
            return -1;
        }
        x+=5;
        while(x < dt.size() && "OR".equals(dt.get(x).tok)){
            step = Integer.parseInt(dt.get(x+3).tok);
            if(step>0 && step <= drumSteps.size()){
                drumSteps.get(step-1).qOuts++;
                outs.get(outs.size()-1).addStep(step);
            }else{
                System.out.println("Неправильный шаг в выходе барабана " + name + "\n");
                return -1;
            }
            x+=4;
        }
        return x;
    }

*/

        /*
        x = buf.indexOf("//");
        //System.out.println("name: " + name +", rusName: " + rusName); //------------ контрольный вывод
        String ds = readDrumText(fm);
        if(ds.isEmpty()){
            System.out.println("Не удалось прочитать тело барабана \""+rusName+ "\" ("+ name +")\n");
            return;
        }
        ArrayList<token> dt = StrTools.strToTokArr(ds);
        x = 1;
        while(y > 0 && y < dt.size()-1){
            y = readDrumStep(x, dt, y);
            x++;
        }
        if(y == 0){
            System.out.println("Не найден конец барабана \""+rusName+ "\" ("+ name +")\n");
            return;
        }
        x=-y + 4;
        while(x > 0 && y < dt.size()-1){
            x = readDrumOuts(dt,x,fm);
        }
        */
        //srch.add(new token(0, "Drum."+name+".timer[" + qTimer + "]"));
        //srch.add(new token(0, "+"));
        //srch.add(new token(0, "cycle"));
/*
            int x1 = drumPathX-4*globVar.simbolWidth/2*24/14/100;
            drumSteps.get(i).y = y+drumNodeH/2;
            fm.wr(svgTools.drawSvgTextRect(x1, y, 4*globVar.simbolWidth*24/14/100, drumNodeH, drumNodeH/2, 
                                                   "", ""+(i+1), 24, true));
            int outH = drumSteps.get(i).qOuts*(globVar.simbolHeight/100 + globVar.vertGap/100);
            int h = (outH > drumNodeH)? outH : drumNodeH;
            if(drumSteps.get(i).f!= null){
                int H = drumSteps.get(i).f.CalcH();
                if(h < H - drumNodeH*2/3) h = H - drumNodeH*2/3;
                drumSteps.get(i).f.drawSelf(W, y+drumNodeH+h/2-H/2, H, "Drum"+name+"");
            }else{
                fm.wr(svgTools.drawSvgTimerRect(W-5*globVar.simbolWidth/100, y + drumNodeH, 
                                                   5*globVar.simbolWidth/100, drumNodeH, x1, y+drumNodeH/2, "", ""+(i+1)));

            }
            y += drumNodeH;
            fm.wr(svgTools.drawSvgDrumPath(W,y,drumPathX,y+h));
            y += h;
*/
    /*
    int readDrumStep(int step, ArrayList<token> dt){    
        return readDrumStep(step, dt, 0);
    }
    int readDrumStep(int step, ArrayList<token> dt, int start){    
        int x = -1;
        int y = -1;
        String stepFormula = "";
        //--- Ищем начало шага барабана
        ArrayList<token> srch = new ArrayList<>();
        srch.add(new token(0, "Drum."+name+".state"));
        srch.add(new token(0, "="));
        srch.add(new token(0, ""+step));
        srch.add(new token(0, "then"));
        x = StrTools.searchSequence(dt, srch, start);
        
        if(x<0){
            System.out.println("Не найден шаг " + step + " барабана " + name + "["+start+"]\n");
            return 0;
        } // если не нашли - возвращаем ошибку
        x+=4;
        //--- Ищем а не последний ли это шаг барабана --------------------------------
        srch.clear();
        srch.add(new token(0, "Drum."+name+".complete"));
        srch.add(new token(0, ":="));
        srch.add(new token(0, "true"));
        y = StrTools.searchSequence(dt, srch, x, x+3);
        if(y > 0)
            return -y;
        
        //--- Если шаг не последний - ищем блок if - then --------------------------------
        srch.clear();
        srch.add(new token(0, "if"));
        y = StrTools.searchSequence(dt, srch, x);
        if(y<0){
            System.out.println("Не найдено условие перехода на шаг " + (step+1) + " барабана " + name);
            return 0;
        } // если не нашли - возвращаем ошибку
        //---------------------------
        srch.clear();
        srch.add(new token(0, "then"));
        x = StrTools.searchSequence(dt, srch, y+1);
        if(x<0){
            System.out.println("Не найдено условие перехода на шаг " + (step+1) + " барабана " + name);
            return 0;
        } // если не нашли - возвращаем ошибку
        //----------- Проверяем, а не таймер ли там есть -----------------
        srch.clear();
        srch.add(new token(0, "Drum."+name+".timer[" + qTimer + "]"));
        srch.add(new token(0, ">="));
        int tPos = StrTools.searchSequence(dt, srch, y+1, x);
        
        if(tPos >= 0){//----если таймер ---------
            qTimer ++;//---проверяем, один ли он там. Если да, то добавляем в список шагов шаг с чистым таймером
            if(x-y == 4){ 
                drumSteps.add(new drumStep(qTimer));
            }else if(tPos == y+1){
                for(int i=y+4; i < x; i++) stepFormula += dt.get(i).tok + " ";
                drumSteps.add(new drumStep(new formula(stepFormula),qTimer));
            }else{
                for(int i=y+1; i < x-4; i++) stepFormula += dt.get(i).tok + " ";
                drumSteps.add(new drumStep(new formula(stepFormula),qTimer));
            }
        }else{
            for(int i=y+1; i < x; i++) stepFormula += dt.get(i).tok + " ";
            drumSteps.add(new drumStep(new formula(stepFormula)));
        }
        //System.out.println("["+ stepFormula +"]"); //------------ контрольный вывод
        return x;
    }
    */
