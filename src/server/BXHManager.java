package server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Timer;
import real.ClanManager;
import real.ItemData;

/**
 *
 * @author ghost
 */
public class BXHManager {

    public static class Entry {
        int index;
        String name;
        long[] nXH;
    }
    
    public static final ArrayList<Entry> bangXH[] = new ArrayList[4];
    public static final Timer t = new Timer(true);
    
    public static void init() {
        for(int i = 0; i < bangXH.length; i++)
            bangXH[i] = new ArrayList<>();
        System.out.println("load BXH");
        for(int i = 0; i < bangXH.length; i++)
            initBXH(i);
//        Calendar cl = GregorianCalendar.getInstance();
//        Date d = new Date();cl.setTime(d);
//        cl.set(Calendar.HOUR_OF_DAY, 0);
//        cl.set(Calendar.MINUTE, 0);
//        cl.set(Calendar.SECOND, 0);
//        cl.add(Calendar.DATE, 0);
//        t.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                System.out.println("Refresh BXH");
//            }
//        }, cl.getTime(), 1000*60*10);
    }
    
    public static void initBXH(int type) {
        ResultSet red;
        bangXH[type].clear();
        ArrayList<Entry> bxh = bangXH[type];
        switch(type) {
            case 0:
                try {
                    int i = 1;
                    red = SQLManager.stat.executeQuery("SELECT `name`,`yen`,`level` FROM `ninja` WHERE (`yen` > 0) ORDER BY `yen` DESC LIMIT 10;");
                    while(red.next()) {
                        String name = red.getString("name");
                        int coin = red.getInt("yen");
                        int level = red.getInt("level");
                        Entry bXHE = new Entry();
                        bXHE.nXH = new long[2];
                        bXHE.name = name;
                        bXHE.index = i;
                        bXHE.nXH[0] = coin;
                        bXHE.nXH[1] = level;
                        bxh.add(bXHE);
                        i++;
                    }
                    red.close();
                } catch(SQLException e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                try {
                    int i = 1;
                    red = SQLManager.stat.executeQuery("SELECT `name`,`exp`,`level` FROM `ninja` WHERE (`exp` > 0) ORDER BY `exp` DESC LIMIT 10;");
                    while(red.next()) {
                        String name = red.getString("name");
                        long exp = red.getLong("exp");
                        int level = red.getInt("level");
                        Entry bXHE = new Entry();
                        bXHE.nXH = new long[2];
                        bXHE.name = name;
                        bXHE.index = i;
                        bXHE.nXH[0] = exp;
                        bXHE.nXH[1] = level;
                        bxh.add(bXHE);
                        i++;
                        }
                    red.close();
                } catch(SQLException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                try {
                    int i = 1;
                    red = SQLManager.stat.executeQuery("SELECT `name`,`level` FROM `clan` WHERE (`level` > 0) ORDER BY `level` DESC LIMIT 10;");
                    while(red.next()) {
                        String name = red.getString("name");
                        int level = red.getInt("level");
                        Entry bXHE = new Entry();
                        bXHE.nXH = new long[1];
                        bXHE.name = name;
                        bXHE.index = i;
                        bXHE.nXH[0] = level;
                        bxh.add(bXHE);
                        i++;
                        }
                    red.close();
                } catch(SQLException e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                try {
                    int i = 1;
                    red = SQLManager.stat.executeQuery("SELECT `name`,`bagCaveMax`,`itemIDCaveMax` FROM `ninja` WHERE (`bagCaveMax` > 0) ORDER BY `bagCaveMax` DESC LIMIT 10;");
                    while(red.next()) {
                        String name = red.getString("name");
                        int cave = red.getInt("bagCaveMax");
                        short id = red.getShort("itemIDCaveMax");
                        Entry bXHE = new Entry();
                        bXHE.nXH = new long[2];
                        bXHE.name = name;
                        bXHE.index = i;
                        bXHE.nXH[0] = cave;
                        bXHE.nXH[1] = id;
                        bxh.add(bXHE);
                        i++;
                        }
                    red.close();
                } catch(SQLException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
    
    public static final Entry[] getBangXH(int type) {
        ArrayList<Entry> bxh = bangXH[type];
        Entry[] bxhA = new Entry[bxh.size()];
        for(int i = 0; i < bxhA.length; i++)
            bxhA[i] = bxh.get(i);
        return bxhA;
    }
    
    public static String getStringBXH(int type) {
        String str = "";
        switch (type) {
            case 0:
                if (bangXH[type].isEmpty()) {
                    str = "Chưa có thông tin";
                } else {
                    for (Entry bxh : bangXH[type]) {
                        str += bxh.index+". "+bxh.name+": "+util.getFormatNumber(bxh.nXH[0])+" yên - cấp: "+bxh.nXH[1]+"\n";
                    }
                    break;
                }
                break;
            case 1:
                if (bangXH[type].isEmpty()) {
                    str = "Chưa có thông tin";
                } else {
                    for (Entry bxh : bangXH[type]) {
                        str += bxh.index+". "+bxh.name+": "+util.getFormatNumber(bxh.nXH[0])+" kinh nghiệm - cấp: "+bxh.nXH[1]+"\n";
                    }
                }
                break;
            case 2:
                if (bangXH[type].isEmpty()) {
                    str = "Chưa có thông tin";
                } else {
                    for (Entry bxh : bangXH[type]) {
                        ClanManager clan = ClanManager.getClanName(bxh.name);
                        if (clan != null)
                            str += bxh.index+". Gia tộc "+bxh.name+" trình độ cấp "+bxh.nXH[0]+" do "+clan.getmain_name()+" làm tộc trưởng, thành viên "+clan.members.size()+"/"+clan.getMemMax()+"\n";
                        else
                            str += bxh.index+". Gia tộc "+bxh.name+" trình độ cấp "+bxh.nXH[0]+" đã bị giải tán\n";
                    }
                }
                break;
            case 3:
                if (bangXH[type].isEmpty()) {
                    str = "Chưa có thông tin";
                } else {
                    for (Entry bxh : bangXH[type]) {
                        str += bxh.index+". "+bxh.name+" nhận được "+util.getFormatNumber(bxh.nXH[0])+" "+ItemData.ItemDataId((int)bxh.nXH[1]).name+"\n";
                    }
                }
                break;
        }
        return str;
    }
    
}
