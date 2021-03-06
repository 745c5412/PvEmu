/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pvemu.network.game.output;

import java.util.Set;
import org.apache.mina.core.session.IoSession;
import org.pvemu.game.objects.item.GameItem;
import org.pvemu.game.objects.item.itemset.ItemSetHandler;
import org.pvemu.models.InventoryEntry;
import org.pvemu.network.game.GamePacketEnum;
import org.pvemu.network.generators.GeneratorsRegistry;

/**
 *
 * @author Vincent Quatrevieux <quatrevieux.vincent@gmail.com>
 */
public class ObjectSender {
    
    public void moveItem(GameItem item, IoSession session){
        moveItem(item.getEntry(), session);
    }
    
    public void quantityChange(GameItem item, IoSession session){
        quantityChange(item.getEntry(), session);
    }
    
    public void addItem(GameItem item, IoSession session){
        addItem(item.getEntry(), session);
    }
    
    public void removeItem(GameItem item, IoSession session){
        removeItem(item.getEntry(), session);
    }
    
    public void moveItem(InventoryEntry entry, IoSession session){
        GamePacketEnum.OBJECT_MOVE.send(
                session,
                GeneratorsRegistry.getObject().generateMoveItem(entry)
        );
    }
    
    public void quantityChange(InventoryEntry entry, IoSession session){
        GamePacketEnum.OBJECT_QUANTITY.send(
                session,
                GeneratorsRegistry.getObject().generateQuantityChange(entry)
        );
    }
    
    public void addItem(InventoryEntry entry, IoSession session){
        GamePacketEnum.OBJECT_ADD_OK.send(
                session,
                GeneratorsRegistry.getObject().generateInventoryEntry(entry)
        );
    }
    
    public void removeItem(InventoryEntry entry, IoSession session){
        GamePacketEnum.OBJECT_REMOVE.send(session, entry.id);
    }
    
    public void itemSets(IoSession session, ItemSetHandler handler, Set<Integer> itemsets){
        for(int itemset : itemsets){
            GamePacketEnum.OBJECT_SET.send(
                    session,
                    GeneratorsRegistry.getObject().generateItemSet(itemset, handler)
            );
        }
    }
    
    public void badLevel(IoSession session){
        GamePacketEnum.OBJECT_ADD_ERROR_LEVEL.send(session);
    }
}
