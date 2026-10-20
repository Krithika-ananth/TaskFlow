package com.dayflow.dao;

import com.dayflow.model.Friend;
import com.dayflow.util.DBConnection;
import java.sql.*;
import java.util.*;

public class TaskShareDAO {
    public List<Friend> participants(long ownerId, long taskId) throws Exception {
        String sql = "SELECT u.USER_ID,u.FULL_NAME,u.USERNAME FROM TASK_SHARES s "
                + "JOIN TASKS t ON t.TASK_ID=s.TASK_ID AND t.USER_ID=? "
                + "JOIN USERS u ON u.USER_ID=s.USER_ID WHERE s.TASK_ID=? ORDER BY LOWER(u.FULL_NAME)";
        List<Friend> people = new ArrayList<>();
        try (Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement(sql)){
            p.setLong(1,ownerId);p.setLong(2,taskId);
            try(ResultSet r=p.executeQuery()){while(r.next()){Friend f=new Friend();f.setUserId(r.getLong(1));f.setFullName(r.getString(2));f.setUsername(r.getString(3));people.add(f);}}
        }
        return people;
    }

    public List<Long> participantIds(long ownerId,long taskId)throws Exception{
        List<Long> ids=new ArrayList<>();
        String sql="SELECT s.USER_ID FROM TASK_SHARES s JOIN TASKS t ON t.TASK_ID=s.TASK_ID "
                +"WHERE t.USER_ID=? AND s.TASK_ID=? ORDER BY s.USER_ID";
        try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement(sql)){
            p.setLong(1,ownerId);p.setLong(2,taskId);try(ResultSet r=p.executeQuery()){while(r.next())ids.add(r.getLong(1));}
        }
        return ids;
    }

    /** Replace participant membership atomically. Existing shares survive later unfriending, but new shares require friendship. */
    public boolean replaceParticipants(long ownerId,long taskId,Collection<Long> requested)throws Exception{
        LinkedHashSet<Long> desired=new LinkedHashSet<>(requested==null?List.of():requested);
        if(desired.contains(ownerId))throw new IllegalArgumentException("Owner cannot be added as a participant");
        try(Connection c=DBConnection.getConnection()){
            c.setAutoCommit(false);
            try{
                try(PreparedStatement lock=c.prepareStatement("SELECT TASK_ID FROM TASKS WHERE TASK_ID=? AND USER_ID=? FOR UPDATE")){
                    lock.setLong(1,taskId);lock.setLong(2,ownerId);try(ResultSet r=lock.executeQuery()){if(!r.next()){c.rollback();return false;}}
                }
                Set<Long> existing=new HashSet<>();
                try(PreparedStatement p=c.prepareStatement("SELECT USER_ID FROM TASK_SHARES WHERE TASK_ID=?")){
                    p.setLong(1,taskId);try(ResultSet r=p.executeQuery()){while(r.next())existing.add(r.getLong(1));}
                }
                for(long participant:desired){
                    if(participant<1)throw new IllegalArgumentException("Invalid participant");
                    if(existing.contains(participant))continue;
                    String check="SELECT 1 FROM FRIEND_REQUESTS WHERE STATUS='ACCEPTED' AND "
                            +"((SENDER_USER_ID=? AND RECEIVER_USER_ID=?) OR (SENDER_USER_ID=? AND RECEIVER_USER_ID=?))";
                    try(PreparedStatement p=c.prepareStatement(check)){
                        p.setLong(1,ownerId);p.setLong(2,participant);p.setLong(3,participant);p.setLong(4,ownerId);
                        try(ResultSet r=p.executeQuery()){if(!r.next())throw new IllegalArgumentException("Only accepted friends can be added");}
                    }
                }
                try(PreparedStatement p=c.prepareStatement("DELETE FROM TASK_SHARES WHERE TASK_ID=?")){
                    p.setLong(1,taskId);p.executeUpdate();
                }
                try(PreparedStatement p=c.prepareStatement("INSERT INTO TASK_SHARES(TASK_ID,USER_ID) VALUES(?,?)")){
                    for(long participant:desired){p.setLong(1,taskId);p.setLong(2,participant);p.addBatch();}
                    if(!desired.isEmpty())p.executeBatch();
                }
                c.commit();return true;
            }catch(Exception e){c.rollback();throw e;}
            finally{c.setAutoCommit(true);}
        }
    }
}
