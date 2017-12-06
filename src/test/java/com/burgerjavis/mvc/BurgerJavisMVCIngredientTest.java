/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.mvc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
import com.burgerjavis.entities.Ingredient;
import com.burgerjavis.repositories.IngredientRepository;
import com.burgerjavis.util.UnitTestUtil;

import io.github.bonigarcia.wdm.ChromeDriverManager;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={MongoTestConfiguration.class})
@WebAppConfiguration
public class BurgerJavisMVCIngredientTest {
	
	@Autowired
	private IngredientRepository ingredientRepository;
	
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
	public void testBurgerJavisMVCIngredientAdd() throws Exception {
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
		
		// Create new ingredient
		driver.findElement(By.linkText(" >  Ingredientes")).click();
		TimeUnit.SECONDS.sleep(1);
		driver.findElement(By.xpath("(//a[contains(text(),'+')])[2]")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:8080/webclient/ingredient/add"));
		driver.findElement(By.id("inputName")).click();
		driver.findElement(By.id("inputName")).clear();
		driver.findElement(By.id("inputName")).sendKeys("Nuevo ingrediente");
		driver.findElement(By.id("inputExtraPrice")).click();
		driver.findElement(By.id("inputExtraPrice")).clear();
		driver.findElement(By.id("inputExtraPrice")).sendKeys("0.10");
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:8080/"));
		Ingredient ingredient = ingredientRepository.findByNameIgnoreCase("Nuevo Ingrediente").get(0);
		assertNotNull(ingredient);
		assertEquals(ingredient.getExtraPrice(), 0.10, UnitTestUtil.DELTA_ERROR);
		
		// Ingredient with name being used
		driver.findElement(By.linkText(" >  Ingredientes")).click();
		TimeUnit.SECONDS.sleep(1);
		driver.findElement(By.xpath("(//a[contains(text(),'+')])[2]")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:8080/webclient/ingredient/add"));
		driver.findElement(By.id("inputName")).click();
		driver.findElement(By.id("inputName")).clear();
		driver.findElement(By.id("inputName")).sendKeys("Nuevo ingrediente");
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:8080/webclient/ingredient/add"));
		assertTrue(driver.findElement(By.xpath("//div/span")).getText().equalsIgnoreCase("NAME_IN_USE"));
		
		// Ingredient with empty name
		driver.findElement(By.id("inputName")).click();
		driver.findElement(By.id("inputName")).clear();
		driver.findElement(By.id("inputName")).sendKeys(" ");
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:8080/webclient/ingredient/add"));
		assertTrue(driver.findElement(By.xpath("//div/span")).getText().equalsIgnoreCase("INVALID_DATA"));
		
		// Ingredient with invalid name
		driver.findElement(By.id("inputName")).click();
		driver.findElement(By.id("inputName")).clear();
		driver.findElement(By.id("inputName")).sendKeys("?Dfkd(-");
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:8080/webclient/ingredient/add"));
		assertTrue(driver.findElement(By.xpath("//div/span")).getText().equalsIgnoreCase("INVALID_DATA"));
	}
	
	@Test
	public void testBurgerJavisMVCIngredientModify() throws Exception {
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
		
		// Modify ingredient
		driver.findElement(By.linkText(" >  Ingredientes")).click();
		TimeUnit.SECONDS.sleep(1);
		driver.findElement(By.xpath("//div[@id='ingredients']/div/div/a[2]/span")).click();
		driver.findElement(By.id("inputName")).click();
		driver.findElement(By.id("inputName")).clear();
		driver.findElement(By.id("inputName")).sendKeys("Carnecita");
		driver.findElement(By.id("inputExtraPrice")).click();
		driver.findElement(By.id("inputExtraPrice")).clear();
		driver.findElement(By.id("inputExtraPrice")).sendKeys("0.80");
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		List<Ingredient> ingredients = ingredientRepository.findByNameIgnoreCase("Carnecita");
		assertEquals(ingredients.size(), 1);
		assertEquals(ingredients.get(0).getExtraPrice(), 0.80, UnitTestUtil.DELTA_ERROR);
		
		// Discard changes
		driver.findElement(By.linkText(" >  Ingredientes")).click();
		TimeUnit.SECONDS.sleep(1);
		driver.findElement(By.xpath("//div[@id='ingredients']/div/div/a[3]/span")).click();
		driver.findElement(By.id("inputName")).click();
		driver.findElement(By.id("inputExtraPrice")).click();
		driver.findElement(By.id("inputExtraPrice")).clear();
		driver.findElement(By.id("inputExtraPrice")).sendKeys("0.20");
		driver.findElement(By.linkText("Descartar cambios")).click();
		List<Ingredient> ingredients2 = ingredientRepository.findByNameIgnoreCase("Lechuga");
		assertEquals(ingredients2.size(), 1);
		assertEquals(ingredients2.get(0).getExtraPrice(), 0.0, UnitTestUtil.DELTA_ERROR);
		
		// Ingredient with name being used
		driver.findElement(By.linkText(" >  Ingredientes")).click();
		TimeUnit.SECONDS.sleep(1);
		driver.findElement(By.xpath("//div[@id='ingredients']/div/div/a[3]/span")).click();
		driver.findElement(By.id("inputName")).click();
		driver.findElement(By.id("inputName")).clear();
		driver.findElement(By.id("inputName")).sendKeys("Pan");
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		assertTrue(driver.findElement(By.xpath("//div/span")).getText().equalsIgnoreCase("NAME_IN_USE"));
		
		// Ingredient with empty name
		driver.findElement(By.id("inputName")).click();
		driver.findElement(By.id("inputName")).clear();
		driver.findElement(By.id("inputName")).sendKeys(" ");
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		assertTrue(driver.findElement(By.xpath("//div/span")).getText().equalsIgnoreCase("INVALID_DATA"));
		
		// Ingredient with invalid price
		driver.findElement(By.id("inputExtraPrice")).click();
		driver.findElement(By.id("inputExtraPrice")).clear();
		driver.findElement(By.id("inputExtraPrice")).sendKeys("-0.20");
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		assertTrue(driver.findElement(By.xpath("//div/span")).getText().equalsIgnoreCase("INVALID_DATA"));
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
