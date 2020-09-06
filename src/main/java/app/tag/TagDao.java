package app.tag;

import app.Dao;
import app.db.Db;
import app.tag_bookmark.TagBookmarkDao;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class TagDao extends Db implements Dao {

    private final String tableName = "tag";

    public Optional<Tag> getById(int tagId) {
        Tag tag = null;

        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tag WHERE id=?");
            stmt.setObject(1, tagId);
            ResultSet res = stmt.executeQuery();

            if (res.next()) {
                tag = new Tag(res.getInt("id"), res.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(tag);
    }

    public Optional<Tag> getByName(String name) {
        Tag tag = null;

        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tag WHERE name=?");
            stmt.setObject(1, name);
            ResultSet res = stmt.executeQuery();

            if (res.next()) {
                tag = new Tag(res.getInt("id"), res.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(tag);
    }



    public List<Tag> getAll() {
        List<Tag> tags = new ArrayList<>();

        try (Connection conn = getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery("SELECT * FROM tag");

            while (res.next()) {
                tags.add(getTagInstance(res));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tags;
    }


    // Pas insérer si name existe. Dans ce cas id pas mise
    public int add(Tag tag) {
        int tagId = 0;

        if (getByName(tag.getName()).isEmpty()) {
            try (Connection conn = getConnection()) {
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO tag (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
                stmt.setObject(1, tag.getName());
                stmt.executeUpdate();

                ResultSet key = stmt.getGeneratedKeys();
                if (key.next()) {
                    tagId = key.getInt(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return tagId;
    }

    public void update(int tagId, Tag tag) throws JdbcSQLIntegrityConstraintViolationException {
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE tag SET name=? WHERE id=?");
            stmt.setObject(1, tag.getName());
            stmt.setObject(2, tagId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            if (e instanceof JdbcSQLIntegrityConstraintViolationException) {
                throw (JdbcSQLIntegrityConstraintViolationException) e;
            }
            e.printStackTrace();
        }
    }

    public void delete(int tagId) {
        // Supprime d'abord associations
        TagBookmarkDao tagBookmarkDao = new TagBookmarkDao();
        tagBookmarkDao.deleteAssociationsForTag(tagId);

        try (Connection conn = getConnection()){
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM tag WHERE id=?");
            stmt.setObject(1, tagId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Tag getTagInstance(ResultSet res) {
        Tag tag = null;

        try {
            tag = new Tag(res.getInt("id"), res.getString("name"));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tag;
    }

    public String getTableName() {
        return tableName;
    }
}
