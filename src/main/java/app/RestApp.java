package app;

import app.bookmark.BookmarkControler;
import app.tag.TagControler;
import app.tag_bookmark.TagBookmarkController;
import io.javalin.Javalin;
import io.javalin.http.MethodNotAllowedResponse;

import java.util.HashMap;

import static io.javalin.apibuilder.ApiBuilder.*;

public class RestApp {
    public static void main(String[] args) {
        Javalin app = Javalin.create(
            config -> {
                config.defaultContentType = "application/json";
                config.requestCacheSize = 16000000L; //+-16MB
                config.enableDevLogging();
            }
        ).routes(() -> {
            TagBookmarkController tagBookmarkController = new TagBookmarkController();

            path("/", () -> get(ctx -> ctx.result("Documenter l'API ?")));

            path("tags", () -> {
                TagControler tagControler = new TagControler();
                get(tagControler::getAll);
                post(tagControler::add);
                //batch update de tags
                put(ctx -> {
                    throw new MethodNotAllowedResponse("Method not allowed", new HashMap<>());
                });
                //Suppression de tous les tags
                delete(ctx -> {
                    throw new MethodNotAllowedResponse("Method not allowed", new HashMap<>());
                });

                path(":tagId", () -> {
                    get(tagControler::getById);
                    put(tagControler::update);
                    delete(tagControler::delete);

                    //Peut pas ajouter sur une ressource existente
                    post(ctx -> {
                        throw new MethodNotAllowedResponse("Method not allowed", new HashMap<>());
                    });

                    path("bookmarks", () -> {
                        get(tagBookmarkController::getBookmarksForTag);
                        post(tagBookmarkController::tagBookmarks); //tag les bookmarkIds passés en paramètre
                        delete(tagBookmarkController::untagBookmarks); // supprime association entre tagId et bookmarkIds
                                                                       // passés en paramètre
                        //modification d'un ensemble de relations ???
                        put(ctx -> {
                            throw new MethodNotAllowedResponse("Method not allowed", new HashMap<>());
                        });

                        path(":bookmarkId", () -> {
                            delete(tagBookmarkController::untagOne); // untag bookmarkId
                            post(tagBookmarkController::tagOne); // tag ce signet
                            //modification d'une relations ???
                            put(ctx -> {
                                throw new MethodNotAllowedResponse("Method not allowed", new HashMap<>());
                            });
                            //Retourne bookmarks ? Autant faire 'get /bookmarks/:bookmarkId'
                            get(ctx -> {
                                throw new MethodNotAllowedResponse("Method not allowed", new HashMap<>());
                            });
                        });
                    });
                });

            });

            path("bookmarks", () -> {
                BookmarkControler bookmarkControler = new BookmarkControler();
                get(bookmarkControler::getAll);
                post(bookmarkControler::add);

                //batch update de tags
                put(ctx -> {
                    throw new MethodNotAllowedResponse("Method not allowed", new HashMap<>());
                });

                path(":bookmarkId", () -> {
                    get(bookmarkControler::getById);
                    put(bookmarkControler::update);
                    delete(bookmarkControler::delete);
                    post(ctx -> {
                        throw new MethodNotAllowedResponse("Method not allowed", new HashMap<>());
                    });

                    path("tags", ()-> {
                        get(tagBookmarkController::getTagsForBookmark); // retourne la liste des tags associés à ce signet
                        post(tagBookmarkController::tagBookmark); // Ajoute la liste de tags passés en paramètre à bookmarkId
                        delete(tagBookmarkController::untagBookmark); // supprime association entre bookmarkId et la liste de
                                                                      // tags passés en paramètres.
                        //modification d'une série de relations ??
                        put(ctx -> {
                            throw new MethodNotAllowedResponse("Method not allowed", new HashMap<>());
                        });

                        path(":tagId", () -> {
                            post(tagBookmarkController::tagOne); //tag ce signet
                            delete(tagBookmarkController::untagOne); // untag bookmarkId

                            //modification d'une relation ??
                            put(ctx -> {
                                throw new MethodNotAllowedResponse("Method not allowed", new HashMap<>());
                            });
                            //Requête tagId ? Plutôt 'GET /tags/:tagId'
                            get(ctx -> {
                                throw new MethodNotAllowedResponse("Method not allowed", new HashMap<>());
                            });
                        });
                    });
                });
            });
/*            //TODO: fuzzy search
            path("search", () -> {
                SearchController searchController = new SearchControler();

                get(searchController::search);
            });*/
        });
        app.start(7000);
    }
}
