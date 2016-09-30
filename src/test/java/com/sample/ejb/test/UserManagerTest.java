package com.sample.ejb.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
//import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.sample.entity.User;
import com.sample.jpa.UserRemote;

@RunWith(Arquillian.class)
public class UserManagerTest {

	@EJB(name = "java:jboss/exported/user-manager-ejb-test/UserManagerImpl!com.sample.jpa.UserRemote")
	UserRemote userRemote;

	private int id;
	
	@Deployment(name="UserManager")
	public static Archive<?> createDeployment() throws IOException {
//		JavaArchive ejbJar = ShrinkWrap.create(JavaArchive.class, "user-manager-ejb.jar")
//				.addClasses(User.class)
//				.addPackage(UserRemote.class.getPackage())
//				.addClass(RunnableDemo.class)
//				.addClass(UserManagerTest.class)
//				//For EJB Remote Test
//				.addAsResource("test-persistence.xml", "META-INF/persistence.xml")
////				.addAsResource("pom.xml", "pom.xml")
//				.addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));
//		//For EJB Remote Test
//		JavaArchive[] libs = Maven.resolver().resolve("org.jboss:jboss-remote-naming:2.0.4.Final","org.jboss.xnio:xnio-nio:3.3.0.Final","org.jboss.xnio:xnio-api:3.2.2.Final").withTransitivity().as(JavaArchive.class);
//		for (JavaArchive lib : libs) {
//			ejbJar = ejbJar.merge(lib);
//		}
//		return ejbJar;
		
		
		return ShrinkWrap.create(WebArchive.class, "user-manager-ejb-test.war")
				.addClasses(User.class)
				.addPackage(UserRemote.class.getPackage())
				.addClass(RunnableDemo.class)
//				.addClass(UserManagerTest.class)
				//For EJB Remote Test
				.addAsResource("test-persistence.xml", "META-INF/persistence.xml")
//				.addAsResource("pom.xml", "pom.xml")
				.addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"))
		        .addAsLibraries(resolveMavenDependencies().asFile());
	}

	private static MavenFormatStage resolveMavenDependencies() {
		   return Maven.resolver()
//		            .loadPomFromFile("pom.xml")
//		            .importRuntimeDependencies()
		            .resolve("org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api-maven:3.0.0-alpha-1","org.jboss:jboss-remote-naming:2.0.4.Final","org.jboss.xnio:xnio-nio:3.3.0.Final","org.jboss.xnio:xnio-api:3.2.2.Final")
		            .withTransitivity();
	}
	
	@Test
	public void callSayHelloShouldBeGetMessage() throws NamingException {
		// fail("Not yet implemented");
		assertTrue("Hello World !!!".equals(userRemote.sayHello()));
	}

	@Test
	public void callCreateUserShouldBeGetUserId() {
		User user = userRemote.createUser(new User("test2", "test2", "test2"));
		id = user.getId();
		Assert.assertNotNull(user.getId());
	}
	
	@Test
	public void callGetUserShouldBeGetUserNotNull() {
		List<User> users = userRemote.getUsers();
		Assert.assertNotNull(users);
	}
	
//	@Ignore
	@Test
	public void callUpdateUserShouldBeSuccess(){
//		helloRemote.getItems();
		User item = userRemote.getUserById(id);
		item.setName("testItem2");
		item = userRemote.updateUser(item);
		assertEquals("testItem2",item.getName());
	}
	
//	@Ignore
	@Test
	public void callRemoveUserShouldBeSuccess(){
//		helloRemote.getItems();
		User item = userRemote.getUserById(id);
		userRemote.removeUser(item);
		assertTrue(true);
	}

	@Test
	public void callUpdateMultipleThreadShouldBeGetException() {
		
		RunnableDemo r1 = new RunnableDemo(50, 1, userRemote,null);
		RunnableDemo r2 = new RunnableDemo(5, 2, userRemote,r1);
		r1.start();
		r2.start();
	}

	class RunnableDemo implements Runnable {
		private Thread t;
		private Integer id;
		private Integer time;
		private final RunnableDemo runnable;
		
		RunnableDemo(Integer id, Integer time, UserRemote userRemote,RunnableDemo runnable) {
			this.id = id;
			this.time = time;
			this.runnable = runnable;
			// this.userRemote = userRemote;
		}

		public void run() {
			try {
				Properties env = new Properties();  
//			    env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");  
//			    env.put(Context.PROVIDER_URL, "remote://localhost:4447");  
//			    env.put(Context.SECURITY_PRINCIPAL, "admin");  
//			    env.put(Context.SECURITY_CREDENTIALS, "password");  
//			    env.put("jboss.naming.client.ejb.context", true);  
			  
				env.put("endpoint.name", "client-endpoint");
				env.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", "false");
				env.put("remote.connections", "default");
				env.put("remote.connection.default.host", "127.0.0.1");
				env.put("remote.connection.default.port", "8080");
				env.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "false");
				env.put("remote.connection.default.username", "admin");
				env.put("remote.connection.default.password", "password");
			    
			    InitialContext ctx = new InitialContext(env);
			   
				final UserRemote userRemote = (UserRemote) ctx.lookup("java:jboss/exported/user-manager-ejb-test/UserManagerImpl!com.sample.jpa.UserRemote");
				
				final User user = userRemote.getUserById(id);
				//				Thread.sleep(time);
				user.setLogin(String.valueOf(time));
//				if(runnable == null){
//					this.wait();
//				}
				String login = userRemote.updateUser(user).getLogin();
//				if(runnable != null){
//					runnable.notify();
//				}
//				TimeUnit.SECONDS.sleep(time);
				System.out.println("Success : "+login);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
			} 
			catch (NamingException e) {
				e.printStackTrace();
			}

		}

		public void start() {
			if (t == null) {
				t = new Thread(this);
				t.start();
			}

		}
	}

}
