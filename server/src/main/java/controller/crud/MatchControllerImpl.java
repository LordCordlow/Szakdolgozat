package controller.crud;

import model.bean.Match;
import model.bean.Player;
import model.bean.VMatch;
import model.dao.MatchDAO;
import model.dao.MatchDAOImpl;

import java.util.ArrayList;
import java.util.List;

public class MatchControllerImpl implements MatchController {
    private MatchDAO dao = new MatchDAOImpl();
    private static MatchController single_instance = null;

    private MatchControllerImpl() { }

    public static MatchController getInstance() {
        if (single_instance == null) {
            single_instance = new MatchControllerImpl();
        }
        return single_instance;
    }

    @Override
    public List<Match> getAllMatch() {
        return dao.getAllMatch();
    }

    @Override
    public boolean addMatch(Match match) {
        return dao.addMatch(match);
    }

    @Override
    public List<VMatch> getVMatchesByPlayerId(int playerId) {
        List<Match> foundMatches = dao.getMatchesByPlayerId(playerId);
        List<Player> players = PlayerControllerImpl.getInstance().getAllPlayer();
        List<VMatch> result = new ArrayList<>();
        for (Match m : foundMatches) {
            VMatch vMatch = new VMatch();
            for (Player p : players) {
                if (p.getId() == m.getWhitePlayerId()) {
                    vMatch.setWhitePlayerName(p.getUsername());
                    break;
                }
            }

            for (Player p : players) {
                if (p.getId() == m.getBlackPlayerId()) {
                    vMatch.setBlackPlayerName(p.getUsername());
                    break;
                }
            }
            vMatch.setWinner(m.getWinner());
            vMatch.setStartTime(m.getStartTime());
            vMatch.setEndTime(m.getEndTime());
            result.add(vMatch);
        }
        return result;
    }
}
