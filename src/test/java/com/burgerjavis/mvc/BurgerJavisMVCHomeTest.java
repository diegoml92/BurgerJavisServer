/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.mvc;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.burgerjavis.BurgerJavisServerApplication;
import com.burgerjavis.MongoTestConfiguration;

import io.github.bonigarcia.wdm.ChromeDriverManager;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={MongoTestConfiguration.class})
@WebAppConfiguration
public class BurgerJavisMVCHomeTest {
	
	private WebDriver driver;
	private static ApplicationContext context;
	private StringBuffer verificationErrors = new StringBuffer();
	
	@BeforeClass public static void setupClass() {
		ChromeDriverManager.getInstance().setup();
		context = (ApplicationContext) SpringApplication.run(BurgerJavisServerApplication.class);
	}
	
	@Before
	public void setUp() throws Exception {
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}
	
	@Test 
	public void testBurgerJavisMVCHome() throws Exception {
		// Login
		driver.get("http://localhost:8080/");
		driver.findElement(By.name("username")).click();
		driver.findElement(By.name("username")).clear();
		driver.findElement(By.name("username")).sendKeys("admin");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("admin");
		driver.findElement(By.xpath("//input[@value='Iniciar sesión']")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:8080/"));
		assertTrue(driver.getTitle().equalsIgnoreCase("Burger Javi's - Inicio"));
		
		// Navigation
		driver.findElement(By.linkText("Menú")).click();
		TimeUnit.SECONDS.sleep(1);
		driver.findElement(By.linkText("+")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:8080/webclient/product/add"));
		driver.findElement(By.linkText("Cancelar")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:8080/"));
		driver.findElement(By.linkText("Menú")).click();
		TimeUnit.SECONDS.sleep(1);
		driver.findElement(By.xpath("//div[@id='menu']/div/div/a")).click();
		//assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:8080/webclient/product/modify"));
		driver.findElement(By.linkText("Descartar cambios")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:8080/"));
		driver.findElement(By.linkText(" >  Ingredientes")).click();
		TimeUnit.SECONDS.sleep(1);
		driver.findElement(By.xpath("(//a[contains(text(),'+')])[2]")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:8080/webclient/ingredient/add"));
		driver.findElement(By.linkText("Cancelar")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:8080/"));
		driver.findElement(By.linkText(" >  Ingredientes")).click();
		TimeUnit.SECONDS.sleep(1);
		driver.findElement(By.xpath("//div[@id='ingredients']/div/div/a/span")).click();
		//assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:8080/webclient/ingredient/modify"));
		driver.findElement(By.linkText("Descartar cambios")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:8080/"));
		driver.findElement(By.linkText(" >  Categorías")).click();
		TimeUnit.SECONDS.sleep(1);
		driver.findElement(By.xpath("(//a[contains(text(),'+')])[3]")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:8080/webclient/category/add"));
		driver.findElement(By.linkText("Cancelar")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:8080/"));
		driver.findElement(By.linkText(" >  Categorías")).click();
		TimeUnit.SECONDS.sleep(1);
		driver.findElement(By.xpath("//div[@id='categories']/div/div/a/span")).click();
		//assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:8080/webclient/category/modify"));
		driver.findElement(By.linkText("Descartar cambios")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:8080/"));
		driver.findElement(By.linkText("Usuarios")).click();
		TimeUnit.SECONDS.sleep(1);
		driver.findElement(By.xpath("(//a[contains(text(),'+')])[4]")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:8080/webclient/user/add"));
		driver.findElement(By.linkText("Cancelar")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:8080/"));
		driver.findElement(By.linkText("Usuarios")).click();
		TimeUnit.SECONDS.sleep(1);
		driver.findElement(By.xpath("//div[@id='users']/div/div/a/span")).click();
		//assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:8080/webclient/user/modify"));
		driver.findElement(By.linkText("Descartar cambios")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:8080/"));
		
		// Logout
		driver.findElement(By.linkText("Cerrar sesión")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:8080/webclient/login?logout"));
	}
	
	@After
	public void tearDown() throws Exception {
		driver.quit();
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
	}
	
	@AfterClass public static void tearDownClass() {
		SpringApplication.exit(context);
	}


}
