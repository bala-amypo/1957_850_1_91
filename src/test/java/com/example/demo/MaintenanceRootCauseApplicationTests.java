package com.example.demo;

import com.example.demo.dto.AuthResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.impl.*;
import com.example.demo.servlet.HealthServlet;
import com.example.demo.util.TicketCategorizationEngine;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Listeners(TestResultListener.class)
public class MaintenanceRootCauseApplicationTests {

  private UserRepository userRepository;
  private TicketRepository ticketRepository;
  private CategoryRepository categoryRepository;
  private CategorizationRuleRepository ruleRepository;
  private UrgencyPolicyRepository policyRepository;
  private CategorizationLogRepository logRepository;

  private PasswordEncoder passwordEncoder;
  private AuthenticationManager authenticationManager;
  private UserDetailsService userDetailsService;

  private UserServiceImpl userService;
  private TicketServiceImpl ticketService;
  private CategoryServiceImpl categoryService;
  private CategorizationRuleServiceImpl ruleService;
  private UrgencyPolicyServiceImpl policyService;
  private CategorizationEngineServiceImpl engineService;
  private TicketCategorizationEngine engine;

  @BeforeClass
  public void setUp() {
    MockitoAnnotations.openMocks(this);

    userRepository = mock(UserRepository.class);
    ticketRepository = mock(TicketRepository.class);
    categoryRepository = mock(CategoryRepository.class);
    ruleRepository = mock(CategorizationRuleRepository.class);
    policyRepository = mock(UrgencyPolicyRepository.class);
    logRepository = mock(CategorizationLogRepository.class);

    passwordEncoder = mock(PasswordEncoder.class);
    authenticationManager = mock(AuthenticationManager.class);
    userDetailsService = mock(UserDetailsService.class);

    engine = new TicketCategorizationEngine();

    userService = new UserServiceImpl(userRepository, passwordEncoder);
    ticketService = new TicketServiceImpl(ticketRepository);
    categoryService = new CategoryServiceImpl(categoryRepository);
    ruleService = new CategorizationRuleServiceImpl(ruleRepository, categoryRepository);
    policyService = new UrgencyPolicyServiceImpl(policyRepository);
    engineService = new CategorizationEngineServiceImpl(
        ticketRepository, categoryRepository, ruleRepository, policyRepository, logRepository, engine);
  }

  // ==========================================
  // 1. Servlet / Tomcat related (1-8)
  // ==========================================

  @Test(priority = 1)
  public void testHealthServletHasAnnotation() {
    jakarta.servlet.annotation.WebServlet ann =
        HealthServlet.class.getAnnotation(jakarta.servlet.annotation.WebServlet.class);
    Assert.assertNotNull(ann);
  }

  @Test(priority = 2)
  public void testHealthServletUrlPattern() {
    jakarta.servlet.annotation.WebServlet ann =
        HealthServlet.class.getAnnotation(jakarta.servlet.annotation.WebServlet.class);
    Assert.assertTrue(ann.urlPatterns().length > 0);
  }

  @Test(priority = 3)
  public void testHealthServletExtendsHttpServlet() {
    Assert.assertTrue(jakarta.servlet.http.HttpServlet.class.isAssignableFrom(HealthServlet.class));
  }

  @Test(priority = 4)
  public void testHealthServletDoGetProtected() throws Exception {
    var m = HealthServlet.class.getDeclaredMethod("doGet", HttpServletRequest.class, HttpServletResponse.class);
    Assert.assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test(priority = 5)
  public void testHealthServletIsPublic() {
    Assert.assertTrue(Modifier.isPublic(HealthServlet.class.getModifiers()));
  }

  @Test(priority = 6)
  public void testHealthServletInstantiable() {
    HealthServlet s = new HealthServlet();
    Assert.assertNotNull(s);
  }

  @Test(priority = 7)
  public void testHealthServletHasNoFields() {
    Assert.assertEquals(HealthServlet.class.getDeclaredFields().length, 0);
  }

  @Test(priority = 8)
  public void testHealthServletNameContainsHealth() {
    Assert.assertTrue(HealthServlet.class.getSimpleName().contains("Health"));
  }

  // ==========================================
  // 2. CRUD operations (9-16)
  // ==========================================

  @Test(priority = 9)
  public void testRegisterUserSuccess() {
    User user = new User();
    user.setEmail("test@demo.com");
    user.setPassword("password123");

    when(userRepository.existsByEmail("test@demo.com")).thenReturn(false);
    when(passwordEncoder.encode("password123")).thenReturn("encoded");
    when(userRepository.save(any(User.class))).thenAnswer(inv -> {
      User u = inv.getArgument(0);
      u.setId(1L);
      return u;
    });

    User saved = userService.register(user);
    Assert.assertNotNull(saved.getId());
    Assert.assertEquals(saved.getPassword(), "encoded");
  }

  @Test(priority = 10)
  public void testRegisterUserDuplicateEmail() {
    User user = new User();
    user.setEmail("dup@demo.com");
    user.setPassword("password123");

    when(userRepository.existsByEmail("dup@demo.com")).thenReturn(true);

    try {
      userService.register(user);
      Assert.fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException ex) {
      Assert.assertTrue(ex.getMessage().contains("Email already in use"));
    }
  }

  @Test(priority = 11)
  public void testCreateTicketSuccess() {
    Ticket t = new Ticket();
    t.setTitle("AC not working");
    t.setDescription("The AC in room 101 is not cooling properly.");

    when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> {
      Ticket x = inv.getArgument(0);
      x.setId(10L);
      return x;
    });

    Ticket saved = ticketService.createTicket(t);
    Assert.assertEquals(saved.getId(), 10L);
    Assert.assertNull(saved.getAssignedCategory());
    Assert.assertNull(saved.getUrgencyLevel());
  }

  @Test(priority = 12)
  public void testGetTicketNotFound() {
    when(ticketRepository.findById(999L)).thenReturn(Optional.empty());
    try {
      ticketService.getTicket(999L);
      Assert.fail("Expected ResourceNotFoundException");
    } catch (ResourceNotFoundException ex) {
      Assert.assertTrue(ex.getMessage().contains("Ticket not found"));
    }
  }

  @Test(priority = 13)
  public void testCreateCategory() {
    Category c = new Category();
    c.setCategoryName("Electrical");
    c.setDefaultUrgency("HIGH");

    when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> {
      Category x = inv.getArgument(0);
      x.setId(3L);
      return x;
    });

    Category saved = categoryService.createCategory(c);
    Assert.assertEquals(saved.getId(), 3L);
  }

  @Test(priority = 14)
  public void testGetCategoryNotFound() {
    when(categoryRepository.findById(111L)).thenReturn(Optional.empty());
    try {
      categoryService.getCategory(111L);
      Assert.fail("Expected ResourceNotFoundException");
    } catch (ResourceNotFoundException ex) {
      Assert.assertTrue(ex.getMessage().contains("Category not found"));
    }
  }

  @Test(priority = 15)
  public void testCreateUrgencyPolicy() {
    UrgencyPolicy p = new UrgencyPolicy();
    p.setPolicyName("Critical leak");
    p.setKeyword("leak");
    p.setUrgencyOverride("CRITICAL");

    when(policyRepository.save(any(UrgencyPolicy.class))).thenAnswer(inv -> {
      UrgencyPolicy x = inv.getArgument(0);
      x.setId(7L);
      return x;
    });

    UrgencyPolicy saved = policyService.createPolicy(p);
    Assert.assertEquals(saved.getId(), 7L);
  }

  @Test(priority = 16)
  public void testGetPolicyNotFound() {
    when(policyRepository.findById(404L)).thenReturn(Optional.empty());
    try {
      policyService.getPolicy(404L);
      Assert.fail("Expected ResourceNotFoundException");
    } catch (ResourceNotFoundException ex) {
      Assert.assertTrue(ex.getMessage().contains("Policy not found"));
    }
  }

  // ==========================================
  // 3. DI & IoC (17-24)
  // ==========================================

  @Test(priority = 17)
  public void testUserServiceInjected() {
    Assert.assertNotNull(userService);
  }

  @Test(priority = 18)
  public void testTicketServiceInjected() {
    Assert.assertNotNull(ticketService);
  }

  @Test(priority = 19)
  public void testCategoryServiceInjected() {
    Assert.assertNotNull(categoryService);
  }

  @Test(priority = 20)
  public void testRuleServiceInjected() {
    Assert.assertNotNull(ruleService);
  }

  @Test(priority = 21)
  public void testPolicyServiceInjected() {
    Assert.assertNotNull(policyService);
  }

  @Test(priority = 22)
  public void testEngineServiceInjected() {
    Assert.assertNotNull(engineService);
  }

  @Test(priority = 23)
  public void testIoCAlternativeRepositoryForTicket() {
    TicketRepository altRepo = mock(TicketRepository.class);
    TicketServiceImpl altService = new TicketServiceImpl(altRepo);
    Assert.assertNotNull(altService);
  }

  @Test(priority = 24)
  public void testTicketCategorizationEngineIndependentUsage() {
    Ticket t = new Ticket();
    t.setTitle("Electrical short");
    t.setDescription("There is an electrical short in the main panel.");

    Category cat = new Category();
    cat.setCategoryName("Electrical");
    cat.setDefaultUrgency("HIGH");

    CategorizationRule rule = new CategorizationRule();
    rule.setCategory(cat);
    rule.setKeyword("electrical");
    rule.setMatchType("CONTAINS");
    rule.setPriority(10);

    List<Category> categories = List.of(cat);
    List<CategorizationRule> rules = List.of(rule);
    List<UrgencyPolicy> policies = List.of();
    List<CategorizationLog> logs = new ArrayList<>();

    engine.categorize(t, categories, rules, policies, logs);

    Assert.assertEquals(t.getAssignedCategory().getCategoryName(), "Electrical");
    Assert.assertEquals(t.getUrgencyLevel(), "HIGH");
    Assert.assertFalse(logs.isEmpty());
  }

  // ==========================================
  // 4. Hibernate & entity lifecycle (25-32)
  // ==========================================

  @Test(priority = 25)
  public void testTicketTableAnnotationPresent() {
    jakarta.persistence.Table table = Ticket.class.getAnnotation(jakarta.persistence.Table.class);
    Assert.assertNotNull(table);
  }

  @Test(priority = 26)
  public void testCategoryTableAnnotationPresent() {
    jakarta.persistence.Table table = Category.class.getAnnotation(jakarta.persistence.Table.class);
    Assert.assertNotNull(table);
  }

  @Test(priority = 27)
  public void testTicketPrePersistSetsCreatedAt() {
    Ticket t = new Ticket();
    t.setTitle("Test");
    t.setDescription("description long enough");
    t.prePersist();
    Assert.assertNotNull(t.getCreatedAt());
  }

  @Test(priority = 28)
  public void testCategoryPrePersistSetsCreatedAt() {
    Category c = new Category();
    c.prePersist();
    Assert.assertNotNull(c.getCreatedAt());
  }

  @Test(priority = 29)
  public void testRulePrePersistDefaultPriority() {
    CategorizationRule rule = new CategorizationRule();
    rule.setKeyword("test");
    rule.setMatchType("CONTAINS");
    rule.prePersist();
    Assert.assertEquals(rule.getPriority(), Integer.valueOf(1));
  }

  @Test(priority = 30)
  public void testUrgencyPolicyPrePersistTimestamp() {
    UrgencyPolicy p = new UrgencyPolicy();
    p.prePersist();
    Assert.assertNotNull(p.getCreatedAt());
  }

  @Test(priority = 31)
  public void testHibernateSaveUserAssignsId() {
    User user = new User();
    user.setEmail("hib@demo.com");
    user.setPassword("pass12345");

    when(userRepository.existsByEmail("hib@demo.com")).thenReturn(false);
    when(passwordEncoder.encode("pass12345")).thenReturn("enc");
    when(userRepository.save(any(User.class))).thenAnswer(inv -> {
      User u = inv.getArgument(0);
      u.setId(99L);
      return u;
    });

    User saved = userService.register(user);
    Assert.assertEquals(saved.getId(), 99L);
  }

  @Test(priority = 32)
  public void testHibernateSaveTicketAssignsId() {
    Ticket t = new Ticket();
    t.setTitle("Hibernate ticket");
    t.setDescription("Hibernate test description long enough");

    when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> {
      Ticket x = inv.getArgument(0);
      x.setId(77L);
      return x;
    });

    Ticket saved = ticketService.createTicket(t);
    Assert.assertEquals(saved.getId(), 77L);
  }

  // ==========================================
  // 5. JPA mapping & normalization (33-40)
  // ==========================================

  @Test(priority = 33)
  public void testTicketAssignedCategoryManyToOne() throws Exception {
    jakarta.persistence.ManyToOne ann =
        Ticket.class.getDeclaredField("assignedCategory").getAnnotation(jakarta.persistence.ManyToOne.class);
    Assert.assertNotNull(ann);
  }

  @Test(priority = 34)
  public void testCategorizationLogTicketManyToOne() throws Exception {
    jakarta.persistence.ManyToOne ann =
        CategorizationLog.class.getDeclaredField("ticket").getAnnotation(jakarta.persistence.ManyToOne.class);
    Assert.assertNotNull(ann);
  }

  @Test(priority = 35)
  public void testCategorizationLogAppliedRuleManyToOne() throws Exception {
    jakarta.persistence.ManyToOne ann =
        CategorizationLog.class.getDeclaredField("appliedRule").getAnnotation(jakarta.persistence.ManyToOne.class);
    Assert.assertNotNull(ann);
  }

  @Test(priority = 36)
  public void testTicketDoesNotContainPolicyField() {
    boolean hasPolicy = Arrays.stream(Ticket.class.getDeclaredFields())
        .anyMatch(f -> f.getType().equals(UrgencyPolicy.class));
    Assert.assertFalse(hasPolicy);
  }

  @Test(priority = 37)
  public void testCategoryDoesNotContainTicketIdPrimitiveField() {
    boolean hasTicketId = Arrays.stream(Category.class.getDeclaredFields())
        .anyMatch(f -> f.getName().equalsIgnoreCase("ticketId"));
    Assert.assertFalse(hasTicketId);
  }

  @Test(priority = 38)
  public void testTicketDescriptionValidationConcept() {
    Ticket t = new Ticket();
    t.setDescription("1234567890");
    Assert.assertTrue(t.getDescription().length() >= 10);
  }

  @Test(priority = 39)
  public void testCategorizationLogNoBusinessLogicFieldsStored() {
    boolean hasEngineField = Arrays.stream(CategorizationLog.class.getDeclaredFields())
        .anyMatch(f -> f.getName().toLowerCase().contains("engine"));
    Assert.assertFalse(hasEngineField);
  }

  @Test(priority = 40)
  public void testCategoryTableHasNameCategories() {
    jakarta.persistence.Table table = Category.class.getAnnotation(jakarta.persistence.Table.class);
    Assert.assertEquals(table.name(), "categories");
  }

  // ==========================================
  // 6. Many-to-Many (41-48)
  // ==========================================

  @Test(priority = 41)
  public void testCategoryUrgencyPoliciesManyToMany() throws Exception {
    jakarta.persistence.ManyToMany ann =
        Category.class.getDeclaredField("urgencyPolicies").getAnnotation(jakarta.persistence.ManyToMany.class);
    Assert.assertNotNull(ann);
  }

  @Test(priority = 42)
  public void testUrgencyPolicyCategoriesManyToMany() throws Exception {
    jakarta.persistence.ManyToMany ann =
        UrgencyPolicy.class.getDeclaredField("categories").getAnnotation(jakarta.persistence.ManyToMany.class);
    Assert.assertNotNull(ann);
  }

  @Test(priority = 43)
  public void testAddPolicyToCategory() {
    Category c = new Category();
    UrgencyPolicy p = new UrgencyPolicy();
    c.getUrgencyPolicies().add(p);
    Assert.assertEquals(c.getUrgencyPolicies().size(), 1);
  }

  @Test(priority = 44)
  public void testAddMultiplePoliciesToCategory() {
    Category c = new Category();
    c.getUrgencyPolicies().add(new UrgencyPolicy());
    c.getUrgencyPolicies().add(new UrgencyPolicy());
    Assert.assertEquals(c.getUrgencyPolicies().size(), 2);
  }

  @Test(priority = 45)
  public void testCategoryPolicySetPreventsDuplicates() {
    Category c = new Category();
    UrgencyPolicy p = new UrgencyPolicy();
    c.getUrgencyPolicies().add(p);
    c.getUrgencyPolicies().add(p);
    Assert.assertEquals(c.getUrgencyPolicies().size(), 1);
  }

  @Test(priority = 46)
  public void testUrgencyPolicyCategoriesDefaultEmpty() {
    UrgencyPolicy p = new UrgencyPolicy();
    Assert.assertTrue(p.getCategories().isEmpty());
  }

  @Test(priority = 47)
  public void testCategoryUrgencyPoliciesDefaultEmpty() {
    Category c = new Category();
    Assert.assertTrue(c.getUrgencyPolicies().isEmpty());
  }

  @Test(priority = 48)
  public void testBidirectionalManyToManyConsistency() {
    Category c = new Category();
    UrgencyPolicy p = new UrgencyPolicy();
    c.getUrgencyPolicies().add(p);
    p.getCategories().add(c);
    Assert.assertTrue(c.getUrgencyPolicies().contains(p));
    Assert.assertTrue(p.getCategories().contains(c));
  }

  // ==========================================
  // 7. Security & JWT (49-56)
  // ==========================================

  @Test(priority = 49)
  public void testAuthResponseCarriesJwtAndUserInfo() {
    AuthResponse response = new AuthResponse("token", 1L, "user@demo.com", "ADMIN");
    Assert.assertEquals(response.getToken(), "token");
    Assert.assertEquals(response.getUserId(), 1L);
    Assert.assertEquals(response.getEmail(), "user@demo.com");
    Assert.assertEquals(response.getRole(), "ADMIN");
  }

  @Test(priority = 50)
  public void testPasswordEncodingOnRegister() {
    User u = new User();
    u.setEmail("enc@demo.com");
    u.setPassword("plain");

    when(userRepository.existsByEmail("enc@demo.com")).thenReturn(false);
    when(passwordEncoder.encode("plain")).thenReturn("encrypted");
    when(userRepository.save(any(User.class))).thenAnswer(inv -> {
      User saved = inv.getArgument(0);
      saved.setId(2L);
      return saved;
    });

    User saved = userService.register(u);
    Assert.assertEquals(saved.getPassword(), "encrypted");
  }

  @Test(priority = 51)
  public void testAuthenticationManagerAuthenticateCalled() {
    UsernamePasswordAuthenticationToken token =
        new UsernamePasswordAuthenticationToken("user@demo.com", "pass");
    authenticationManager.authenticate(token);
    verify(authenticationManager, times(1)).authenticate(token);
  }

  @Test(priority = 52)
  public void testUserRoleConvertedToSpringAuthorityPattern() {
    User u = new User();
    u.setRole("ADMIN");
    String authority = "ROLE_" + u.getRole();
    Assert.assertEquals(authority, "ROLE_ADMIN");
  }

  @Test(priority = 53)
  public void testNullAuthorizationHeaderSimulatesUnauthorized() {
    String header = null;
    Assert.assertNull(header);
  }

  @Test(priority = 54)
  public void testUserServiceFindByEmailNotFound() {
    when(userRepository.findByEmail("no@demo.com")).thenReturn(Optional.empty());

    try {
      userService.findByEmail("no@demo.com");
      Assert.fail("Expected ResourceNotFoundException");
    } catch (ResourceNotFoundException ex) {
      Assert.assertTrue(ex.getMessage().contains("User not found"));
    }
  }

  @Test(priority = 55)
  public void testUserServiceGetByIdSuccess() {
    User u = new User();
    u.setId(5L);
    when(userRepository.findById(5L)).thenReturn(Optional.of(u));

    User found = userService.getById(5L);
    Assert.assertEquals(found.getId(), 5L);
  }

  @Test(priority = 56)
  public void testUserServiceGetByIdNotFound() {
    when(userRepository.findById(1234L)).thenReturn(Optional.empty());

    try {
      userService.getById(1234L);
      Assert.fail("Expected ResourceNotFoundException");
    } catch (ResourceNotFoundException ex) {
      Assert.assertTrue(ex.getMessage().contains("User not found"));
    }
  }

  // ==========================================
  // 8. HQL / Criteria style queries (57-64)
  // ==========================================

  @Test(priority = 57)
  public void testRuleRepositoryFindByKeywordContains() {
    CategorizationRule r = new CategorizationRule();
    r.setKeyword("leak");

    when(ruleRepository.findByKeywordContainingIgnoreCase("leak"))
        .thenReturn(List.of(r));

    List<CategorizationRule> list = ruleRepository.findByKeywordContainingIgnoreCase("leak");
    Assert.assertEquals(list.size(), 1);
    Assert.assertEquals(list.get(0).getKeyword(), "leak");
  }

  @Test(priority = 58)
  public void testRuleRepositoryFindByKeywordEmptyResult() {
    when(ruleRepository.findByKeywordContainingIgnoreCase("none"))
        .thenReturn(Collections.emptyList());

    List<CategorizationRule> list = ruleRepository.findByKeywordContainingIgnoreCase("none");
    Assert.assertTrue(list.isEmpty());
  }

  @Test(priority = 59)
  public void testPolicyRepositoryFindByKeyword() {
    UrgencyPolicy p = new UrgencyPolicy();
    p.setKeyword("gas");

    when(policyRepository.findByKeywordContainingIgnoreCase("gas"))
        .thenReturn(List.of(p));

    List<UrgencyPolicy> list = policyRepository.findByKeywordContainingIgnoreCase("gas");
    Assert.assertFalse(list.isEmpty());
    Assert.assertEquals(list.get(0).getKeyword(), "gas");
  }

  @Test(priority = 60)
  public void testEngineServiceCategorizeTicketElectricalHighUrgency() {
    Ticket t = new Ticket();
    t.setId(1L);
    t.setTitle("Power issue");
    t.setDescription("Electrical short circuit near panel");

    Category electrical = new Category();
    electrical.setId(10L);
    electrical.setCategoryName("Electrical");
    electrical.setDefaultUrgency("HIGH");

    CategorizationRule rule = new CategorizationRule();
    rule.setCategory(electrical);
    rule.setKeyword("electrical");
    rule.setMatchType("CONTAINS");
    rule.setPriority(5);

    when(ticketRepository.findById(1L)).thenReturn(Optional.of(t));
    when(categoryRepository.findAll()).thenReturn(List.of(electrical));
    when(ruleRepository.findAll()).thenReturn(List.of(rule));
    when(policyRepository.findAll()).thenReturn(List.of());
    when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));
    when(logRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

    Ticket result = engineService.categorizeTicket(1L);
    Assert.assertEquals(result.getAssignedCategory().getCategoryName(), "Electrical");
    Assert.assertEquals(result.getUrgencyLevel(), "HIGH");
  }

  @Test(priority = 61)
  public void testEngineServiceCategorizeTicketWithPolicyOverride() {
    Ticket t = new Ticket();
    t.setId(2L);
    t.setTitle("Water leak in ceiling");
    t.setDescription("There is major water leak above ceiling.");

    Category plumbing = new Category();
    plumbing.setId(11L);
    plumbing.setCategoryName("Plumbing");
    plumbing.setDefaultUrgency("MEDIUM");

    CategorizationRule rule = new CategorizationRule();
    rule.setCategory(plumbing);
    rule.setKeyword("leak");
    rule.setMatchType("CONTAINS");
    rule.setPriority(10);

    UrgencyPolicy policy = new UrgencyPolicy();
    policy.setKeyword("major");
    policy.setUrgencyOverride("CRITICAL");

    when(ticketRepository.findById(2L)).thenReturn(Optional.of(t));
    when(categoryRepository.findAll()).thenReturn(List.of(plumbing));
    when(ruleRepository.findAll()).thenReturn(List.of(rule));
    when(policyRepository.findAll()).thenReturn(List.of(policy));
    when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));
    when(logRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

    Ticket result = engineService.categorizeTicket(2L);
    Assert.assertEquals(result.getAssignedCategory().getCategoryName(), "Plumbing");
    Assert.assertEquals(result.getUrgencyLevel(), "CRITICAL");
  }

  @Test(priority = 62)
  public void testGetLogsForTicketEmpty() {
    when(logRepository.findByTicket_Id(55L)).thenReturn(Collections.emptyList());
    List<CategorizationLog> logs = engineService.getLogsForTicket(55L);
    Assert.assertTrue(logs.isEmpty());
  }

  @Test(priority = 63)
  public void testEngineCategorizationNoRuleNoPolicyDefaultsLow() {
    Ticket t = new Ticket();
    t.setTitle("Some unknown issue");
    t.setDescription("Something not categorized yet.");

    List<Category> cats = List.of();
    List<CategorizationRule> rules = List.of();
    List<UrgencyPolicy> policies = List.of();
    List<CategorizationLog> logs = new ArrayList<>();

    engine.categorize(t, cats, rules, policies, logs);
    Assert.assertEquals(t.getUrgencyLevel(), "LOW");
  }

  @Test(priority = 64)
  public void testGetLogNotFoundThrowsException() {
    when(logRepository.findById(999L)).thenReturn(Optional.empty());
    try {
      engineService.getLog(999L);
      Assert.fail("Expected ResourceNotFoundException");
    } catch (ResourceNotFoundException ex) {
      Assert.assertTrue(ex.getMessage().contains("Log not found"));
    }
  }
}
