/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package globalData;

import StringTools.StrTools;
import fileTools.FileManager;
import java.util.Date;

/**
 *
 * @author lev
 */
public class globVar {
	public static String linSep;
	public static String myDir;
	public static String desDir;
	//public static FileManager fm;
	public static String backupDir;
	//public static boolean EOF;
	//public static LineList ll = null;
	public static StrTools st;
        public static float simbolWidth = 200;//210
        public static float simbolHeight = 600;
        public static float vertGap = 200; //Вертикальный зазор между блоками
        public static float scale = 400; //масштаб в %
        
        public static float logicBlockWidth = 10; //ширина логических блоков
	
	static String stUuid;
	static long dinUuid = 0;
	
	public static void SetStatUUID() {
		Date date = new Date();
		stUuid = String.format("%020X", date.getTime());
	}
	
	public static String GetUUID() {
		return stUuid + String.format("%012X", dinUuid);
	}
	
	public static String GetNewUUID() {
		dinUuid ++;
		return stUuid + String.format("%012X", dinUuid);
	}

}