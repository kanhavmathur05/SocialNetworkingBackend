package com.collaborationproject.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.collaborationproject.dao.FriendsDAO;
import com.collaborationproject.model.Friends;
import com.collaborationproject.model.UserDetails;

@Transactional
@Repository
public class FriendsDAOImpl implements FriendsDAO
{

	@Autowired
	SessionFactory sessionFactory;
	
	@Override
	public List<UserDetails> getSuggestedFriends(String userName) {
		List<UserDetails> suggestedFriends;
		try
		{
			Criteria cr=sessionFactory.getCurrentSession().createCriteria(UserDetails.class);
			cr.add(Restrictions.eq("userName", userName));
			suggestedFriends=cr.list();
			return suggestedFriends;
		}
		catch(Exception ex)
		{
		ex.printStackTrace();
		return null;
		}
	}

	@Override
	public void addOrUpdateFriend(Friends friend) {
		try
		{
			sessionFactory.getCurrentSession().saveOrUpdate(friend);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	public List<UserDetails> getFriendRequests(String userName) {
		Session session=sessionFactory.getCurrentSession();
		String queryString="select * from UserDetails where userName in (select fromID from Friends where toID=? and status='P')";
		SQLQuery<UserDetails> query=session.createSQLQuery(queryString);
		query.addEntity(UserDetails.class);
		query.setString(0, userName);
		return query.list();
	}

	@Override
	public List<UserDetails> getFriendsList(String userName) {
		Session session=sessionFactory.getCurrentSession();
		String queryString="select * from UserDetails where usernName in (select fromID from Friends where toID=? and status='A' union select toID from Friends where fromID=? and status='A')";
		SQLQuery<UserDetails> query=session.createSQLQuery(queryString);
		query.addEntity(UserDetails.class);
		query.setString(0, userName);
		query.setString(1, userName);
		return query.list();
	}

	@Override
	public Friends getFriend(String toID, String fromID) {
		Session session=sessionFactory.getCurrentSession();
		Query<Friends> query=session.createQuery("from Friends where toID=:toID and fromId=:fromID");
		query.setParameter("toId",toID);
		query.setParameter("fromId",fromID);
		return query.uniqueResult();
	}

	@Override
	public List<UserDetails> getSentRequests(String userName) {
		Session session=sessionFactory.getCurrentSession();
		String queryString="select * from UserDetails where userName in (select toID from Friends where fromID=? and status='P')";
		SQLQuery<UserDetails> query=session.createSQLQuery(queryString);
		query.addEntity(UserDetails.class);
		query.setString(0, userName);
		return query.list();
	}
}
