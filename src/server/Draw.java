package server;

import io.Message;
import java.io.IOException;
import real.ClanManager;
import real.Char;
import real.Player;
import real.PlayerManager;

/**
 *
 * @author ghost
 */
public class Draw {
    
    private static final Server server = Server.getInstance();
    
    public static void Draw(Player p, Message m) throws IOException {
        short menuId = m.reader().readShort();
        String str = m.reader().readUTF();
        m.cleanup();
        System.out.println("menuId "+menuId+" str "+str);
        byte b = -1;
        try {
            b = m.reader().readByte();
        } catch (IOException e) {}
        m.cleanup();
        switch (menuId) {
            case 1:
                if (p.c.quantityItemyTotal(279) > 0) {
                    Char c = PlayerManager.getInstance().getNinja(str);
                    if (c != null) { 
                        if (!c.place.map.LangCo() && c.place.map.getXHD() == -1) {
                            p.c.place.leave(p);
                            p.c.get().x = c.get().x;
                            p.c.get().y = c.get().y;
                            c.place.Enter(p);
                            return;
                        }
                    }
                    p.sendAddchatYellow("Ví trí người này không thể đi tới");
                }
                break;
            case 50:
                ClanManager.createClan(p, str);
                break;
            case 51:
                p.passnew = "";
                p.passold = str;
                p.changePassword();
                server.menu.sendWrite(p, (short)52, "Nhập mật khẩu mới");
                break;
            case 52:
                p.passnew = str;
                p.changePassword();
                break;
            case 100:
                String num = str.replaceAll(" ", "").trim();
                if (num.length() > 10 || !util.checkNumInt(num) || b < 0 || b >= server.manager.rotationluck.length) {
                    return;
                }
                int xujoin = Integer.parseInt(num);
                server.manager.rotationluck[b].joinLuck(p, xujoin);
                break;
            case 101:
                if (b < 0 || b >= server.manager.rotationluck.length) {
                    return;
                }
                server.manager.rotationluck[b].luckMessage(p);
                break;
            case 102:
                p.typemenu = 92;
                server.menu.doMenuArray(p, new String[]{"Vòng xoay vip", "Vòng xoay thường"});
                break;
        }
    }
}
