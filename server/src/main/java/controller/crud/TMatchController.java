package controller.crud;

import model.bean.TMatch;
import model.bean.VTMatch;

import java.util.List;

public interface TMatchController {
    List<TMatch> getAllMatch();
    boolean addMatch(TMatch match);
    List<VTMatch> getVTMatchesByPlayerId(int playerId);
}
