package com.sample.jpa;

import java.util.List;

import com.sample.entity.User;

public interface UserManager {
	public String sayHello();
	public List<User> getUsers();
	public User createUser(User user);
	public User updateUser(User user);
	public void removeUser(User user);
	public User getUserById(Integer id);
}
