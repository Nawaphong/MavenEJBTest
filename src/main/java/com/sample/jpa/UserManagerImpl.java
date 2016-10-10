package com.sample.jpa;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.sample.entity.User;

@Stateless
@Local(UserLocal.class)
@Remote(UserRemote.class)
public class UserManagerImpl implements UserLocal,UserRemote {
	
	@PersistenceContext(unitName = "userDB")
	private EntityManager em;

	public UserManagerImpl() {
	}

	public String sayHello() {
		return "Hello World !!!";
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getUsers() {
		Query query = em.createQuery("SELECT u FROM User u ORDER BY u.id");
		List<User> users = (List<User>) query.getResultList();
		return users;
	}
	
	@Override
	public User getUserById(final Integer id){
		return em.find(User.class,id);
	}

	@Override
	public User createUser(final User user) {
		em.persist(user);
		return user;
	}

	@Override
	public User updateUser(final User user) {
		
		return em.merge(user);
	}

	@Override
	public void removeUser(final User user) {
		User userToBeRemoved = em.getReference(User.class, user.getId());
	    em.remove(userToBeRemoved);
	}
}
