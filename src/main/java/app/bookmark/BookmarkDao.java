package app.bookmark;

import app.Dao;
import app.db.Db;
import app.tag_bookmark.TagBookmarkDao;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookmarkDao extends Db implements Dao {

    private final String tableName = "bookmark";

    public Optional<Bookmark> getById(int bookmarkId) {
        Bookmark bookmark = null;

        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM bookmark WHERE id=?");
            stmt.setObject(1, bookmarkId);
            ResultSet res = stmt.executeQuery();

            if (res.next()) {
                bookmark = getBookmarkInstance(res);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(bookmark);
    }

    public Optional<Bookmark> getByUrl(String bookmarkUrl) {
        Bookmark bookmark = null;

        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM bookmark WHERE url=?");
            stmt.setObject(1, bookmarkUrl);
            ResultSet res = stmt.executeQuery();

            if (res.next()) {
                bookmark = getBookmarkInstance(res);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(bookmark);
    }


    public List<Bookmark> getAll() {
        List<Bookmark> bookmarks = new ArrayList<>();

        try (Connection conn = getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery("SELECT * FROM bookmark");

            while (res.next()) {
                bookmarks.add(getBookmarkInstance(res));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookmarks;
    }

    // Insère un signet si l'URL n'existe pas en db. retourne l'id ou 0;
    public int add(Bookmark bookmark) {
        int bookmarkId = 0;

        if (getByUrl(bookmark.getUrl()).isEmpty()) {
            try (Connection conn = getConnection()) {
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO bookmark " +
                        "(url, title, excerpt, imageUrl, content, html) " +
                        "VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                stmt.setObject(1, bookmark.getUrl());
                stmt.setObject(2, bookmark.getTitle());
                stmt.setObject(3, bookmark.getExcerpt());
                stmt.setObject(4, bookmark.getImageUrl());
                stmt.setObject(5, bookmark.getContent());
                stmt.setObject(6, bookmark.getHtml());
                stmt.executeUpdate();

                ResultSet key = stmt.getGeneratedKeys();
                if (key.next()) {
                    bookmarkId = key.getInt(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return bookmarkId;
    }

    /*Mets à jour les données de base du signet, à l'exclusion du contenu (readability)
     * et du html.
     */
    public void update(int bookmarkId, Bookmark bookmark) throws JdbcSQLIntegrityConstraintViolationException {
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE bookmark SET " +
                    "(url, title, excerpt, imageUrl) = (?, ?, ?, ?) WHERE id=?");
            stmt.setObject(1, bookmark.getUrl());
            stmt.setObject(2, bookmark.getTitle());
            stmt.setObject(3, bookmark.getExcerpt());
            stmt.setObject(4, bookmark.getImageUrl());
            stmt.setObject(5, bookmarkId);
            stmt.executeUpdate();
        } catch (SQLException e ) {
            //violation de l'intégrité sur URL
            if (e instanceof JdbcSQLIntegrityConstraintViolationException) {
                throw  (JdbcSQLIntegrityConstraintViolationException) e;
            }
            e.printStackTrace();
        }
    }

    //Mise à jour du contenu et du html du signet
    public void updateContentAndHtml(int bookmarkId, String content, String html) {
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE bookmark SET " +
                    "(content, html) = (?, ?) WHERE id=?");
            stmt.setObject(1, content);
            stmt.setObject(2, html);
            stmt.setObject(3, bookmarkId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int bookmarkId) {
        TagBookmarkDao tagBookmarkDao = new TagBookmarkDao();
        tagBookmarkDao.deleteAssociationsForBookmark(bookmarkId);

        try (Connection conn = getConnection()){
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM bookmark WHERE id=?");
            stmt.setObject(1, bookmarkId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Bookmark getBookmarkInstance(ResultSet res) {
        Bookmark bookmark = null;

        try {
            System.out.println("excerpt: " + res.getString("excerpt"));
            bookmark = new Bookmark(res.getInt("id"), res.getString("url"), res.getString("title"),
                    res.getString("excerpt"), res.getString("imageUrl"));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookmark;
    }

    public String getTableName() {
        return tableName;
    }

}
