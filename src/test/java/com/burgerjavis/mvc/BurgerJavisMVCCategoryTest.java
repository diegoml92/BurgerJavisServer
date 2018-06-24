/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.mvc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.burgerjavis.BurgerJavisServerApplication;
import com.burgerjavis.MongoTestConfiguration;
import com.burgerjavis.entities.Category;
import com.burgerjavis.repositories.CategoryRepository;
import com.burgerjavis.util.TestDatabaseLoader;

import io.github.bonigarcia.wdm.ChromeDriverManager;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={MongoTestConfiguration.class})
@WebAppConfiguration
public class BurgerJavisMVCCategoryTest {
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private TestDatabaseLoader dbLoad;
	
	private WebDriver driver;
	private static ApplicationContext context;
	private StringBuffer verificationErrors = new StringBuffer();
	
	@BeforeClass public static void setupClass() {
		ChromeDriverManager.getInstance().setup();
	}

	@Before
	public void setUp() throws Exception {
		dbLoad.initDatabase();
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		context = (ApplicationContext) SpringApplication.run(BurgerJavisServerApplication.class);
	}
	
	@Test
	public void testBurgerJavisMVCCategoryAdd() throws Exception {
		// Login
		driver.get("http://localhost:12345/webclient/login");
		driver.findElement(By.name("username")).click();
		driver.findElement(By.name("username")).clear();
		driver.findElement(By.name("username")).sendKeys("admin");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("admin");
		driver.findElement(By.xpath("//input[@value='Iniciar sesión']")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:12345/"));
		assertTrue(driver.getTitle().equalsIgnoreCase("Burger Javi's - Inicio"));
		
		// Create new category
		driver.findElement(By.linkText(" >  Categorías")).click();
		TimeUnit.SECONDS.sleep(1);
		driver.findElement(By.xpath("(//a[contains(text(),'+')])[3]")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:12345/webclient/category/add"));
		driver.findElement(By.id("inputName")).click();
		driver.findElement(By.id("inputName")).clear();
		driver.findElement(By.id("inputName")).sendKeys("Nueva categoria");
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:12345/"));
		assertNotNull(categoryRepository.findByNameIgnoreCase("Nueva categoria"));
		
		// Category with name being used
		driver.findElement(By.linkText(" >  Categorías")).click();
		TimeUnit.SECONDS.sleep(1);
		driver.findElement(By.xpath("(//a[contains(text(),'+')])[3]")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:12345/webclient/category/add"));
		driver.findElement(By.id("inputName")).click();
		driver.findElement(By.id("inputName")).clear();
		driver.findElement(By.id("inputName")).sendKeys("Nueva categoria");
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:12345/webclient/category/add"));
		assertTrue(driver.findElement(By.xpath("//div/span")).getText().equalsIgnoreCase("NAME_IN_USE"));
		
		// Category with empty name
		driver.findElement(By.id("inputName")).click();
		driver.findElement(By.id("inputName")).clear();
		driver.findElement(By.id("inputName")).sendKeys(" ");
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:12345/webclient/category/add"));
		assertTrue(driver.findElement(By.xpath("//div/span")).getText().equalsIgnoreCase("INVALID_DATA"));
	}
	
	@Test
	public void testBurgerJavisMVCCategoryModify() throws Exception {
		// Login
		driver.get("http://localhost:12345/webclient/login");
		driver.findElement(By.name("username")).click();
		driver.findElement(By.name("username")).clear();
		driver.findElement(By.name("username")).sendKeys("admin");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("admin");
		driver.findElement(By.xpath("//input[@value='Iniciar sesión']")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:12345/"));
		assertTrue(driver.getTitle().equalsIgnoreCase("Burger Javi's - Inicio"));
		
		// Modify category name
		driver.findElement(By.linkText(" >  Categorías")).click();
		TimeUnit.SECONDS.sleep(1);
		driver.findElement(By.linkText("Hamburguesas")).click();
		driver.findElement(By.id("inputName")).click();
		driver.findElement(By.id("inputName")).clear();
		driver.findElement(By.id("inputName")).sendKeys("Hamburguesas2");
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:12345/"));
		assertNotNull(categoryRepository.findByNameIgnoreCase("Hamburguesas2"));
		assertEquals(categoryRepository.findByNameIgnoreCase("Hamburguesas"), new ArrayList<Category>());
		
		// Set category as favorite (1)
		driver.findElement(By.linkText(" >  Categorías")).click();
		TimeUnit.SECONDS.sleep(1);
		driver.findElement(By.xpath("//div[@id='categories']/div/div/a[2]/span")).click();
		driver.findElement(By.id("inputFav")).click();
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:12345/"));
		assertEquals(categoryRepository.findByFavoriteTrue().size(), 1);
		
		// Set category as favorite (2)
		driver.findElement(By.linkText(" >  Categorías")).click();
		TimeUnit.SECONDS.sleep(1);
		driver.findElement(By.linkText("Ensaladas")).click();
		driver.findElement(By.id("inputFav")).click();
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:12345/"));
		assertEquals(categoryRepository.findByFavoriteTrue().size(), 2);
		
		// Set category as favorite (3)
		driver.findElement(By.linkText(" >  Categorías")).click();
		TimeUnit.SECONDS.sleep(1);
		driver.findElement(By.linkText("Tostas")).click();
		driver.findElement(By.id("inputFav")).click();
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:12345/"));
		assertEquals(categoryRepository.findByFavoriteTrue().size(), 3);
		
		// Set category as favorite, MAX_FAVORITES exceeded
		driver.findElement(By.linkText(" >  Categorías")).click();
		TimeUnit.SECONDS.sleep(1);
		driver.findElement(By.linkText("Bebida")).click();
		driver.findElement(By.id("inputFav")).click();
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		assertEquals(categoryRepository.findByFavoriteTrue().size(), 3);
		assertTrue(driver.findElement(By.xpath("//div/span")).getText().equalsIgnoreCase("MAX_FAVS"));
		
		// New category name is already in use
		driver.get("http://localhost:12345");
		driver.findElement(By.linkText(" >  Categorías")).click();
		TimeUnit.SECONDS.sleep(1);
		driver.findElement(By.linkText("Hamburguesas2")).click();
		driver.findElement(By.id("inputName")).click();
		driver.findElement(By.id("inputName")).clear();
		driver.findElement(By.id("inputName")).sendKeys("Bebida");
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		assertTrue(driver.findElement(By.xpath("//div/span")).getText().equalsIgnoreCase("NAME_IN_USE"));
		
		// New category name is empty
		driver.get("http://localhost:12345");
		driver.findElement(By.linkText(" >  Categorías")).click();
		TimeUnit.SECONDS.sleep(1);
		driver.findElement(By.linkText("Hamburguesas2")).click();
		driver.findElement(By.id("inputName")).click();
		driver.findElement(By.id("inputName")).clear();
		driver.findElement(By.id("inputName")).sendKeys(" ");
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		assertTrue(driver.findElement(By.xpath("//div/span")).getText().equalsIgnoreCase("INVALID_DATA"));
	}
	
	@Test
	public void testBurgerJavisMVCCategoryDelete() throws Exception {
		// Login
		driver.get("http://localhost:12345/webclient/login");
		driver.findElement(By.name("username")).click();
		driver.findElement(By.name("username")).clear();
		driver.findElement(By.name("username")).sendKeys("admin");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("admin");
		driver.findElement(By.xpath("//input[@value='Iniciar sesión']")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:12345/"));
		assertTrue(driver.getTitle().equalsIgnoreCase("Burger Javi's - Inicio"));
		
		// Delete category
		int nCategories = ((List<Category>) categoryRepository.findAll()).size();
		driver.findElement(By.linkText(" >  Categorías")).click();
		TimeUnit.SECONDS.sleep(1);
		driver.findElement(By.xpath("//div[@id='categories']/div/div/a[2]/span")).click();
		driver.findElement(By.xpath("(//button[@type='submit'])[2]")).click();
		assertEquals(categoryRepository.findByNameIgnoreCase("Hamburguesas").size(), 0);
		assertEquals(((List<Category>) categoryRepository.findAll()).size(), nCategories - 1);
	}
	
	@After
	public void tearDown() throws Exception {
		driver.quit();
		SpringApplication.exit(context);
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
	}
	
	@AfterClass public static void tearDownClass() {
	}

}
