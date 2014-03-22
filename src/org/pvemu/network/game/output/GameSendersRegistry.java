/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pvemu.network.game.output;

/**
 *
 * @author Vincent Quatrevieux <quatrevieux.vincent@gmail.com>
 */
public class GameSendersRegistry {
    
    private static InformativeMessageSender informativeMessage = null;
    private static ObjectSender object = null;
    private static AccountSender account = null;
    private static MapSender map = null;
    private static PlayerSender player = null;
    private static ExchangeSender exchange = null;

    /**
     * Get the value of exchange
     *
     * @return the value of exchange
     */
    public static ExchangeSender getExchange() {
        if(exchange == null)
            exchange = new ExchangeSender();
        return exchange;
    }

    /**
     * Set the value of exchange
     *
     * @param exchange new value of exchange
     */
    public static void setExchange(ExchangeSender exchange) {
        GameSendersRegistry.exchange = exchange;
    }


    /**
     * Get the value of player
     *
     * @return the value of player
     */
    public static PlayerSender getPlayer() {
        if(player == null)
            player = new PlayerSender();
        return player;
    }

    /**
     * Set the value of player
     *
     * @param player new value of player
     */
    public static void setPlayer(PlayerSender player) {
        GameSendersRegistry.player = player;
    }


    /**
     * Get the value of map
     *
     * @return the value of map
     */
    public static MapSender getMap() {
        if(map == null)
            map = new MapSender();
        return map;
    }

    /**
     * Set the value of map
     *
     * @param map new value of map
     */
    public static void setMap(MapSender map) {
        GameSendersRegistry.map = map;
    }


    /**
     * Get the value of account
     *
     * @return the value of account
     */
    public static AccountSender getAccount() {
        if(account == null)
            account = new AccountSender();
        return account;
    }

    /**
     * Set the value of account
     *
     * @param account new value of account
     */
    public static void setAccount(AccountSender account) {
        GameSendersRegistry.account = account;
    }


    /**
     * Get the value of object
     *
     * @return the value of object
     */
    public static ObjectSender getObject() {
        if(object == null)
            object = new ObjectSender();
        return object;
    }

    /**
     * Set the value of object
     *
     * @param object new value of object
     */
    public static void setObject(ObjectSender object) {
        GameSendersRegistry.object = object;
    }


    /**
     * Get the value of informativeMessage
     *
     * @return the value of informativeMessage
     */
    public static InformativeMessageSender getInformativeMessage() {
        if(informativeMessage == null)
            informativeMessage = new InformativeMessageSender();
        return informativeMessage;
    }

    /**
     * Set the value of informativeMessage
     *
     * @param informativeMessage new value of informativeMessage
     */
    public static void setInformativeMessage(InformativeMessageSender informativeMessage) {
        GameSendersRegistry.informativeMessage = informativeMessage;
    }

}
