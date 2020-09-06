package app.user;

import app.db.Db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//DAO = data access object
public class UserDao extends Db {

    public UserDao() {
        //Pas nécessaire d'initialiser la superclasse ici
        //super();
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        try (Connection conn = getConnection()){

            //h2 mets noms (de table) en majuscules ??
            ResultSet results = conn.getMetaData().getTables(null, null, "USER", null);

            if (!results.next()) {
                Statement stmt = conn.createStatement();
                stmt.executeUpdate("CREATE TABLE user (id INT PRIMARY KEY AUTOINCREMENT, name VARCHAR(50))");
                stmt.executeUpdate("INSERT INTO user VALUES (1, 'Machin')");
                stmt.executeUpdate("INSERT INTO user VALUES (2, 'Truc')");
                stmt.executeUpdate("INSERT INTO user VALUES (3, 'Truc')");
                stmt.executeUpdate("INSERT INTO user VALUES (4, 'Brol')");

                //Pas nécessaire, est en auto-commit par défaut
                //conn.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public User getUserById(int id) {
        User user = new User();

        try (Connection conn = getConnection()){
            PreparedStatement stmt = conn.prepareStatement("SELECT id, name FROM user WHERE id=?");
            stmt.setObject(1, id);
            ResultSet res = stmt.executeQuery();

            if (res.next()){
                user.setId(res.getInt("id"));
                user.setName(res.getString("name"));
            }
        } catch (SQLException e ) {
            e.printStackTrace();
        }
        return user;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        try (Connection conn = getConnection()){
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery("SELECT id, name FROM user");

            while (res.next()){
                User user = new User(res.getInt("id"), res.getString("name"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public User addUser(User user) {
        try (Connection conn = getConnection()){
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO user (NAME) VALUES (?)");
            stmt.setObject(1, user.getName());
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                user.setId(keys.getInt(1));
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    public void updateUser(int id, User user){
        User u = getUserById(id);

        if (!u.isNull()){
            try (Connection conn = getConnection()) {
                PreparedStatement stmt = conn.prepareStatement("UPDATE user SET id=?, name=? WHERE id=?");
                stmt.setObject(1, user.getId());
                stmt.setObject(2, user.getName());
                stmt.setObject(3, id);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteUser(int id) {
        User u = getUserById(id);
        if (!u.isNull()) {
            try (Connection conn = getConnection()){
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM user WHERE id=?");
                stmt.setObject(1, id);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
