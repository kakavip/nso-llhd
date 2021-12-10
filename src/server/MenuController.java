package server;

import boardGame.Place;
import io.Message;
import java.io.IOException;
import real.Cave;
import real.ClanManager;
import real.Item;
import real.ItemData;
import real.Map;
import real.Player;

/**
 *
 * @author Văn Tú
 */
public class MenuController {

    Server server = Server.getInstance();
    
    public void sendMenu(Player p, Message m) throws IOException {
        byte b1 = m.reader().readByte();//idnpc
        byte b2 = m.reader().readByte();//class1
        byte b3 = m.reader().readByte();//class2
        m.cleanup();
        switch (p.typemenu) {
            //menu npc Katana
            case 0:
                if (b2 == 0) {
                    p.requestItem(2);
                } else if (b2 == 1) {
                    if (b3 == 0) {
                        if (!p.c.clan.clanName.isEmpty()) {
                            p.c.place.chatNPC(p, (short)b1, "Hiện tại con đã có gia tộc không thể thành lập thêm được nữa.");
                        } else if (p.luong < 1500000) {
                            p.c.place.chatNPC(p, (short)b1, "Để thành lập gia tộc con cần phải cóc đủ 1.500.000 lượng trong người.");
                        } else {
                            this.sendWrite(p, (short)50, "Tên gia tộc");
                        }
                    }
                } else if (b2 == 2) {
                    if (p.c.isNhanban) {
                        p.conn.sendMessageLog("Chức năng này không dành cho phân thân");
                        return;
                    }
                    if (b3 == 0) {
                        Service.evaluateCave(p.c);
                    } else {
                        Cave cave = null;
                        if (p.c.caveID != -1) {
                            if (Cave.caves.containsKey(p.c.caveID)) {
                                cave = Cave.caves.get(p.c.caveID);
                                p.c.place.leave(p);
                                cave.map[0].area[0].EnterMap0(p.c);
                            }
                        } else if (p.c.party != null) {
                            if (p.c.party.cave == null && p.c.party.master != p.c.id) {
                                p.conn.sendMessageLog("Chỉ có nhóm trưởng mới được phép mở cửa hang động");
                                return;
                            }
                        }
                        if (cave == null) {
                            if (p.c.nCave <= 0) {
                                p.c.place.chatNPC(p, (short)b1, "Số lần vào hang động cảu con hôm nay đã hết hãy quay lại vào ngày mai.");
                                return;
                            }
                            if (b3 == 1) {
                                if (p.c.level < 30 || p.c.level > 39) {
                                    p.conn.sendMessageLog("Trình độ không phù hợp");
                                    return;
                                }
                                if (p.c.party != null) {
                                    synchronized (p.c.party.ninjas) {
                                        for (byte i = 0; i < p.c.party.ninjas.size(); i++) {
                                            if (p.c.party.ninjas.get(i).level < 30 || p.c.party.ninjas.get(i).level > 39) {
                                                p.conn.sendMessageLog("Thành viên trong nhóm trình độ không phù hợp");
                                                return;
                                            }
                                        }
                                    }
                                }
                                if (p.c.party != null){
                                    if (p.c.party.cave == null) {
                                        cave = new Cave(3);
                                        p.c.party.openCave(cave, p.c.name);
                                    } else {
                                        cave = p.c.party.cave;
                                    }
                                } else {
                                    cave = new Cave(3);
                                }
                                p.c.caveID = cave.caveID;
                            }
                            if (b3 == 2) {
                                if (p.c.level < 40 || p.c.level > 49) {
                                    p.conn.sendMessageLog("Trình độ không phù hợp");
                                    return;
                                }
                                if (p.c.party != null) {
                                    synchronized (p.c.party) {
                                        for (byte i = 0; i < p.c.party.ninjas.size(); i++) {
                                            if (p.c.party.ninjas.get(i).level < 40 || p.c.party.ninjas.get(i).level > 49) {
                                                p.conn.sendMessageLog("Thành viên trong nhóm trình độ không phù hợp");
                                                return;
                                            }
                                        }
                                    }
                                }
                                if (p.c.party != null){
                                    if (p.c.party.cave == null) {
                                        cave = new Cave(4);
                                        p.c.party.openCave(cave, p.c.name);
                                    } else {
                                        cave = p.c.party.cave;
                                    }
                                } else {
                                    cave = new Cave(4);
                                }
                                p.c.caveID = cave.caveID;
                            }
                            if (b3 == 3) {
                                if (p.c.level < 50 || p.c.level > 59) {
                                    p.conn.sendMessageLog("Trình độ không phù hợp");
                                    return;
                                }
                                if (p.c.party != null) {
                                    synchronized (p.c.party.ninjas) {
                                        for (byte i = 0; i < p.c.party.ninjas.size(); i++) {
                                            if (p.c.party.ninjas.get(i).level < 50 || p.c.party.ninjas.get(i).level > 59) {
                                                p.conn.sendMessageLog("Thành viên trong nhóm trình độ không phù hợp");
                                                return;
                                            }
                                        }
                                    }
                                }
                                if (p.c.party != null){
                                    if (p.c.party.cave == null) {
                                        cave = new Cave(5);
                                        p.c.party.openCave(cave, p.c.name);
                                    } else {
                                        cave = p.c.party.cave;
                                    }
                                } else {
                                    cave = new Cave(5);
                                }
                                p.c.caveID = cave.caveID;
                            }
                            if (b3 == 4) {
                                if (p.c.level < 60 || p.c.level > 69) {
                                    p.conn.sendMessageLog("Trình độ không phù hợp");
                                    return;
                                } else if (p.c.party != null && p.c.party.ninjas.size() > 1) {
                                    p.conn.sendMessageLog("Hoạt động lần này chỉ được phép một mình");
                                    return;
                                }
                                cave = new Cave(6);
                                p.c.caveID = cave.caveID;
                            }
                            if (b3 == 5) {
                                if (p.c.level < 70 || p.c.level > 89) {
                                    p.conn.sendMessageLog("Trình độ không phù hợp");
                                    return;
                                }
                                if (p.c.party != null) {
                                    synchronized (p.c.party.ninjas) {
                                        for (byte i = 0; i < p.c.party.ninjas.size(); i++) {
                                            if (p.c.party.ninjas.get(i).level < 70 || p.c.party.ninjas.get(i).level > 89) {
                                                p.conn.sendMessageLog("Thành viên trong nhóm trình độ không phù hợp");
                                                return;
                                            }
                                        }
                                    }
                                }
                                if (p.c.party != null){
                                    if (p.c.party.cave == null) {
                                        cave = new Cave(7);
                                        p.c.party.openCave(cave, p.c.name);
                                    } else {
                                        cave = p.c.party.cave;
                                    }
                                } else {
                                    cave = new Cave(7);
                                }
                                p.c.caveID = cave.caveID;
                            }
                            if (b3 == 6) {
                                if (p.c.level < 90 || p.c.level > 130) {
                                    p.conn.sendMessageLog("Trình độ không phù hợp");
                                    return;
                                }
                                if (p.c.party != null) {
                                    synchronized (p.c.party.ninjas) {
                                        for (byte i = 0; i < p.c.party.ninjas.size(); i++) {
                                            if (p.c.party.ninjas.get(i).level < 90 || p.c.party.ninjas.get(i).level > 131) {
                                                p.conn.sendMessageLog("Thành viên trong nhóm trình độ không phù hợp");
                                                return;
                                            }
                                        }
                                    }
                                }
                                if (p.c.party != null){
                                    if (p.c.party.cave == null) {
                                        cave = new Cave(9);
                                        p.c.party.openCave(cave, p.c.name);
                                    } else {
                                        cave = p.c.party.cave;
                                    }
                                } else {
                                    cave = new Cave(9);
                                }
                                p.c.caveID = cave.caveID;
                            }
                            if (cave != null) {
                                p.c.nCave--;
                                p.c.pointCave = 0;
                                p.c.place.leave(p);
                                cave.map[0].area[0].EnterMap0(p.c);
                            }
                        }
                        p.setPointPB(p.c.pointCave);
                    }
                }
                break;
            //menu npc Furoya
            case 1:
                if (b2 == 0) {
                    if (b3 == 0)
                        p.requestItem(21-p.c.gender);
                    else if (b3 == 1)
                        p.requestItem(23-p.c.gender);
                    else if (b3 == 2)
                        p.requestItem(25-p.c.gender);
                    else if (b3 == 3)
                        p.requestItem(27-p.c.gender);
                    else if (b3 == 4)
                        p.requestItem(29-p.c.gender);
                }
                break;
            //menu npc Ameji
            case 2:
                if (b2 == 0) {
                    if (b3 == 0)
                        p.requestItem(16);
                    else if (b3 == 1)
                        p.requestItem(17);
                    else if (b3 == 2)
                        p.requestItem(18);
                    else if (b3 == 3)
                        p.requestItem(19);
                }
                break;
            //menu npc Kiriko
            case 3:
                if (b2 == 0) {
                    p.requestItem(7);
                } else if (b2 == 1) {
                    p.requestItem(6);
                }
                break;
            //menu npc Tabemono
            case 4:
                switch (b2) {
                    case 0:
                        p.requestItem(9);
                        break;
                    case 1:
                        p.requestItem(8);
                        break;
                }
                break;
            //menu npc Kamakura
            case 5:
                switch (b2) {
                    case 0:
                        p.requestItem(4);
                        break;
                    case 1:
                        p.c.mapLTD = p.c.place.map.id;
                        p.c.place.chatNPC(p, (short)b1, "Lưu tọa độ thành công, khi kiệt sức con sẽ được khiêng về đây");
                        break;
                    case 2:
                        if (b3==0) {
                            if (p.c.isNhanban) {
                                p.conn.sendMessageLog("Chức năng này không dành cho phân thân");
                                return;
                            }
                            if (p.c.level < 60) {
                                p.conn.sendMessageLog("Chức năng yêu cầu trình độ 60");
                                return;
                            }
                            Map ma = server.manager.getMapid(139);
                            for (Place area : ma.area) {
                                if (area.numplayers < ma.template.maxplayers) {
                                    p.c.place.leave(p);
                                    area.EnterMap0(p.c);
                                    return;
                                }
                            }
                        }
                        break;
                }
                break;
            //menu npc Kenshinto
            case 6:
                switch (b2) {
                    case 0:
                        if (b3 == 0)
                            p.requestItem(10);
                        else if (b3 == 1)
                            p.requestItem(31);
                        break;
                    case 1:
                        if (b3 == 0)
                            p.requestItem(12);
                        else if (b3 == 1)
                            p.requestItem(11);
                        break;
                    case 2:
                        p.requestItem(13);
                        break;
                    case 3:
                        p.requestItem(33);
                        break;
                }
                break;
            //menu noc Umayaki
            case 7:
                if (b2 == 0) {
                }
                else if (b2 > 0 && b2 <= Map.arrLang.length) {
                    Map ma = Manager.getMapid(Map.arrLang[b2-1]);
                    for (Place area : ma.area) {
                        if (area.numplayers < ma.template.maxplayers) {
                            p.c.place.leave(p);
                            area.EnterMap0(p.c);
                            return;
                        }
                    }
                }
                break;
            //menu noc Umayaki
            case 8:
                if (b2 >= 0 && b2 < Map.arrTruong.length) {
                    Map ma = Manager.getMapid(Map.arrTruong[b2]);
                    for (Place area : ma.area) {
                        if (area.numplayers < ma.template.maxplayers) {
                            p.c.place.leave(p);
                            area.EnterMap0(p.c);
                            return;
                        }
                    }
                } else {
                    
                }
                break;
            //menu npc cô toyotomi
            case 9:
                if (b2 == 0) {
                    if (b3 == 0) {
                        server.manager.sendTB(p, "Top đại gia yên", BXHManager.getStringBXH(0));
                    } else if (b3 == 1) {
                        server.manager.sendTB(p, "Top cao thủ", BXHManager.getStringBXH(1));
                    } else if (b3 == 2) {
                        server.manager.sendTB(p, "Top gia tộc", BXHManager.getStringBXH(2));
                    } else if (b3 == 3) {
                        server.manager.sendTB(p, "Top hang động", BXHManager.getStringBXH(3));
                    }
                }
                if (b2 == 1) {
                    if (p.c.get().nclass > 0)
                        p.c.place.chatNPC(p, (short)b1, "Con đã vào lớp từ trước rồi mà");
                    else if (p.c.get().ItemBody[1] != null)
                        p.c.place.chatNPC(p, (short)b1, "Con cần tháo vũ khí ra để đến đây nhập học nhé");
                    else if (p.c.getBagNull() < 3)
                        p.c.place.chatNPC(p, (short)b1, "Hành trang phải có đủ 2 ô để nhận đồ con nhé");
                    else {
                        p.c.addItemBag(false, ItemData.itemDefault(420));
                        if (b3 == 0)
                            p.Admission((byte)1);
                        else if (b3 == 1)
                            p.Admission((byte)2);
                        p.c.place.chatNPC(p, (short)b1, "Hãy chăm chỉ quay tay để lên cấp con nhé");
                    }
                } else if (b2 == 2) {
                    if (p.c.get().nclass != 1 && p.c.get().nclass != 2) {
                        p.c.place.chatNPC(p, (short)b1, "Con không phải học sinh trường này nên không thể tẩy điểm ở đây");
                    } else {
                        if (b3 == 0) {
                            p.restPpoint();
                            p.c.place.chatNPC(p, (short)b1, "Ta đã giúp con tẩy điểm tiềm năng, hãy sử dụng tốt điểm tiềm năng nhé");
                        } else if (b3 == 1) {
                            p.restSpoint();
                            p.c.place.chatNPC(p, (short)b1, "Ta đã giúp con tẩy điểm kĩ năng, hãy sử dụng tốt điểm kĩ năng nhé");
                        }
                    }
                }
                break;
            //menu npc cô Ookamesama
            case 10:
                if (b2 == 0) {
                    if (b3 == 0) {
                        server.manager.sendTB(p, "Top đại gia yên", BXHManager.getStringBXH(0));
                    } else if (b3 == 1) {
                        server.manager.sendTB(p, "Top cao thủ", BXHManager.getStringBXH(1));
                    } else if (b3 == 2) {
                        server.manager.sendTB(p, "Top gia tộc", BXHManager.getStringBXH(2));
                    } else if (b3 == 3) {
                        server.manager.sendTB(p, "Top hang động", BXHManager.getStringBXH(3));
                    }
                }
                if (b2 == 1) {
                    if (p.c.get().nclass > 0)
                        p.c.place.chatNPC(p, (short)b1, "Con đã vào lớp từ trước rồi mà");
                    else if (p.c.get().ItemBody[1] != null)
                        p.c.place.chatNPC(p, (short)b1, "Con cần tháo vũ khí ra để đến đây nhập học nhé");
                    else if (p.c.getBagNull() < 3)
                        p.c.place.chatNPC(p, (short)b1, "Hành trang phải có đủ 2 ô để nhận đồ con nhé");
                    else {
                        p.c.addItemBag(false, ItemData.itemDefault(421));
                        if (b3 == 0)
                            p.Admission((byte)3);
                        else if (b3 == 1)
                            p.Admission((byte)4);
                        p.c.place.chatNPC(p, (short)9, "Hãy chăm chỉ quay tay để lên cấp con nhé");
                    }
                } else if (b2 == 2) {
                    if (p.c.get().nclass != 3 && p.c.get().nclass != 4) {
                        p.c.place.chatNPC(p, (short)b1, "Con không phải học sinh trường này nên không thể tẩy điểm ở đây");
                    } else {
                        if (b3 == 0) {
                            p.restPpoint();
                            p.c.place.chatNPC(p, (short)b1, "Ta đã giúp con tẩy điểm tiềm năng, hãy sử dụng tốt điểm tiềm năng nhé");
                        } else if (b3 == 1) {
                            p.restSpoint();
                            p.c.place.chatNPC(p, (short)b1, "Ta đã giúp con tẩy điểm kĩ năng, hãy sử dụng tốt điểm kĩ năng nhé");
                        }
                    }
                }
                break;
            //menu npc thầy Kazeto
            case 11:
                if (b2 == 0) {
                    if (b3 == 0) {
                        server.manager.sendTB(p, "Top đại gia yên", BXHManager.getStringBXH(0));
                    } else if (b3 == 1) {
                        server.manager.sendTB(p, "Top cao thủ", BXHManager.getStringBXH(1));
                    } else if (b3 == 2) {
                        server.manager.sendTB(p, "Top gia tộc", BXHManager.getStringBXH(2));
                    } else if (b3 == 3) {
                        server.manager.sendTB(p, "Top hang động", BXHManager.getStringBXH(3));
                    }
                }
                if (b2 == 1) {
                    if (p.c.get().nclass > 0)
                        p.c.place.chatNPC(p, (short)b1, "Con đã vào lớp từ trước rồi mà");
                    else if (p.c.get().ItemBody[1] != null)
                        p.c.place.chatNPC(p, (short)b1, "Con cần tháo vũ khí ra để đến đây nhập học nhé");
                    else if (p.c.getBagNull() < 3)
                        p.c.place.chatNPC(p, (short)b1, "Hành trang phải có đủ 2 ô để nhận đồ con nhé");
                    else {
                        p.c.addItemBag(false, ItemData.itemDefault(422));
                        if (b3 == 0)
                            p.Admission((byte)5);
                        else if (b3 == 1)
                            p.Admission((byte)6);
                        p.c.place.chatNPC(p, (short)b1, "Hãy chăm chỉ quay tay để lên cấp con nhé");
                    }
                } else if (b2 == 2) {
                    if (p.c.get().nclass != 5 && p.c.get().nclass != 6) {
                        p.c.place.chatNPC(p, (short)b1, "Con không phải học sinh trường này nên không thể tẩy điểm ở đây");
                    } else {
                        if (b3 == 0) {
                            p.restPpoint();
                            p.c.place.chatNPC(p, (short)b1, "Ta đã giúp con tẩy điểm tiềm năng, hãy sử dụng tốt điểm tiềm năng nhé");
                        } else if (b3 == 1) {
                            p.restSpoint();
                            p.c.place.chatNPC(p, (short)b1, "Ta đã giúp con tẩy điểm kĩ năng, hãy sử dụng tốt điểm kĩ năng nhé");
                        }
                    }
                }
                break;        
            //menu npc Tajima
           case 12:
                if (b2 == 0) {
//                    if (p.nj.denbu == 2) {
//                        p.nj.place.chatNPC(p, (short) b1, "Con đã nhận đền bù từ ad Đức rồi nha");
//                    } else {
//                        if (p.nj.getBagNull() < 1) {
//                            p.nj.place.chatNPC(p, (short) b1, "Hành trang không đủ 51 chỗ trống");
//                        } else {
//                            p.nj.place.chatNPC(p, (short) b1, "Xin lỗi bạn vì bảo trì hơi lâu");
//                            p.nj.denbu = 2;
//                            p.upluongMessage(100000);
//                            Item it = new Item();
//                            it.id = 384;
//                            it.quantity = 15;
//                            it.isLock = true;
//                            p.nj.addItemBag(true, it);
////                            for (byte i = 0; i < 50; i++) {
////                                it = new Item();
////                                it.id = 454;
////                                it.isLock = true;
////                                p.nj.addItemBag(true, it);
////                            }
//                        }
//                    }
                } else if (b2 == 3) {
                    if (p.c.timeRemoveClone > System.currentTimeMillis()) {
                        p.toNhanBan();
                    }
                } else if (b2 == 4) {
                    if (!p.c.clone.isDie && p.c.timeRemoveClone > System.currentTimeMillis()) {
                        p.exitNhanBan(false);
                    }
                } else {
                    p.c.place.chatNPC(p, (short) b1, "Con đang thực hiện nhiệm vụ kiên trì diệt ác, hãy chọn Menu/Nhiệm vụ để biết mình đang làm đến đâu");
                }
                break;
            //Menu npc Kirin
           /* case 19:
                if (b2 == 0) {
                    if (p.c.exptype == 0) {
                        p.c.exptype = 1;
                        p.c.place.chatNPC(p, (short) b1, "Đã tắt không nhận kinh nghiệm");
                    } else {
                        p.c.exptype = 0;
                        p.c.place.chatNPC(p, (short) b1, "Đã bật không nhận kinh nghiệm");
                    }
                } else if (b2 == 1) {
                    p.passold = "";
                    this.sendWrite(p, (short)51, "Nhập mật khẩu cũ");
                }
                break;
            //menu npc Guriin
           /* case 22:
                if (b2 == 0) {
                    if (p.c.clan.clanName.isEmpty()) {
                        p.c.place.chatNPC(p, (short) b1, "Con cần phải có gia tộc thì mới có thể điểm danh được nhé");
                    } else if (p.c.ddClan) {
                        p.c.place.chatNPC(p, (short) b1, "Hôm nay con đã điểm danh rồi nhé, hãy quay lại đây vào ngày mai");
                    } else {
                        p.c.ddClan = true;
                        ClanManager clan = ClanManager.getClanName(p.c.clan.clanName);
                        if (clan == null) {
                            p.c.place.chatNPC(p, (short) b1, "Gia tộc lỗi");
                            return;
                        }
                        p.upExpClan(util.nextInt(1,10+clan.level));
                        p.upluongMessage(50*clan.level);
                        p.c.upyenMessage(500000*clan.level);
                        p.c.place.chatNPC(p, (short) b1, "Điểm danh mỗi ngày sẽ nhận được các phần quà giá trị");
                    }
                }
                break;*/
            //Menu npc Goosho
            case 26:
                if (b2 == 0) {
                    p.requestItem(14);
                    break;
                } else if (b2 == 1) {
                    p.requestItem(15);
                    break;
                } else if (b2 == 2) {
                    p.requestItem(32);
                } else if (b2 == 3) {
                    p.requestItem(34);
                }
                break;
            //Menu npc Rakkii
            case 30:
                switch (b2) {
                    case 0:
                        p.requestItem(38);
                        break;
                    case 2:
                        if (b3 == 0) {
                            server.manager.rotationluck[0].luckMessage(p);
                        } else if (b3 == 2) {
                            server.manager.sendTB(p, "Vòng xoay vip", "Tham gia đi xem luật lm gì");
                        }
                        break;
                    case 3:
                        if (b3 == 0) {
                            server.manager.rotationluck[1].luckMessage(p);
                        } else if (b3 == 2) {
                            server.manager.sendTB(p, "Vòng xoay thường", "Tham gia đi xem luật lm gì");
                        }
                        break;
                }
                break;
            //menu npc Kagai
            case 32:
                switch (b2) {
                    case 4:
                       if (b3 == 1)
                           p.requestItem(44);
                       else if (b3 == 2)
                           p.requestItem(45);
                        break;
                }
                break;
            //menu npc Tiên nữ
            case 33:
                if (p.typemenu == 33) {
                    switch (server.manager.event) {
                        case 1:
                            switch (b2) {
                                case 0:
                                    if (p.c.quantityItemyTotal(432) < 1 || p.c.quantityItemyTotal(428) < 3 || p.c.quantityItemyTotal(429) < 2 || p.c.quantityItemyTotal(430) < 3)
                                        p.c.place.chatNPC(p, (short)b1, "Hành trang của con không có đủ nguyên liệu");
                                    else if (p.c.getBagNull() == 0)
                                        p.conn.sendMessageLog("Hành trang không đủ chỗ trống");
                                    else {
                                        Item it = ItemData.itemDefault(434);
                                        p.c.addItemBag(true, it);
                                        p.c.removeItemBags(432, 1);
                                        p.c.removeItemBags(428, 3);
                                        p.c.removeItemBags(429, 2);
                                        p.c.removeItemBags(430, 3);
                                    }
                                    break;
                                case 1:
                                    if (p.c.quantityItemyTotal(433) < 1 || p.c.quantityItemyTotal(428) < 2 || p.c.quantityItemyTotal(429) < 3 || p.c.quantityItemyTotal(431) < 2)
                                        p.c.place.chatNPC(p, (short)b1, "Hành trang của con không có đủ nguyên liệu");
                                    else if (p.c.getBagNull() == 0)
                                        p.conn.sendMessageLog("Hành trang không đủ chỗ trống");
                                    else {
                                        Item it = ItemData.itemDefault(435);
                                        p.c.addItemBag(true, it);
                                        p.c.removeItemBags(433, 1);
                                        p.c.removeItemBags(428, 2);
                                        p.c.removeItemBags(429, 3);
                                        p.c.removeItemBags(431, 2);
                                    }
                                    break;
                            }
                            break;
                        case 2:
                            switch (b2) {
                                case 0:
                                    if (p.c.quantityItemyTotal(304) < 1 || p.c.quantityItemyTotal(298) < 1 || p.c.quantityItemyTotal(299) < 1 || p.c.quantityItemyTotal(300) < 1 || p.c.quantityItemyTotal(301) < 1)
                                        p.c.place.chatNPC(p, (short)b1, "Hành trang của con không có đủ nguyên liệu");
                                    else if (p.c.getBagNull() == 0)
                                        p.conn.sendMessageLog("Hành trang không đủ chỗ trống");
                                    else {
                                        Item it = ItemData.itemDefault(302);
                                        p.c.addItemBag(true, it);
                                        p.c.removeItemBags(304, 1);
                                        p.c.removeItemBags(298, 1);
                                        p.c.removeItemBags(299, 1);
                                        p.c.removeItemBags(300, 1);
                                        p.c.removeItemBags(301, 1);
                                    }
                                    break;
                                case 1:
                                    if (p.c.quantityItemyTotal(305) < 1 || p.c.quantityItemyTotal(298) < 1 || p.c.quantityItemyTotal(299) < 1 || p.c.quantityItemyTotal(300) < 1 || p.c.quantityItemyTotal(301) < 1)
                                        p.c.place.chatNPC(p, (short)b1, "Hành trang của con không có đủ nguyên liệu");
                                    else if (p.c.getBagNull() == 0)
                                        p.conn.sendMessageLog("Hành trang không đủ chỗ trống");
                                    else {
                                        Item it = ItemData.itemDefault(303);
                                        p.c.addItemBag(true, it);
                                        p.c.removeItemBags(305, 1);
                                        p.c.removeItemBags(298, 1);
                                        p.c.removeItemBags(299, 1);
                                        p.c.removeItemBags(300, 1);
                                        p.c.removeItemBags(301, 1);
                                    }
                                    break;
                                case 2:
                                    if (p.c.yen < 10000|| p.c.quantityItemyTotal(292) < 3 || p.c.quantityItemyTotal(293) < 2 || p.c.quantityItemyTotal(294) < 3)
                                        p.c.place.chatNPC(p, (short)b1, "Hành trang của con không có đủ nguyên liệu hoặc yên");
                                    else if (p.c.getBagNull() == 0)
                                        p.conn.sendMessageLog("Hành trang không đủ chỗ trống");
                                    else {
                                        Item it = ItemData.itemDefault(298);
                                        p.c.addItemBag(true, it);
                                        p.c.upyenMessage(-10000);
                                        p.c.removeItemBags(292, 3);
                                        p.c.removeItemBags(293, 2);
                                        p.c.removeItemBags(294, 3);
                                    }
                                    break;
                                case 3:
                                    if (p.c.yen < 10000|| p.c.quantityItemyTotal(292) < 2 || p.c.quantityItemyTotal(295) < 3 || p.c.quantityItemyTotal(294) < 2)
                                        p.c.place.chatNPC(p, (short)b1, "Hành trang của con không có đủ nguyên liệu hoặc yên");
                                    else if (p.c.getBagNull() == 0)
                                        p.conn.sendMessageLog("Hành trang không đủ chỗ trống");
                                    else {
                                        Item it = ItemData.itemDefault(299);
                                        p.c.addItemBag(true, it);
                                        p.c.upyenMessage(-10000);
                                        p.c.removeItemBags(292, 2);
                                        p.c.removeItemBags(295, 3);
                                        p.c.removeItemBags(294, 2);
                                    }
                                    break;
                                case 4:
                                    if (p.c.yen < 10000|| p.c.quantityItemyTotal(292) < 2 || p.c.quantityItemyTotal(295) < 3 || p.c.quantityItemyTotal(297) < 3)
                                        p.c.place.chatNPC(p, (short)b1, "Hành trang của con không có đủ nguyên liệu hoặc yên");
                                    else if (p.c.getBagNull() == 0)
                                        p.conn.sendMessageLog("Hành trang không đủ chỗ trống");
                                    else {
                                        Item it = ItemData.itemDefault(300);
                                        p.c.addItemBag(true, it);
                                        p.c.upyenMessage(-10000);
                                        p.c.removeItemBags(292, 2);
                                        p.c.removeItemBags(295, 3);
                                        p.c.removeItemBags(297, 3);
                                    }
                                    break;
                                case 5:
                                    if (p.c.yen < 10000|| p.c.quantityItemyTotal(292) < 2 || p.c.quantityItemyTotal(296) < 2 || p.c.quantityItemyTotal(297) < 3)
                                        p.c.place.chatNPC(p, (short)b1, "Hành trang của con không có đủ nguyên liệu hoặc yên");
                                    else if (p.c.getBagNull() == 0)
                                        p.conn.sendMessageLog("Hành trang không đủ chỗ trống");
                                    else {
                                        Item it = ItemData.itemDefault(301);
                                        p.c.addItemBag(true, it);
                                        p.c.upyenMessage(-10000);
                                        p.c.removeItemBags(292, 2);
                                        p.c.removeItemBags(296, 2);
                                        p.c.removeItemBags(297, 3);
                                    }
                                    break;
                            }
                            break;
                        default:
                            p.c.place.chatNPC(p, (short)b1, "Hiện tại chưa có sự kiện diễn ra");
                            break;
                    }
                }
                break;
            case 92:
                p.typemenu = ((b2 == 0) ? 93 : 94);
                doMenuArray(p, new String[]{"Thông tin", "Luật chơi"});
                break;
            case 93:
                if (b2 == 0) {
                    server.manager.rotationluck[0].luckMessage(p);
                } else if (b2 == 1) {
                    server.manager.sendTB(p, "Vòng xoay vip", "Tham gia đi xem luật lm gì");
                }
                break;
            case 94:
                if (b2 == 0) {
                    server.manager.rotationluck[1].luckMessage(p);
                } else if (b2 == 1) {
                    server.manager.sendTB(p, "Vòng xoay thường", "Tham gia đi xem luật lm gì");
                }
                break;
            case 95:
                break;
            case 120:
                if (b2 > 0 && b2 < 7) {
                    p.Admission(b2);
                }
                break;
            default:
                p.c.place.chatNPC(p, (short) b1, "Chức năng này đang cập nhật");
                break;

        }
        util.Debug("byte1 "+b1+" byte2 "+b2+" byte3 "+b3);
    }
    

    public void openUINpc(Player p, Message m) throws IOException {
        short idnpc = m.reader().readShort();//idnpc
        m.cleanup();
        p.c.typemenu = 0;
        p.typemenu = idnpc;
        if (idnpc == 33) {
            switch (server.manager.event) {
                case 1:
                    doMenuArray(p,new String[]{"Diều giấy","Diều vải"});
                    return;
                case 2:
                    doMenuArray(p,new String[]{"Hộp bánh thường","Hộp bánh vip","Bánh thập cẩm","Bánh Dẻo","Đậu xanh","Bánh pía"});
                    return;
            }
        }
       m = new Message(40);
//        if (idnpc == 12) {
//            m.writer().writeUTF("Nhận đền bù");
//        }
        
        m.writer().flush();
        p.conn.sendMessage(m);
        m.cleanup();
    }


    public void doMenuArray(Player p, String[] menu) throws IOException {
        Message m = new Message(63);
        for (byte i = 0; i < menu.length; i++) {
            m.writer().writeUTF(menu[i]);//menu
        }
        m.writer().flush();
        p.conn.sendMessage(m);
        m.cleanup();
    }
    
    public void sendWrite(Player p, short type, String title) {
        try {
            Message m = new Message(92);
            m.writer().writeUTF(title);
            m.writer().writeShort(type);
            m.writer().flush();
            p.conn.sendMessage(m);
            m.cleanup();
        } catch (IOException e) {}
    }
}
