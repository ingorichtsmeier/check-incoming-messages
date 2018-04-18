import static org.assertj.core.api.Assertions.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class PatternTest {
  
  @Test
  public void testPattern() {
    String suchen = "nachricht 1";
    Pattern pattern = Pattern.compile("^[a-zA-Z ]*([0-9]*)[a-zA-Z ]*$");
    Matcher matcher = pattern.matcher(suchen);
    boolean find = matcher.find();
    assertThat(find).isTrue();
    
    String number = matcher.group(1);
    System.out.println(number);
    assertThat(number).isEqualTo("1");
  }

  @Test
  public void testPattern50() {
    String suchen = "50 ist doof";
    Pattern pattern = Pattern.compile("^[a-zA-Z ]*([0-9]*)[a-zA-Z ]*$");
    Matcher matcher = pattern.matcher(suchen);
    boolean find = matcher.find();
    assertThat(find).isTrue();
    
    String number = matcher.group(1);
    System.out.println(number);
    assertThat(number).isEqualTo("50");
  }

}
