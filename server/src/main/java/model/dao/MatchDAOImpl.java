package model.dao;

import model.bean.Match;
import model.bean.VMatch;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatchDAOImpl implements MatchDAO {
    private static final String DB_STRING = "jdbc:sqlite:jchess.db";
    private static final String CREATE_MATCHES_TABLE =
            "CREATE TABLE IF NOT EXISTS MATCHES (" +
            "id integer PRIMARY KEY AUTOINCREMENT," +
            "whitePlayerId integer NOT NULL," +
            "blackPlayerId integer NOT NULL," +
            "winner integer NOT NULL," +
            "startTime DATETIME NOT NULL," +
            "endTime DATETIME NOT NULL" +
            ");";
    private static final String INSERT_MATCH =
            "INSERT INTO Matches" +
                    "(whitePlayerId, blackPlayerId," +
                    "winner," +
                    "startTime, endTime)" +
                    "VALUES (?, ?, ?, ?, ?);";
    private static final String SELECT_ALL_MATCH = "SELECT * FROM Matches";
    private static final String GET_MATCHES_BY_PLAYER_ID = "SELECT * FROM Matches WHERE whitePlayerId = ? OR blackPlayerId = ?;";

    public void initializeTable() {
        try (Connection conn = DriverManager.getConnection(DB_STRING); Statement st = conn.createStatement()) {
            st.executeUpdate(CREATE_MATCHES_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public MatchDAOImpl() {
        initializeTable();
    }

    @Override
    public boolean addMatch(Match match) {
        try (Connection conn = DriverManager.getConnection(DB_STRING); PreparedStatement st = conn.prepareStatement(INSERT_MATCH)) {
            st.setInt(1, match.getWhitePlayerId());
            st.setInt(2, match.getBlackPlayerId());
            st.setInt(3, match.getWinner());
            st.setTimestamp(4, match.getStartTime());
            st.setTimestamp(5, match.getEndTime());

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
    public List<Match> getAllMatch() {
        List<Match> result = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_STRING); Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery(SELECT_ALL_MATCH);

            while (rs.next()) {
                Match match = new Match(
                  rs.getInt(1),
                  rs.getInt(2),
                  rs.getInt(3),
                  rs.getInt(4),
                  rs.getTimestamp(5),
                  rs.getTimestamp(6)
                );
                result.add(match);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<Match> getMatchesByPlayerId(int playerId) {
        List<Match> result = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_STRING); PreparedStatement st = conn.prepareStatement(GET_MATCHES_BY_PLAYER_ID)) {
            st.setInt(1, playerId);
            st.setInt(2, playerId);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Match match = new Match(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getInt(4),
                        rs.getTimestamp(5),
                        rs.getTimestamp(6)
                );
                result.add(match);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
