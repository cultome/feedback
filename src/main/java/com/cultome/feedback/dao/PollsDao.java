package com.cultome.feedback.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.cultome.feedback.entity.Answer;
import com.cultome.feedback.entity.Poll;
import com.cultome.feedback.exception.InvalidScriptException;

/** 
 * PollsDao.java

 * @author		Carlos Soria <cultome@gmail.com>
 * @creation	17/07/2015
 */
public class PollsDao extends BaseDao {
	
	// Polls
	private static final String GET_ALL_POLLS = "select ID, SCRIPT, UPDATED, SENT from POLLS";
	private static final String GET_POLL = "select ID, SCRIPT, UPDATED, SENT from POLLS where ID = ~";
	private static final String INSERT_NEW_POLL = "insert into POLLS(SCRIPT,UPDATED,SENT) VALUES(~,datetime('now'),NULL)";
	private static final String NEWEST_POLL = "select max(ID), SCRIPT, UPDATED, SENT from POLLS";
	private static final String UPDATE_POLL = "update POLLS set SCRIPT=~, UPDATED=~ where ID=~";
	private static final String SEND_POLL = "update POLLS set SENT=datetime('now') where ID=~";
	private static final String DELETE_POLL = "delete from POLLS where ID=~";
	
	// Answers
	private static final String INSERT_ANSWER = "insert into ANSWERS(QUESTION_IDX,ANSWER,TOKEN,TX_ID,SENT) values(~,~,~,~,datetime('now'))";
	private static final String GET_NEXT_TX_ID = "select max(TX_ID) from ANSWERS where TOKEN = ~";
	private static final String GET_ANSWERS = "select a.QUESTION_IDX, a.ANSWER, t.EMAIL, a.TX_ID, a.SENT from ANSWERS a join TOKENS t on t.TOKEN = a.TOKEN where t.POLL_ID = ~";
	private static final String DELETE_ANSWERS = "delete from ANSWERS where TOKEN in (select TOKEN from TOKENS where POLL_ID=~)";
	
	// Tokens
	private static final String GET_TOKEN_IDS = "select EMAIL, POLL_ID from TOKENS where TOKEN = ~";
	private static final String CHECK_TOKEN_EXISTENCE = "select TOKEN from TOKENS where TOKEN = ~";
	private static final String INSERT_TOKEN = "insert into TOKENS(TOKEN, POLL_ID, EMAIL) values(~,~,~)";
	private static final String DELETE_TOKEN = "delete from TOKENS where POLL_ID=~";
	
	private RowMapper<Poll> pollMapper = new RowMapper<Poll>() {
		@Override public Poll map(ResultSet rs) throws SQLException {
			try {
				return new Poll(rs.getInt(1), rs.getString(2), dateFormatter.parse(rs.getString(3)), rs.getString(4) != null ? dateFormatter.parse(rs.getString(4)) : null);
			} catch (Exception e) {
				throw new SQLException(e);
			}
		}
	};
	
	private RowMapper<Answer> answersMapper = new RowMapper<Answer>() {
		@Override public Answer map(ResultSet rs) throws SQLException {
			try {
				return new Answer(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4), dateFormatter.parse(rs.getString(5)));
			} catch (Exception e) {
				throw new SQLException(e);
			}
		}
	};

	public PollsDao() {
	}

	public List<Poll> list() throws SQLException {
		return query(GET_ALL_POLLS, pollMapper);
	}

	public void sendPoll(Integer pollId) throws SQLException {
		update(SEND_POLL, pollId);
	}

	public Poll get(Integer pollId) throws SQLException {
		return getUniquePoll(GET_POLL, pollId);
	}

	public Poll save(String script) throws SQLException, InvalidScriptException {
		update(INSERT_NEW_POLL, script);
		return getUniquePoll(NEWEST_POLL);
	}

	public Poll update(Integer pollId, String script) throws SQLException, InvalidScriptException {
		update(UPDATE_POLL, script, new Date(), pollId);
		return get(pollId);
	}

	public boolean saveResponse(String token, Map<String, List<String>> responses) throws SQLException {
		int idx;
		
		Integer txId = getTxId(token);
		
		for(Entry<String, List<String>> entry : responses.entrySet()){
			idx = Integer.parseInt(entry.getKey().replaceFirst("q", ""));
			for(String answer : entry.getValue()){
				update(INSERT_ANSWER, idx, answer, token, txId);
			}
		}
		return true;
	}

	private synchronized Integer getTxId(String token) throws SQLException {
		List<Integer> maxTxId = query(GET_NEXT_TX_ID, new RowMapper<Integer>() {
			@Override public Integer map(ResultSet rs) throws SQLException {
				return rs.getInt(1);
			}
		}, token);
		
		if(maxTxId.isEmpty()){
			return 0;
		}
		
		return maxTxId.get(0) + 1;
	}

	public List<Answer> getAnswers(int pollId) throws SQLException {
		return query(GET_ANSWERS, answersMapper, pollId);
	}

	public synchronized boolean createToken(String email, Integer pollId, String token) throws SQLException {
		List<Boolean> tokens = query(CHECK_TOKEN_EXISTENCE, new RowMapper<Boolean>() {
			@Override public Boolean map(ResultSet rs) throws SQLException {
				return true;
			}
		}, token);
		
		if(tokens.isEmpty()){
			update(INSERT_TOKEN, token, pollId, email);
			return true;
		}
		
		return false;
	}

	public Object[] getTokenIds(String token) throws SQLException {
		List<Object[]> ids = query(GET_TOKEN_IDS, new RowMapper<Object[]>() {
			@Override public Object[] map(ResultSet rs) throws SQLException {
				return new Object[]{rs.getString(1), rs.getInt(2)};
			}
		}, token);
		
		if(ids.isEmpty()){
			throw new SQLException("Invalid token!");
		}
		return ids.get(0);
	}

	public boolean deletePoll(Integer pollId) throws SQLException {
		update(DELETE_POLL, pollId);
		update(DELETE_ANSWERS, pollId);
		update(DELETE_TOKEN, pollId);
		return true;
	}

	private Poll getUniquePoll(final String query, final Object... parameters) throws SQLException {
		List<Poll> polls = query(query, pollMapper, parameters);
		
		if(polls.size() != 1){
			throw new SQLException("Not unique poll! [" + polls.size() + "]");
		}
		
		return polls.get(0);
	}

}
