package controller.crud;

import model.bean.Player;
import model.bean.TMatch;
import model.bean.VTMatch;
import model.dao.TMatchDAO;
import model.dao.TMatchDAOImpl;

import java.util.ArrayList;
import java.util.List;

public class TMatchControllerImpl implements TMatchController{
    private TMatchDAO dao = new TMatchDAOImpl();
    private static TMatchController single_instance;

    private TMatchControllerImpl() { }

    public static TMatchController getInstance() {
        if (single_instance == null) {
            single_instance = new TMatchControllerImpl();
        }
        return single_instance;
    }

    @Override
    public List<TMatch> getAllMatch() {
        return dao.getAllMatch();
    }

    @Override
    public boolean addMatch(TMatch match) {
        return dao.addMatch(match);
    }

    @Override
    public List<VTMatch> getVTMatchesByPlayerId(int playerId) {
        List<TMatch> foundMatches = dao.getMatchesByPlayerId(playerId);
        List<Player> players = PlayerControllerImpl.getInstance().getAllPlayer();
        List<VTMatch> result = new ArrayList<>();
        for (TMatch m : foundMatches) {
            VTMatch vtMatch = new VTMatch();
            for (Player p : players) {
                if (p.getId() == m.getHostWhiteId()) {
                    vtMatch.setHostWhiteName(p.getUsername());
                }
                if (p.getId() == m.getHostBlackId()) {
                    vtMatch.setHostBlackName(p.getUsername());
                }
                if (p.getId() == m.getOppWhiteId()) {
                    vtMatch.setOppWhiteName(p.getUsername());
                }
                if (p.getId() == m.getOppBlackId()) {
                    vtMatch.setOppBlackName(p.getUsername());
                }
            }
            vtMatch.setWinner(m.getWinner());
            vtMatch.setStartTime(m.getStartTime());
            vtMatch.setEndTime(m.getEndTime());
            result.add(vtMatch);
        }
        return result;
    }
}
