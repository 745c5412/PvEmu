/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pvemu.game.objects.itemlist.entrystate;

import org.apache.mina.core.session.IoSession;
import org.pvemu.common.Loggin;
import org.pvemu.models.InventoryEntry;
import org.pvemu.models.dao.DAOFactory;
import org.pvemu.network.game.output.GameSendersRegistry;

/**
 *
 * @author Vincent Quatrevieux <quatrevieux.vincent@gmail.com>
 */
class StackState extends EntryState {

    StackState(InventoryEntry entry) {
        super(entry);
    }

    @Override
    public void commit(IoSession out) {
        GameSendersRegistry.getObject().quantityChange(entry, out);
        DAOFactory.inventory().update(entry);
        Loggin.debug("Stack de l'item %d", entry.id);
    }
    
}
