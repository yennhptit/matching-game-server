package org.example.matchinggameserver.controller;

import org.example.matchinggameserver.MainApp;
import org.example.matchinggameserver.dao.GameDAO;
import org.example.matchinggameserver.dao.MessageDAO;
import org.example.matchinggameserver.dao.UserDAO;
import org.example.matchinggameserver.model.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ServerThread implements Runnable {

    private User user;
    private final Socket socketOfServer;
    private final int clientNumber;
    private BufferedReader is;
    private BufferedWriter os;
    private boolean isClosed;
    private Room room;
    private Match match;
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final String clientIP;
    private AdminController adminController;
    private ArrayList<Invitation> invitationList;
    private boolean isFindingMatch;
    private final MessageDAO messageDAO;

    public ServerThread(Socket socketOfServer, int clientNumber) {
        this.socketOfServer = socketOfServer;
        this.clientNumber = clientNumber;
        System.out.println("Server thread number " + clientNumber + " Started");
        userDAO = new UserDAO();
        gameDAO = new GameDAO();
        messageDAO = new MessageDAO();
        isClosed = false;
        room = null;
        adminController = new AdminController();
        invitationList = new ArrayList<>();
        match = null;
        if (this.socketOfServer.getInetAddress().getHostAddress().equals("26.250.85.6")) {
            clientIP = "26.250.85.6";
        } else {
            clientIP = this.socketOfServer.getInetAddress().getHostAddress();
        }

    }

    public BufferedReader getIs() {
        return is;
    }

    public BufferedWriter getOs() {
        return os;
    }

    public int getClientNumber() {
        return clientNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getClientIP() {
        return clientIP;
    }
    
    public boolean getIsFindingMatch()
    {
    	return isFindingMatch;
    }
    
    public void setIsFindingMatch(boolean value)
    {
    	isFindingMatch = value;
    }

    public String getStringFromUser(User user1) {
        return user1.getID() + "," + user1.getUsername()
                + "," + user1.getPassword() + "," + user1.getNumberOfGame() + "," +
                user1.getNumberOfWin() + "," + user1.getNumberOfDraw() + "," +  user1.isOnline() + "," + user1.isPlaying() + "," + user1.getStar() + "," + user1.getRank();
    }

    public void goToOwnRoom() throws IOException {
        write("go-to-room," + room.getId() + "," + room.getCompetitor(this.getClientNumber()).getClientIP() + ",1," + getStringFromUser(room.getCompetitor(this.getClientNumber()).getUser()));
        room.getCompetitor(this.clientNumber).write("go-to-room," + room.getId() + "," + this.clientIP + ",0," + getStringFromUser(user));
    }

    public void goToPartnerRoom() throws IOException {
        write("go-to-room," + room.getId() + "," + room.getCompetitor(this.getClientNumber()).getClientIP() + ",0," + getStringFromUser(room.getCompetitor(this.getClientNumber()).getUser()));
        room.getCompetitor(this.clientNumber).write("go-to-room," + room.getId() + "," + this.clientIP + ",1," + getStringFromUser(user));
    }
    public String getStringFromMessage(Message message)
    {
        return message.getId() + "," + message.getSenderId() + "," + message.getReceiverId() + "," + message.getContent() + "," + message.getTimestamp()
                + "," + message.getType();
    }

    @Override
    public void run() {
        try {
            // Mở luồng vào ra trên Socket tại Server.
            is = new BufferedReader(new InputStreamReader(socketOfServer.getInputStream()));
            os = new BufferedWriter(new OutputStreamWriter(socketOfServer.getOutputStream()));
            System.out.println("Khời động luông mới thành công, ID là: " + clientNumber);
            write("server-send-id" + "," + this.clientNumber);
            AdminController adminController = MainApp.getAdminController();
            String message;
            while (!isClosed) {
                message = is.readLine();
                if (message == null) {
                    break;
                }
                String[] messageSplit = message.split(",");
                System.out.println("messageSplit[0]: " + messageSplit[0]);
                
                //Xác minh
                if (messageSplit[0].equals("client-verify")) {
                    System.out.println(message);
                    User user1 = userDAO.verifyUser(new User(messageSplit[1], messageSplit[2]));
                    if (user1 == null)
                        write("wrong-user," + messageSplit[1] + "," + messageSplit[2]);
                    else if (!user1.isOnline()) {
                        write("login-success," + getStringFromUser(user1));
                        this.user = user1;
                        userDAO.updateToOnline(this.user.getID());
                        adminController.addMessage("[" + user1.getID() + "] " + user1.getUsername() + ": đang online");

                        Server.serverThreadBus.boardCast(clientNumber, "chat-server," + user1.getUsername() + ": đang online");
//                        Server.admin.addMessage("[" + user1.getID() + "] " + user1.getUsername() + " đang online");
//                        Server.adminController.addMessage("[" + user1.getID() + "] " + user1.getUsername() + " đang online");
                    } else {
                        write("dupplicate-login," + messageSplit[1] + "," + messageSplit[2]);
                    }
                }
                //Xử lý đăng kí
                if (messageSplit[0].equals("register")) {
                    boolean checkdup = userDAO.checkDuplicated(messageSplit[1]);
                    if (checkdup) write("duplicate-username,");
                    else {
                        User userRegister = new User(messageSplit[1], messageSplit[2]);
                        userDAO.addUser(userRegister);
                        this.user = userDAO.verifyUser(userRegister);
                        userDAO.updateToOnline(this.user.getID());
                        adminController.addMessage("[" + user.getID() + "] " + user.getUsername() + ": đang online");
                        Server.serverThreadBus.boardCast(clientNumber, "chat-server," + this.user.getUsername() + ": đang online");
                        write("login-success," + getStringFromUser(this.user));
                    }
                }
                //Xử lý người chơi đăng xuất
                if (messageSplit[0].equals("offline")) {
                    System.out.println(messageSplit[1]);
                    int id = Integer.parseInt(messageSplit[1]);
                    User u = userDAO.getbyID(id);
                    removeAllInvitationsAsSenderAndReceiver();
                    isFindingMatch = false;
//                    userDAO.updateToOffline(this.user.getID());
                    userDAO.updateToOffline(id);
//                    Server.admin.addMessage("[" + user.getID() + "] " + user.getUsername() + " đã offline");
                    adminController.addMessage("[" + u.getID() + "] " + u.getUsername() + ": đã offline");
                    Server.serverThreadBus.boardCast(clientNumber, "chat-server," + u.getUsername() + ": đã offline");
                    write("back-to-login,restart");

//                    write("restart,login");
                    this.user = null;
                }
                //Xử lý xem danh sách bạn bè
                if (messageSplit[0].equals("view-friend-list")) {
                    List<User> friends = userDAO.getListFriend(this.user.getID());
                    StringBuilder res = new StringBuilder("return-friend-list,");
                    for (User friend : friends) {
                        res.append(friend.getID()).append(",").append(friend.getUsername()).append(",").append(friend.isOnline() ? 1 : 0).append(",").append(friend.isPlaying() ? 1 : 0).append(",");
                    }
                    System.out.println(res);
                    write(res.toString());
                }
                //Xử lý chat toàn server
                if (messageSplit[0].equals("chat-server")) {
                    String[] messageSplitTemp = message.split(",", 2);
                    adminController.addMessage("[" + user.getID() + "] " + user.getUsername() + " : " + messageSplitTemp[1]);

                    Server.serverThreadBus.boardCast(clientNumber, "chat-server," + this.user.getUsername() + ": " + messageSplitTemp[1]);



//                    Server.serverThreadBus.boardCast(clientNumber, messageSplit[0] + "," + user.getUsername() + " : " + messageSplit[1]);
//                    Server.admin.addMessage("[" + user.getID() + "] " + user.getUsername() + " : " + messageSplit[1]);
                }
                //Xử lý vào phòng trong chức năng tìm kiếm phòng
                if (messageSplit[0].equals("go-to-room")) {
                    int roomName = Integer.parseInt(messageSplit[1]);
                    boolean isFinded = false;
                    for (ServerThread serverThread : Server.serverThreadBus.getListServerThreads()) {
                        if (serverThread.getRoom() != null && serverThread.getRoom().getId() == roomName) {
                            isFinded = true;
                            if (serverThread.getRoom().getNumberOfUser() == 2) {
                                write("room-fully,");
                            } else {
                                if (serverThread.getRoom().getPassword() == null || serverThread.getRoom().getPassword().equals(messageSplit[2])) {
                                    this.room = serverThread.getRoom();
                                    room.setUser2(this);
                                    room.increaseNumberOfGame();
                                    this.userDAO.updateToPlaying(this.user.getID());
                                    goToPartnerRoom();
                                } else {
                                    write("room-wrong-password,");
                                }
                            }
                            break;
                        }
                    }
                    if (!isFinded) {
                        write("room-not-found,");
                    }
                }
                //Xử lý lấy danh sách bảng xếp hạng
                if (messageSplit[0].equals("get-rank-charts")) {
                    System.out.println("get-rank-charts");
                    List<User> ranks = userDAO.getUserStaticRank();
                    StringBuilder res = new StringBuilder("return-get-rank-charts,");
                    for (User user : ranks) {
                        res.append(getStringFromUser(user)).append(",");
                        System.out.println(user.toString());
                    }
                    System.out.println(res);
                    write(res.toString());
                }
                // Xử lý lấy list user chua string
                if (messageSplit[0].equals("get-list-user-contain-string")) {
                    List<User> users = userDAO.getUsersByUsernameContaining(messageSplit[1]);
                    StringBuilder res = new StringBuilder("return-get-list-user-contain-string,");
                    for (User user : users) {
                        res.append(user.getID()).append(",").append(user.getUsername()).append(",");
                    }
                    System.out.println(res);
                    write(res.toString());
                }
                //Xử lý tạo phòng
                if (messageSplit[0].equals("create-room")) {
                    room = new Room(this);
                    if (messageSplit.length == 2) {
                        room.setPassword(messageSplit[1]);
                        write("your-created-room," + room.getId() + "," + messageSplit[1]);
                        System.out.println("Tạo phòng mới thành công, password là " + messageSplit[1]);
                    } else {
                        write("your-created-room," + room.getId());
                        System.out.println("Tạo phòng mới thành công");
                    }
                    userDAO.updateToPlaying(this.user.getID());
                }
                //Xử lý xem danh sách phòng trống
                if (messageSplit[0].equals("view-room-list")) {
                    StringBuilder res = new StringBuilder("room-list,");
                    int number = 1;
                    for (ServerThread serverThread : Server.serverThreadBus.getListServerThreads()) {
                        if (number > 8) break;
                        if (serverThread.room != null && serverThread.room.getNumberOfUser() == 1) {
                            res.append(serverThread.room.getId()).append(",").append(serverThread.room.getPassword()).append(",");
                        }
                        number++;
                    }
                    write(res.toString());
                    System.out.println(res);
                }
                //Xử lý lấy thông tin kết bạn và rank
                if (messageSplit[0].equals("check-friend")) {
                    String res = "check-friend-response,";
                    res += (userDAO.checkIsFriend(this.user.getID(), Integer.parseInt(messageSplit[1])) ? 1 : 0);
                    write(res);
                }
                //Xử lý tìm phòng nhanh
                if (messageSplit[0].equals("quick-room")) {
                    boolean isFinded = false;
                    for (ServerThread serverThread : Server.serverThreadBus.getListServerThreads()) {
                        if (serverThread.room != null && serverThread.room.getNumberOfUser() == 1 && serverThread.room.getPassword().equals(" ")) {
                            serverThread.room.setUser2(this);
                            this.room = serverThread.room;
                            room.increaseNumberOfGame();
                            System.out.println("Đã vào phòng " + room.getId());
                            goToPartnerRoom();
                            userDAO.updateToPlaying(this.user.getID());
                            isFinded = true;
                            //Xử lý phần mời cả 2 người chơi vào phòng
                            break;
                        }
                    }

                    if (!isFinded) {
                        this.room = new Room(this);
                        userDAO.updateToPlaying(this.user.getID());
                        System.out.println("Không tìm thấy phòng, tạo phòng mới");
                    }
                }
                //Xử lý không tìm được phòng
                if (messageSplit[0].equals("cancel-room")) {
                    userDAO.updateToNotPlaying(this.user.getID());
                    System.out.println("Đã hủy phòng");
                    this.room = null;
                }
                //Xử lý khi có người chơi thứ 2 vào phòng
                if (messageSplit[0].equals("join-room")) {
                    int ID_room = Integer.parseInt(messageSplit[1]);
                    for (ServerThread serverThread : Server.serverThreadBus.getListServerThreads()) {
                        if (serverThread.room != null && serverThread.room.getId() == ID_room) {
                            serverThread.room.setUser2(this);
                            this.room = serverThread.room;
                            System.out.println("Đã vào phòng " + room.getId());
                            room.increaseNumberOfGame();
                            goToPartnerRoom();
                            userDAO.updateToPlaying(this.user.getID());
                            break;
                        }
                    }
                }
                //Xử lý yêu cầu kết bạn
                if (messageSplit[0].equals("make-friend")) {
                    Server.serverThreadBus.getServerThreadByUserID(Integer.parseInt(messageSplit[1]))
                            .write("make-friend-request," + this.user.getID() + "," + userDAO.getUsernameByID(this.user.getID()));
                }
                //Xử lý xác nhận kết bạn
                if (messageSplit[0].equals("make-friend-confirm")) {
                    userDAO.makeFriend(this.user.getID(), Integer.parseInt(messageSplit[1]));
                    System.out.println("Kết bạn thành công");
                }
                //Xử lý khi gửi yêu cầu thách đấu tới bạn bè
                if (messageSplit[0].equals("duel-request")) {
                    Server.serverThreadBus.sendMessageToUserID(Integer.parseInt(messageSplit[1]),
                            "duel-notice," + this.user.getID() + "," + this.user.getUsername());
                }
                //Xử lý khi đối thủ đồng ý thách đấu
                if (messageSplit[0].equals("agree-duel")) {
                    this.room = new Room(this);
                    int ID_User2 = Integer.parseInt(messageSplit[1]);
                    ServerThread user2 = Server.serverThreadBus.getServerThreadByUserID(ID_User2);
                    room.setUser2(user2);
                    user2.setRoom(room);
                    room.increaseNumberOfGame();
                    goToOwnRoom();
                    userDAO.updateToPlaying(this.user.getID());
                }
                //Xử lý khi không đồng ý thách đấu
                if (messageSplit[0].equals("disagree-duel")) {
                    Server.serverThreadBus.sendMessageToUserID(Integer.parseInt(messageSplit[1]), message);
                }
                //Xử lý khi người chơi đánh 1 nước
                if (messageSplit[0].equals("caro")) {
                    room.getCompetitor(clientNumber).write(message);
                }
                if (messageSplit[0].equals("chat")) {
                    room.getCompetitor(clientNumber).write(message);
                }
                if (messageSplit[0].equals("win")) {
                    userDAO.addWinGame(this.user.getID());
                    room.increaseNumberOfGame();
                    room.getCompetitor(clientNumber).write("caro," + messageSplit[1] + "," + messageSplit[2]);
                    room.boardCast("new-game,");
                }
                if (messageSplit[0].equals("lose")) {
                    userDAO.addWinGame(room.getCompetitor(clientNumber).user.getID());
                    room.increaseNumberOfGame();
                    room.getCompetitor(clientNumber).write("competitor-time-out");
                    write("new-game,");
                }
                if (messageSplit[0].equals("draw-request")) {
                    room.getCompetitor(clientNumber).write(message);
                }
                if (messageSplit[0].equals("draw-confirm")) {
                    room.increaseNumberOfDraw();
                    room.increaseNumberOfGame();
                    room.boardCast("draw-game,");
                }
                if (messageSplit[0].equals("draw-refuse")) {
                    room.getCompetitor(clientNumber).write("draw-refuse,");
                }
                if (messageSplit[0].equals("voice-message")) {
                    room.getCompetitor(clientNumber).write(message);
                }
                if (messageSplit[0].equals("left-room")) {
                    if (room != null) {
                        room.setUsersToNotPlaying();
                        room.decreaseNumberOfGame();
                        room.getCompetitor(clientNumber).write("left-room,");
                        room.getCompetitor(clientNumber).room = null;
                        this.room = null;
                    }
                }
                if(messageSplit[0].equals("invite"))
                {
                	int receiverID = Integer.parseInt(messageSplit[1]);
                	ServerThread receiverThread = Server.serverThreadBus.getServerThreadByUserID(receiverID);
                	receiverThread.insertInvitation(user);
                }
                if(messageSplit[0].equals("accept-invitation"))
                {
                	int senderID = Integer.parseInt(messageSplit[1]);
                	User sender = Server.serverThreadBus.getServerThreadByUserID(senderID).user;
                	if(!invitationExistedCheck(sender))
                	{
                		write("invitation-unavailable," + senderID);
                	}
                	else
                	{
                		ServerThread st = Server.serverThreadBus.getServerThreadByUserID(senderID);
                		startMatch(st, this);
                	}
                	removeInvitation(senderID);
                }
                if(messageSplit[0].equals("decline-invitation"))
                {
                	int senderID = Integer.parseInt(messageSplit[1]);User sender = Server.serverThreadBus.getServerThreadByUserID(senderID).user;
                	removeInvitation(senderID);
                }
                if(messageSplit[0].equals("start-finding-match"))
                {
                	isFindingMatch = true;
              	    System.out.println(user.getUsername() + " is finding match");
                	for(ServerThread st : Server.serverThreadBus.getListServerThreads())
                	{
                		if(st.getUser().getID() != user.getID() && st.getIsFindingMatch())
                		{
                			startMatch(this, st);
                			break;
                		}
                	}
                }

                if(messageSplit[0].equals("cancel-finding-match"))
                {
                	isFindingMatch = false;
                	System.out.println(user.getUsername() + " cancel finding match");
                }
                if(messageSplit[0].equals("card-flip")){
                    for(ServerThread st : Server.serverThreadBus.getListServerThreads())
                    {
                        if(st.getUser().getID() == Long.parseLong(messageSplit[2]))
                        {
                            Match match1 = gameDAO.updateUserPoint(Long.parseLong(messageSplit[1]) , Integer.parseInt(messageSplit[2]), Boolean.valueOf(messageSplit[4]));
                            ServerThread opponent = Server.serverThreadBus.getServerThreadByUserID(Integer.parseInt(messageSplit[3]));
                            opponent.write("update-opponent-point," + Long.parseLong(messageSplit[1])  + "," + messageSplit[3]);
                            if(match1.getScore1() >= 10 || match1.getScore2() >= 10){
                                Integer winnerId = match1.getWinnerId() == null ? null : match1.getWinnerId();
                                write("get-result," + Long.parseLong(messageSplit[1]) + "," + messageSplit[2] + "," + messageSplit[3] + "," + winnerId);
                                opponent.write("get-result," + Long.parseLong(messageSplit[1]) + "," + messageSplit[3] + "," + messageSplit[2] + "," + winnerId);
                            }
                            break;
                        }
                    }
                    System.out.println(message);
                }
                if(messageSplit[0].equals("end-game"))
                {
                	room = null;
                    Match match1 = gameDAO.getMatchById(Long.parseLong(messageSplit[1]));
                    String winnerId = match1.getWinnerId() != null ? match1.getWinnerId().toString() : null;
                    write("get-result," + messageSplit[1] + "," + messageSplit[2] + "," + messageSplit[3] + "," + winnerId);
                    System.out.println(message);
                }
                if(messageSplit[0].equals("history-to-home"))
                {
                    write("home-to-history-success");
                }
                if(messageSplit[0].equals("practice-to-home"))
                {
                    write("practice-to-home-success");
                }
                if(messageSplit[0].equals("show-histoy"))
                {
                    List<MatchHistory> matchHistories = gameDAO.getHistory(Integer.parseInt(messageSplit[1]));
                    String matchHistoryStr = matchHistories.stream()
                        .map(MatchHistory::toString)
                        .collect(Collectors.joining(", "));
                    write("get-history," + messageSplit[1] + "," + matchHistoryStr);
                }
                if(messageSplit[0].equals("show-history-popup"))
                {
                    String result = gameDAO.getMatchStats(Integer.parseInt(messageSplit[1]), Integer.parseInt(messageSplit[2]));
                    write("get-history-popup," + messageSplit[1]+ "," + messageSplit[2] + "," + result);
                }
                if(messageSplit[0].equals("end-match-exit"))
                {
                	room.getCompetitor(clientNumber).setRoom(null);
                	room = null;
                    write("end-match-exit-success," + messageSplit[2]);
                    Match match1 = gameDAO.updateAndGetMatchById(Long.parseLong(messageSplit[1]), Integer.parseInt(messageSplit[4]));
                    String winnerId = match1.getWinnerId() != null ? match1.getWinnerId().toString() : null;
                    room = null;
                    for(ServerThread st : Server.serverThreadBus.getListServerThreads())
                    {
                        if(st.getUser().getID() == Integer.parseInt(messageSplit[2]))
                        {
                            ServerThread opponent = Server.serverThreadBus.getServerThreadByUserID(Integer.parseInt(messageSplit[3]));
                            opponent.setRoom(null);
                            opponent.write("get-result," + messageSplit[1]  + "," + messageSplit[3] + "," + messageSplit[2] + "," + winnerId);
                            break;
                        }
                    }
//                    write("get-result," + messageSplit[1] + "," + messageSplit[2] + "," + messageSplit[3] + "," + winnerId);
                    System.out.println(message);
                }
                if(messageSplit[0].equals("send-message-to-user"))
                {
                    String[] messageSplitTemp = message.split(",", 5);

                    String type = messageSplitTemp[1];
                    String senderId = messageSplitTemp[2]; // User ID của người gửi
                    String receiverId = messageSplitTemp[3]; // User ID của người nhận
                    String messageContent = messageSplitTemp[4]; // Nội dung tin nhắn
                    sendMessageToUser(type, receiverId, senderId, messageContent);

                }
                if(messageSplit[0].equals("get-list-message"))
                {
                    int u1 = Integer.parseInt(messageSplit[1]);
                    int u2 = Integer.parseInt(messageSplit[2]);
                    List<Message> messageList = messageDAO.getMessages(u1, u2);
                    StringBuilder res = new StringBuilder("return-get-list-message," + u1 + "," + u2 + ",");
                    for (Message message1 : messageList) {
                        res.append(getStringFromMessage(message1)).append(",");
                    }
                    System.out.println("check: " + res.toString());
                    write(res.toString());
                }
                if(messageSplit[0].equals("get-last-message"))
                {
                    int u1 = Integer.parseInt(messageSplit[1]);
                    List<Message> messageList = messageDAO.getLastMessagesForUser(u1);
                    StringBuilder res = new StringBuilder("return-get-last-message,");
                    for (Message message1 : messageList) {
                        res.append(getStringFromMessage(message1)).append(",");
                    }
                    write(res.toString());
                }
            }
        } catch (IOException e) {
            //Thay đổi giá trị cờ để thoát luồng
            isClosed = true;
            //Cập nhật trạng thái của user
            if (this.user != null) {
                userDAO.updateToOffline(this.user.getID());
                userDAO.updateToNotPlaying(this.user.getID());
                adminController.addMessage("[" + user.getID() + "] " + user.getUsername() + ": đã offline");

                Server.serverThreadBus.boardCast(clientNumber, "chat-server," + this.user.getUsername() + ": đã offline");
//                Server.admin.addMessage("[" + user.getID() + "] " + user.getUsername() + " đã offline");
            }

            //remove thread khỏi bus
            Server.serverThreadBus.remove(clientNumber);
            System.out.println(this.clientNumber + " đã thoát");
            if (room != null) {
                try {
                    if (room.getCompetitor(clientNumber) != null) {
                        room.decreaseNumberOfGame();
                        room.getCompetitor(clientNumber).write("left-room,");
                        room.getCompetitor(clientNumber).room = null;
                    }
                    this.room = null;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        }
    }
    
    public void removeInvitation(int senderID)
    {
    	try {
			write("remove-invitation," + senderID);
			for(Invitation i : invitationList)
			{
				if(i.getSenderID() == senderID)
				{
					invitationList.remove(i);
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void insertInvitation(User sender)
    {
    	if(!invitationExistedCheck(sender))
    	{    		
    		try {
    			Invitation inv = new Invitation(sender, this);
    			invitationList.add(inv);
				write("add-invitation," + sender.getID());
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
    
    private boolean invitationExistedCheck(User sender)
    {
    	for(Invitation inv : invitationList)
    	{
    		if(inv.getSenderID() == sender.getID())
    		{
    			return true;
    		}
    	}
    	return false;
    }
    
    private void startMatch(ServerThread sender, ServerThread receiver)
    {
		try {
			receiver.setIsFindingMatch(false);
			sender.setIsFindingMatch(false);
			receiver.removeAllInvitationsAsSenderAndReceiver();
			sender.removeAllInvitationsAsSenderAndReceiver();
			room = new Room(sender);
			room.setUser2(receiver);
			sender.setRoom(room);
			receiver.setRoom(room);
            Match match = new Match(sender.getUser(), receiver.getUser());
            Long matchId = gameDAO.create(sender.getUser().getID(), receiver.getUser().getID());
//            user.setMatchId(matchId);
            match.setId(matchId);
            this.match = match;
            receiver.write(match.toStringPlayer1());
            sender.write(match.toStringPlayer2());
            System.out.println("set up matchId success : " + match.getId());
//            receiver.write("start-match," + sender.getUser().getID());
//            sender.write("start-match," + receiver.getUser().getID());
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void removeAllInvitationsAsSenderAndReceiver()
    {
    	invitationList.clear();
    	removeAllInvitationsAsSender();
    }
    
    private void removeAllInvitationsAsSender()
    {
    	for(ServerThread st : Server.serverThreadBus.getListServerThreads())
    	{
    		st.removeInvitation(user.getID());
    	}
    }

    public void write(String message) throws IOException {
        os.write(message);
        os.newLine();
        os.flush();
        System.out.println(message + " send thành công");
    }
    public void sendMessageToUser(String type, String receiverId, String senderId, String messageContent) {
        ServerThread receiverThread = Server.serverThreadBus.getServerThreadByUserID(Integer.parseInt(receiverId));
//        System.out.println("receive id " + receiverId);

        if (receiverThread != null) {
            try {
                // Nếu người nhận đang online, gửi tin nhắn
                receiverThread.write("receive-message-from-user," + type + "," + senderId + "," + messageContent);
                messageDAO.sendMessage(Integer.parseInt(senderId), Integer.parseInt(receiverId), messageContent, type);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Nếu người nhận không online, lưu tin nhắn vào cơ sở dữ liệu
            try {
                messageDAO.sendMessage(Integer.parseInt(senderId), Integer.parseInt(receiverId), messageContent, type);
                System.out.println("User " + receiverId + " is offline. Message saved to database.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
