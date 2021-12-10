package server;

import io.Message;
import io.Session;
import java.io.IOException;
import real.Body;
import real.Item;
import real.ItemData;
import real.Char;
import real.Mob;
import real.Player;
import real.Skill;
import real.SkillData;
import real.cloneChar;
import static sun.audio.AudioPlayer.player;

/**
 *
 * @author Ghost
 */
public class Service {
    
    protected static Message messageSubCommand(byte command) throws Exception{
        Message message = new Message(-30);
        message.writer().writeByte(command);
        return message;
    }
    
    protected static Message messageNotLogin(byte command) throws Exception {
        Message message = new Message(-29);
        message.writer().writeByte(command);
        return message;
    }
    
    protected static  Message messageNotMap(byte command) throws Exception {
        Message message = new Message(-28);
        message.writer().writeByte(command);
        return message;
    }
    
    public static void evaluateCave(Char nj) {
        Message msg = null;
        try {
            msg = messageNotMap((byte)-83);
            msg.writer().writeShort(nj.pointCave);
            msg.writer().writeShort(2);
            msg.writer().writeByte(0);
            msg.writer().writeShort(nj.pointCave/10);
            nj.p.conn.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (msg != null)
                msg.cleanup();
        }
    }
    
    public static void sendclonechar(Player p, Player top) {
        try {
            Message m = new Message(3);
            m.writer().writeInt(p.c.clone.id);//id ninja
            m.writer().writeUTF("");//clan name
            if (false) {
                m.writer().writeByte(0);//type clan
            }
            m.writer().writeBoolean(false);//isInvisible
            m.writer().writeByte(p.c.clone.typepk);// type pk
            m.writer().writeByte(p.c.clone.nclass);// class
            m.writer().writeByte(p.c.clone._char.gender);// gender
            m.writer().writeShort(p.c.clone.partHead());//head
            m.writer().writeUTF(p.c.clone._char.name);//name
            m.writer().writeInt(p.c.clone.hp);//hp
            m.writer().writeInt(p.c.clone.getMaxHP());//hp max
            m.writer().writeByte(p.c.clone.level);//level
            m.writer().writeShort(p.c.clone.Weapon());//vu khi
            m.writer().writeShort(p.c.clone.Body());// body
            m.writer().writeShort(p.c.clone.Leg());//leg
            m.writer().writeByte(-1);//mob
            m.writer().writeShort(p.c.clone.x);// X
            m.writer().writeShort(p.c.clone.y);// Y
            m.writer().writeShort(p.c.eff5buffHP());//eff5BuffHp
            m.writer().writeShort(p.c.eff5buffMP());//eff5BuffMP
            m.writer().writeByte(0);
            m.writer().writeBoolean(p.c.clone.isHuman); // human
            m.writer().writeBoolean(p.c.clone.isNhanban); // nhan ban
            m.writer().writeShort(-1);
            m.writer().writeShort(-1);
            m.writer().writeShort(-1);
            m.writer().writeShort(-1);
            m.writer().flush();
            top.conn.sendMessage(m);
            m.cleanup();
            if (p.c.clone.mobMe != null) {
                m = new Message(-30);
                m.writer().writeByte(-68);
                m.writer().writeInt(p.c.clone.id);
                m.writer().writeByte(p.c.clone.mobMe.templates.id);
                m.writer().writeByte(p.c.clone.mobMe.isboss?1:0);
                m.writer().flush();
                top.conn.sendMessage(m);
                m.cleanup();
            }
            p.c.place.sendCoat(p.c.clone, top);
            p.c.place.sendGlove(p.c.clone, top);
            p.c.place.sendMounts(p.c.clone, top);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    
    public static void setHPMob(Char nj, int mobid, int hp) {
        Message msg = null;
        try {
            msg = new Message(51);
            msg.writer().writeByte(mobid);
            msg.writer().writeInt(0);
            nj.p.conn.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (msg != null)
                msg.cleanup();
        }
    }
    
    public static void CharViewInfo(Player p) {
        Message msg = null;
        try {
            Char c = p.c;
            msg = messageSubCommand((byte)115);
            msg.writer().writeInt(c.get().id);//id ninja
            msg.writer().writeUTF(c.clan.clanName);// clan name
            if (!c.clan.clanName.isEmpty()) {
                msg.writer().writeByte(c.clan.typeclan);//type clan
            }
            msg.writer().writeByte((c.taskId = 50));//taskId (id nhiem vu)
            msg.writer().writeByte(c.gender);// gender
            msg.writer().writeShort(c.get().partHead());// head defult
            msg.writer().writeByte(c.get().speed());//speed
            msg.writer().writeUTF(c.name);//neame
            msg.writer().writeByte(c.get().pk);//pk
            msg.writer().writeByte(c.get().typepk);//type pk
            msg.writer().writeInt(c.get().getMaxHP()); // maxHP
            msg.writer().writeInt(c.get().hp); // hp
            msg.writer().writeInt(c.get().getMaxMP()); // maxMP
            msg.writer().writeInt(c.get().mp); // mp
            msg.writer().writeLong(c.get().exp); // exp
            msg.writer().writeLong(c.get().expdown); //exp dowm
            msg.writer().writeShort(c.get().eff5buffHP()); // eff5buff
            msg.writer().writeShort(c.get().eff5buffMP()); // eff5buff
            msg.writer().writeByte(c.get().nclass);// nclass
            msg.writer().writeShort(c.get().ppoint); //ppoint
            msg.writer().writeShort(c.get().potential0); //potential 0
            msg.writer().writeShort(c.get().potential1); //potential 1
            msg.writer().writeInt(c.get().potential2); //potential 2
            msg.writer().writeInt(c.get().potential3); //potential 3
            msg.writer().writeShort(c.get().spoint); // spoint
            msg.writer().writeByte(c.get().skill.size());// skill lent
            for (short i = 0; i < c.get().skill.size(); i++) {
                Skill skill = c.get().skill.get(i);
                msg.writer().writeShort(SkillData.Templates(skill.id, skill.point).skillId);//id skill
            }
            msg.writer().writeInt(c.xu);//xu
            msg.writer().writeInt(c.yen);//yen
            msg.writer().writeInt(p.luong);//luong
            msg.writer().writeByte(c.maxluggage);//IteamBag max leght
            for (byte i = 0; i < c.maxluggage; i++) {
                Item item = c.ItemBag[i];
                if (item != null) {
                    msg.writer().writeShort(item.id);//id
                    msg.writer().writeBoolean(item.isLock);//khoa
                    if (ItemData.isTypeBody(item.id) || ItemData.isTypeMounts(item.id) || ItemData.isTypeNgocKham(item.id)) {
                        msg.writer().writeByte(item.upgrade);//nang cap +0-16 
                    }
                    msg.writer().writeBoolean(item.isExpires);//het han
                    msg.writer().writeShort(item.quantity);//dinh luong
                } else {
                    msg.writer().writeShort(-1);
                }
            }
            // trang bi
            for (int i = 0; i < 16; i++) {
                Item item = c.get().ItemBody[i];
                if (item != null) {
                    msg.writer().writeShort(item.id);
                    msg.writer().writeByte(item.upgrade);
                    msg.writer().writeByte(item.sys);
                } else {
                    msg.writer().writeShort(-1);
                }
            }
            msg.writer().writeBoolean(c.isHuman);
            msg.writer().writeBoolean(c.isNhanban);
            msg.writer().writeShort(c.get().partHead());
            msg.writer().writeShort(c.get().Weapon());
            msg.writer().writeShort(c.get().Body());
            msg.writer().writeShort(c.get().Leg());
            msg.writer().flush();
            p.conn.sendMessage(msg);
            msg.cleanup();
            p.getMobMe();
            for (byte n = 0; n < c.get().veff.size(); n++) {
                p.addEffectMessage(c.get().veff.get(n));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (msg != null)
                msg.cleanup();
        }
    }
    
    public static void Mobstart(Player p, int mobid, int dame, boolean flag) {
        Message msg = null;
        try {
            msg = new Message(-4);
            msg.writer().writeByte(mobid);
            msg.writer().writeInt(dame);
            msg.writer().writeBoolean(flag);//flag
            msg.writer().flush();
            p.conn.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (msg != null)
                msg.cleanup();
        }
    }
    
    public static void Mobstart(Player p, int mobid, int hp, int dame, boolean flag, int levelboss, int hpmax) {
        Message msg = null;
        try {
            msg = new Message(-1);
            msg.writer().writeByte(mobid);
            msg.writer().writeInt(hp);
            msg.writer().writeInt(dame);
            msg.writer().writeBoolean(flag);//flag
            msg.writer().writeByte(levelboss);
            msg.writer().writeInt(hpmax);
            msg.writer().flush();
            p.conn.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (msg != null)
                msg.cleanup();
        }
    }
    
    public static void PlayerAttack(Player p, Mob[] mob, Body b) {
        Message msg = null;
        try {
            msg = new Message(60);
            msg.writer().writeInt(b.id);
            msg.writer().writeByte(b.CSkill);
            for (byte i = 0; i < mob.length; i++) {
                if (mob[i] != null)
                    msg.writer().writeByte(mob[i].id);
            }
            msg.writer().flush();
            p.conn.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (msg != null)
                msg.cleanup();
        }
    }
}
