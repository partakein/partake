package in.partake.base;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.util.UUID;


import org.junit.Test;

public class IdTest {

    @Test
    public void isCanBeCreatedWithValidUUIDString() {
        new Id("00000000-0000-0000-0000-000000000000");
    }

    @Test
    public void isCanBeCreatedWithUUID() {
        new Id(UUID.randomUUID());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfInvalidUUIDString() {
        new Id("This is a invalid UUID string");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfNull() {
        new Id((String) null);
        new Id((UUID) null);
    }

    @Test
    public void equalityTest() {
        String uuidString = "00000000-0000-0000-0000-000000000000";
        Id lhs = new Id(uuidString);
        Id rhs = new Id(UUID.fromString(uuidString));

        assertThat(lhs, is(rhs));
        assertThat(lhs.hashCode(), is(rhs.hashCode()));
        assertThat(lhs, not(sameInstance(rhs)));
    }
}
