package boardGame;

import io.Message;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import real.Body;
import real.Effect;
import real.Item;
import real.ItemData;
import real.Level;
import real.Option;
import real.Map;
import real.Mob;
import real.Char;
import real.Npc;
import real.Party;
import real.Player;
import real.Skill;
import real.SkillData;
import real.SkillTemplates;
import real.Vgo;
import server.GameCanvas;
import server.GameScr;
import server.Manager;
import server.Server;
import server.Service;
import server.util;

/**
 *
 * @author Văn Tú
 */
public class Place {

    public Map map;
    protected byte id;
    public byte numplayers = 0;
    private int numTA = 0;
    private int numTL = 0;
    protected int numMobDie = 0;
    public final ArrayList<Player> players = new ArrayList<>();
    public final ArrayList<Mob> mobs = new ArrayList<>();
    private final ArrayList<ItemMap> itemMap = new ArrayList<>();
    Server server = Server.getInstance();

    public Place(Map map, byte id) {
        this.map = map;
        this.id = id;
    }

    public void sendMessage(Message m) {
        try {
            for (int i = players.size()-1; i >= 0; i--) {
                players.get(i).conn.sendMessage(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMyMessage(Player p, Message m) {
        for (int i = players.size()-1; i >= 0; i--) {
            if (p.id != players.get(i).id) {
                players.get(i).conn.sendMessage(m);
            }
        }
    }

    public Mob getMob(int id) {
        for (short i = 0; i < mobs.size(); i++) {
            if (mobs.get(i).id == id) {
                return mobs.get(i);
            }
        }
        return null;
    }

    public ArrayList getArryListParty() {
        synchronized (this) {
            ArrayList<Party> partys = new ArrayList<>();
            for (int i = 0; i < players.size(); i++) {
                Player p = players.get(i);
                if (p.c.get().party != null) {
                    boolean co = true;
                    for (int j = 0; j < partys.size(); j ++) {
                        if (p.c.get().party.id == partys.get(j).id) {
                            co = false;
                            break;
                        }
                    }
                    if (co)
                        partys.add(p.c.get().party);
                }
            }
            return partys;
        }
    }

    public Char getNinja(int id) {
        synchronized (this) {
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).c.id == id) {
                    return players.get(i).c;
                }
            }
            return null;
        }
    }

    private short getItemMapNotId() {
        short itemmapid = 0;
        while (true) {
            boolean isset = false;
            for (int i = itemMap.size()-1; i >= 0; i--) {
                if (itemMap.get(i).itemMapId == itemmapid) {
                    isset = true;
                }
            }
            if (!isset) {
                return itemmapid;
            }
            itemmapid++;
        }
    }

    public void leave(Player p) {
        synchronized (this) {
            if (map.cave != null && map.cave.ninjas.contains(p.c)) {
                map.cave.ninjas.remove(p.c);
            }
            if (players.contains(p)) {
                players.remove(p);
                removeMessage(p.c.id);
//                removeMessage(p.c.clone.id);
                numplayers--;
            }
        }
    }

    public void changerTypePK(Player p, Message m) throws IOException {
        if (p.c.isNhanban) {
            p.sendAddchatYellow("Bạn đang trong chế độ thứ thân không thể dùng được chức năng này");
            return;
        }
        byte pk = m.reader().readByte();
        m.cleanup();
        if (p.c.pk > 14){
            p.sendAddchatYellow("Điểm hiếu chiến quá cao không thể thay đổi chế độ pk");
            return;
        }
        if (pk < 0 || pk > 3) {
            return;
        }
        p.c.typepk = pk;
        m = new Message(-30);
        m.writer().writeByte(-92);
        m.writer().writeInt(p.c.id);
        m.writer().writeByte(pk);
        sendMessage(m);
        m.cleanup();
    }


    public void sendCoat(Body b, Player pdo) {
        try {
            if (b.ItemBody[12] == null)
                return;
            Message m = new Message(-30);
            m.writer().writeByte((72-128));
            m.writer().writeInt(b.id);
            m.writer().writeInt(b.hp);
            m.writer().writeInt(b.getMaxHP());
            m.writer().writeShort(b.ItemBody[12].id);
            m.writer().flush();
            pdo.conn.sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendGlove(Body b, Player pdo) {
        try {
            if (b.ItemBody[13] == null)
                return;
            Message m = new Message(-30);
            m.writer().writeByte((73-128));
            m.writer().writeInt(b.id);
            m.writer().writeInt(b.hp);
            m.writer().writeInt(b.getMaxHP());
            m.writer().writeShort(b.ItemBody[13].id);
            m.writer().flush();
            pdo.conn.sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMounts(Body b, Player pdo) {
        try {
            Message m = new Message(-30);
            m.writer().writeByte(-54);
            m.writer().writeInt(b.id);//id ninja
            for (byte i = 0; i < 5; i++) {
                Item item = b.ItemMounts[i];
                if (item != null) {
                    m.writer().writeShort(item.id);
                    m.writer().writeByte(item.upgrade);//cap
                    m.writer().writeLong(item.expires);//het han
                    m.writer().writeByte(item.sys);//thuoc tinh
                    m.writer().writeByte(item.options.size());//lent option
                    for (Option Option : item.options) {
                        m.writer().writeByte(Option.id);
                        m.writer().writeInt(Option.param);
                    }
                } else {
                    m.writer().writeShort(-1);
                }
            }
            m.writer().flush();
            pdo.conn.sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Chat(Player p, Message m) throws IOException {
        String chat = m.reader().readUTF();
        if (chat.equals("baotri")) {
            server.stop();
            return;
        }
        m.cleanup();
        m = new Message(-23);
        m.writer().writeInt(p.c.get().id);
        m.writer().writeUTF(chat);
        m.writer().flush();
        sendMessage(m);
        m.cleanup();
    }

    public void EnterMap0(Char n) {
//        n.clone.x = n.x = map.template.x0;
//        n.clone.y = n.y = map.template.y0;
        n.mapid = map.id;
        try {
            Enter(n.p);
        } catch (IOException e) {}
    }

    public void Enter(Player p) throws IOException {
        synchronized(this) {
            players.add(p);
            p.c.place = this;
            numplayers++;
            p.c.mobAtk = -1;
            p.c.eff5buff = System.currentTimeMillis()+5000L;
            if (map.cave != null) {
                map.cave.ninjas.add(p.c);
            }
            if (map.timeMap != -1) {
                p.setTimeMap((int)(map.cave.time-System.currentTimeMillis())/1000);
            }
            Message m = new Message(57);
            m.writer().flush();
            p.conn.sendMessage(m);
            m = new Message(-18);
            m.writer().writeByte(map.id);//map id
            m.writer().writeByte(map.template.tileID);//tile id
            m.writer().writeByte(map.template.bgID);//bg id
            m.writer().writeByte(map.template.typeMap);//type map
            m.writer().writeUTF(map.template.name);//name map
            m.writer().writeByte(id);//zone
            m.writer().writeShort(p.c.get().x);//X
            m.writer().writeShort(p.c.get().y); // Y
            m.writer().writeByte(map.template.vgo.length);// vgo
            for (byte i = 0; i < map.template.vgo.length; i++) {
                m.writer().writeShort(map.template.vgo[i].minX);//x
                m.writer().writeShort(map.template.vgo[i].minY);//y
                m.writer().writeShort(map.template.vgo[i].maxX);//xnext
                m.writer().writeShort(map.template.vgo[i].maxY);//ynext
            }
            m.writer().writeByte(mobs.size());// mob
            for (short i = 0; i < mobs.size(); i++) {
                Mob mob = mobs.get(i);
                m.writer().writeBoolean(mob.isDisable());//isDisable
                m.writer().writeBoolean(mob.isDonteMove());//isDontMove
                m.writer().writeBoolean(mob.isFire);//isFire
                m.writer().writeBoolean(mob.isIce);//isIce
                m.writer().writeBoolean(mob.isWind);//isWind
                m.writer().writeByte(mob.templates.id);//id templates
                m.writer().writeByte(mob.sys);//sys
                m.writer().writeInt(mob.hp);//hp
                m.writer().writeByte(mob.level);//level
                m.writer().writeInt(mob.hpmax);//hp max
                m.writer().writeShort(mob.x);//x
                m.writer().writeShort(mob.y);//y
                m.writer().writeByte(mob.status);//status
                m.writer().writeByte(mob.lvboss);//level boss
                m.writer().writeBoolean(mob.isboss);//isBosss
            }
            m.writer().writeByte(0); //
            for (int i = 0; i < 0; i++) {
                m.writer().writeUTF("khúc gỗ");//name
                m.writer().writeShort(1945);//x
                m.writer().writeShort(240);//y
            }
            m.writer().writeByte(map.template.npc.length);//numb npc
            for (Npc npc : map.template.npc) {
                m.writer().writeByte(npc.type); //type
                m.writer().writeShort(npc.x); //x
                m.writer().writeShort(npc.y); //y
                m.writer().writeByte(npc.id); //id
            }
            m.writer().writeByte(itemMap.size());// item map
            for (int i = 0; i < itemMap.size(); i++) {
                ItemMap im = itemMap.get(i);
                m.writer().writeShort(im.itemMapId); //item map id
                m.writer().writeShort(im.item.id); //id item
                m.writer().writeShort(im.x); //x
                m.writer().writeShort(im.y); //y
            }
            m.writer().writeUTF(map.template.name);//name zone
            m.writer().writeByte(0);// item
            m.writer().flush();
            p.conn.sendMessage(m);
            m.cleanup();
            //Send Info team to me
            for (int i = players.size()-1; i >= 0; i--) {
                Player player = players.get(i);
                if (player.id != p.id) {
                    sendCharInfo(player, p);
                    sendCoat(player.c.get(), p);
                    sendGlove(player.c.get(), p);
                }
//                if (!player.c.isNhanban
//                    && !player.c.clone.isDie
//                ) {
//                    Service.sendclonechar(player, p);
//                }
                sendMounts(player.c.get(), p);
            }
            //Send Info do team
            for (int i = players.size()-1; i >= 0; i--) {
                Player player = players.get(i);
                if (player.id != p.id) {
                    sendCharInfo(p, player);
                    sendCoat(p.c.get(), player);
                    sendGlove(p.c.get(), player);
                    if (!player.c.isNhanban && p.c.timeRemoveClone > System.currentTimeMillis()) {
                        Service.sendclonechar(p, player);
                    }
                }
                sendMounts(p.c.get(), player);
            }
            if (p.c.level == 1) {
               //for (byte n = 0; n < 15; n++)
              //  p.c.addItemBag(false, ItemData.itemDefault(384));
               p.updateExp(Level.getMaxExp(130)-p.c.exp);
               // p.upluongMessage(500000);
                p.c.upxuMessage(500000000);
               // p.c.upyenMessage(500000000);

            }
            if (util.compare_Day(Date.from(Instant.now()),p.c.newlogin)) {
                p.c.pointCave = 0;
                p.c.nCave = 1;
                p.c.useCave = 5;
                p.c.ddClan = false;
                p.c.newlogin = Date.from(Instant.now());
            }
        }
    }

    public void VGo(Player p, Message m) throws IOException {
        m.cleanup();
        for (byte i = 0; i < map.template.vgo.length; i++) {
            Vgo vg = map.template.vgo[i];
            if (p.c.get().x + 100 >= vg.minX && p.c.get().x <= vg.maxX + 100 && p.c.get().y + 100 >= vg.minY && p.c.get().y <= vg.maxY + 100) {
                leave(p);
                int mapid;
                if (map.id == 138)
                    mapid = new int[]{134,135,136,137}[util.nextInt(4)];
                else
                    mapid = vg.mapid;
                Map ma = Manager.getMapid(mapid);
                if (map.cave != null) {
                    for (byte j = 0; j < map.cave.map.length; j++) {
                        if (map.cave.map[j].id == mapid) {
                            ma = map.cave.map[j];
                        }
                    }
                }
                for (byte j = 0; j < ma.template.vgo.length; j++) {
                    Vgo vg2 = ma.template.vgo[j];
                    if (vg2.mapid == map.id) {
                        p.c.get().x = (short) (vg2.goX);
                        p.c.get().y = (short) (vg2.goY);
                    }
                }
                byte errornext = -1;
                for (byte n = 0; n < p.c.get().ItemMounts.length; n++) {
                    if (p.c.get().ItemMounts[n] != null && p.c.get().ItemMounts[n].isExpires && p.c.get().ItemMounts[n].expires < System.currentTimeMillis()) {
                        errornext = 1;
                    }
                }
                if (map.cave != null && map.getXHD() < 9 && map.cave.map.length > map.cave.level && map.cave.map[map.cave.level].id < mapid) {
                    errornext = 2;
                }
                if (errornext == -1)
                    for (byte j = 0; j < ma.area.length; j++) {
                        if (ma.area[j].numplayers < ma.template.maxplayers) {
                            if (map.id == 138) {
                                ma.area[j].EnterMap0(p.c);
                            } else {
                                p.c.mapid = mapid;
                                p.c.x = vg.goX;
                                p.c.y = vg.goY;
//                                p.c.clone.x = p.c.x;
//                                p.c.clone.y = p.c.y;
                                ma.area[j].Enter(p);
                            }
                            return;
                        }
                        if (j == ma.area.length - 1) {
                            errornext = 0;
                        }
                    }
                Enter(p);
                switch (errornext) {
                    case 0:
                        p.conn.sendMessageLog("Bản đồ quá tải.");
                        return;
                    case 1:
                        p.conn.sendMessageLog("Trang bị thú cưới đã hết hạn. Vui lòng tháo ra để di chuển");
                        return;
                    case 2:
                        p.conn.sendMessageLog("Cửa "+ma.template.name+" vẫn chưa mở");
                        return;
                }
            }
        }
    }

   public void moveMessage(Player p, Message m) throws IOException {
        short x, y, xold, yold;
        if (p.c.get().getEffId(18) != null)
            return;
        xold = p.c.get().x;
        yold = p.c.get().y;
        x = m.reader().readShort();
        y = m.reader().readShort();
        p.c.x = x;
        p.c.y = y;
        if (p.c.isNhanban) {
            p.c.clone.x = x;
            p.c.clone.y = y;
        }
        m.cleanup();
        move(p.c.get().id, p.c.get().x, p.c.get().y);
  /*     m = new Message(-23);
       m.writer().writeInt(p.nj.id);
        m.writer().writeUTF("X="+p.nj.x+"\nY="+p.nj.y);
        m.writer().flush();
       sendMessage(m);
       m.cleanup();*/
    }

   public void move(int id, short x, short y) {
       try {
            Message m = new Message(1);
            m.writer().writeInt(id);
            m.writer().writeShort(x);
            m.writer().writeShort(y);
            m.writer().flush();
            sendMessage(m);
            m.cleanup();
       } catch (IOException e) {
           e.printStackTrace();
       }
   }

    public void removeItemMapMessage(short itemmapid) throws IOException {
        Message m = new Message(-15);
        m.writer().writeShort(itemmapid);
        m.writer().flush();
        sendMessage(m);
        m.cleanup();
    }

    public synchronized void pickItem(Player p, Message m) throws IOException {
        if (m.reader().available() == 0) {
            return;
        }
        short itemmapid = m.reader().readShort();
        m.cleanup();
        for (short i = 0; i < itemMap.size(); i++) {
            if (itemMap.get(i).itemMapId == itemmapid) {
                ItemMap itemmap = itemMap.get(i);
                Item item = itemmap.item;
                ItemData data = ItemData.ItemDataId(item.id);
                if (itemmap.master != -1 && itemmap.master != p.c.id) {
                    p.sendAddchatYellow("Vật phẩm của người khác.");
                    return;
                } else if (Math.abs(itemmap.x-p.c.get().x) > 1000 || Math.abs(itemmap.y-p.c.get().y) > 1000) {
                    p.sendAddchatYellow("Khoảng cách quá xa.");
                    return;
                } else if (data.type == 19 || p.c.getBagNull() > 0 || (p.c.getIndexBagid(item.id, item.isLock) != -1 && data.isUpToUp)) {
                    itemMap.remove(i);
                    m = new Message(-13);
                    m.writer().writeShort(itemmap.itemMapId);
                    m.writer().writeInt(p.c.get().id);
                    m.writer().flush();
                    sendMyMessage(p, m);
                    m.cleanup();
                    m = new Message(-14);
                    m.writer().writeShort(itemmap.itemMapId);
                    if (ItemData.ItemDataId(item.id).type == 19) {
                        p.c.upyen(item.quantity);
                        m.writer().writeShort(item.quantity);
                    }
                    m.writer().flush();
                    p.conn.sendMessage(m);
                    m.cleanup();
                    if (ItemData.ItemDataId(item.id).type != 19) {
                        p.c.addItemBag(true, itemmap.item);
                    }
                    break;
               } else {
                    p.conn.sendMessageLog("Hành trang không đủ chỗ trống.");
                }
            }
        }
    }

    public void leaveItemBackground(Player p, Message m) throws IOException {
        byte index = m.reader().readByte();
        m.cleanup();
        Item itembag = p.c.getIndexBag(index);
        if (itembag == null || itembag.isLock) {
            return;
        }
        if (itemMap.size() > 100) {
            removeItemMapMessage(itemMap.remove(0).itemMapId);
        }
        short itemmapid = getItemMapNotId();
        ItemMap item = new ItemMap();
        item.x = p.c.get().x;
        item.y = p.c.get().y;
        item.itemMapId = itemmapid;
        item.item = itembag;
        itemMap.add(item);
        p.c.ItemBag[index] = null;
        m = new Message(-6);
        m.writer().writeInt(p.c.get().id);
        m.writer().writeShort(item.itemMapId);
        m.writer().writeShort(item.item.id);
        m.writer().writeShort(item.x);
        m.writer().writeShort(item.y);
        m.writer().flush();
        sendMyMessage(p, m);
        m.cleanup();
        m = new Message(-12);
        m.writer().writeByte(index);
        m.writer().writeShort(item.itemMapId);
        m.writer().writeShort(item.x);
        m.writer().writeShort(item.y);
        m.writer().flush();
        p.conn.sendMessage(m);
        m.cleanup();
    }

    public void refreshMob(int mobid)  {
        try {
            synchronized (this) {
                Mob mob = getMob(mobid);
                mob.ClearFight();
                mob.sys = (byte) util.nextInt(1,3);
                if (map.cave == null && mob.lvboss !=3 && !mob.isboss) {
                    if (mob.lvboss > 0)
                        mob.lvboss = 0;
                    if (mob.level >= 10 && 1 > util.nextInt(100) && numTA < 2 && numTL < 1)
                        mob.lvboss = util.nextInt(1,2);
                }
                if (map.cave != null && map.cave.finsh > 0 && map.getXHD() == 6) {
                    int hpup = mob.templates.hp*((10*map.cave.finsh)+100)/100;
                    mob.hp = mob.hpmax = hpup;
                } else {
                    mob.hp = mob.hpmax = mob.templates.hp;
                }
                if (mob.lvboss == 3)
                    mob.hp = mob.hpmax *= 200;
                else if (mob.lvboss == 2) {
                    numTL++;
                    mob.hp = mob.hpmax *= 100;
                }
                else if (mob.lvboss == 1) {
                    numTA++;
                    mob.hp = mob.hpmax *= 10;
                }
                mob.status = 5;
                mob.isDie = false;
                mob.timeRefresh = 0;
                Message m = new Message(-5);
                m.writer().writeByte(mob.id);
                m.writer().writeByte(mob.sys);
                m.writer().writeByte(mob.lvboss);
                m.writer().writeInt(mob.hpmax);
                m.writer().flush();
                sendMessage(m);
                m.cleanup();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void attachedMob(int dame, int mobid, boolean fatal) throws IOException {
        Message m = new Message(-1);
        m.writer().writeByte(mobid);
        Mob mob = getMob(mobid);
        m.writer().writeInt(mob.hp);
        m.writer().writeInt(dame);
        m.writer().writeBoolean(fatal);//flag
        m.writer().writeByte(mob.lvboss);
        m.writer().writeInt(mob.hpmax);
        m.writer().flush();
        sendMessage(m);
        m.cleanup();
    }

    private void MobStartDie(int dame, int mobid, boolean fatal) throws IOException {
        Mob mob = getMob(mobid);
        Message m = new Message(-4);
        m.writer().writeByte(mobid);
        m.writer().writeInt(dame);
        m.writer().writeBoolean(fatal);//flag
        m.writer().flush();
        sendMessage(m);
        m.cleanup();
    }

    private void sendXYPlayer(Player p) throws IOException {
        Message m = new Message(52);
        m.writer().writeShort(p.c.get().x);
        m.writer().writeShort(p.c.get().y);
        m.writer().flush();
        p.conn.sendMessage(m);
        m.cleanup();
    }

    private void setXYPlayers(short x, short y,Player p1, Player p2) throws IOException {
        p1.c.get().x = p2.c.get().x = x;
        p1.c.get().y = p2.c.get().y = y;
        Message m = new Message(64);
        m.writer().writeInt(p1.c.get().id);
        m.writer().writeShort(p1.c.get().x);
        m.writer().writeShort(p1.c.get().y);
        m.writer().writeInt(p2.c.get().id);
        m.writer().flush();
        sendMessage(m);
        m.cleanup();
    }

    public void removeMessage(int id) {
        try {
            Message m = new Message(2);
            m.writer().writeInt(id);
            m.writer().flush();
            sendMessage(m);
            m.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void sendCharInfo(Player p, Player p2) {
        try {
            Message m = new Message(3);
            m.writer().writeInt(p.c.get().id);//id ninja
            m.writer().writeUTF(p.c.clan.clanName);//clan name
            if (!p.c.clan.clanName.isEmpty()) {
                m.writer().writeByte(p.c.clan.typeclan);//type clan
            }
            m.writer().writeBoolean(false);//isInvisible
            m.writer().writeByte(p.c.get().typepk);// type pk
            m.writer().writeByte(p.c.get().nclass);// class
            m.writer().writeByte(p.c.gender);// gender
            m.writer().writeShort(p.c.get().partHead());//head
            m.writer().writeUTF(p.c.name);//name
            m.writer().writeInt(p.c.get().hp);//hp
            m.writer().writeInt(p.c.get().getMaxHP());//hp max
            m.writer().writeByte(p.c.get().level);//level
            m.writer().writeShort(p.c.get().Weapon());//vu khi
            m.writer().writeShort(p.c.get().Body());// body
            m.writer().writeShort(p.c.get().Leg());//leg
            m.writer().writeByte(-1);//mob
            m.writer().writeShort(p.c.get().x);// X
            m.writer().writeShort(p.c.get().y);// Y
            m.writer().writeShort(p.c.get().eff5buffHP());//eff5BuffHp
            m.writer().writeShort(p.c.get().eff5buffMP());//eff5BuffMP
            m.writer().writeByte(0);
            m.writer().writeBoolean(p.c.isHuman); // human
            m.writer().writeBoolean(p.c.isNhanban); // nhan ban
            m.writer().writeShort(p.c.get().partHead());
            m.writer().writeShort(p.c.get().Weapon());
            m.writer().writeShort(p.c.get().Body());
            m.writer().writeShort(p.c.get().Leg());
            m.writer().flush();
            p2.conn.sendMessage(m);
            m.cleanup();
            if (p.c.get().mobMe != null) {
                m = new Message(-30);
                m.writer().writeByte(-68);
                m.writer().writeInt(p.c.get().id);
                m.writer().writeByte(p.c.get().mobMe.templates.id);
                m.writer().writeByte(p.c.get().mobMe.isboss?1:0);
                m.writer().flush();
                p2.conn.sendMessage(m);
                m.cleanup();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void FightMob2(Player p, Message m) throws IOException {
        int mobId = m.reader().readByte();
        m.cleanup();
        Mob mob = getMob(mobId);
        if (p.c.get().ItemBody[1] == null || mob == null || mob.isDie)
            return;
        Skill skill = p.c.get().getSkill(p.c.get().CSkill);
        if (skill == null)
            return;
        SkillTemplates data = SkillData.Templates(skill.id, skill.point);
        if (skill.coolDown > System.currentTimeMillis() || Math.abs(p.c.get().x - mob.x) > data.dx || Math.abs(p.c.get().y - mob.y) > data.dy || p.c.get().mp < data.manaUse)
            return;
        p.c.get().upMP(-data.manaUse);
        skill.coolDown = System.currentTimeMillis() + data.coolDown;
        Mob[] arMob = new Mob[10];
        arMob[0] = mob;
        byte n = 1;
        for (Mob mob2 : mobs) {
            if (mob2.isDie || mob.id == mob2.id || Math.abs(mob2.x - mob2.x) > data.dx || Math.abs(mob2.y - mob2.y) > data.dy)
                continue;
            if (data.maxFight > n){
                arMob[n] = mob2;
                n++;
            } else
                break;
        }
        m = new Message(60);
        m.writer().writeInt(p.c.get().id);
        m.writer().writeByte(p.c.get().CSkill);
        for (byte i = 0; i < arMob.length; i++) {
            if (arMob[i] != null)
                m.writer().writeByte(arMob[i].id);
        }
        m.writer().flush();
        sendMyMessage(p, m);
        m.cleanup();
        long xpup = 0;
        for (byte i = 0; i < arMob.length; i++) {
            if (arMob[i] == null)
                continue;
            Mob mob3 = arMob[i];
            int dame = util.nextInt(p.c.get().dameMin(), p.c.get().dameMax());
            int oldhp = mob3.hp;
            if (dame <= 0) {
                dame = 1;
            }
            int fatal = p.c.get().Fatal();
            boolean isfatal = fatal > util.nextInt(1, 1000);
            if (isfatal) {
                dame *= 2;
            }
            xpup += mob3.xpup+dame;
            mob3.updateHP(-dame);
            attachedMob((oldhp - mob3.hp), mob3.id, isfatal);

        }

        p.updateExp(xpup);
    }


    public void selectUIZone(Player p, Message m) throws IOException {
        byte zoneid = m.reader().readByte();
        byte index = m.reader().readByte();
        m.cleanup();
        if (zoneid == id) {
            return;
        }
        Item item = null;
        try {
            item = p.c.ItemBag[index];
        } catch (Exception e) {}
        boolean isalpha = false;
        for (byte i = 0; i < map.template.npc.length; i++) {
            Npc npc = map.template.npc[i];
            if (npc.id == 13 && Math.abs(npc.x - p.c.get().x) < 50 && Math.abs(npc.y - p.c.get().y) < 50) {
                isalpha = true;
                break;
            }
        }
        if ((item != null && (item.id == 35 || item.id == 37)) || (isalpha)) {
            if (zoneid >= 0 && zoneid < map.area.length) {
                if (map.area[zoneid].numplayers < map.template.maxplayers) {
                    leave(p);
                    map.area[zoneid].Enter(p);
                    p.endLoad(true);
                    if (item != null && item.id != 37) {
                        p.c.removeItemBag(index);
                    }
                } else {
                    p.sendAddchatYellow("Khu vực này đã đầy.");
                    p.endLoad(true);
                }
            }
        }
        m = new Message(57);
        m.writer().flush();
        p.conn.sendMessage(m);
        m.cleanup();
    }

    public void openUIZone(Player p) throws IOException {
        boolean isalpha = false;
        for (byte i = 0; i < map.template.npc.length; i++) {
            Npc npc = map.template.npc[i];
            if (npc.id == 13 && Math.abs(npc.x - p.c.get().x) < 50 && Math.abs(npc.y - p.c.get().y) < 50) {
                isalpha = true;
                break;
            }
        }
        if (p.c.quantityItemyTotal(37) > 0 || p.c.quantityItemyTotal(35) > 0 || isalpha) {
            Message m = new Message(36);
            m.writer().writeByte(map.area.length);//so khu
            for (byte j = 0; j < map.area.length; j++) {
                m.writer().writeByte(map.area[j].numplayers);//map.area[i].numplayers);//so nguoi
                m.writer().writeByte(map.area[j].getArryListParty().size());//grups
            }
            m.writer().flush();
            p.conn.sendMessage(m);
            m.cleanup();
        } else {
            p.c.get().upDie();
        }
    }

    public void chatNPC(Player p, Short idnpc, String chat) throws IOException {
        Message m = new Message(38);
        m.writer().writeShort(idnpc);//npcid
        m.writer().writeUTF(chat);//chat
        m.writer().flush();
        p.conn.sendMessage(m);
        m.cleanup();
    }

    public void selectMenuNpc(Player p, Message m) throws IOException {
        chatNPC(p, (short) m.reader().readByte(), m.reader().readByte() + "");
    }

    private ItemMap LeaveItem(short id, short x, short y) throws IOException {
        if (itemMap.size() > 100)
            removeItemMapMessage(itemMap.remove(0).itemMapId);
        Item item;
        ItemData data = ItemData.ItemDataId(id);
        if (data.type < 10) {
            if (data.type == 1) {
                item = ItemData.itemDefault(id);
                item.sys = GameScr.SysClass(data.nclass);
            } else {
                byte sys = (byte) util.nextInt(1,3);
                item = ItemData.itemDefault(id, sys);
            }
        } else
            item = ItemData.itemDefault(id);
        ItemMap im = new ItemMap();
        im.itemMapId = getItemMapNotId();
        im.x = x;
        im.y = y;
        im.item = item;
        itemMap.add(im);
        Message m = new Message(6);
        m.writer().writeShort(im.itemMapId);
        m.writer().writeShort(item.id);
        m.writer().writeShort(im.x);
        m.writer().writeShort(im.y);
        m.writer().flush();
        sendMessage(m);
        m.cleanup();
        return im;
    }

    public void PlayerAttack(Mob[] arrmob, Body b, int type) {
        for (int j = 0; j < this.players.size(); j++) {
            Service.PlayerAttack(this.players.get(j), arrmob, b);
        }
        Message m;
        long xpup = 0;
        for (byte i = 0; i < arrmob.length; i++) {
            Mob mob = arrmob[i];
            int dame = util.nextInt(b.dameMin(), b.dameMax());
            if (map.cave == null && mob.isboss && b.level-mob.level > 30)
                dame = 0;
            int fatal = b.Fatal();
            if (fatal > 800)
                fatal = 800;
            boolean flag = fatal > util.nextInt(1, 1000);
            if (flag) {
                dame *= 2;
                dame = dame*(100+b.percentFantalDame())/100;
                dame += b.FantalDame();
            }
            if (dame <= 0) {
                dame = 1;
            }
            if (mob.isFire) {
                dame *= 2;
            }
            if (b.c.isNhanban) {
                dame = dame*b.c.clone.percendame/100;
            }
            if (dame > 0) {
                mob.Fight(b.c.p.conn.id, dame);
                mob.updateHP(-dame);
            }

            int xpnew = dame/25*b.level;
            if (b.getEffType((byte)18) != null) {
                xpnew *= b.getEffType((byte)18).param;
            }
            if (mob.lvboss == 1)
                xpnew*=2;
            else if (mob.lvboss == 2)
                xpnew*=3;
            else if (mob.lvboss == 3)
                xpnew/=2;
            if (map.LangCo())
                xpnew = xpnew*105/100;
            else if (map.VDMQ())
                xpnew = xpnew*105/100;
            if (b.level > 99) {
                xpnew /= 4;
            }
            if (map.cave != null || (b.level > 1 && Math.abs(b.level-b.level) <= 10)) {
                xpup += xpnew;
            }
            for (int j = 0; j < this.players.size(); j++) {
                Service.Mobstart(this.players.get(j), mob.id, mob.hp, dame, flag, mob.lvboss, mob.hpmax);
            }
            if (!mob.isDie) {
                if (b.percentFire2() >= util.nextInt(1,100)) {
                    FireMobMessage(mob.id, 0);
                }
                if (b.percentFire4() >= util.nextInt(1,100)) {
                    FireMobMessage(mob.id, 1);
                }
                if (b.percentIce1_5() >= util.nextInt(1,100)) {
                    IceMobMessage(mob.id, 0);
                }
                if (b.percentWind1() >= util.nextInt(1,100)) {
                    WindMobMessage(mob.id, 0);
                }
                if (b.percentWind2() >= util.nextInt(1,100)) {
                    WindMobMessage(mob.id, 1);
                }
            }
        }
    }

    public void FightMob(Player p, Message m) throws IOException {
        if (p.c.get().CSkill == -1 && p.c.get().skill.size() > 0) {
            p.c.get().CSkill = p.c.get().skill.get(0).id;
        }
        Skill skill = p.c.get().getSkill(p.c.get().CSkill);
        if (skill == null)
            return;
        int mobId = m.reader().readUnsignedByte();
        synchronized (this) {
        Mob mob = getMob(mobId);
        Mob[] arMob = new Mob[10];
        arMob[0] = mob;
        if (mob == null || mob.isDie)
            return;
        if (p.c.get().ItemBody[1] == null) {
            p.sendAddchatYellow("Vũ khí không thích hợp");
            return;
        }
        p.removeEffect(15);
        p.removeEffect(16);
        SkillTemplates data = SkillData.Templates(skill.id, skill.point);
        if (p.c.get().mp < data.manaUse) {
            p.getMp();
            return;
        }
        if (skill.coolDown > System.currentTimeMillis() || Math.abs(p.c.get().x - mob.x) > 150 || Math.abs(p.c.get().y - mob.y) > 150)
            return;
        skill.coolDown = System.currentTimeMillis() + data.coolDown;
        p.c.mobAtk = mob.id;
        p.c.get().upMP(-data.manaUse);
        if (skill.id == 42) {
            p.c.get().x = mob.x;
            p.c.get().y = mob.y;
            this.sendXYPlayer(p);
        }
        int size = m.reader().available();
        byte n = 1;
        for (int i = 0; i < size; i++) {
            Mob mob2 = getMob(m.reader().readUnsignedByte());
            if (mob2.isDie || mob.id == mob2.id)// || Math.abs(mob.x - mob2.x) > data.dx || Math.abs(mob.y - mob2.y) > data.dy)
                continue;
            if (data.maxFight > n){
                arMob[n] = mob2;
                n++;
            } else
                break;
        }
        m.cleanup();
        for (int j = 0; j < this.players.size(); j++) {
            Service.PlayerAttack(this.players.get(j), arMob, p.c.get());
        }
        long xpup = 0;
        for (byte i = 0; i < arMob.length; i++) {
            if (arMob[i] == null)
                continue;
            Mob mob3 = arMob[i];
            int dame = util.nextInt(p.c.get().dameMin(), p.c.get().dameMax());
            if (map.cave == null && mob3.isboss && p.c.get().level-mob3.level > 30)
                dame = 0;
            int oldhp = mob3.hp;
            int fatal = p.c.get().Fatal();
            if (fatal > 800)
                fatal = 800;
            boolean isfatal = fatal > util.nextInt(1, 1000);
            if (isfatal) {
                dame *= 2;
                dame = dame*(100+p.c.get().percentFantalDame())/100;
                dame += p.c.get().FantalDame();
            }
            if (dame <= 0) {
                dame = 1;
            }
            if (mob3.isFire)
                dame *= 2;
           if (p.c.isNhanban) {
               dame = dame*p.c.clone.percendame/100;
           }
            int xpnew = dame/25*p.c.get().level;
            if (p.c.get().getEffType((byte)18) != null) {
                xpnew *= p.c.get().getEffType((byte)18).param;
            }
            if (mob3.lvboss==1)
                xpnew*=2;
            else if (mob3.lvboss==2)
                xpnew*=3;
            else if (mob3.lvboss==3)
                xpnew/=2;
            if (map.LangCo())
                xpnew = xpnew*105/100;
            else if (map.VDMQ())
                xpnew = xpnew*105/100;
            if (p.c.get().level > 99) {
                xpnew /= 7;
            }
            if (map.cave != null || (mob3.level > 1 && Math.abs(mob3.level-p.c.get().level) <= 10))
                xpup += xpnew;
            mob3.updateHP(-dame);
            if (dame > 0) {
                mob3.Fight(p.conn.id, dame);
            }
            if(!mob3.isFire) {
                if (p.c.get().percentFire2() >= util.nextInt(1,100)) {
                    FireMobMessage(mob3.id, 0);
                }
                if (p.c.get().percentFire4() >= util.nextInt(1,100)) {
                    FireMobMessage(mob3.id, 1);
                }
            }
            if(!mob3.isIce) {
                if (p.c.get().percentIce1_5() >= util.nextInt(1,100)) {
                    IceMobMessage(mob3.id, 0);
                }
            }
            if(!mob3.isWind) {
                if (p.c.get().percentWind1() >= util.nextInt(1,100)) {
                    WindMobMessage(mob3.id, 0);
                }
                if (p.c.get().percentWind2() >= util.nextInt(1,100)) {
                    WindMobMessage(mob3.id, 1);
                }
            }
            if (mob3.isDie) {
                MobStartDie((oldhp - mob3.hp), mob3.id, isfatal);
            } else {
                attachedMob((oldhp - mob3.hp), mob3.id, isfatal);
            }
            if (mob3.isDie && mob3.level > 1) {
                this.numMobDie++;
                if (map.cave != null) {
                    map.cave.updatePoint(1);
                }
                int master = mob3.sortNinjaFight();
                if (mob3.lvboss == 1) {
                    numTA--;
                    p.c.upyenMessage(100000);
                } else if (mob3.lvboss == 2) {
                    numTL--;
                    p.c.upyenMessage(500000);
                }
                ItemMap im;
                short[] arid;
                short[] aridsk = null;
                if (map.LangCo()) {
                    arid = new short[]{-1,-1,-1,-1,-1,12,12,12,12,12,16,16,16,16,17,17,17,17,18,292,293,2294,295,296,297,439,440,441,442,455,456,486,487,488,489};
                } else if (map.VDMQ()) {
                    arid = new short[]{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,3,3,3,4,4,4,12,12,12,20,20,20,20,21,21,21,21,22,292,293,294,295,296,297,573,574,575,576,577,578};
                } else {
                    arid = new short[]{-1,-1,-1,-1,-1,3,4,12};
                }
                int per = 100;
                switch (server.manager.event) {
                    case 1:
                        per = 5;
                        aridsk = new short[]{428,429,430,431};
                        break;
                    case 2:
                        per = 3;
                        aridsk = new short[]{292,293,294,295,296,297};
                        break;
                }
                if (aridsk != null && util.nextInt(per) == 0) {
                    arid = aridsk;
                }
                if (map.VDMQ() && p.c.get().level >= 100 && util.nextInt(100) <= 15) {
                    if (mob3.lvboss == 1 || mob3.lvboss == 2) {
                        arid = new short[]{545};
                    }
                }
                int lent = util.nextInt(arid.length);
                if ((map.LangCo()|| Math.abs(mob3.level-p.c.get().level) <= 10) && arid[lent] != -1) {
                    im = LeaveItem(arid[lent],mob3.x, mob3.y);
                    int quantity = 1;
                    if (im.item.id == 12)
                        quantity = util.nextInt(10000,30000);
                    if (im.item.id == 455 || im.item.id == 456) {
                        im.item.isExpires = true;
                        im.item.expires = util.TimeDay(7);
                    } else if (im.item.id == 545) {
                        im.item.isExpires = true;
                        im.item.expires = util.TimeDay(1);
                    }
                    im.item.quantity = quantity;
                    im.master = master;
                }
                if (mob3.isboss) {
                    if (map.cave == null) {
                        Manager.chatKTG(p.c.name+" đã tiêu diệt "+mob3.templates.name);
                    }
                    if (map.VDMQ()) {
                        im = LeaveItem((short)547,(short)util.nextInt(mob3.x-30, mob3.x+30), mob3.y);
                        im.master = master;
                    }
                    int l = mob3.templates.arrIdItem.length;
                    if (l>1) {
                        for (int j = 0; j < mob3.templates.arrIdItem[0];j++) {
                            lent = util.nextInt(1,l-1);
                            short idi = mob3.templates.arrIdItem[lent];
                            if (idi == -1)
                                continue;
                            im = LeaveItem(idi,(short)util.nextInt(mob3.x-30, mob3.x+30), mob3.y);
                            if (im.item.id == 12)
                                im.item.quantity = util.nextInt(10000,30000);
                            im.master = master;
                        }
                    }
                    if (map.cave != null && map.getXHD() == 9) {
                        if ((map.id == 157 && map.cave.level == 0) || (map.id == 158 && map.cave.level == 1) || (map.id == 159 && map.cave.level == 2)) {
                            if (util.nextInt(3)<3) {
                                map.cave.updatePoint(mobs.size());
                                for (short k = 0; k < mobs.size(); k++) {
                                    mobs.get(k).updateHP(-mobs.get(k).hpmax);
                                    mobs.get(k).isRefresh = false;
                                    for (short h = 0; h < players.size(); h++) {
                                        Service.setHPMob(players.get(h).c, mobs.get(k).id, 0);
                                    }
                                }
                                map.cave.level++;
                            }
                        }
                    }
                }
                if (map.cave != null) {
                    if (this.map.getXHD() < 9) {
                        mob3.isRefresh = false;
                        if (mobs.size() == this.numMobDie) {
                            if (map.getXHD() == 5) {
                                if (map.id == 105) {
                                    map.cave.openMap();
                                    map.cave.openMap();
                                    map.cave.openMap();
                                } else if (map.id == 106 || map.id == 107 || map.id == 108) {
                                    map.cave.finsh++;
                                    if (map.cave.finsh >= 3) {
                                        map.cave.openMap();
                                    }
                                } else {
                                    map.cave.openMap();
                                }
                            } else if (map.getXHD() == 6 && map.id == 116) {
                                if (map.cave.finsh == 0) {
                                    map.cave.openMap();
                                } else {
                                    map.cave.finsh++;
                                }
                                this.numMobDie = 0;
                                for (short l = 0; l < mobs.size(); l ++) {
                                    this.refreshMob(l);
                                }
                            } else {
                                map.cave.openMap();
                            }
                        }
                    }
                }

            } else {
            }
        }
        if (xpup > 0) {
            if (map.cave != null) {
                map.cave.updateXP(xpup);
            } else {
                if (p.c.isNhanban) {
                    xpup/=4;
                }
                p.updateExp(xpup);
                xpup/=5;
                if (p.c.get().party != null) {
                    for (int i = 0; i < players.size(); i++) {
                        Player p2 = players.get(i);
                        if (p2.c.id != p.c.id) {
                            if (p2.c.party == p.c.party && Math.abs(p2.c.level - p.c.level) <= 10){
                                p2.updateExp(xpup/2*100);
                            }
                        }
                    }
                }
            }
        }
        }
    }

    public void FightNinja(Player p, Message m) throws IOException {
        int idP = m.reader().readInt();
        if (GameScr.mapNotPK(map.id))
            return;
        synchronized (this) {
        Char c = getNinja(idP);
        if (p.c.get().ItemBody[1] != null && c.get() != null && ((p.c.get().typepk == 1 && c.get().typepk == 1) || p.c.get().typepk == 3 || c.get().typepk == 3)) {
            if (p.c.get().CSkill == -1 && p.c.get().skill.size() > 0) {
                p.c.get().CSkill = p.c.get().skill.get(0).id;
            }
            Skill skill = p.c.get().getSkill(p.c.get().CSkill);
            if (skill == null || c.get().isDie || c.get().getEffId(15) != null || c.get().getEffId(16) != null) {
                return;
            }
            Char[] arNinja = new Char[10];
            arNinja[0] = c;
            p.removeEffect(15);
            p.removeEffect(16);
            SkillTemplates temp = SkillData.Templates(skill.id, skill.point);
            if (skill.coolDown > System.currentTimeMillis() || Math.abs(p.c.get().x - c.get().x) > temp.dx || Math.abs(p.c.get().y - c.get().y) > temp.dy || p.c.get().mp < temp.manaUse) {
                return;
            }
            p.c.get().upMP(-temp.manaUse);
            skill.coolDown = System.currentTimeMillis() + temp.coolDown;
            if (skill.id == 24) {
                c.p.setEffect(18, 0, p.c.get().getPramSkill(55)*1000, 0);
                return;
            }
            if (skill.id == 42) {
                this.setXYPlayers(c.get().x, c.get().y, p, c.p);
                c.p.setEffect(18, 0, 5000, 0);
            }
            byte n = 1;
            try {
                for (;;) {
                    int idn = m.reader().readInt();
                    Char nj2 = this.getNinja(idn);
                    if (nj2 == null || nj2.isDie || nj2.getEffId(15) != null || c.get().id == p.c.get().id || nj2.id == p.c.get().id || Math.abs(c.get().x - nj2.x) > temp.dx || Math.abs(c.get().y - nj2.y) > temp.dy) {
                        continue;
                    }
                    if (temp.maxFight > n) {
                        if (nj2.typepk == 3 || p.c.get().typepk == 3 || (p.c.get().typepk == 1 && nj2.typepk == 1)) {
                            arNinja[n] = nj2;
                        }
                    } else {
                        break;
                    }
                    n++;
                }
            } catch (IOException e) {}
            m = new Message(61);
            m.writer().writeInt(p.c.get().id);
            m.writer().writeByte(skill.id);
            for (byte i = 0; i < arNinja.length; i++) {
                Char nj3 = arNinja[i];
                if (nj3 != null)
                    m.writer().writeInt(nj3.id);
            }
            m.writer().flush();
            sendMyMessage(p, m);
            m.cleanup();
            for (byte i = 0; i < arNinja.length; i++) {
                Char nj4 = arNinja[i];
                if (nj4 == null)
                    continue;
                int dame = p.c.get().dameSide()*10/100;
                dame = util.nextInt(dame*90/100, dame);
                int oldhp = nj4.hp;
                dame -= nj4.dameDown();// Giam sat thuong
                if (dame <= 0) {
                    dame = 1;
                }
                int miss = nj4.Miss() - p.c.get().Exactly();
                if (miss > util.nextInt(10000)) {
                    dame = 0;
                }
                nj4.upHP(-dame);
                attached((oldhp - nj4.hp), nj4.id);
                if (nj4.isDie) {
                    if (p.c.get().typepk == 1 || p.c.get().typepk == 3)
                        p.c.get().updatePk(1);
                    long num1 = Level.getMaxExp(nj4.level);
                    long num2 = Level.getLevel(nj4.level).exps;
                    if (nj4.pk > 0) {
                        if (nj4.exp > num1) {
                            nj4.expdown = 0;
                            nj4.exp -= num2*(5+nj4.pk)/100;
                            if (nj4.exp < num1)
                                nj4.exp = num1;
                        } else {
                            nj4.exp = num1;
                            nj4.expdown += num2*(5+nj4.pk)/100;
                            if (nj4.expdown > (num2*50/100))
                                nj4.expdown = num2*50/100;
                        }
                        nj4.updatePk(-1);
                    }
                    nj4.type = 14;
                    sendDie(nj4);
                }
            }
        }
        }
    }

    public void wakeUpDieReturn(Player p) throws IOException {
        if (!p.c.isDie || map.LangCo()) {
            return;
        }
        if (p.luong < 100) {
            p.conn.sendMessageLog("Bạn không có đủ 100 lượng!") ;
            return;
        }
        p.c.get().isDie = false;
        p.luongMessage(-1);
        p.c.get().hp = p.c.get().getMaxHP();
        p.c.get().mp = p.c.get().getMaxMP();
        p.liveFromDead();
    }

    public void sendDie(Char c) throws IOException {
        if (c.get().exp > Level.getMaxExp(c.get().level)) {
            Message m = new Message(-11);
            m.writer().writeByte(c.get().pk);
            m.writer().writeShort(c.get().x);
            m.writer().writeShort(c.get().y);
            m.writer().writeLong(c.get().exp);
            m.writer().flush();
            c.p.conn.sendMessage(m);
            m.cleanup();
        } else {
            c.get().exp = Level.getMaxExp(c.get().level);
            Message m = new Message(72);
            m.writer().writeByte(c.get().pk);
            m.writer().writeShort(c.get().x);
            m.writer().writeShort(c.get().y);
            m.writer().writeLong(c.get().expdown);
            m.writer().flush();
            c.p.conn.sendMessage(m);
            m.cleanup();
        }
        Message m = new Message(0);
        m.writer().writeInt(c.get().id);
        m.writer().writeByte(c.get().pk);
        m.writer().writeShort(c.get().x);
        m.writer().writeShort(c.get().y);
        m.writer().flush();
        sendMyMessage(c.p, m);
        m.cleanup();
    }

    public void DieReturn(Player p) throws IOException {
        leave(p);
        p.c.get().isDie = false;
        Map ma;
        if (map.cave != null) {
            ma = map.cave.map[0];
        } else {
            ma = Manager.getMapid(p.c.mapLTD);
        }
        for (Place area : ma.area) {
            if (area.numplayers < ma.template.maxplayers) {
                area.EnterMap0(p.c);
                p.c.get().hp = p.c.get().getMaxHP();
                p.c.get().mp = p.c.get().getMaxMP();
                Message m = new Message(-30);
                m.writer().writeByte(-123);
                m.writer().writeInt(p.c.xu);
                m.writer().writeInt(p.c.yen);
                m.writer().writeInt(p.luong);
                m.writer().writeInt(p.c.get().getMaxHP());
                m.writer().writeInt(p.c.get().getMaxMP());
                m.writer().writeByte(0);
                m.writer().flush();
                p.conn.sendMessage(m);
                m.cleanup();
                m = new Message(57);
                m.writer().flush();
                p.conn.sendMessage(m);
                m.cleanup();
                return;
            }
        }
    }

    private void attached(int dame, int nid) throws IOException {
        Char n = getNinja(nid);
        Message m = new Message(62);
        m.writer().writeInt(nid);
        m.writer().writeInt(n.hp);//hp
        m.writer().writeInt(dame);//dame
        m.writer().writeInt(n.mp);// mp
        m.writer().writeInt(0);// dame2
        m.writer().flush();
        sendMessage(m);
        m.cleanup();
    }

    private void FireMobMessage(int mobid, int type) {
        try {
            Mob mob = getMob(mobid);
            switch (type) {
                case -1:
                    mob.isFire = false;
                    break;
                case 0:
                    mob.isFire = true;
                    mob.timeFire = System.currentTimeMillis()+2000L;
                    break;
                case 1:
                    mob.isFire = true;
                    mob.timeFire = System.currentTimeMillis()+4000L;
                    break;
                default:
                    break;
            }
            Message m = new Message(89);
            m.writer().writeByte(mobid);
            m.writer().writeBoolean(mob.isFire);
            m.writer().flush();
            sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void IceMobMessage(int mobid, int type) {
        try {
            Mob mob = getMob(mobid);
            switch (type) {
                case -1:
                    mob.isIce = false;
                    break;
                case 0:
                    mob.isIce = true;
                    mob.timeIce = System.currentTimeMillis()+1500L;
                    break;
                case 1:
                    mob.isIce = true;
                    mob.timeIce = System.currentTimeMillis()+4000L;
                    break;
                default:
                    break;
            }
            Message m = new Message(90);
            m.writer().writeByte(mobid);
            m.writer().writeBoolean(mob.isIce);
            m.writer().flush();
            sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void WindMobMessage(int mobid, int type) {
        try {
            Mob mob = getMob(mobid);
            switch (type) {
                case -1:
                    mob.isWind = false;
                    break;
                case 0:
                    mob.isWind = true;
                    mob.timeWind = System.currentTimeMillis()+1000L;
                    break;
                case 1:
                    mob.isWind = true;
                    mob.timeWind = System.currentTimeMillis()+2000L;
                    break;
                default:
                    break;
            }
            Message m = new Message(91);
            m.writer().writeByte(mobid);
            m.writer().writeBoolean(mob.isWind);
            m.writer().flush();
            sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadMobAttached(int mobid) {
        synchronized (this) {
        try {
            Mob mob = getMob(mobid);
            if (mob.isIce || mob.isWind)
                return;
            long tFight = System.currentTimeMillis()+1500L;
            if (mob.isboss)
                tFight = System.currentTimeMillis()+500L;
            mob.timeFight = tFight;
            for (short i = 0; i < players.size(); i++) {
                Player player = players.get(i);
                if (player.c.get().isDie || player.c.get().getEffId(15) != null ||player.c.get().getEffId(16) != null)
                    continue;
                short dx = 80;
                short dy = 2;
                if (mob.templates.type > 3)
                    dy = 80;
                if (mob.isboss) {
                    dx = 110;
                }
                if (mob.isFight(player.conn.id)) {
                    dx = 200;
                    dy = 160;
                }
                if (Math.abs(player.c.get().x-mob.x) < dx && Math.abs(player.c.get().y-mob.y) < dy) {
                    int dame = mob.level*mob.level/6;
                    if (map.cave != null && map.cave.finsh > 0 && map.getXHD() == 6) {
                        int dup = dame*((10*map.cave.finsh)+100)/100;
                        dame = dup;
                    }
                    if (mob.lvboss == 1)
                        dame *= 2;
                    else if (mob.lvboss == 2)
                        dame *= 3;
                    else if (mob.lvboss == 3)
                        dame *= 4;
                    if (mob.isboss)
                        dame *= 4;
                    if (mob.sys == 1)
                        dame -= player.c.get().ResFire();
                    else if (mob.sys == 2)
                        dame -= player.c.get().ResIce();
                    else if (mob.sys == 3)
                        dame -= player.c.get().ResWind();
                    dame -= player.c.get().dameDown();
                    dame = util.nextInt((dame*90/100),dame);
                    if (dame <= 0)
                        dame = 1;
                    int miss = player.c.get().Miss();
                    if (miss > util.nextInt(10000))
                        dame = 0;
                    int mpdown = 0;
                    if (player.c.get().hp*100/player.c.get().getMaxHP() > 10) {
                        Effect eff = player.c.get().getEffId(10);
                        if (eff != null) {
                            int mpold = player.c.get().mp;
                            player.c.get().upMP(-(dame*eff.param/100));
                            dame -= mpdown = (mpold - player.c.get().mp);
                        }
                    }
                    player.c.get().upHP(-dame);
                    MobAtkMessage(mob.id, player.c, dame, mpdown, (short)-1, (byte)-1, (byte)-1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        }
    }

    private void MobAtkMessage(int mobid, Char n,int dame, int mpdown, short idskill_atk, byte typeatk, byte typetool) throws IOException {
        Message m = new Message(-3);
        m.writer().writeByte(mobid);
        m.writer().writeInt(dame);//-Hp;
        m.writer().writeInt(mpdown);//-mp
        m.writer().writeShort(idskill_atk);//idSkill_atk
        m.writer().writeByte(typeatk);//type atk
        m.writer().writeByte(typetool);//type tool
        m.writer().flush();
        n.p.conn.sendMessage(m);
        m.cleanup();
        m = new Message(-2);
        m.writer().writeByte(mobid);
        m.writer().writeInt(n.id);//id ninja
        m.writer().writeInt(dame);//-Hp;
        m.writer().writeInt(mpdown);//-mp
        m.writer().writeShort(idskill_atk);//idSkill_atk
        m.writer().writeByte(typeatk);//type atk
        m.writer().writeByte(typetool);//type tool
        m.writer().flush();
        sendMyMessage(n.p, m);
        if (n.isDie && !map.LangCo())
            sendDie(n);
    }


    private void loadMobMeAtk(Char n) {
        n.mobMe.timeFight = System.currentTimeMillis()+3000L;
        try {
            if (n.mobAtk != -1 && n.mobMe.templates.id >= 211 && n.mobMe.templates.id <= 217) {
                Mob mob = getMob(n.mobAtk);
                if (!mob.isDie) {
                    int dame = n.dameMax()*20/100;
                    MobMeAtkMessage(n, mob.id, dame, (short)40, (byte)1, (byte)1, (byte)0);
                    mob.updateHP(-dame);
                    attachedMob(dame, mob.id, false);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void MobMeAtkMessage(Char n,int idatk,int dame, short idskill_atk, byte typeatk, byte typetool,byte type) throws IOException {
        Message m = new Message(87);
        m.writer().writeInt(n.id);
        m.writer().writeByte(idatk);
        m.writer().writeShort(idskill_atk);//idSkill_atk
        m.writer().writeByte(typeatk);//type atk
        m.writer().writeByte(typetool);//type tool
        m.writer().writeByte(type);//type
        if (type == 1)
            m.writer().writeInt(idatk);//char atk
        m.writer().flush();
        n.p.conn.sendMessage(m);
        m.cleanup();
    }

    public void openFindParty(Player p) {
        try {
            ArrayList<Party> partys = this.getArryListParty();
            Message m = new Message(-30);
            m.writer().writeByte(-77);
            for (int i = 0; i < partys.size(); i++) {
                Char n = partys.get(i).getNinja(partys.get(i).master);
                m.writer().writeByte(n.nclass);
                m.writer().writeByte(n.level);
                m.writer().writeUTF(n.name);
                m.writer().writeByte(partys.get(i).ninjas.size());
            }
            m.writer().flush();
            p.conn.sendMessage(m);
            m.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        synchronized (this) {
        try {
            //quai vat
            for (int i = mobs.size()-1; i >= 0; i--) {
                Mob mob = mobs.get(i);
                if (mob.timeRefresh > 0 && System.currentTimeMillis() >= mob.timeRefresh && mob.isRefresh) {
                    refreshMob(mob.id);
                }
                if (mob.isFire && System.currentTimeMillis() >= mob.timeFire) {
                    FireMobMessage(mob.id,-1);
                }
                if (mob.isIce && System.currentTimeMillis() >= mob.timeIce) {
                    IceMobMessage(mob.id,-1);
                }
                if (mob.isWind && System.currentTimeMillis() >= mob.timeWind) {
                    WindMobMessage(mob.id,-1);
                }
                if (!mob.isDie && mob.status != 0 && mob.level != 1 && System.currentTimeMillis() >= mob.timeFight) {
                    loadMobAttached(mob.id);
                }
            }
            for (int i = players.size()-1; i >= 0; i--) {
                Player p = players.get(i);
                //effect
                for (int j = p.c.get().veff.size()-1;j >= 0; j--) {
                    Effect eff = p.c.get().veff.get(j);
                    if (System.currentTimeMillis() >= eff.timeRemove) {
                        p.removeEffect(eff.template.id);
                        j--;
                    } else if (eff.template.type == 0 || eff.template.type == 12) {
                        p.c.get().upHP(eff.param);
                        p.c.get().upMP(eff.param);
                    } else if (eff.template.type == 4 || eff.template.type == 17)
                        p.c.get().upHP(eff.param);
                    else if (eff.template.type == 13) {
                        p.c.get().upHP(-(p.c.get().getMaxHP()*3/100));
                        if (p.c.get().isDie) {
                            p.c.get().upDie();
                        }
                    }
                }
                //eff5buff
                if (p.c.eff5buffHP() > 0 || p.c.get().eff5buffMP() > 0) {
                    if (p.c.eff5buff <= System.currentTimeMillis()) {
                        p.c.eff5buff = System.currentTimeMillis()+5000L;
                        p.c.get().upHP(p.c.get().eff5buffHP());
                        p.c.get().upMP(p.c.get().eff5buffMP());
                    }
                }
//                GameCanvas.addEffect(p.conn, (byte) 0, p.nj.id, (byte)25, 1, 1, false);
                //eff
                if (p.c.get().fullTL() >= 7 && System.currentTimeMillis() >  p.c.delayEffect) {
                    p.c.delayEffect = System.currentTimeMillis()+5000;
                    byte tl = 0;
                    switch (GameScr.SysClass(p.c.nclass)) {
                        case 1:
                            tl = 9;
                            break;
                        case 2:
                            tl = 3;
                            break;
                        case 3:
                            tl = 6;
                            break;
                    }
                    if (p.c.fullTL()>=9) {
                        tl += 1;
                    }
                    if (p.c.fullTL()>=7) {
                        tl+=0;
                    }
                    for (int j = 0; j < players.size(); j++) {
                        GameCanvas.addEffect(players.get(j).conn, (byte) 0, p.c.get().id, tl, 1, 1, false);
                    }
                }
                //pet attack
                if (p.c.get().mobMe != null && p.c.get().mobMe.timeFight <= System.currentTimeMillis()) {
                    loadMobMeAtk(p.c);
                }
                //xoa item tui  het han cua ng choi
                for (byte j = 0; j < p.c.ItemBag.length; j++) {
                    Item item = p.c.ItemBag[j];
                    if (item == null || !item.isExpires) {
                        continue;
                    }
                    if (System.currentTimeMillis() >= item.expires) {
                        p.c.removeItemBag(j, item.quantity);
                    }
                }
                //xoa item tra bi  het han cua ng choi
                for (byte j = 0; j < p.c.get().ItemBody.length; j++) {
                    Item item = p.c.get().ItemBody[j];
                    if (item == null || !item.isExpires) {
                        continue;
                    }
                    if (System.currentTimeMillis() >= item.expires) {
                        p.c.removeItemBody(j);
                    }
                }
                //xoa item ruong het han cua ng choi
                for (byte j = 0; j < p.c.ItemBox.length; j++) {
                    Item item = p.c.ItemBox[j];
                    if (item == null || !item.isExpires) {
                        continue;
                    }
                    if (System.currentTimeMillis() >= item.expires) {
                        p.c.removeItemBox(j);
                    }
                }
                if (map.LangCo()&& (p.c.isDie || p.c.expdown > 0)) {
                    DieReturn(p);
                }
                if(System.currentTimeMillis() > p.c.deleyRequestClan) {
                    p.c.requestclan = -1;
                }
//                if (p.c.clone != null && !p.c.clone.isDie && (Math.abs(p.c.x-p.c.clone.x) > 80 || Math.abs(p.c.y-p.c.clone.y) > 30)) {
//                    p.c.clone.move((short) util.nextInt(p.c.x-35,p.c.x+35), p.c.y);
//                }
//                if (!p.c.clone.isDie && System.currentTimeMillis() > p.c.timeRemoveClone) {
//                    p.c.clone.off();
//                }
                if (p.c.get().isDie) {
                    p.exitNhanBan(true);
                    System.out.println("cho quay ve");
                }
            }
            //xoa item map
            for (int i = 0; i < itemMap.size(); i++) {
                ItemMap itm = itemMap.get(i);
                if (System.currentTimeMillis() >= itm.removedelay) {
                    removeItemMapMessage(itm.itemMapId);
                    itemMap.remove(i);
                    i--;
                } else if ((itm.removedelay - System.currentTimeMillis()) < 70000L && itm.master != -1) {
                    itm.master = -1;
                }
            }
            if (map.cave != null && System.currentTimeMillis() > map.cave.time) {
                map.cave.rest();
            }
            if (map.cave != null && map.cave.level == map.cave.map.length) {
                map.cave.finsh();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
    }

    public void close() {
    }
}
