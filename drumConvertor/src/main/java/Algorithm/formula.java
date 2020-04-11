package Algorithm;

import fileTools.FileManager;
import fileTools.svgTools;
import globalData.globVar;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*@author lev*/
public final class formula {
    String resalt = "";
    String operation;
    ArrayList<operand> operands = new ArrayList<>();
    String tail;
    ArrayList<token> toks;
    int err = 0;
    static int id;
            
    public formula(){}
    public formula(String fs){stringProcessing(fs);}
    public formula(ArrayList<token> t, int start, int end){tokenProcessing(t, start, end);}
    
    public String getSelf2() {
        if(operands.isEmpty()) return ""; // если нет операндов, то и возвращать нечего
        String s = "";
        if(!"".equals(resalt)) s += resalt + " := ";  // если есть переменная результата, значит это полная формула,
        else s += resalt + "(";                // а если нет - это элемент большей формулы.
        s += operands.get(0).getSelf();
        for(int i=1; i<operands.size(); i++) {
            s += " " + operation + " " + operands.get(i).getSelf();
        }
        //--- Если результат не пустая строка - ставим ";", иначе закрываем скобку
        if(!"".equals(resalt)) s += ";";
        else s += ")";
        return s;
    }
    public String getSelf() {
        if(operands.isEmpty()) return "ничего нет"; // если нет операндов, то и возвращать нечего
        String s = "";
        if(!resalt.equals("")) s += resalt + " := ";  // если есть переменная результата, значит это полная формула,
        s += operation + "(";
        for(int i=0; i<operands.size()-1; i++) {
            s += operands.get(i).getSelf() + ", ";
        }
        s += operands.get(operands.size()-1).getSelf() + ")";
        return s;
    }
    public float CalcW(){
        float w = 0;
        for(int i = 0; i < operands.size(); i++){
            float wO = operands.get(i).CalcW();
            if(w < wO) w = wO;
        }
        w += resalt.length() * globVar.simbolWidth/100;
        //if(!"NOT".equalsIgnoreCase(operation) ) 
        w += 20;
        return w;
    }
    public float CalcH(){
        int qOp = operands.size();
        float h = 0;
        for(int i = 0; i < operands.size(); i++) h += operands.get(i).CalcH();
        if(h < 10 + globVar.vertGap/100) h = 10 + globVar.vertGap/100;
        return h;
    }
    public void drawSelf(float x, float y, float h, String id, FileManager fm) throws IOException{// x, у - правый верхний угол рисунка, h - его высота
        float w = 0; // ширина текущего куска рисунка
        float H = 0; // высота текщего оператора
        float logocGap = globVar.logicBlockWidth + 3;
        if(!resalt.equals("")){ // если это 0-левой уровень
            w = (resalt.length() + 2) * globVar.simbolWidth/100; // считаем размер текста - результата и рисуем квадратик с результатом
            fm.wr(svgTools.drawSvgTextRect((x-w+globVar.logicBlockWidth),(y+h/2-globVar.simbolHeight/100/2), w, globVar.simbolHeight/100, id+"resalt", resalt));
            //if(!"NOT".equalsIgnoreCase(operation)) w+=(logocGap + globVar.logicBlockWidth);
        }
        
        int q = operands.size();
        H = (q < 2)? 10 : (q < 4)? 15 : q * 5;
        //globVar.fm.wr(svgTools.drawSvgTextRect((x-w-(logocGap + globVar.logicBlockWidth)),(y+h/2-H/2), globVar.logicBlockWidth, H, id, operation));
        fm.wr(svgTools.drawSvgTextRect((x-w-(globVar.logicBlockWidth)),(y+h/2-H/2), globVar.logicBlockWidth, H, id, operation));
        float y1 = y;
        float [][] xy =  new float [q][4];
        int terminator = 0;
        for(int i = 0; i < q; i++){
            float h1 = operands.get(i).CalcH();
            xy[i][0] = x-w-(logocGap + globVar.logicBlockWidth);
            xy[i][1] = y1+h1/2;
            xy[i][2] = x-w-(globVar.logicBlockWidth);
            xy[i][3] = (y+h/2-H/2) + 3 + i*5;
            if(xy[i][1]<xy[i][3])terminator++;
            y1 += h1;
        }
        
        y1 = y;
        for(int i = 0; i < q; i++){
            float h1 = operands.get(i).CalcH();
            operands.get(i).drawSelf(x-w-(logocGap + globVar.logicBlockWidth), y1, h1, id+i, fm);
            int offset = 50;
            if(i < terminator) offset = (terminator - i)*100/(terminator+1);
            else               offset = (i - terminator + 1)*100/(q - terminator + 1);
            fm.wr(svgTools.drawSvgPath(xy[i][0],xy[i][1],xy[i][2],xy[i][3], offset, id+i , id, operands.get(i).isNot));
            y1 += h1;
        }
        if(!resalt.equals(""))  fm.wr(svgTools.drawSvgPath((x-w),(y+h/2),(x-w+globVar.logicBlockWidth),(y+h/2), 50, id , id+"resalt", false));
    }
    
    void addOperand(String o, String opa){ 
        //simpleOperand sO = new simpleOperand(o);
        operands.add(new simpleOperand(o,opa));
    }
    void addOperand(formula o, String opa){ 
        //simpleOperand sO = new simpleOperand(o);
        operands.add(new complexOperand(o,opa));
    }
    int  indexOf(ArrayList<token> t, int start, String desiredTok, int desiredType){
        int end = t.size();
        for(int i = start; i<end; i++){
            if(t.get(i).type == desiredType && ("".equals(desiredTok) || t.get(i).tok.equals(desiredTok))) return i;
        }
        return -1;
    }
    void strSplit(String inStr, String pattern){
        
        Pattern p1 = Pattern.compile(pattern);
        //System.out.println(inStr);//------------------------ контрольный вывод
        Matcher match = p1.matcher(inStr);
        while (match.find()){
            int s = match.start();
            int e = match.end();
            toks.add(new token(s, inStr.substring(s, e).trim()));
            //System.out.println(toks.get(toks.size()-1).start + ") " + toks.get(toks.size()-1).tok);//----------------- контрольный вывод
        }
    }
    
    void stringProcessing(String s){
        int x = s.indexOf(":=");
        if(x >= 0){
            resalt = s.substring(0, x).trim();
            tail = s.substring(x+2).trim();
        }else{
            tail = s.trim();
        }
        tail = tail.replaceAll("\\[\\s+", "[");
        tail = tail.replaceAll("\\s+\\]", "]");
        
        toks = new ArrayList<token>();
        
        strSplit(tail, "[\\w\\.\\[\\],]+");
        strSplit(tail, "[\\+\\*\\^:/<>=%]+");
        strSplit(tail, "[\\(\\)]");
        
        Collections.sort(toks);
        
        tokenProcessing(toks, 0, toks.size()-1);
        System.out.println(getSelf());
        optization();
    }
    
    int tokenProcessing(ArrayList<token> t, int start, int end){
        if(end < start)                                     return 3;   // Ошибка вызова
        if(start == end){// всего один операнд без операции
            if(t.get(start).type==0) {addOperand(t.get(start).tok, "");
                                                            return 0;}  // всего один операнд без операции
                                                            return 4;   // Ошибка в формуле
        }
        if(start+1 == end){// унарный НЕТ с одним операндом
            if("NOT".equalsIgnoreCase(t.get(start).tok) && t.get(end).type == 0){
                operation = "NOT";
                addOperand(t.get(end).tok, "NOT");                 return 0;}  // унарный НЕТ с одним операндом
                                                            return 5;   // Ошибка в унарной операции с одним операндом
        }
        //------------- Простостые случаи кончились. Дальше обработка сложных формул -------------------------------
        int maxType = t.get(start).type;    //Дла писка операции с минимальным пиоритетом (максимальным type) запоминаем тип первого тока
        int maxTypeTok = start;             // и его индекс
        int typeRepeat = 1;                 // а также, заводим счётчик повторения одинаковых операций
        
        for(int i=start; i <= end; i++){
            if(t.get(i).type < -10000)                      return 1;   // Неправильное имя или нарушена последовательность скобок
            if("(".equals(t.get(i).tok)){
                int x = indexOf(t, i, ")", t.get(i).type);
                if(x < 0)                                   return 2;   // Нарушена последовательность скобок
                i = x;
            }else if(t.get(i).type > maxType){
                maxTypeTok = i;
                maxType = t.get(i).type;
                typeRepeat = 1;
            }else if(t.get(i).type == maxType){
                typeRepeat++;
            }
        }
        //----------------------------------------------------------------------------------------------------
        // -------- если maxType < 0, значит в выражении только 1 вложенная скобка и больше ничего ------------
        if(maxType < 0)                                     return tokenProcessing(t, start + 1, end-1);
        
        //-------------- Проверяем найденный токен операции на NOT--------------------------------------------------------------------
        if("NOT".equalsIgnoreCase(t.get(maxTypeTok).tok)){// унарный НЕТ с формулой
            operation = "NOT";
            formula f = new formula(t,start+1,end);
            if(f.err == 0) {addOperand(f,"NOT");                  return 0;}  
                                                            return 6;   // Ошибка в унарной операции с формулой
        }
        
        //-------------- Проверяем первый токен --------------------------------------------------------------------
        if(t.get(start).type > 0 && !"NOT".equalsIgnoreCase(t.get(start).tok))                           
                                                            return 7;   // формула начинается не должна начинаться с неунарного оператора
        //--------------- делим выражение на части по вхождению операции с минимальным приоритетом -------------------
        int [] formulaParts = new int[typeRepeat];          // Массив для индексов токенов - операторов
        int ind = 0;                                        // счётчик для этого массива
        for(int i=start; i <= end; i++){                    // проходим всю формулу,  
            if("(".equals(t.get(i).tok)){                   // обходя скобки
                int x = indexOf(t, i, ")", t.get(i).type);
                i = x;
            }else if(t.get(i).type == maxType){             // На ошибки уже не проверяем, поскольку это было сделано выше
                formulaParts[ind] = i;                      // Записываем, где встретилась такая операция
                ind++;
            }
        }
        operation = t.get(formulaParts[0]).tok;
        int startFormulaPart = start;
        String opa = "";
        for(int i = 0; i<typeRepeat; i++){
            if(formulaParts[i] - startFormulaPart == 3 &&   // Если в участке формулы всего один операнд окружённый скобками
               "(".equals(t.get(startFormulaPart).tok) && 
               ")".equals(t.get(startFormulaPart+2).tok)     ){
                addOperand(t.get(startFormulaPart+1).tok, opa);  // отбрасываем скобки
            }else
            if(formulaParts[i] - startFormulaPart > 1){     // если токенов много и это не предыдущий случай
                formula f = new formula(t,startFormulaPart,formulaParts[i]-1); // создаём из участка формулы вложенную формулу
                if(f.err > 0)                               return f.err; // если удалось это сделать без ошибок -
                addOperand(f, opa);                              // записываем её в общую формулу
            }else{
                addOperand(t.get(formulaParts[i]-1).tok, opa);
            }
            opa = t.get(formulaParts[i]).tok;
            startFormulaPart = formulaParts[i]+1;
        }
        
        if(end - startFormulaPart == 2 && 
           "(".equals(t.get(startFormulaPart).tok) && 
           ")".equals(t.get(end).tok)     ){
            addOperand(t.get(startFormulaPart+1).tok, opa);
        }else
        if(end - startFormulaPart > 0){
            formula f = new formula(t,startFormulaPart,end);
            if(f.err > 0)                                   return f.err;
            addOperand(f, opa);
        }else{
            addOperand(t.get(startFormulaPart).tok, opa);
        }
                                                            return 0; // Всё получилось
    }
    
    public void optization(){
        for(int i = 0; i < operands.size(); i++){
            operands.get(i).optimization();
            formula f = operands.get(i).getFormula();
            if(f != null && f.operands.get(0).isNotOp()){
                boolean isNot = operands.get(i).isNot;
                
                System.out.println("not operand[" + i + "][0] = " + f.operands.get(0).getSelf());
                operand o = f.operands.get(0);
                operands.add(i, o);
                operands.get(i).isNot = !operands.get(i).isNot;
                operands.get(i).opa = operands.get(i+1).opa;
                operands.remove(i+1);
                //f.operands = f.operands.get(0).getFormula().operands;
                //System.out.println("f.operands(" + i + ") = " + operands.get(i).getSelf());  
            }
        }
    }

}
