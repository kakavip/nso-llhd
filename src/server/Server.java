package server;

import io.Message;
import io.Session;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import real.ClanManager;
import real.Map;
import real.MapTemplate;
import real.PlayerManager;
import real.RealController;

/**
 *
 * @author Văn Tú
 */
public class Server {

    private static Server instance = null;
    private ServerSocket listenSocket = null;
    public static boolean start = false;

    public Manager manager;
    public MenuController menu;
    public ServerController controllerManager;
    public Controller serverMessageHandler;

    public Map[] maps;
    public static final Object LOCK_MYSQL = new Object();

    private static final int[] hoursRefreshBoss = new int[]{8,12,15,18,20,23};
    private static final boolean[] isRefreshBoss = new boolean[]{false,false,false,false,false,false};
    private static final short[] mapBossVDMQ = new short[]{141,142,143};
    private static final short[] mapBoss45 = new short[]{14,15,16,34,35,52,68};
    private static final short[] mapBoss55 = new short[]{44,67};
    private static final short[] mapBoss65 = new short[]{24,41,45,59};
    private static final short[] mapBoss75 = new short[]{18,36,54};
    
    private static boolean running = true;
    
    public static Thread run = new Thread (new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                while(running) {
                    synchronized (ClanManager.entrys) {
                        for (int i = ClanManager.entrys.size()-1; i >= 0; i--) {
                            ClanManager clan = ClanManager.entrys.get(i);
                            if (util.compare_Week(Date.from(Instant.now()),util.getDate(clan.week))) {
                                clan.payfeesClan();
                            }
                        }
                    }
                    Calendar rightNow = Calendar.getInstance();
                    int hour = rightNow.get(Calendar.HOUR_OF_DAY);
                    for (int i = 0; i < hoursRefreshBoss.length; i++) {
                        if (hoursRefreshBoss[i] == hour) {
                            if (!isRefreshBoss[i]) {
                                String textchat = "Thần thú đã suất hiện tại";
                                for (byte j = 0; j < util.nextInt(1,1); j++) {
                                    Map map = Manager.getMapid(mapBoss75[util.nextInt(mapBoss75.length)]);
                                    if (map != null) {
                                        map.refreshBoss(util.nextInt(15,29));
                                        textchat += " "+map.template.name;
                                        isRefreshBoss[i] = true;
                                    }
                                }
                                for (byte j = 0; j < util.nextInt(1,2); j++) {
                                    Map map = Manager.getMapid(mapBoss65[util.nextInt(mapBoss65.length)]);
                                    if (map != null) {
                                        map.refreshBoss(util.nextInt(15,30));
                                        textchat += ", "+map.template.name;
                                        isRefreshBoss[i] = true;
                                    }
                                }
                                for (byte j = 0; j < util.nextInt(1,2); j++) {
                                    Map map = Manager.getMapid(mapBoss55[util.nextInt(mapBoss55.length)]);
                                    if (map != null) {
                                        map.refreshBoss(util.nextInt(15,30));
                                        textchat += ", "+map.template.name;
                                        isRefreshBoss[i] = true;
                                    }
                                }
                                for (byte j = 0; j < util.nextInt(1,2); j++) {
                                    Map map = Manager.getMapid(mapBoss45[util.nextInt(mapBoss45.length)]);
                                    if (map != null) {
                                        map.refreshBoss(util.nextInt(15,30));
                                        textchat += ", "+map.template.name;
                                        isRefreshBoss[i] = true;
                                    }
                                }
                                for (byte j = 0; j < mapBossVDMQ.length; j++) {
                                    Map map = Manager.getMapid(mapBossVDMQ[j]);
                                    if (map != null) {
                                        map.refreshBoss(util.nextInt(15,30));
                                        textchat += ", "+map.template.name;
                                        isRefreshBoss[i] = true;
                                    }
                                }
                                Manager.chatKTG(textchat);
                            }
                        } else {
                            isRefreshBoss[i] = false;
                        }
                    }
                    Thread.sleep(1000);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }));
    
    private void init() {
        manager = new Manager();
        menu = new MenuController();
        controllerManager = new RealController();
        serverMessageHandler = new Controller();
    }

    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
            instance.init();
            BXHManager.init();
            run.start();
        }
        return instance;
    }

    public static void main(String[] args) {
        start = true;
        getInstance().run();
    }

    public void run() {
        maps = new Map[MapTemplate.arrTemplate.length];
        for (short i = 0; i < maps.length; i++) {
            maps[i] = new Map(i, null);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Shutdown Server!");
                stop();
            }
        }));
        listenSocket = null;
        try {
            listenSocket = new ServerSocket(manager.post);
            System.out.println("Listen " + manager.post);
            while (start) {
                Socket clientSocket = listenSocket.accept();
                Session conn = new Session(clientSocket, serverMessageHandler);
                PlayerManager.getInstance().put(conn);
                conn.start();
                System.out.println("Accept socket size :" + PlayerManager.getInstance().conns_size());
            }
        } catch (BindException bindEx) {
            System.exit(0);
        } catch (IOException genEx) {
            genEx.printStackTrace();
        }
        try {
            if (listenSocket != null) {
                listenSocket.close();
            }
            System.out.println("Close server socket");
        } catch (Exception ioEx) {
        }
    }

    public void stop() {
        if (start) {
            start = false;
            try {
                listenSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            //Kick all Player game
            PlayerManager.getInstance().Clear();
            ClanManager.close();
            manager.close();
            manager = null;
            PlayerManager.getInstance().close();
            menu = null;
            controllerManager = null;
            serverMessageHandler = null;
            SQLManager.close();
            System.gc();
        }
    }
}
