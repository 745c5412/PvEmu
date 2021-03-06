package org.pvemu.game.effect.physical;

import org.pvemu.game.fight.Fight;
import org.pvemu.game.fight.Fighter;
import org.pvemu.game.objects.dep.Stats;
import org.pvemu.common.utils.Utils;
import org.pvemu.game.effect.EffectData;
import org.pvemu.game.effect.PhysicalEffect;

/**
 * simple physical attacks
 * @author Vincent Quatrevieux <quatrevieux.vincent@gmail.com>
 */
abstract public class BasicAttackEffect extends PhysicalEffect{

    abstract protected Stats.Element getActiveElement();
    abstract protected Stats.Element getResistanceElement();

    @Override
    public void applyToFighter(int min, int max, Fighter caster, Fighter target) {
        int jet = getJet(min, max, caster, target);
        target.removeVita(jet);
    }
    
    protected int getJet(int min, int max, Fighter caster, Fighter target){
        int jet = Utils.rand(min, max);
        
        jet *= (1 + .01 * (double)(caster.getTotalStats().get(getActiveElement()) + caster.getTotalStats().get(Stats.Element.PERDOM)));
        jet += caster.getTotalStats().get(Stats.Element.DOMMAGE);
        
        jet -= jet * (.01 * (double)(target.getTotalStats().get(getResistanceElement())));
        
        if(jet < 0)
            jet = 0;
        
        return jet;
    }

    @Override
    protected int getEfficiencyForOneFighter(EffectData data, Fight fight, Fighter caster, Fighter target) {
        float avgJet = (data.getMin() + data.getMax()) / 2;
        int coef = 1 + caster.getTotalStats().get(getActiveElement());
        
        if(coef < 1)
            coef = 1;
        
        float res = .01f * target.getTotalStats().get(getResistanceElement());
        
        if(res > 1)
            res = 1;
        
        int efficiency = (int)(10 * avgJet * coef * (1 - res));
        
        if(target.getTeam() == caster.getTeam())
            efficiency = -efficiency;
        
        return efficiency;
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.ATTACK;
    }
}
