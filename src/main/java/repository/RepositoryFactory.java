package repository;

import repository.mysql.MySqlSolicitudRepository;
import repository.mysql.MySqlUsuarioRepository;
import repository.mysql.MySqlInfoPersonalRepository;
import repository.mysql.MySqlInfoAcademicaRepository;

import repository.mongo.MongoInfoPersonalRepository;
import repository.mongo.MongoInfoAcademicaRepository;
import repository.mongo.MongoSolicitudRepository;

/**
 * RepositoryFactory
 * Fase 1: devuelve implementaciones MySQL.
 * Fases siguientes: permitirá elegir Mongo según variable de entorno.
 */
public class RepositoryFactory {

    public static UsuarioRepository getUsuarioRepository() {
        String backend = System.getenv().getOrDefault("DB_BACKEND", "mysql").toLowerCase();
        if ("mongo".equals(backend)) {
            return new repository.mongo.MongoUsuarioRepository();
        }
        return new MySqlUsuarioRepository();
    }

    public static SolicitudRepository getSolicitudRepository() {
        String backend = System.getenv().getOrDefault("DB_BACKEND", "mysql").toLowerCase();
        if ("mongo".equals(backend)) {
            return new MongoSolicitudRepository();
        }
        return new MySqlSolicitudRepository();
    }

    public static InfoPersonalRepository getInfoPersonalRepository() {
        String backend = System.getenv().getOrDefault("DB_BACKEND", "mysql").toLowerCase();
        if ("mongo".equals(backend)) {
            return new MongoInfoPersonalRepository();
        }
        return new MySqlInfoPersonalRepository();
    }

    public static InfoAcademicaRepository getInfoAcademicaRepository() {
        String backend = System.getenv().getOrDefault("DB_BACKEND", "mysql").toLowerCase();
        if ("mongo".equals(backend)) {
            return new MongoInfoAcademicaRepository();
        }
        return new MySqlInfoAcademicaRepository();
    }
}
