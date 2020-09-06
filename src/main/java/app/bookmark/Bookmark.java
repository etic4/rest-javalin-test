package app.bookmark;

import java.awt.print.Book;
import java.util.Objects;

public class Bookmark {
    private static final String STRING_DEFAULT = "";
    private int id;
    private String url;
    private String title = "";
    private String excerpt = "";
    private String imageUrl = "";
    private String content = "";
    private String html = "";

    public Bookmark() {
    }

    public Bookmark(int id, String url, String title, String excerpt, String imageUrl) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.excerpt = excerpt;
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) { this.url = url; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) { this.title = getDefaultIfNull(title); }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = getDefaultIfNull(excerpt);
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = getDefaultIfNull(imageUrl);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = getDefaultIfNull(content);
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = getDefaultIfNull(html);
    }

    private String getDefaultIfNull(String value) {
        return value == null ? STRING_DEFAULT : value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Bookmark bookmark = (Bookmark) o;

        return id == bookmark.id;
    };

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
