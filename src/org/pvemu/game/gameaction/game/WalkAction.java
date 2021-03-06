package org.pvemu.game.gameaction.game;

import java.util.Collection;
import org.pvemu.actions.ActionsRegistry;
import org.pvemu.game.gameaction.GameAction;
import org.pvemu.game.gameaction.GameActionData;
import org.pvemu.game.objects.player.Player;
import org.pvemu.common.Loggin;
import org.pvemu.common.utils.Crypt;
import org.pvemu.common.utils.Pathfinding;
import org.pvemu.common.utils.Utils;
import org.pvemu.network.game.output.GameSendersRegistry;

/**
 *
 * @author Vincent Quatrevieux <quatrevieux.vincent@gmail.com>
 */
public class WalkAction implements GameAction<Player> {

    @Override
    public short id() {
        return GameActionsRegistry.WALK;
    }

    @Override
    public void start(GameActionData<Player> data) {
        Collection<Short> path = Pathfinding.parsePath(data.getPerformer().getMap(), data.getPerformer().getCellId(), data.getArgument(0), false);
        Loggin.debug("path : %s", path);
        
        int steps = path.size();

        Loggin.debug("Tentative de déplacement de %s de %d en %d étapes", data.getPerformer().getName(), data.getPerformer().getCell().getID(), steps);

        if (steps == -1000 || steps == 0) {
            Loggin.debug("Path invalide !");
            GameSendersRegistry.getGameAction().error(data.getPerformer().getSession());
            return;
        }
        
        String newPath = Crypt.compressPath(
                data.getPerformer().getMap(), 
                data.getPerformer().getCellId(), 
                path,
                false
        );

        data.setArgument(0, newPath);

        short id = data.getPerformer().getActionsManager().addGameAction(data);

        GameSendersRegistry.getGameAction().gameActionToMap(
                data.getPerformer().getMap(),
                id,
                data
        );
        data.getPerformer().getActionsManager().setWalking(true);
        data.getPerformer().getActionsManager().clearPendingActions();
    }

    @Override
    public void end(GameActionData<Player> data, boolean success, String[] args) {
        short cellDest;

        if (success) {
            cellDest = Crypt.cellCode_To_ID(data.getArgument(0).substring(data.getArgument(0).length() - 2));
        } else {
            cellDest = Short.parseShort(args[1]);
        }

        ActionsRegistry.getPlayer().arrivedOnCell(data.getPerformer(), data.getPerformer().getMap().getCellById(cellDest));
        data.getPerformer().orientation = Utils.parseBase64Char(data.getArgument(0).charAt(data.getArgument(0).length() - 3));

        data.getPerformer().getActionsManager().setWalking(false);
        data.getPerformer().getActionsManager().performPendingActions();
    }

}
