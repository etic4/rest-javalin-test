package app.tag_bookmark;

import app.bookmark.Bookmark;
import app.bookmark.BookmarkDao;
import app.db.Db;
import app.tag.Tag;
import app.tag.TagDao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class TagBookmarkDao extends Db {

    // Retourne les signets associés à ce tag
    public List<Bookmark> getBookmarksForTag(int tagId) {
        String sql = "SELECT id, url, title, excerpt, imageUrl " +
                "FROM bookmark JOIN tag_bookmark ON id=bookmarkId " +
                "WHERE tagId=?";
        List<Bookmark> bookmarks = new ArrayList<>();

        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setObject(1, tagId);
            ResultSet res = stmt.executeQuery();

            while (res.next()) {
                bookmarks.add(new Bookmark(
                        res.getInt("id"),
                        res.getString("url"),
                        res.getString("title"),
                        res.getString("excerpt"),
                        res.getString("imageUrl")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookmarks;
    }

    //Retourne les tags associés à ce signet
    public List<Tag> getTagsForBookmark(int bookmarkId) {
        String sql = "SELECT id, name " +
                "FROM tag JOIN tag_bookmark ON id=tagId " +
                "WHERE bookmarkId=?";
        List<Tag> tags = new ArrayList<>();

        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setObject(1, bookmarkId);
            ResultSet res = stmt.executeQuery();

            while (res.next()) {
                tags.add(new Tag(res.getInt("id"), res.getString("name")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tags;
    }

    public void tagBookmark(int tagId, int bookmarkId) {
        String sql = "INSERT INTO tag_bookmark (tagId, bookmarkID) VALUES (?, ?)";

        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setObject(1, tagId);
            stmt.setObject(2, bookmarkId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void tagBookmark(int[] tagIds, int bookmarkId) {
        String sql = "INSERT INTO tag_bookmark (tagId, bookmarkID) VALUES (?, ?) ";

        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);

            for (int tagId : tagIds) {
                stmt.setObject(1, tagId);
                stmt.setObject(2, bookmarkId);
                stmt.addBatch();
            }

            stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void tagBookmarks(int tagId, int[] bookmarksIds) {
        String sql = "INSERT INTO tag_bookmark (tagId, bookmarkID) VALUES (?, ?) ";

        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);

            for (int bookmarkId : bookmarksIds) {
                stmt.setObject(1, tagId);
                stmt.setObject(2, bookmarkId);
                stmt.addBatch();
            }

            stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

 /*   public void tagBookmarks(int[] tagIds, int[] bookmarksIds) {
        String sql = "INSERT INTO tag_bookmark (tagId, bookmarkID) VALUES (?, ?) ";

        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);

            for (int bookmarkId : bookmarksIds) {
                for (int tagId : tagIds) {
                    stmt.setObject(1, tagId);
                    stmt.setObject(1, bookmarkId);
                    stmt.addBatch();
                }
            }

            stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/

    // Supprime l'association entre un tag et un signet
    public void untagBookmark(int tagId, int bookmarkId) {
        String sql = "DELETE FROM tag_bookmark WHERE tagId=? AND bookmarkId=?";

        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setObject(1, tagId);
            stmt.setObject(2, bookmarkId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
     * Supprime l'association entre une liste de tags et un signet
     * */
    public void untagBookmark(int[] tagIds, int bookmarkId) {
        String sql = "DELETE FROM tag_bookmark WHERE tagId IN ? AND bookmarkId=?";

        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setObject(1, tagIds);
            stmt.setObject(2, bookmarkId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
     * Supprime l'association entre un tag et plusieurs signets
     * */
    public void untagBookmarks(int tagId, int[] bookmarkIds){
        String sql = "DELETE FROM tag_bookmarks WHERE tagId=? AND bookmarkId IN ?";

        try(Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setObject(1, tagId);
            stmt.setObject(2, bookmarkIds);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Supprime les associations entre une liste de tags et une liste de signets
    public void untagBookmarks(int[] tagIds, int[] bookmarkIds) {
        String sql = "DELETE FROM tag_bookmarks WHERE tagId IN ? AND bookmarkId IN ?";

        try(Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setObject(1, tagIds);
            stmt.setObject(2, bookmarkIds);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
     * Supprime toutes les associations de ce tag
     * */
    public void deleteAssociationsForTag(int tagId) {
        String sql = "DELETE FROM tag_bookmark WHERE tagId=?";

        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setObject(1, tagId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
     * Supprime toutes les associations de ce signet
     * */
    public void deleteAssociationsForBookmark(int bookmarkId) {
        String sql = "DELETE FROM tag_bookmark WHERE bookmarkID=?";

        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setObject(1, bookmarkId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
