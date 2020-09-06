package app.user;

import io.javalin.http.Context;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;


public class UserController {
    private final UserDao userDao;

    public UserController() {
        this.userDao = new UserDao();
    }

    public void getAllUsers(Context ctx) {
        List<User> allUsers = userDao.getAllUsers();
        ctx.json(allUsers);
    }

    public void getUserById(Context ctx) {
        int id = Integer.parseInt(Objects.requireNonNull(ctx.pathParam("userid")));
        User user = userDao.getUserById(id);
        if (user != null){
            ctx.json(user);
        }
    }

    public void addUser(Context ctx) {
        User user = ctx.bodyAsClass(User.class);
        user = userDao.addUser(user);
        ctx.json(user);
    }

    public void updateUser(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("userid"));
        User user = ctx.bodyAsClass(User.class);

        userDao.updateUser(id, user);
    }

    public void deleteUser(Context ctx) {
        int userId = Integer.parseInt(ctx.pathParam("userid"));
        userDao.deleteUser(userId);
    }
}
