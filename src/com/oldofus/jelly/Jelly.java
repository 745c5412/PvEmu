package com.oldofus.jelly;

import com.oldofus.game.World;
import com.oldofus.game.objects.dep.Stats;
import com.oldofus.jelly.Shell.GraphicRenditionEnum;
import com.oldofus.jelly.database.Database;
import com.oldofus.jelly.scripting.API;
import com.oldofus.jelly.utils.SystemStats;
import com.oldofus.models.dao.DAOFactory;
import com.oldofus.network.game.GameServer;
import com.oldofus.network.realm.RealmServer;

public class Jelly {

    public static boolean DEBUG = false;
    public static long start;
    public static boolean running = true;

    public static void main(String[] args) {
        Shell.setTitle(Constants.NAME);
        Shell.println("======================================================", GraphicRenditionEnum.YELLOW);
        printAsciiLogo();
        Shell.print("\t\t\t" + Constants.NAME + " by ", GraphicRenditionEnum.YELLOW);
        Shell.println("v4vx", GraphicRenditionEnum.YELLOW, GraphicRenditionEnum.BOLD);
        Shell.println("Version " + Constants.VERSION + " (r" + Constants.REV + ")", GraphicRenditionEnum.YELLOW);
        Shell.println("======================================================", GraphicRenditionEnum.YELLOW);

        if (args.length == 0) {
            start();
        } else {
            switch (args[0].toLowerCase()) {
                case "?":
                case "help":
                    System.out.println("\nAide Jelly CLI :");
                    System.out.println("----------------\n");
                    System.out.println("HELP                  : affiche ce menu");
                    System.out.println("SET [param] [value]   : Ajoute / change la configuration de [param]");
                    System.out.println("DEBUG                 : lancer l'émulateur en mode débug");
                    break;
                case "debug":
                    System.out.println("Mode DEBUG actif");
                    DEBUG = true;
                    start();
                    break;
                case "set":
                    if (args.length < 2) {
                        System.out.println("Arguments manquants");
                        return;
                    }
                    Config.set(args[1], args[2]);
                    break;
            }
        }
    }

    private static void start() {
        start = System.currentTimeMillis();
        Config.load();
        Database.connect();
        World.instance().create(Config.getBool("preload"));
        RealmServer.start();
        GameServer.start();
        API.initialise();
        Shell.println("Serveur lancé en " + (System.currentTimeMillis() - start) + "ms", GraphicRenditionEnum.GREEN, GraphicRenditionEnum.BOLD);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                Jelly.close();
            }
        });
        SystemStats.displayStatsAtFixedRate();
    }

    private static void printAsciiLogo() {
        Shell.println(
                "   ___      _ _       _____\n"
                + "  |_  |    | | |     |  ___|\n"
                + "    | | ___| | |_   _| |__ _ __ ___  _   _\n"
                + "    | |/ _ \\ | | | | |  __| '_ ` _ \\| | | |\n"
                + "/\\__/ /  __/ | | |_| | |__| | | | | | |_| |\n"
                + "\\____/ \\___|_|_|\\__, \\____/_| |_| |_|\\__,_|\n"
                + "                 __/ |\n"
                + "                |___/\n",
                GraphicRenditionEnum.YELLOW, GraphicRenditionEnum.BOLD);
    }

    private static void close() {
        if (running) {
            running = false;
            Shell.println("Arrêt du serveur en cours...", GraphicRenditionEnum.RED);
            World.instance().destroy();
            Database.close();
            RealmServer.stop();
            GameServer.stop();
            Shell.println("JellyEmu : arrêt", GraphicRenditionEnum.RED);
            Shell.flush();
        }else{
            Shell.println("Serveur déjà en cours d'arrêt...", GraphicRenditionEnum.RED);
        }
    }
}
