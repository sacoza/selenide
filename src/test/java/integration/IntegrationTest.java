package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.junit.ScreenShooter;
import com.codeborne.selenide.logevents.PrettyReportCreator;
import org.junit.*;
import org.junit.rules.TestRule;

import static com.codeborne.selenide.Configuration.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.*;
import static org.openqa.selenium.net.PortProber.findFreePort;

public abstract class IntegrationTest {
  @Rule
  public ScreenShooter img = ScreenShooter.failedTests() ;

  @Rule
  public TestRule prettyReportCreator = new PrettyReportCreator();

  private static int port;
  protected static LocalHttpServer server;
  private long defaultTimeout;
  protected static long averageSeleniumCommandLength = -1;

  @BeforeClass
  public static void runLocalHttpServer() throws Exception {
    if (server == null) {
      synchronized (IntegrationTest.class) {
        port = findFreePort();
        server = new LocalHttpServer(port).start();
        System.out.println("START " + browser + " TESTS");
      }
    }
  }

  @Before
  public void restartReallyUnstableBrowsers() {
    if (isSafari()) {
      closeWebDriver();
    }
  }

  @Before
  public void setBaseUrl() {
    Configuration.baseUrl = "http://0.0.0.0:" + port;
    startMaximized = false;
  }

  @AfterClass
  public static void restartUnstableWebdriver() {
    if (isIE() || isPhantomjs()) {
      closeWebDriver();
    }
  }

  protected void openFile(String fileName) {
    measureSeleniumCommandDuration();
    open("/" + fileName + "?" + averageSeleniumCommandLength);
  }

  protected <T> T openFile(String fileName, Class<T> pageObjectClass) {
    measureSeleniumCommandDuration();
    return open("/" + fileName + "?" + averageSeleniumCommandLength, pageObjectClass);
  }

  @Before
  public final void rememberTimeout() {
    defaultTimeout = timeout;
  }

  @After
  public final void restoreDefaultProperties() {
    timeout = defaultTimeout;
    clickViaJs = false;
  }

  private void measureSeleniumCommandDuration() {
    if (averageSeleniumCommandLength < 0) {
      open("/start_page.html");
      long start = System.currentTimeMillis();
      for (int i = 0; i < 5; i++) {
        $("h1").isDisplayed();
      }
      averageSeleniumCommandLength = (System.currentTimeMillis() - start) / 5;
      System.out.println("Average selenium command duration: " + averageSeleniumCommandLength + " ms.");
    }
  }
}
