package model.dao;

import controller.rest.RestServer;
import model.bean.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerDAOImpl implements PlayerDAO {
    private static final String DB_STRING = "jdbc:sqlite:jchess.db";
    private static final String CREATE_PLAYERS_TABLE =
            "CREATE TABLE IF NOT EXISTS PLAYERS (" +
                    "id integer PRIMARY KEY AUTOINCREMENT," +
                    "username text NOT NULL," +
                    "password text NOT NULL" +
                    ");";
    private static final String INSERT_PLAYER =
            "INSERT INTO Players" +
                    "(username, password)" +
                    "VALUES (?, ?);";
    private static final String SELECT_ALL_PLAYER = "SELECT * FROM Players;";
    private static final String GET_PLAYER_BY_ID = "SELECT * FROM Players WHERE id = ?;";
    private static final String GET_PLAYER_BY_USERNAME = "SELECT * FROM Players WHERE username = ?;";
    private static final String GET_PLAYER_BY_CREDENTIALS = "SELECT id, username FROM Players WHERE username = ? AND password = ?;";

    public void initializeTable() {
        try (Connection conn = DriverManager.getConnection(DB_STRING); Statement st = conn.createStatement()) {
            st.executeUpdate(CREATE_PLAYERS_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PlayerDAOImpl() {
        initializeTable();
    }

    @Override
    public boolean addPlayer(Player player) {
        try (Connection conn = DriverManager.getConnection(DB_STRING); PreparedStatement st = conn.prepareStatement(INSERT_PLAYER)) {
            st.setString(1, player.getUsername());
            st.setString(2, player.getPassword());

            int res = st.executeUpdate();
            if (res == 1) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Player> getAllPlayer() {
        List<Player> result = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_STRING); Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery(SELECT_ALL_PLAYER);

            while (rs.next()) {
                Player player = new Player(
                  rs.getInt(1),
                  rs.getString(2),
                  rs.getString(3)
                );
                result.add(player);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Player getPlayerById(int id) {
        Player result = null;

        try (Connection conn = DriverManager.getConnection(DB_STRING); PreparedStatement st = conn.prepareStatement(GET_PLAYER_BY_ID)) {
            st.setInt(1, id);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                result = new Player(
                        rs.getInt(1),
                        rs.getString(2)
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Player getPlayerByUsername(String username) {
        Player result = null;

        try (Connection conn = DriverManager.getConnection(DB_STRING); PreparedStatement st = conn.prepareStatement(GET_PLAYER_BY_USERNAME)) {
            st.setString(1, username);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                result = new Player(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3)
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public Player getPlayerByCredentials(String username, String password) {
        Player result = null;

        try (Connection conn = DriverManager.getConnection(DB_STRING); PreparedStatement st = conn.prepareStatement(GET_PLAYER_BY_CREDENTIALS)) {
            st.setString(1, username);
            st.setString(2, password);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                result = new Player(
                        rs.getInt(1),
                        rs.getString(2)
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }
}
