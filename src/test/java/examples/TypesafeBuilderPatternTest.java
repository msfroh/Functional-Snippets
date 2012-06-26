package examples;

import org.junit.Test;

import static examples.TypesafeBuilderPattern.PersonBuilder.build;
import static examples.TypesafeBuilderPattern.PersonBuilder.person;

/**
 * User: msfroh
 * Date: 12-06-09
 * Time: 12:06 AM
 */
public class TypesafeBuilderPatternTest {

    @Test
    public void testBuilder() throws Exception {
        TypesafeBuilderPattern.Person michael =
                build(
                        person().withAge(32).withName("Michael").makeAwesome()
                );
        System.out.println(michael);
    }
}
