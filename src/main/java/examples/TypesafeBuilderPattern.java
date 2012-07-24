package examples;

import collections.ImmutableList;
import collections.Option;

import static collections.Option.some;

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
        public final ImmutableList<String> friends;

        public Person(final int age, final String name, final boolean awesome,
                      final ImmutableList<String> friends) {
            this.age = age;
            this.name = name;
            isAwesome = awesome;
            this.friends = friends;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "age=" + age +
                    ", name='" + name + '\'' +
                    ", isAwesome=" + isAwesome +
                    ", friends=" + friends +
                    '}';
        }
    }

    private abstract class TRUE {
    }

    private abstract class FALSE {
    }

    public static final class PersonBuilder<HAS_AGE, HAS_NAME> {
        private static final PersonBuilder<FALSE, FALSE> INITIAL_PERSONBUILDER =
                new PersonBuilder<FALSE, FALSE>(Option.<Integer>none(),
                        Option.<String>none(), false,
                        ImmutableList.<String>nil());
        private final Option<Integer> age;
        private final Option<String> name;
        private final boolean awesome;
        private final ImmutableList<String> friends;

        public PersonBuilder(final Option<Integer> age,
                             final Option<String> name,
                             final boolean awesome,
                             final ImmutableList<String> friends) {
            this.age = age;
            this.name = name;
            this.awesome = awesome;
            this.friends = friends;
        }

        public PersonBuilder<TRUE, HAS_NAME> withAge(int age) {
            return new PersonBuilder<TRUE, HAS_NAME>(some(age),
                    name, awesome, friends);
        }

        public PersonBuilder<HAS_AGE, TRUE> withName(String name) {
            return new PersonBuilder<HAS_AGE, TRUE>(age, some(name),
                    awesome, friends);
        }

        public PersonBuilder<HAS_AGE, HAS_NAME> makeAwesome() {
            return new PersonBuilder<HAS_AGE, HAS_NAME>(age, name, true,
                    friends);
        }

        public PersonBuilder<HAS_AGE, HAS_NAME> withFriends(String... friends) {
            ImmutableList<String> newFriends = this.friends;
            for (String friend : friends) {
                newFriends = newFriends.prepend(friend);
            }
            return new PersonBuilder<HAS_AGE, HAS_NAME>(age, name, awesome,
                    newFriends);
        }

        public static PersonBuilder<FALSE, FALSE> person() {
            return INITIAL_PERSONBUILDER;
        }

        public static Person build(PersonBuilder<TRUE, TRUE> builder) {
            return new Person(builder.age.get(), builder.name.get(),
                    builder.awesome, builder.friends);
        }
    }
}
