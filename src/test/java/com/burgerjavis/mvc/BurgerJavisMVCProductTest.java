/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.mvc;

import static org.junit.Assert.assertEquals;
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
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.burgerjavis.BurgerJavisServerApplication;
import com.burgerjavis.MongoTestConfiguration;
import com.burgerjavis.entities.Product;
import com.burgerjavis.repositories.ProductRepository;
import com.burgerjavis.util.UnitTestUtil;

import io.github.bonigarcia.wdm.ChromeDriverManager;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={MongoTestConfiguration.class})
@WebAppConfiguration
public class BurgerJavisMVCProductTest {
	
	@Autowired
	private ProductRepository productRepository;
	
	private WebDriver driver;
	private static ApplicationContext context;
	private StringBuffer verificationErrors = new StringBuffer();
	
	@BeforeClass public static void setupClass() {
		ChromeDriverManager.getInstance().setup();
	}
	
	@Before
	public void setUp() throws Exception {
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		context = (ApplicationContext) SpringApplication.run(BurgerJavisServerApplication.class);
	}
	
	@Test
	public void testBurgerJavisMVCProductAdd() throws Exception {
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
		
		// Add new product
		driver.findElement(By.linkText("Menú")).click();
		TimeUnit.SECONDS.sleep(1);
		driver.findElement(By.linkText("+")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:8080/webclient/product/add"));
		driver.findElement(By.id("inputName")).click();
		driver.findElement(By.id("inputName")).clear();
		driver.findElement(By.id("inputName")).sendKeys("Nuevo producto");
		driver.findElement(By.id("inputPrice")).click();
		driver.findElement(By.id("inputPrice")).clear();
		driver.findElement(By.id("inputPrice")).sendKeys("5.20");
		driver.findElement(By.id("categoryId")).click();
		new Select(driver.findElement(By.id("categoryId"))).selectByVisibleText("Hamburguesas");
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		List<Product> products = productRepository.findByNameIgnoreCase("Nuevo producto");
		assertEquals(products.size(), 1);
		assertEquals(products.get(0).getPrice(), 5.20, UnitTestUtil.DELTA_ERROR);
		assertTrue(products.get(0).getCategory().getName().equalsIgnoreCase("Hamburguesas"));
		
		// Product with name in use
		driver.findElement(By.linkText("Menú")).click();
		TimeUnit.SECONDS.sleep(1);
		driver.findElement(By.linkText("+")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:8080/webclient/product/add"));
		driver.findElement(By.id("inputName")).click();
		driver.findElement(By.id("inputName")).clear();
		driver.findElement(By.id("inputName")).sendKeys("Nuevo producto");
		driver.findElement(By.id("inputPrice")).click();
		driver.findElement(By.id("inputPrice")).clear();
		driver.findElement(By.id("inputPrice")).sendKeys("3.50");
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:8080/webclient/product/add"));
		assertTrue(driver.findElement(By.xpath("//div/span")).getText().equalsIgnoreCase("NAME_IN_USE"));
		
		// Product with empty name
		driver.findElement(By.id("inputName")).click();
		driver.findElement(By.id("inputName")).clear();
		driver.findElement(By.id("inputName")).sendKeys(" ");
		driver.findElement(By.id("inputPrice")).click();
		driver.findElement(By.id("inputPrice")).clear();
		driver.findElement(By.id("inputPrice")).sendKeys("3.50");
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:8080/webclient/product/add"));
		assertTrue(driver.findElement(By.xpath("//div/span")).getText().equalsIgnoreCase("INVALID_DATA"));
		
		// Product with invalid name
		driver.findElement(By.id("inputName")).click();
		driver.findElement(By.id("inputName")).clear();
		driver.findElement(By.id("inputName")).sendKeys("Jfs?5$");
		driver.findElement(By.id("inputPrice")).click();
		driver.findElement(By.id("inputPrice")).clear();
		driver.findElement(By.id("inputPrice")).sendKeys("3.50");
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:8080/webclient/product/add"));
		assertTrue(driver.findElement(By.xpath("//div/span")).getText().equalsIgnoreCase("INVALID_DATA"));
		
		// Product with invalid price
		driver.findElement(By.id("inputName")).click();
		driver.findElement(By.id("inputName")).clear();
		driver.findElement(By.id("inputName")).sendKeys("Otro producto");
		driver.findElement(By.id("inputPrice")).click();
		driver.findElement(By.id("inputPrice")).clear();
		driver.findElement(By.id("inputPrice")).sendKeys("-3.50");
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:8080/webclient/product/add"));
		assertTrue(driver.findElement(By.xpath("//div/span")).getText().equalsIgnoreCase("INVALID_DATA"));
	}
	
	@Test
	public void testBurgerJavisMVCProductModify() throws Exception {
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
		
		// Modify product
		driver.findElement(By.linkText("Menú")).click();
		TimeUnit.SECONDS.sleep(1);
		driver.findElement(By.xpath("//div[@id='menu']/div/div/a[2]")).click();
		driver.findElement(By.id("categoryId")).click();
		new Select(driver.findElement(By.id("categoryId"))).selectByVisibleText("Tostas");
		driver.findElement(By.id("inputPrice")).click();
		driver.findElement(By.id("inputPrice")).clear();
		driver.findElement(By.id("inputPrice")).sendKeys("3.50");
		driver.findElement(By.id("inputName")).click();
		driver.findElement(By.id("inputName")).clear();
		driver.findElement(By.id("inputName")).sendKeys("Sandwichhh");
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		List<Product> products = productRepository.findByNameIgnoreCase("Sandwich");
		assertEquals(products.size(), 0);
		products = productRepository.findByNameIgnoreCase("Sandwichhh");
		assertEquals(products.size(), 1);
		assertEquals(products.get(0).getPrice(), 3.50, UnitTestUtil.DELTA_ERROR);
		assertTrue(products.get(0).getCategory().getName().equalsIgnoreCase("Tostas"));
		
		// Discard changes
		driver.findElement(By.linkText("Menú")).click();
		TimeUnit.SECONDS.sleep(1);
		driver.findElement(By.xpath("//div[@id='menu']/div/div/a[2]")).click();
		driver.findElement(By.id("inputPrice")).click();
		driver.findElement(By.id("inputPrice")).clear();
		driver.findElement(By.id("inputPrice")).sendKeys("8.50");
		driver.findElement(By.linkText("Descartar cambios")).click();
		List<Product> products2 = productRepository.findByNameIgnoreCase("Sandwichhh");
		assertEquals(products2.size(), 1);
		assertEquals(products2.get(0).getPrice(), 3.50, UnitTestUtil.DELTA_ERROR);
		
		// Product name is empty
		driver.findElement(By.linkText("Menú")).click();
		TimeUnit.SECONDS.sleep(1);
		driver.findElement(By.xpath("//div[@id='menu']/div/div/a[2]")).click();
		driver.findElement(By.id("inputName")).click();
		driver.findElement(By.id("inputName")).clear();
		driver.findElement(By.id("inputName")).sendKeys(" ");
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		assertTrue(driver.findElement(By.xpath("//div/span")).getText().equalsIgnoreCase("INVALID_DATA"));
		
		// Product name is invalid
		driver.findElement(By.id("inputName")).click();
		driver.findElement(By.id("inputName")).clear();
		driver.findElement(By.id("inputName")).sendKeys("KSjf?=sd(");
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		assertTrue(driver.findElement(By.xpath("//div/span")).getText().equalsIgnoreCase("INVALID_DATA"));
		
		// Product price is invalid
		driver.findElement(By.id("inputPrice")).click();
		driver.findElement(By.id("inputPrice")).clear();
		driver.findElement(By.id("inputPrice")).sendKeys("-3.50");
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		assertTrue(driver.findElement(By.xpath("//div/span")).getText().equalsIgnoreCase("INVALID_DATA"));
		
		// Product name is in use
		driver.findElement(By.id("inputName")).click();
		driver.findElement(By.id("inputName")).clear();
		driver.findElement(By.id("inputName")).sendKeys("Hamburguesa");
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		assertTrue(driver.findElement(By.xpath("//div/span")).getText().equalsIgnoreCase("NAME_IN_USE"));
	}
	
	@Test
	public void testBurgerJavisMVCProductDelete() throws Exception {
		// Login
		driver.get("http://localhost:8080/webclient/login");
		driver.findElement(By.name("username")).click();
		driver.findElement(By.name("username")).clear();
		driver.findElement(By.name("username")).sendKeys("admin");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("admin");
		driver.findElement(By.xpath("//input[@value='Iniciar sesión']")).click();
		assertTrue(driver.getCurrentUrl().equalsIgnoreCase("http://localhost:8080/"));
		assertTrue(driver.getTitle().equalsIgnoreCase("Burger Javi's - Inicio"));
		
		// Delete product
		int nProducts = ((List<Product>) productRepository.findAll()).size();
		driver.findElement(By.linkText("Menú")).click();
		TimeUnit.SECONDS.sleep(1);
		driver.findElement(By.xpath("//div[@id='menu']/div/div/a[3]/span")).click();
		driver.findElement(By.xpath("(//button[@type='submit'])[2]")).click();
		assertEquals(productRepository.findByNameIgnoreCase("CocaCola").size(), 0);
		assertEquals(((List<Product>) productRepository.findAll()).size(), nProducts - 1);
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
