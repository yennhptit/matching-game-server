package org.example.matchinggameserver.dao;

import org.example.matchinggameserver.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UserDAO extends DAO{
    public UserDAO() {
        super();
    }

    public User verifyUser(User user) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT *\n"
                    + "FROM user\n"
                    + "WHERE username = ?\n"
                    + "AND password = ?"
            );
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                System.out.println(user.toString());
                return new User(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getInt(4),
                        rs.getInt(5),
                        rs.getInt(6),
                        (rs.getInt(7) != 0),
                        (rs.getInt(8) != 0),
                        rs.getInt(9),
                        getRank(rs.getInt(1)
                        ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public int getRank(int ID) {
        int rank = 1;
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT user.ID\n"
                    + "FROM user\n"
                    + "ORDER BY (user.star) DESC");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                if (rs.getInt(1) == ID)
                    return rank;
                rank++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public void addUser(User user) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO user(username, password)\n"
                    + "VALUES(?,?)");
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    public boolean checkDuplicated(String username) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM user WHERE username = ?");
            preparedStatement.setString(1, username);
            System.out.println(preparedStatement);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public void updateToOnline(int ID) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE user\n"
                    + "SET IsOnline = 1\n"
                    + "WHERE ID = ?");
            preparedStatement.setInt(1, ID);
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    public void updateToOffline(int ID) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE user\n"
                    + "SET IsOnline = 0\n"
                    + "WHERE ID = ?");
            preparedStatement.setInt(1, ID);
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void updateToPlaying(int ID) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE user\n"
                    + "SET IsPlaying = 1\n"
                    + "WHERE ID = ?");
            preparedStatement.setInt(1, ID);
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void updateToNotPlaying(int ID) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE user\n"
                    + "SET IsPlaying = 0\n"
                    + "WHERE ID = ?");
            preparedStatement.setInt(1, ID);
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    public List<User> getListFriend(int ID) {
        List<User> ListFriend = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT user.ID, user.username, user.IsOnline, user.IsPlaying\n"
                    + "FROM user\n"
                    + "WHERE user.ID IN (\n"
                    + "    SELECT ID_User1\n"
                    + "    FROM friend\n"
                    + "    WHERE ID_User2 = ?\n"
                    + ")\n"
                    + "OR user.ID IN(\n"
                    + "    SELECT ID_User2\n"
                    + "    FROM friend\n"
                    + "    WHERE ID_User1 = ?\n"
                    + ")");
            preparedStatement.setInt(1, ID);
            preparedStatement.setInt(2, ID);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                ListFriend.add(new User(rs.getInt(1),
                        rs.getString(2),  // Username thay vì Nickname vì bảng mới không có trường 'Nickname'
                        (rs.getInt(3) == 1),  // IsOnline
                        (rs.getInt(4) == 1))); // IsPlaying
            }

            // Sắp xếp danh sách bạn bè theo trạng thái
            ListFriend.sort(new Comparator<User>() {
                @Override
                public int compare(User o1, User o2) {
                    if (o1.isOnline() && !o2.isOnline()) return -1;
                    if (o1.isPlaying() && !o2.isOnline()) return -1;
                    if (!o1.isPlaying() && o1.isOnline() && o2.isPlaying() && o2.isOnline()) return -1;
                    return 0;
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ListFriend;
    }
    public boolean checkIsFriend(int ID1, int ID2) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement(
                    "SELECT 1 FROM friend " +
                            "WHERE (ID_User1 = ? AND ID_User2 = ?) " +
                            "OR (ID_User1 = ? AND ID_User2 = ?)"
            );
            preparedStatement.setInt(1, ID1);
            preparedStatement.setInt(2, ID2);
            preparedStatement.setInt(3, ID2);
            preparedStatement.setInt(4, ID1);

            ResultSet rs = preparedStatement.executeQuery();
            return rs.next();  // Nếu có kết quả, nghĩa là họ là bạn bè, trả về true

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // Trả về false nếu có
    }
    public void addFriend(int ID1, int ID2) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement(
                    "INSERT INTO friend(ID_User1, ID_User2) " +
                            "VALUES(?, ?)"
            );
            preparedStatement.setInt(1, ID1);
            preparedStatement.setInt(2, ID2);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void removeFriend(int ID1, int ID2) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement(
                    "DELETE FROM friend " +
                            "WHERE (ID_User1 = ? AND ID_User2 = ?) " +
                            "OR (ID_User1 = ? AND ID_User2 = ?)"
            );
            preparedStatement.setInt(1, ID1);
            preparedStatement.setInt(2, ID2);
            preparedStatement.setInt(3, ID2);
            preparedStatement.setInt(4, ID1);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    //getUserStaticRank
    public List<User> getUserStaticRank() {
        List<User> ListUser = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT user.ID, user.username, user.numberOfGame, user.numberOfWin, " +
                    "user.numberOfDraw, user.IsOnline, user.IsPlaying, user.star\n"
                    + "FROM user\n"
                    + "ORDER BY user.star DESC"

            );
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                ListUser.add(new User(rs.getInt(1),
                        rs.getString(2),
                        rs.getInt(3),
                        rs.getInt(4),
                        rs.getInt(5),
                        (rs.getInt(6) != 0),
                        (rs.getInt(7) != 0),
                        rs.getInt(8),
                        getRank(rs.getInt(1)
                        )));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ListUser;
    }
    public void makeFriend(int ID1, int ID2) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO friend(ID_User1,ID_User2)\n"
                    + "VALUES(?,?)");
            preparedStatement.setInt(1, ID1);
            preparedStatement.setInt(2, ID2);
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public int getNumberOfWin(int ID) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT user.NumberOfWin\n"
                    + "FROM user\n"
                    + "WHERE user.ID = ?");
            preparedStatement.setInt(1, ID);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getNumberOfDraw(int ID) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT user.NumberOfDraw\n"
                    + "FROM user\n"
                    + "WHERE user.ID = ?");
            preparedStatement.setInt(1, ID);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void addDrawGame(int ID) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE user\n"
                    + "SET user.NumberOfDraw = ?\n"
                    + "WHERE user.ID = ?");
            preparedStatement.setInt(1, new UserDAO().getNumberOfDraw(ID) + 1);
            preparedStatement.setInt(2, ID);
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void addWinGame(int ID) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE user\n"
                    + "SET user.NumberOfWin = ?\n"
                    + "WHERE user.ID = ?");
            preparedStatement.setInt(1, new UserDAO().getNumberOfWin(ID) + 1);
            preparedStatement.setInt(2, ID);
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public int getNumberOfGame(int ID) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT user.NumberOfGame\n"
                    + "FROM user\n"
                    + "WHERE user.ID = ?");
            preparedStatement.setInt(1, ID);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void addGame(int ID) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE user\n"
                    + "SET user.NumberOfGame = ?\n"
                    + "WHERE user.ID = ?");
            preparedStatement.setInt(1, new UserDAO().getNumberOfGame(ID) + 1);
            preparedStatement.setInt(2, ID);
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void decreaseGame(int ID) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE user\n"
                    + "SET user.NumberOfGame = ?\n"
                    + "WHERE user.ID = ?");
            preparedStatement.setInt(1, new UserDAO().getNumberOfGame(ID) - 1);
            preparedStatement.setInt(2, ID);
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    public String getUsernameByID(int ID) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT user.username\n"
                    + "FROM user\n"
                    + "WHERE user.ID=?");
            preparedStatement.setInt(1, ID);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public User getbyID(int id)
    {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM user WHERE ID = ?");
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getInt(4),
                        rs.getInt(5),
                        rs.getInt(6),
                        (rs.getInt(7) != 0),
                        (rs.getInt(8) != 0),
                        rs.getInt(9),
                        getRank(rs.getInt(1)
                        ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<User> getUsersByUsernameContaining(String character) {
        List<User> ListUser = new ArrayList<>();
        String query = "SELECT user.ID, user.username, user.numberOfGame, user.numberOfWin, " +
                "user.numberOfDraw, user.IsOnline, user.IsPlaying, user.star " +
                "FROM user " +
                "WHERE user.username LIKE ? " + // Use LIKE for pattern matching
                "ORDER BY user.star DESC"; // Order by star in descending order

        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, "%" + character + "%"); // Set the parameter for the LIKE clause

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                ListUser.add(new User(rs.getInt(1),
                        rs.getString(2),
                        rs.getInt(3),
                        rs.getInt(4),
                        rs.getInt(5),
                        (rs.getInt(6) != 0),
                        (rs.getInt(7) != 0),
                        rs.getInt(8),
                        getRank(rs.getInt(1)))); // Assuming you have a getRank method to fetch the rank
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ListUser;
    }

}
