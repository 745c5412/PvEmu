package org.pvemu.models;

import org.pvemu.common.database.Model;

public class NpcTemplate implements Model {
    public int id;
    public int gfxID;
    public short scaleX, scaleY;
    public byte sex;
    public int color1, color2, color3;
    public String accessories;
    public int initQuestion;
    
    
    @Override
    public int getPk() {
        return id;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
