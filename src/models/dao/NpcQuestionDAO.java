package models.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import jelly.Loggin;
import jelly.database.DAO;
import models.NpcQuestion;

public class NpcQuestionDAO extends DAO<NpcQuestion> {
    private HashMap<Integer, NpcQuestion> questionsById = new HashMap<>();

    @Override
    protected String tableName() {
        return "npc_questions";
    }

    @Override
    protected NpcQuestion createByResultSet(ResultSet RS) {
        try {
            NpcQuestion Q = new NpcQuestion();
            
            Q.id = RS.getInt("id");
            Q.responses = RS.getString("responses");
            
            questionsById.put(Q.id, Q);
            
            return Q;
        } catch (SQLException ex) {
            Loggin.error("Impossible de charger la question !", ex);
            return null;
        }
        
    }
    
    public NpcQuestion getById(int id){
        if(questionsById.containsKey(id)){
            return questionsById.get(id);
        }
        return find(id);
    }

    @Override
    public boolean update(NpcQuestion obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean create(NpcQuestion obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
