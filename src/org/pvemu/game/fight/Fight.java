package org.pvemu.game.fight;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import org.pvemu.game.effect.EffectData;
import org.pvemu.game.fight.buttin.FightButtinFactory;
import org.pvemu.game.fight.endactions.EndActionsHandler;
import org.pvemu.common.Constants;
import org.pvemu.common.Loggin;
import org.pvemu.game.fight.fightertype.InvocationFighter;
import org.pvemu.network.game.output.GameSendersRegistry;

/**
 * the main fight class
 * @author Vincent Quatrevieux <quatrevieux.vincent@gmail.com>
 */
abstract public class Fight {
    final private int id;
    final private FightMap map;
    final private FightTeam[] teams;
    final private int initID;
    final private FighterList fighters = new FighterList();
    private byte state;
    private long startTime = 0;
    private ScheduledFuture timer;
    private int lastID = -1;
    private int startCountdown = Constants.START_FIGHT_TIME;
    private ScheduledFuture startCountdownTimer = null;
    
    final static public byte STATE_INIT     = 1;
    final static public byte STATE_PLACE    = 2;
    final static public byte STATE_ACTIVE   = 3;
    final static public byte STATE_FINISHED = 4;

    public Fight(int id, FightMap map, FightTeam[] teams, int initID) {
        this.id = id;
        this.map = map;
        this.teams = teams;
        this.initID = initID;
        state = STATE_INIT;
        GameSendersRegistry.getFight().flagsToMap(map.getMap(), this);
        FightUtils.startCountdownTimer(this);
    }
    
    /**
     * Add a fighter into a team by the team id
     * @param fighter the fighter to add
     * @param teamID the team id
     * @return 0 on success or the error char
     * @see #addToTeam(org.pvemu.game.fight.Fighter, org.pvemu.game.fight.FightTeam) 
     */
    public char addToTeamById(Fighter fighter, int teamID){
        FightTeam team;
        int number = 0;
        
        do{
            team = teams[number++];
        }while(team.getId() != teamID && teams.length < number);
        
        return addToTeam(fighter, team);
    }
    
    /**
     * Add a fighter into the team
     * @param fighter the fighter to add
     * @param team the team
     * @return 0 on success or the error char
     * @see FightTeam#addFighter(org.pvemu.game.fight.Fighter) 
     */
    public char addToTeam(Fighter fighter, FightTeam team){
        char error = team.canAddToTeam(fighter);
        
        if(error != 0)
            return error;
        
        Loggin.debug("new player (%s) into fight (there are %d players)", fighter.getName(), fighters.size());
        team.addFighter(fighter);
        addFighter(fighter);
        
        return 0;
    }
    
    public void addInvocation(InvocationFighter invocation, short cell){
        invocation.setCell(cell);
        fighters.addInvoc(invocation);
        map.addFighter(invocation);
        invocation.enterFight();
    }
    
    private void addFighter(Fighter fighter){
        fighters.add(fighter);
        map.addFighter(fighter);
        fighter.enterFight();
    }

    public FightTeam[] getTeams() {
        return teams;
    }
    
    public void startIfAllReady(){
        if(!fighters.isAllReady())
            return;
        
        startFight();
    }

    public void setStartCountdownTimer(ScheduledFuture startCountdownTimer) {
        this.startCountdownTimer = startCountdownTimer;
    }
    
    public void startFight(){
        if(startCountdownTimer != null)
            startCountdownTimer.cancel(true);
        
        state = STATE_ACTIVE;
        startTime = System.currentTimeMillis();
        GameSendersRegistry.getFight().removeFlags(map.getMap(), id);
        GameSendersRegistry.getFight().startFight(this);
        GameSendersRegistry.getFight().turnList(this);
        nextFighter();
    }
    
    /**
     * End the current fighter turn, an start the turn of the next fighter
     */
    public void nextFighter(){
        if(state == STATE_FINISHED)
            return;
        
        if(timer != null && !timer.isCancelled() && !timer.isDone()){
            timer.cancel(true); //stop the timer if is not stoped
        }
        
        Fighter fighter = fighters.getCurrent();
        
        if(fighter != null){
            fighter.setCanPlay(false);
            fighter.endTurn();
            GameSendersRegistry.getFight().turnEnd(this, fighter.getID());
        }
        
        GameSendersRegistry.getFight().turnMiddle(this);
        
        fighter = fighters.getNext();
        GameSendersRegistry.getFight().turnStart(this, fighter.getID());
        fighter.setCanPlay(true);
        fighter.startTurn();
        
        timer = FightUtils.turnTimer(this);
    }
    
    /**
     * Get the type of the fight
     * @return 
     */
    abstract public byte getType();
    abstract public int spec();
    
    /**
     * @return true if the player can click to ready button
     */
    abstract public boolean canReady();
    /**
     * @return true if the player can cancel the fight
     */
    abstract public boolean canCancel();
    abstract public boolean isHonnorFight();

    public int getStartCountdown() {
        return startCountdown;
    }
    
    public int decrementCountdown(){
        return --startCountdown;
    }

    /**
     * Get the current fight state
     * @return one of the states constants
     */
    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }
    
    public Collection<Fighter> getFighters(){
        return fighters;
    }

    public FightMap getFightMap() {
        return map;
    }

    public int getId() {
        return id;
    }

    public int getInitID() {
        return initID;
    }
    
    public boolean canMove(Fighter fighter, short dest, short nbPM){
        return map.canWalk(dest) && fighter.getNumPM() >= nbPM;
    }
    
    /**
     * Apply the effects (spell or weapon) to fight
     * @param caster the caster
     * @param effects the effects list (of spell or weapon)
     * @param cell the targeted cell
     */
    public void applyEffects(Fighter caster, Set<EffectData> effects, short cell){
        if(effects.isEmpty())
            return;
        
        for(EffectData effect : effects){
            effect.getEffect().applyToFight(effect, this, caster, cell);
        }
    }
    
    protected void onFighterDie(Fighter fighter){
        if(state == STATE_FINISHED)
            return;
        
        Loggin.debug("fighter %s die", fighter.getName());
        map.removeFighter(fighter);
        
        GameSendersRegistry.getEffect().fighterDie(this, fighter.getID());
        fighter.onDie();
        
        if(verifyEndOfGame()){
            endOfGame();
            return;
        }
        
        if(fighter == fighters.getCurrent())
            nextFighter();
    }
    
    /**
     * Check if there are zombies and kill them
     */
    public void checkZombies(){
        for(Fighter fighter : fighters){
            if(fighter.isZombie()){
                onFighterDie(fighter);
            }
        }
    }
    
    /**
     * Verify if all teams are dead except one
     * @return 
     */
    private boolean verifyEndOfGame(){
        int count = 0;
        
        for(FightTeam team : teams){
            if(!team.isAllDead())
                ++count;
        }
        
        return count <= 1;
    }
    
    /**
     * Get the only team witch have at least one player alive
     * @return 
     */
    private FightTeam getWinTeam(){
        for(FightTeam team : teams){
            if(!team.isAllDead())
                return team;
        }
        Loggin.warning("No winner team found !");
        return teams[0];
    }
    
    private void endOfGame(){
        Loggin.debug("end of fight %d", id);
        if(timer != null && !timer.isDone() && !timer.isCancelled())
            timer.cancel(true);
        
        fighters.getCurrent().setCanPlay(false);
        
        state = STATE_FINISHED;
        map.getMap().removeFight(this);
        
        FightUtils.scheduleTask(new Runnable() {
            @Override
            public void run() {
                try{
                    FightTeam winners = getWinTeam();
                    endActions(winners);
                    endRewards(winners);
                }catch(Exception e){
                    Loggin.error("Cannot terminate fight !", e);
                }
            }
        }, 2);
    }
    
    private void endActions(FightTeam winners){
        for(Fighter fighter : fighters){
            fighter.onEnd(fighter.getTeam() == winners);
            EndActionsHandler.instance().applyEndActions(this, fighter, fighter.getTeam() == winners);
        }
    }
    
    private void endRewards(FightTeam winners){
        Collection<FightTeam> loosers = new HashSet<>();
        
        for(FightTeam team : teams){
            if(team != winners)
                loosers.add(team);
        }
        
        for(Fighter fighter : getFighters()){
            fighter.setFightButtin(FightButtinFactory.instance().getButtin(this, fighter, winners, loosers));
        }
        
        GameSendersRegistry.getFight().gameEnd(this, winners.getNumber());
    }
    
    public long getTime(){
        return (System.currentTimeMillis() - startTime) / 1000;
    }
    
    /**
     * Generate a new fighter id
     * @return 
     */
    public int getNewId(){
        return --lastID;
    }
}
