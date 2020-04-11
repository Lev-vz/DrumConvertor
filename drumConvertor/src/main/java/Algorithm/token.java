package Algorithm;

/*@author lev*/
public class token implements Comparable<token>{
    public int start;
    public String tok;
    public int type = 0;
    static int bracketLevel = -1;
    public token(int s, String t) {
        start = s; tok = t; 
        if("(".equals(tok)){
            type = bracketLevel;
            bracketLevel--;
        }else if(")".equals(tok)){
            bracketLevel++;
            if(bracketLevel >= 0)type = -10001;
            else type = bracketLevel;
        }
        //1000 - это нечто в кавычках
        else if("OR".equals(tok.toUpperCase()))  {type = 700; tok = tok.toUpperCase();}
        else if("AND".equals(tok.toUpperCase())) {type = 600; tok = tok.toUpperCase();}
        else if("NOT".equals(tok.toUpperCase())) {type = 500; tok = tok.toUpperCase();}
        
        else if("=".equals(tok))   type = 400;
        else if("==".equals(tok))  type = 400;
        else if(">".equals(tok))   type = 400;
        else if("<".equals(tok))   type = 400;
        else if(">=".equals(tok))  type = 400;
        else if("<=".equals(tok))  type = 400;
        
        else if("+".equals(tok)) type = 300;
        else if("-".equals(tok)) type = 300;
        
        else if("*".equals(tok)) type = 200;
        else if("/".equals(tok)) type = 200;
        else if("%".equals(tok)) type = 200;
        else if(":".equals(tok)) type = 200;
        
        else if("^".equals(tok))  type = 100;
        else if("**".equals(tok)) type = 100;
        
    }
    public token(int s, String t, int T){
        start = s;
        tok = t;
        type = T;
    }

    @Override
    public int compareTo(token t) {
        return this.start - t.start;
    }
}
