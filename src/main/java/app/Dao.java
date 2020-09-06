package app;

import java.util.Optional;

public interface Dao<T> {
    Optional<T> getById(int id);
    String getTableName();
}
