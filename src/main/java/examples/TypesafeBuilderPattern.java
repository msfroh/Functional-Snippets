package examples;

/**
 * User: msfroh
 * Date: 12-06-08
 * Time: 11:46 PM
 */
public class TypesafeBuilderPattern {
    public static final class Person {
        public final int age;
        public final String name;
        public final boolean isAwesome;

        public Person(final int age, final String name, final boolean awesome) {
            this.age = age;
            this.name = name;
            isAwesome = awesome;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "age=" + age +
                    ", name='" + name + '\'' +
                    ", isAwesome=" + isAwesome +
                    '}';
        }
    }

    private abstract class TRUE {}
    private abstract class FALSE {}

    public static final class PersonBuilder<HAS_AGE, HAS_NAME> {
        private final int age;
        private final String name;
        private final boolean awesome;

        private PersonBuilder(final int age, final String name,
                             final boolean awesome) {
            this.age = age;
            this.name = name;
            this.awesome = awesome;
        }

        public PersonBuilder<TRUE, HAS_NAME> withAge(int age) {
            return new PersonBuilder<TRUE, HAS_NAME>(age, this.name,
                    this.awesome);
        }

        public PersonBuilder<HAS_AGE, TRUE> withName(String name) {
            return new PersonBuilder<HAS_AGE, TRUE>(this.age, name,
                    this.awesome);
        }

        public PersonBuilder<HAS_AGE, HAS_NAME> makeAwesome() {
            return new PersonBuilder<HAS_AGE, HAS_NAME>(this.age, this.name,
                    true);
        }

        public static PersonBuilder<FALSE, FALSE> person() {
            return new PersonBuilder<FALSE, FALSE>(-1, "", false);
        }

        public static Person build(PersonBuilder<TRUE, TRUE> builder) {
            return new Person(builder.age, builder.name, builder.awesome);
        }
    }
}
