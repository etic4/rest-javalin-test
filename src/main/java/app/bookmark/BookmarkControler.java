package app.bookmark;

import io.javalin.http.ConflictResponse;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;

import java.sql.SQLException;
import java.util.Optional;


public class BookmarkControler {
    private final BookmarkDao bookmarkDao;

    public BookmarkControler() { bookmarkDao = new BookmarkDao(); };

    public void getById(Context ctx) {
        int bookmarkId = ctx.pathParam("bookmarkId", Integer.class).get();
        Optional<Bookmark> optBookmark = bookmarkDao.getById(bookmarkId);

        if (optBookmark.isPresent()) {
            ctx.json(optBookmark.get());
        } else {
            throw new NotFoundResponse();
        }
    };

    public void getAll(Context ctx) {
        ctx.json(bookmarkDao.getAll());
    };

    public void add(Context ctx) {
        Bookmark bookmark = ctx.bodyAsClass(Bookmark.class);
        int bookmarkId = bookmarkDao.add(bookmark);

        if (bookmarkId == 0) {
            throw new ConflictResponse("Resource exists");
        }
        class Resp {
            public int id = bookmarkId;
        }
        ctx.json(new Resp()).status(201);
    };

    public void update(Context ctx) {
        System.out.println("Dans update");
        Bookmark bookmark = ctx.bodyAsClass(Bookmark.class);
        int bookmarkId = Integer.parseInt(ctx.pathParam("bookmarkId"));

        try {
            bookmarkDao.update(bookmarkId, bookmark);
        } catch (JdbcSQLIntegrityConstraintViolationException e) {
            throw new ConflictResponse("Url must be unique");
        }

        ctx.status(204);
    };

    public void delete(Context ctx) {
        int bookmarkId = Integer.parseInt(ctx.pathParam("bookmarkId"));
        bookmarkDao.delete(bookmarkId);

        ctx.status(204);
    };

}
