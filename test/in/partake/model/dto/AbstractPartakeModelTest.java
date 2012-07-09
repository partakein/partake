package in.partake.model.dto;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;
import in.partake.AbstractPartakeTestWithApplication;
import in.partake.model.fixture.TestDataProvider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractPartakeModelTest<T extends PartakeModel<T>> extends AbstractPartakeTestWithApplication {
    private Logger logger = Logger.getLogger(getClass());

    protected abstract TestDataProvider<T> getTestDataProvider();
    protected abstract T copy(T t);

    /**
    * freezeしてから破壊的メソッドを実行するとUnsupportedOperationExceptionが投げられることを保証する。
    * freezeしていない時に正しく実行できることの保証は、各サブクラスのテストが担う。
    * @author skypencil(@eller86)
    */
    @Test
    public final void testToCheckFrozen() throws Exception {
        final T model = getTestDataProvider().create().freeze();

        for (Method method : model.getClass().getMethods()) {
            if (!checkDestructiveMethod(method) || method.getDeclaringClass().equals(Object.class))
                continue;

            logger.debug(String.format("Test for %s#%s starts.", model.getClass().getName(), method.getName()));
            Object[] args = createArgsFor(method);

            try {
                method.invoke(model, args);
                Assert.fail(method.getName() + " should throw UnsupportedOperationException but doesn't throw.");
            } catch (InvocationTargetException e) {
                if (!e.getTargetException().getClass().equals(UnsupportedOperationException.class))
                    throw new AssertionError(e);

            }
        }
    }

    @Test
    public final void testToCheckSetterWhenNotFrozen() throws Exception {
        final T model = getTestDataProvider().create();

        for (Method method : model.getClass().getMethods()) {
            if (!checkDestructiveMethod(method) || method.getDeclaringClass().equals(Object.class))
                continue;

            Object[] args = createArgsFor(method);
            method.invoke(model, args);
        }
    }

    @Test
    public final void testToCheckCopy() throws Exception {
        final T original = getTestDataProvider().create();
        final T copied = copy(original);

        assertThat(copied.equals(original), is(true));
    }

    @Test
    public final void testToCheckEquals() throws Exception {
        List<T> samples = getTestDataProvider().createSamples();

        for (int i = 1; i < samples.size(); ++i) {
            assertThat("Index " + String.valueOf(i) + " is the same as index 0.",
                    samples.get(0).equals(samples.get(i)), is(false));
        }

        assertThat(samples.get(0).equals(null), is(false));
    }

    @Test
    public final void testToCheckJSON() throws Exception {
        List<T> samples = getTestDataProvider().createSamples();
        Constructor<?> ctor = samples.get(0).getClass().getConstructor(JSONObject.class);

        assumeThat(samples.get(0).getClass(), not(isIn(new Object[] { UserImage.class, UserThumbnail.class })));

        for (T t : samples) {
            Object created = ctor.newInstance(t.toJSON());
            assertThat(t.equals(created), is(true));
        }
    }

    // ----------------------------------------------------------------------

    private Object[] createArgsFor(final Method m) {
        final Type[] types = m.getGenericParameterTypes();
        final Object[] result = new Object[types.length];
        for (int i = 0; i < types.length; ++i) {
            Type type = types[i];
            result[i] = createArg(type);
        }
        return result;
    }

    private Object createArg(Type c) {
        if (c.equals(int.class)) {
            return Integer.valueOf(0);
        } else if (c.equals(long.class)) {
            return Long.valueOf(0L);
        } else if (c.equals(boolean.class)) {
            return Boolean.FALSE;
        } else if (c.equals(char.class)) {
            return Character.valueOf((char)0);
        } else if (c.equals(byte.class)) {
            return Byte.valueOf((byte)0);
        } else if (c.equals(short.class)) {
            return Short.valueOf((short)0);
        } else if (c.equals(float.class)) {
            return Float.valueOf(0.0f);
        } else if (c.equals(double.class)) {
            return Double.valueOf(0.0d);
        } else if (c.equals(UUID.class)) {
            return new UUID(0, 0);
        } else {
            return null;
        }
    }

    private boolean checkDestructiveMethod(final Method m) {
        return m.getReturnType().equals(Void.TYPE) && !Modifier.isStatic(m.getModifiers());
    }
}
