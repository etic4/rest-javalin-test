package app.tag_bookmark;


import app.Dao;
import app.bookmark.Bookmark;
import app.bookmark.BookmarkDao;
import app.tag.Tag;
import app.tag.TagDao;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TagBookmarkController {
    private final TagBookmarkDao tagBookmarkDao;
    private final TagDao tagDao;
    private final BookmarkDao bookmarkDao;

    public TagBookmarkController() {
        tagBookmarkDao = new TagBookmarkDao();
        tagDao = new TagDao();
        bookmarkDao = new BookmarkDao();
    }

    public void getBookmarksForTag(Context ctx) {
        int tagId = ctx.pathParam("tagId", Integer.class).get();
        checkExistence(tagId, tagDao);

        List<Bookmark> bookmarks = tagBookmarkDao.getBookmarksForTag(tagId);

        ctx.json(bookmarks);
    }

    public void getTagsForBookmark(Context ctx) {
        int bookmarkId = ctx.pathParam("bookmarkId", Integer.class).get();
        checkExistence(bookmarkId, bookmarkDao);

        List<Tag> tags = tagBookmarkDao.getTagsForBookmark(bookmarkId);

        ctx.json(tags);
    }

    // associe un tag et un signet
    public void tagOne(Context ctx) {
        int tagId = ctx.pathParam("tagId", Integer.class).get();
        checkExistence(tagId, tagDao);

        int bookmarkId = ctx.pathParam("bookmarkId", Integer.class).get();
        checkExistence(bookmarkId, bookmarkDao);

        tagBookmarkDao.tagBookmark(tagId, bookmarkId);

        ctx.status(201);
    }

    // associer plusieurs tags à un signet
    public void tagBookmark(Context ctx) {
        int bookmarkId = ctx.pathParam("bookmarkId", Integer.class).get();
        checkExistence(bookmarkId, bookmarkDao);

        int[] tagIds = stringToIntArray(Objects.requireNonNull(ctx.queryParam("tagIds")));
        tagBookmarkDao.tagBookmark(tagIds, bookmarkId);

        ctx.status(201);
    }

    // associe un signet à plusieurs tags
    public void tagBookmarks(Context ctx) {
        int tagId = ctx.pathParam("tagId", Integer.class).get();
        checkExistence(tagId, tagDao);

        int[] bookmarkIds = stringToIntArray(Objects.requireNonNull(ctx.queryParam("bookmarkIds")));
        tagBookmarkDao.tagBookmarks(tagId, bookmarkIds);

        ctx.status(201);
    }

    public void untagBookmark(Context ctx) {
        int bookmarkId = ctx.pathParam("bookmarkId", Integer.class).get();
        checkExistence(bookmarkId, bookmarkDao);

        int[] tagIds = stringToIntArray(Objects.requireNonNull(ctx.queryParam("tagIds")));
        tagBookmarkDao.untagBookmark(tagIds, bookmarkId);

        ctx.status(204);
    }


    public void untagBookmarks(Context ctx) {
        int tagId = ctx.pathParam("tagId", Integer.class).get();
        checkExistence(tagId, tagDao);

        int[] bookmarkIds = stringToIntArray(Objects.requireNonNull(ctx.queryParam("bookmarkIds")));
        tagBookmarkDao.untagBookmarks(tagId, bookmarkIds);

        ctx.status(204);
    }

    public void untagOne(Context ctx) {
        int tagId = ctx.pathParam("tagId", Integer.class).get();
        checkExistence(tagId, tagDao);

        int bookmarkId = ctx.pathParam("bookmarkId", Integer.class).get();
        checkExistence(bookmarkId, bookmarkDao);

        tagBookmarkDao.untagBookmark(tagId, bookmarkId);

        ctx.status(204);
    }

    /*
    * Check l'existence de l'id 'id' avec la methode getById du Dao passé en argument
    * throws NotFoundResponse si n'existe pas.
    * */
    public void checkExistence(int id, Dao<?> dao) {
        if (dao.getById(id).isEmpty()) {
            throw new NotFoundResponse( dao.getTableName() + " not found");
        }
    }

    public int[] stringToIntArray(String str) {
        // L'un ou l'autre...
        // return Arrays.stream(str.split(",")).map(Integer::parseInt).collect(Collectors.toList());

        return Arrays.stream(str.split(",")).mapToInt(Integer::parseInt).toArray();
    }

}
