package model.dao;

import model.bean.Match;
import model.bean.VMatch;

import java.util.List;

public interface MatchDAO {
    boolean addMatch(Match match);
    List<Match> getAllMatch();
    List<Match> getMatchesByPlayerId(int playerId);
}
