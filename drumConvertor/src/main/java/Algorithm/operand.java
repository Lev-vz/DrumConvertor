/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Algorithm;

import fileTools.FileManager;

/**
 *
 * @author lev
 */
public abstract class operand {
        public String opa ="";
        public boolean isNot = false;
        public boolean isNotOp(){ return opa.equals("NOT");};
        public abstract void optimization();
        public abstract String getSelf();
        public abstract formula getFormula();
        public abstract float CalcW();
        public abstract float CalcH();
        public abstract void drawSelf(float x, float y, float h, String id, FileManager fm);
        //public operand getSelf
}
