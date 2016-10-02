package com.sample.jbehave;
import org.jbehave.core.annotations.*;

import com.sample.entity.User;
public class UserManagerSteps{
	User user;
	
	@Given("user data for create : $name $login $password")
	public void givenUserDataForCreateUser1User11(String name,String login,String password){
		 user = new User(name,login,password);
	}

	@When("add user data")
	public void whenAddUserData(){
		user.setId(123);
	}
	
	@Then("user id can't be null")
	public void thenUserIdCantBeNull(){
		
	}
}