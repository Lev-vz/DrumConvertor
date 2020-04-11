package main;

import Algorithm.DrumTools.drum;
import StringTools.StrTools;
import XMLTools.nodeXML;
//import Algorithm.formula;
//import StringTools.StrTools;
import fileTools.FileManager;
import fileTools.svgTools;
//import static fileTools.svgTools.drawSvgTextRect;
//import fileTools.svgTools;
import globalData.globVar;

import java.io.IOException;
import java.util.ArrayList;

public class main {

    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub
        globVar.linSep = System.getProperty("line.separator");
        globVar.myDir = System.getProperty("user.dir"); //user.dir
        //globVar.desDir = globVar.myDir+"\\Design";
        FileManager fm = new FileManager();        
        fm = new FileManager();
        int x = fm.openFile4read(globVar.myDir, "drum2.st");
        if(x != 0 ){
            System.out.println("Не удалось открыть файл на чтение. Ошибка " + x);
            return;
        }
        x = fm.createFile2write(globVar.myDir, "Error.log");
        if(x != 0 ){
            System.out.println("Не удалось создать файл "+"Error.log"+". Ошибка " + x);
            return;
        }
        drum d1 = new drum(fm);
        drum d2 = new drum(fm);
        
        fm.wrStream.close();

        x = fm.createFile2write(globVar.myDir, "drum2.svg");
        if(x != 0 ){
            System.out.println("Не удалось создать файл "+"drum1.svg"+". Ошибка " + x);
            return;
        }
        fm.wr(svgTools.getSvgTitleA4());

        d1.drawSelf(10, fm);
        fm.wr(svgTools.getSvgEnd());
     

        fm.wrStream.close();
        fm.rdStream.close();

    }

}

/*
		//globVar.st = new StrTools();
                //formula f2 = new formula("uuu:=(qqq[  0 ] OR (ww.w AND (1eee or .rrr)) : _tt,t ++ (yy/ff*gg^2))>=(16.5 % 3)");
                //formula f2 = new formula("DGO.Sirena_ON:= not (not(not BBB and EEE and or CCC or A1234567890) and HHHHHHHHHH and A1234567890 and DDD and GGGGGGGGGGGGGGGG)");//
                formula f2 = new formula("DGO.Sirena_ON:= DDD and (not(not BBB and EEE and or CCC or A1234567890) and HHHHHHHHHH and A1234567890 and GGGGGGGGGGGGGGGG)");//
                System.out.println(f2.getSelf());
               
                int h = f2.CalcH();
                int w = f2.CalcW();
                System.out.println("H = " + h + ", W = " + w);
                globVar.fm.createFile2write(globVar.myDir, "drum1.svg");
                f2.drawSelf(w+40, 10, h, "L0_");
                globVar.fm.wrStream.close();
		//System.out.println(uuid);// + globVar.linSep);
		int x = globVar.fm.openFile4read(globVar.myDir, "drum1.st");
                if(x != 0) System.out.println(Integer.toString(x));
                else {
                    globVar.fm.createFile2write(globVar.myDir, "drum1.swg");
                    while(!globVar.EOF) {
                            String buf = globVar.fm.rd();
                            globVar.fm.wr(buf + globVar.linSep);
                            //System.out.println("[" + buf +"]" + globVar.EOF); //------------ контрольный вывод
                    }
                    globVar.fm.wrStream.close();
                    globVar.fm.rdStream.close();
                }


        int x = fm.openFile4read(globVar.myDir, "pom.xml");
        nodeXML node = new nodeXML(fm);
        fm.rdStream.close();
        fm.createFile2write(globVar.myDir, "bom.xml");
        node.fprintSelf(fm, "");
        fm.wrStream.close();
*/
