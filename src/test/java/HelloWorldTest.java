import org.junit.Test;

import static org.junit.Assert.*;

public class HelloWorldTest {

    private HelloWorld instance = new HelloWorld();

    @Test
    public void hello() {
        String who = "World";
        final String actual = instance.hello(who);
        assertTrue(actual.contains("Hello"));
        assertTrue(actual.contains(who));
    }
}