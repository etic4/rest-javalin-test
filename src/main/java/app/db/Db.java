package app.db;

import org.h2.jdbcx.JdbcConnectionPool;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/*
* Classe chargée de l'initialisation de la db:
*   - création de la connection
*   - création d'un pool de connection
*   - création des tables si n'existent pas
*
* Les Classes "dao" en hérite
*
* */
public class Db {
    private static final String CONFIG_FILENAME = "config.properties";
    private static final JdbcConnectionPool CONN_POOL;

    static {
        Properties props = new Properties();
        InputStream in = Db.class.getClassLoader().getResourceAsStream(CONFIG_FILENAME);

        try {
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        CONN_POOL = JdbcConnectionPool.create (
                props.getProperty("url"),
                props.getProperty("username"),
                props.getProperty("password")
        );

        createTablesIfNotExists();
    }

    public Connection getConnection() throws SQLException {
        return CONN_POOL.getConnection();
    }

    public static void createTablesIfNotExists() {
        try (Connection conn = CONN_POOL.getConnection()) {
            Statement stmt = conn.createStatement();

            ResultSet table = getTable(conn, "TAG");

            if (!table.next()) {
                stmt.executeUpdate("CREATE TABLE tag (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT," +
                        "name VARCHAR(50) UNIQUE NOT NULL" +
                        ")");

                stmt.executeUpdate(
                        "INSERT INTO tag (name) VALUES " +
                                "('tag1'), " +
                                "('tag2'), " +
                                "('tag3'), " +
                                "('tag4'), " +
                                "('tag5') "
                );
            }

            table = getTable(conn, "BOOKMARK");

            if(!table.next()) {
                stmt.executeUpdate("CREATE TABLE bookmark (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT," +
                        "url VARCHAR(150) UNIQUE NOT NULL," +
                        "title VARCHAR(150) NOT NULL," +
                        "excerpt VARCHAR(200) NOT NULL," +
                        "imageUrl VARCHAR(150) NOT NULL, " +
                        "content TEXT NOT NULL," +
                        "html TEXT NOT NULL" +
                        ")");

                stmt.executeUpdate(
                        "INSERT INTO bookmark (url, title, excerpt, imageUrl, content, html) VALUES " +
                                "('http://une.url.com', 'Titres pour une.url.com', 'Résumé pour une.url.com', 'chemin/image/une.url.com', '', ''), " +
                                "('http://deux.url.com', 'Titres pour deux.url.com', 'Résumé pour deux.url.com', 'chemin/image/deux.url.com', '', ''), " +
                                "('http://trois.url.com', 'Titres pour trois.url.com', 'Résumé pour trois.url.com', 'chemin/image/trois.url.com', '', ''), " +
                                "('http://quatre.url.com', 'Titres pour quatre.url.com', 'Résumé pour quatre.url.com', 'chemin/image/quatre.url.com', '', ''), " +
                                "('http://cinq.url.com', 'Titres pour cinq.url.com', 'Résumé pour cinq.url.com', 'chemin/image/cinq.url.com', '', '')"
                );
            }

            table = getTable(conn, "TAG_BOOKMARK");

            if (!table.next()) {
                stmt.executeUpdate("CREATE TABLE tag_bookmark (" +
                        "tagId INT NOT NULL," +
                        "bookmarkId INT NOT NULL" +
                        ")");
                stmt.executeUpdate("ALTER TABLE tag_bookmark ADD PRIMARY KEY (tagId, bookmarkId)");
                stmt.executeUpdate("ALTER TABLE tag_bookmark ADD FOREIGN KEY (tagId) REFERENCES tag(id)");
                stmt.executeUpdate("ALTER TABLE tag_bookmark ADD FOREIGN KEY (bookmarkId) REFERENCES bookmark(id)");

                stmt.executeUpdate("INSERT INTO tag_bookmark VALUES " +
                        "(1, 1), " +
                        "(1, 2), " +
                        "(1, 3), " +
                        "(1, 4), " +
                        "(2, 1), " +
                        "(2, 2), " +
                        "(2, 3), " +
                        "(3, 1), " +
                        "(3, 2), " +
                        "(4, 4), " +
                        "(5, 4) "
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static ResultSet getTable(Connection conn, String tableName) throws SQLException {
            return conn.getMetaData().getTables(null, null, tableName, null);
    }
}
