package repository;

import java.util.Map;

public interface InfoPersonalRepository {
    Map<String, String> getByEmail(String email);
    boolean saveFromApi(String email, Map<String, String> payload);
}
