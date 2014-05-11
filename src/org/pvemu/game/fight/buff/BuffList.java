/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pvemu.game.fight.buff;

import java.util.ArrayList;
import java.util.List;
import org.pvemu.game.fight.Fighter;
import org.pvemu.jelly.Loggin;

/**
 *
 * @author Vincent Quatrevieux <quatrevieux.vincent@gmail.com>
 */
final public class BuffList {
    final private Fighter fighter;
    private List<Buff> buffs = new ArrayList<>();

    public BuffList(Fighter fighter) {
        this.fighter = fighter;
    }
    
    public void applyBuffs(){
        Loggin.debug("Apply buffs for %s", fighter);
        if(buffs.isEmpty())
            return;
        
        List<Buff> newBuffs = new ArrayList<>(buffs.size());
        
        for(Buff buff : buffs){
            if(buff.apply(fighter.getFight(), fighter))
                newBuffs.add(buff);
        }
        
        buffs = newBuffs;
        
        if(!fighter.isAlive())
            fighter.getFight().checkZombies();
    }
    
    public void addBuff(Buff buff){
        buffs.add(buff);
    }
}