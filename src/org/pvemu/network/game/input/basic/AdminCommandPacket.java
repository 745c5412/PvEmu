/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pvemu.network.game.input.basic;

import org.apache.mina.core.session.IoSession;
import org.pvemu.commands.CommandsHandler;
import org.pvemu.commands.askers.ConsoleAsker;
import org.pvemu.game.objects.player.Player;
import org.pvemu.models.Account;
import org.pvemu.network.InputPacket;
import org.pvemu.network.SessionAttributes;

/**
 *
 * @author Vincent Quatrevieux <quatrevieux.vincent@gmail.com>
 */
public class AdminCommandPacket implements InputPacket {

    @Override
    public String id() {
        return "BA";
    }

    @Override
    public void perform(String extra, IoSession session) {
        Account account = SessionAttributes.ACCOUNT.getValue(session);
        Player player = SessionAttributes.PLAYER.getValue(session);

        if (account == null || player == null) {
            session.close(false);
            return;
        }
        
        if(account.level < 1){
            return;
        }

        CommandsHandler.instance().execute(extra, new ConsoleAsker(session, account, player));
        
    }
    
}
