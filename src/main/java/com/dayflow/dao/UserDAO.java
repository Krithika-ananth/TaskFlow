package com.dayflow.dao;

import com.dayflow.model.User; import com.dayflow.util.*; import java.sql.*; import java.security.SecureRandom; import java.util.HexFormat;

public class UserDAO {
 public User findByLogin(String login)throws Exception{String q="SELECT USER_ID,FULL_NAME,USERNAME,EMAIL,PASSWORD_HASH,CREATED_AT FROM USERS WHERE LOWER(EMAIL)=LOWER(?) OR LOWER(USERNAME)=LOWER(?)";try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement(q)){p.setString(1,login);p.setString(2,login);try(ResultSet r=p.executeQuery()){if(r.next())return map(r);}}return null;}
 public User findByEmail(String email)throws Exception{return findByLogin(email);}
 public User findById(long id)throws Exception{String q="SELECT USER_ID,FULL_NAME,USERNAME,EMAIL,PASSWORD_HASH,CREATED_AT FROM USERS WHERE USER_ID=?";try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement(q)){p.setLong(1,id);try(ResultSet r=p.executeQuery()){return r.next()?map(r):null;}}}
 public boolean emailExists(String email)throws Exception{return exists("SELECT 1 FROM USERS WHERE LOWER(EMAIL)=LOWER(?)",email);}
 public boolean usernameExists(String username)throws Exception{return exists("SELECT 1 FROM USERS WHERE LOWER(USERNAME)=LOWER(?)",username);}
 private boolean exists(String q,String value)throws Exception{try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement(q)){p.setString(1,value);try(ResultSet r=p.executeQuery()){return r.next();}}}
 public boolean exists(String email)throws Exception{return emailExists(email);}
 public long create(String name,String username,String email,String passwordHash)throws Exception{String q="INSERT INTO USERS(FULL_NAME,USERNAME,EMAIL,PASSWORD_HASH) VALUES(?,?,?,?)";try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement(q,new String[]{"USER_ID"})){p.setString(1,name);p.setString(2,username);p.setString(3,email);p.setString(4,passwordHash);p.executeUpdate();try(ResultSet r=p.getGeneratedKeys()){r.next();long id=r.getLong(1);seedCategories(c,id);return id;}}}
 private void seedCategories(Connection c,long id)throws SQLException{try(PreparedStatement p=c.prepareStatement("INSERT INTO CATEGORIES(USER_ID,CATEGORY_NAME) VALUES(?,?)")){for(String s:new String[]{"Work","Study","Personal","Health"}){p.setLong(1,id);p.setString(2,s);p.addBatch();}p.executeBatch();}}
 public boolean updateName(long id,String name)throws Exception{try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement("UPDATE USERS SET FULL_NAME=? WHERE USER_ID=?")){p.setString(1,name);p.setLong(2,id);return p.executeUpdate()==1;}}
 public boolean emailExistsForOther(String email,long userId)throws Exception{String q="SELECT 1 FROM USERS WHERE LOWER(EMAIL)=LOWER(?) AND USER_ID<>?";try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement(q)){p.setString(1,email);p.setLong(2,userId);try(ResultSet r=p.executeQuery()){return r.next();}}}
 public boolean updateProfile(long id,String name,String email)throws Exception{String q="UPDATE USERS SET FULL_NAME=?,EMAIL=? WHERE USER_ID=?";try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement(q)){p.setString(1,name);p.setString(2,email);p.setLong(3,id);return p.executeUpdate()==1;}}
 public boolean updatePassword(long id,String hash)throws Exception{try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement("UPDATE USERS SET PASSWORD_HASH=? WHERE USER_ID=?")){p.setString(1,hash);p.setLong(2,id);return p.executeUpdate()==1;}}
 public String createPasswordResetToken(long userId)throws Exception {
  byte[] bytes=new byte[32];new SecureRandom().nextBytes(bytes);String token=java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);String hash=tokenHash(token);
  try(Connection c=DBConnection.getConnection()) { c.setAutoCommit(false);try(PreparedStatement d=c.prepareStatement("DELETE FROM PASSWORD_RESET_TOKENS WHERE USER_ID=?")){d.setLong(1,userId);d.executeUpdate();}
   try(PreparedStatement p=c.prepareStatement("INSERT INTO PASSWORD_RESET_TOKENS(TOKEN_HASH,USER_ID,EXPIRES_AT) VALUES(?,?,SYSTIMESTAMP + INTERVAL '30' MINUTE)")){p.setString(1,hash);p.setLong(2,userId);p.executeUpdate();}c.commit();return token;
  }
 }
 public boolean isPasswordResetTokenValid(String token)throws Exception {
  if(token==null||!token.matches("[A-Za-z0-9_-]{40,50}"))return false;
  try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement("SELECT 1 FROM PASSWORD_RESET_TOKENS WHERE TOKEN_HASH=? AND EXPIRES_AT>SYSTIMESTAMP")){p.setString(1,tokenHash(token));try(ResultSet r=p.executeQuery()){return r.next();}}
 }
 public boolean resetPassword(String token,String passwordHash)throws Exception {
  if(token==null||!token.matches("[A-Za-z0-9_-]{40,50}"))return false;
  try(Connection c=DBConnection.getConnection()) { c.setAutoCommit(false);long userId;
   try(PreparedStatement p=c.prepareStatement("SELECT USER_ID FROM PASSWORD_RESET_TOKENS WHERE TOKEN_HASH=? AND EXPIRES_AT>SYSTIMESTAMP FOR UPDATE")){p.setString(1,tokenHash(token));try(ResultSet r=p.executeQuery()){if(!r.next()){c.rollback();return false;}userId=r.getLong(1);}}
   try(PreparedStatement p=c.prepareStatement("UPDATE USERS SET PASSWORD_HASH=? WHERE USER_ID=?")){p.setString(1,passwordHash);p.setLong(2,userId);if(p.executeUpdate()!=1){c.rollback();return false;}}
   try(PreparedStatement p=c.prepareStatement("DELETE FROM PASSWORD_RESET_TOKENS WHERE USER_ID=?")){p.setLong(1,userId);p.executeUpdate();}c.commit();return true;
  }
 }
 private String tokenHash(String token)throws Exception{return HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256").digest(token.getBytes(java.nio.charset.StandardCharsets.US_ASCII)));}
 public boolean delete(long id)throws Exception{try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement("DELETE FROM USERS WHERE USER_ID=?")){p.setLong(1,id);return p.executeUpdate()==1;}}

 private User map(ResultSet r)throws SQLException{User u=new User();u.setUserId(r.getLong(1));u.setFullName(r.getString(2));u.setUsername(r.getString(3));u.setEmail(r.getString(4));u.setPasswordHash(r.getString(5));if(r.getTimestamp(6)!=null)u.setCreatedAt(r.getTimestamp(6).toLocalDateTime());return u;}
}
