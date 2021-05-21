package model.dao;

import model.bean.TMatch;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TMatchDAOImpl implements TMatchDAO {
    private static final String DB_STRING = "jdbc:sqlite:jchess.db";
    private static final String CREATE_TMATCHES_TABLE =
            "CREATE TABLE IF NOT EXISTS TMATCHES (" +
                    "id integer PRIMARY KEY AUTOINCREMENT," +
                    "hostWhiteId integer NOT NULL," +
                    "hostBlackId integer NOT NULL," +
                    "oppWhiteId integer NOT NULL," +
                    "oppBlackId integer NOT NULL," +
                    "winner integer NOT NULL," +
                    "startTime DATETIME NOT NULL," +
                    "endTime DATETIME NOT NULL" +
                    ");";
    private static final String INSERT_TMATCH =
            "INSERT INTO TMATCHES" +
                    "(hostWhiteId, hostBlackId, oppWhiteId, oppBlackId," +
                    "winner," +
                    "startTime, endTime)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?);";
    private static final String SELECT_ALL_TMATCH = "SELECT * FROM TMATCHES";
    private static final String GET_TMATCHES_BY_PLAYER_ID = "SELECT * FROM TMATCHES WHERE " +
            "hostWhiteId = ? OR hostBlackId = ? OR oppWhiteId = ? OR oppBlackId = ?;";

    public void initializeTable() {
        try (Connection conn = DriverManager.getConnection(DB_STRING); Statement st = conn.createStatement()) {
            st.executeUpdate(CREATE_TMATCHES_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public TMatchDAOImpl() {
        initializeTable();
    }

    @Override
    public boolean addMatch(TMatch match) {
        try (Connection conn = DriverManager.getConnection(DB_STRING); PreparedStatement st = conn.prepareStatement(INSERT_TMATCH)) {
            st.setInt(1, match.getHostWhiteId());
            st.setInt(2, match.getHostBlackId());
            st.setInt(3, match.getOppWhiteId());
            st.setInt(4, match.getOppBlackId());
            st.setInt(5, match.getWinner());
            st.setTimestamp(6, match.getStartTime());
            st.setTimestamp(7, match.getEndTime());

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
    public List<TMatch> getAllMatch() {
        List<TMatch> result = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_STRING); Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery(SELECT_ALL_TMATCH);

            while (rs.next()) {
                TMatch match = new TMatch(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getInt(4),
                        rs.getInt(5),
                        rs.getInt(6),
                        rs.getTimestamp(7),
                        rs.getTimestamp(8)
                );
                result.add(match);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<TMatch> getMatchesByPlayerId(int playerId) {
        List<TMatch> result = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_STRING); PreparedStatement st = conn.prepareStatement(GET_TMATCHES_BY_PLAYER_ID)) {
            st.setInt(1, playerId);
            st.setInt(2, playerId);
            st.setInt(3, playerId);
            st.setInt(4, playerId);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                TMatch match = new TMatch(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getInt(4),
                        rs.getInt(5),
                        rs.getInt(6),
                        rs.getTimestamp(7),
                        rs.getTimestamp(8)
                );
                result.add(match);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
