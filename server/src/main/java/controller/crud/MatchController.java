package controller.crud;

import model.bean.Match;
import model.bean.VMatch;

import java.util.List;

public interface MatchController {
    List<Match> getAllMatch();
    boolean addMatch(Match match);
    List<VMatch> getVMatchesByPlayerId(int playerId);
}
