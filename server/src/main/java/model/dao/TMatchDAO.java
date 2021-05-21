package model.dao;

import model.bean.TMatch;

import java.util.List;

public interface TMatchDAO {
    boolean addMatch(TMatch match);
    List<TMatch> getAllMatch();
    List<TMatch> getMatchesByPlayerId(int playerId);
}
