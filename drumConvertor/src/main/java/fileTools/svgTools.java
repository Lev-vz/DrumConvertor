/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileTools;

import StringTools.StrTools;
import globalData.globVar;
import java.util.ArrayList;

/*@author lev*/
public class svgTools {
    public static float xSVG, ySVG, wSVG, hSVG;
    public static String getSvgTitleA4(){
        return "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"210mm\" height=\"297mm\">\n"+
                "<g fill=\"none\" stroke=\"black\" stroke-width=\"1\">" + 
                    "<rect x=\"20mm\" y=\"5mm\" width=\"185mm\" height=\"287mm\" />"+
                    "<rect x=\"8mm\" y=\"147mm\" width=\"12mm\" height=\"145mm\"/>"+
                    "<line x1=\"13mm\" x2=\"13mm\" y1=\"147mm\" y2=\"292mm\"/>" + 
                    "<line x1=\"8mm\" x2=\"20mm\" y1=\"182mm\" y2=\"182mm\"/>" + 
                    "<line x1=\"8mm\" x2=\"20mm\" y1=\"207mm\" y2=\"207mm\"/>" + 
                    "<line x1=\"8mm\" x2=\"20mm\" y1=\"232mm\" y2=\"232mm\"/>" + 
                    "<line x1=\"8mm\" x2=\"20mm\" y1=\"267mm\" y2=\"267mm\"/>" + 
                    "<line x1=\"20mm\" x2=\"205mm\" y1=\"277mm\" y2=\"277mm\"/>" + 
                    "<line x1=\"20mm\" x2=\"85mm\" y1=\"282mm\" y2=\"282mm\"/>" + 
                    "<line x1=\"20mm\" x2=\"85mm\" y1=\"287mm\" y2=\"287mm\"/>" + 
                    "<line x1=\"195mm\" x2=\"205mm\" y1=\"282mm\" y2=\"282mm\"/>" + 
                    "<line x1=\"195mm\" x2=\"205mm\" y1=\"287mm\" y2=\"287mm\"/>" + 
                    "<line x1=\"27mm\" x2=\"27mm\" y1=\"277mm\" y2=\"292mm\"/>" + 
                    "<line x1=\"37mm\" x2=\"37mm\" y1=\"277mm\" y2=\"292mm\"/>" + 
                    "<line x1=\"60mm\" x2=\"60mm\" y1=\"277mm\" y2=\"292mm\"/>" + 
                    "<line x1=\"75mm\" x2=\"75mm\" y1=\"277mm\" y2=\"292mm\"/>" + 
                    "<line x1=\"85mm\" x2=\"85mm\" y1=\"277mm\" y2=\"292mm\"/>" + 
                    "<line x1=\"195mm\" x2=\"195mm\" y1=\"277mm\" y2=\"292mm\"/>" + 
                "</g>\n";
    }
    public static String getSvgEnd(){
        return "</svg>";
    }
    public static String getSvgRect(float x, float y, float w, float h, String id){
        return "<rect x=\"" + x*globVar.scale/100 + 
                  "\" y=\"" + y*globVar.scale/100 + 
              "\" width=\"" + w*globVar.scale/100 +
             "\" height=\"" + h*globVar.scale/100 +
             "\" fill=\"none\" stroke=\"black\" id=\"rect_" + id + "\"/>\n";
    }
    public static String getSvgText(float x, float y, String id, String t){
        return "<text x=\"" + x*globVar.scale/100 + 
                  "\" y=\"" + y*globVar.scale/100 + 
            "\" id=\"rect_" + id + "\"\n" +
                "style=\"font-style:normal;font-weight:bold;font-size:12px;line-height:1.25;font-family:'DejaVu Sans Mono';" + 
                "letter-spacing:0px;word-spacing:0px;fill:#000000;fill-opacity:1;stroke:none\">\n" + t + "</text>\n";
    }
    
    public static String drawSvgTextRect(float x, float y, float w, float h, String id, String t){
        return drawSvgTextRect(x, y, w, h, 0, id, t, 0, false, false);
    }
    public static String drawSvgTextRect(float x, float y, float w, float h, float r, String id, String t, int fontSize, boolean sentredX){
        return drawSvgTextRect(x, y, w, h, r, id, t, fontSize, sentredX, false);
    }
    public static String drawSvgTextRect(float x, float y, float w, float h, float r, String id, String t, int fontSize, 
            boolean sentredX, boolean sentredY){
        if(fontSize == 0) fontSize = 14;
        float fontSizeCoeffH = globVar.simbolHeight*fontSize/14/100;
        float fontSizeCoeffW = globVar.simbolWidth*fontSize/14/100;
        float scale = globVar.scale/100;
        ArrayList<String> als;
        float textOffsetX = fontSizeCoeffW;
        float textOffsetY = fontSizeCoeffH*2/3;
        if(w==0){
            w = (t.length()+3)*fontSizeCoeffW;
            als = new ArrayList<>();
            als.add(t);
        }else{
            als = StrTools.sliceString(t, (int)(w/fontSizeCoeffW-2));
        }
        if(h==0) h = als.size()*fontSizeCoeffH;
        if(sentredX){
            textOffsetX = w/2 - t.length()*fontSizeCoeffW/2;
            textOffsetY = h/2 + globVar.simbolHeight*fontSize/14/100/6;
        }
        if(sentredY) y = y - h/2;
        String tmp = "";
        float y1 = y;
        for(String s : als){
            tmp+= "    <text x=\"" + (x + textOffsetX)*scale + 
                  "\" y=\"" + (y1 + textOffsetY)*scale + 
                  "\"\n       style=\"font-style:normal;font-weight:bold;font-size:"+fontSize+"px;line-height:1.25;font-family:'DejaVu Sans Mono';" + 
                  "letter-spacing:0px;word-spacing:0px;fill:#000000;fill-opacity:1;stroke:none\">\n   " + s + "</text>\n";
            y1 += fontSizeCoeffH;
        }
        xSVG = x; ySVG = y; wSVG = w; hSVG = h;
        return  "<g id=\"" + id + "\" >\n" + 
                "   <rect x=\""+x*scale+"\" y=\""+y*scale+"\" width=\""+w*scale+"\" height=\""+h*scale+"\" rx=\""+r*scale+
                "\" fill=\"none\" stroke=\"black\"/>\n" + tmp + "</g>\n";
    }
    public static String drawSvgPath(float x0, float y0, float x2, float y2, float offset, String id1, String id2, boolean inv){
        if(offset > 90 || offset < 10) offset = 50;
        float x1 = x0 + (x2 - x0)*offset/100;
        float x3 = x2;
        String isInv = "";
        if(inv){
            x3 = x2-1;
            float r = 1*globVar.scale/100;
            isInv = " a " + r + " "+ r + " 0 1 1 " + 2*r + " 0 a " + r + " "+ r + " 0 1 1 -" + 2*r + " 0";
        }
        //float xWhite = 2;
        //if(y0<y2)xWhite = -2;
        return "<path fill=\"none\" stroke=\"black\" id=\"" + id1+ "_" + id2 +
               "\" d=\"M "+ x0*globVar.scale/100 +","    + y0*globVar.scale/100 + " H " + x1*globVar.scale/100 + " V " + 
                         y2*globVar.scale/100 + " H " + x3*globVar.scale/100  + isInv + "\"\n" +
               "    inkscape:connection-start=\"#" + id1 +"\"\n" +
               "    inkscape:connection-end=\"#" + id2 +"\" />\n" +
                /*
               "<path fill=\"none\" stroke=\"white\" stroke-width=\"3\" d=\""+
                "M "+ (x0*globVar.scale/100+5) +","    + (y0*globVar.scale/100+2) + " H " + (x1*globVar.scale/100+xWhite) + 
                " V " + (y2*globVar.scale/100+2) + " H " + (x3*globVar.scale/100-5)  + 
                "M "+ (x0*globVar.scale/100+5) +","    + (y0*globVar.scale/100-2) + " H " + (x1*globVar.scale/100-xWhite) + 
                " V " + (y2*globVar.scale/100-2) + " H " + (x3*globVar.scale/100-5)  + 
                "\"/>"+*/
                "\n";
    }    
    public static String drawSvgDrumPath(float x0, float y0, float x1, float y1){
        return "    <path fill=\"none\" stroke=\"black\" "+
                "d=\"M " + x0*globVar.scale/100 +"," + (y0+y1)/2*globVar.scale/100 + " H " + x1*globVar.scale/100 + 
                   " M " + x1*globVar.scale/100 +"," +  y0*globVar.scale/100 + " V " + y1*globVar.scale/100  + "\"/>\n" + 
               "    <path fill=\"#FFFFFF\" stroke=\"black\" "+
                "d=\"M " + (x1-1)*globVar.scale/100 +"," + ((y0+y1)/2)  *globVar.scale/100 + " " +
                           (x1-1)*globVar.scale/100 +"," + ((y0+y1)/2-2)*globVar.scale/100 + " " +
                           (x1+1)*globVar.scale/100 +"," + ((y0+y1)/2-2)*globVar.scale/100 + " " +
                           (x1+1)*globVar.scale/100 +"," + ((y0+y1)/2)  *globVar.scale/100 + " " +
                            x1   *globVar.scale/100 +"," + ((y0+y1)/2+2)*globVar.scale/100 + " " +
                           (x1-1)*globVar.scale/100 +"," + ((y0+y1)/2)  *globVar.scale/100 + " " +
                 "\"/>";
    }    
    // левый, верхний, ширина, высота, х, у точки коннекта, id, text
    public static String drawSvgTimerRect(float x, float y, float w, float h, float x1, float y1, String id, String t){
        int fontSize = 14;
        float textOffsetX = w/2 - t.length()*globVar.simbolWidth/100/2;
        float textOffsetY = h/2 + globVar.simbolHeight/100/8;
        float scale = globVar.scale/100;
        return  "<g id=\"" + id + "\" >\n" + 
                "   <rect x=\"" + x*scale + 
                  "\" y=\"" + y*scale + 
              "\" width=\"" + w*scale +
             "\" height=\"" + h*scale +
             "\" fill=\"none\" stroke=\"black\"/>\n" + 
                
                "       <path fill=\"none\" stroke=\"black\" "+
                "d=\"M " + (x+2)*scale +"," + (y+h-3)*scale + " H " + (x+w-2)*scale + 
                " M "    + (x+2)*scale +"," + (y+h-4)*scale + " V " + (y+h-2)*scale  + 
                " M "  + (x+w-2)*scale +"," + (y+h-4)*scale + " V " + (y+h-2)*scale  + 
                " M "  + x1*scale +"," + y1*scale + " H " + (x-4)*scale  + 
                " V "  + (y+h/2)*scale  + " H " + x*scale +
                "\"/>\n" + 
                
               "    <text x=\"" + (x + textOffsetX)*scale + 
                  "\" y=\"" + (y + textOffsetY)*scale + 
                "\"\n       style=\"font-style:normal;font-weight:bold;font-size:"+fontSize+"px;line-height:1.25;font-family:'DejaVu Sans Mono';" + 
                "letter-spacing:0px;word-spacing:0px;fill:#000000;fill-opacity:1;stroke:none\">\n   " + t + "</text>\n</g>\n";
    }
}
