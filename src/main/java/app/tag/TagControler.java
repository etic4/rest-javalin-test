package app.tag;

import io.javalin.http.ConflictResponse;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;

import java.util.Optional;


public class TagControler {
    private final TagDao tagDao;

    public TagControler () {
        tagDao = new TagDao();
    }

    public void getById(Context ctx) {
        int tagId = ctx.pathParam("tagId", Integer.class).get();
        Optional<Tag> optTag = tagDao.getById(tagId);

        if (optTag.isPresent()) {
            ctx.json(optTag.get());
        } else {
            throw new NotFoundResponse();
        }
    }

    public void getAll(Context ctx) {
        ctx.json(tagDao.getAll());
    }

    public void add(Context ctx) {
        Tag tag = ctx.bodyAsClass(Tag.class);
        int tagId = tagDao.add(tag);

        if (tagId == 0) {
            throw new ConflictResponse("Resource exists");
        }

        class Resp {
            public int id = tagId;
        }

        ctx.json(new Resp()).status(201);
    }

    public void update(Context ctx) {
        Tag tag = ctx.bodyAsClass(Tag.class);
        int tagId = Integer.parseInt(ctx.pathParam("tagId"));

        try {
            tagDao.update(tagId, tag);
        } catch (JdbcSQLIntegrityConstraintViolationException e) {
            throw new ConflictResponse("Tag name exists");
        }

        ctx.status(204);
    }

    public void delete(Context ctx) {
        int tagId = Integer.parseInt(ctx.pathParam("tagId"));
        tagDao.delete(tagId);

        ctx.status(204);
    }
}
