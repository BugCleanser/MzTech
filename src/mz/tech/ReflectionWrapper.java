package mz.tech;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.bukkit.Bukkit;

/**
 * nms相关操作
 * 不强制try/catch的反射操作
 */
public final class ReflectionWrapper
{
  private ReflectionWrapper()
  {
  }

  public static <T> T newInstance(final Constructor<T> con, final Object... args)
  {
    try {
      con.setAccessible(true);
      return con.newInstance(args);
    } catch (final Throwable e) {
      if (e instanceof RuntimeException) {
        throw (RuntimeException) e;
      }
      throw new RuntimeException(e);
    }
  }

  public static Class<?> getClassByName(final String n)
  {
    try {
      return Class.forName(n);
    } catch (final Throwable e) {
      if (e instanceof RuntimeException) {
        throw (RuntimeException) e;
      }
      throw new RuntimeException(e);
    }
  }

  public static Method getMethodParent(final Class<?> c, final String method, final Class<?>... args)
  {
    try {
      try {
        return ReflectionWrapper.getMethod(c, method, args);
      } catch (final Throwable e) {
        if (c.getSuperclass() != null) {
          return ReflectionWrapper.getMethodParent(c.getSuperclass(), method, args);
        } else if (e instanceof RuntimeException) {
          throw (RuntimeException) e;
        } else {
          throw new RuntimeException(e);
        }
      }
    } catch (final Throwable e) {
      final Class<?>[] classes = c.getInterfaces();
      Throwable lastExc = null;
      if (classes.length != 0) {
        for (final Class<?> i : classes) {
          Throwable exc = null;
          try {
            return ReflectionWrapper.getMethodParent(i, method, args);
          } catch (final Throwable e2) {
            exc = e2;
            // ignore
          }
          lastExc = exc;
        }
      }
      if (lastExc != null) {
        if (lastExc instanceof RuntimeException) {
          throw (RuntimeException) lastExc;
        } else {
          throw new RuntimeException(e);
        }
      } else if (e instanceof RuntimeException) {
        throw (RuntimeException) e;
      } else {
        throw new RuntimeException(e);
      }
    }
  }

  public static Field getFieldParent(final Class<?> c, final String name)
  {
    try {
      try {
        return ReflectionWrapper.getField(c, name);
      } catch (final Throwable e) {
        if (c.getSuperclass() != null) {
          return ReflectionWrapper.getFieldParent(c.getSuperclass(), name);
        } else if (e instanceof RuntimeException) {
          throw (RuntimeException) e;
        } else {
          throw new RuntimeException(e);
        }
      }
    } catch (final Throwable e) {
      final Class<?>[] classes = c.getInterfaces();
      Throwable lastExc = null;
      if (classes.length != 0) {
        for (final Class<?> i : classes) {
          Throwable exc = null;
          try {
            return ReflectionWrapper.getFieldParent(i, name);
          } catch (final Throwable e2) {
            exc = e2;
            // ignore
          }
          lastExc = exc;
        }
      }
      if (lastExc != null) {
        if (lastExc instanceof RuntimeException) {
          throw (RuntimeException) lastExc;
        } else {
          throw new RuntimeException(e);
        }
      } else if (e instanceof RuntimeException) {
        throw (RuntimeException) e;
      } else {
        throw new RuntimeException(e);
      }
    }
  }

  public static <T> Constructor<T> getInnerConstructor(final Class<T> c, final Class<?>... args)
  {
    try {
      final Class<?>[] rargs = new Class<?>[args.length + 1];
      rargs[0] = c.getEnclosingClass();
      for (int i = 1; i < rargs.length; i++) {
        rargs[i] = args[i - 1];
      }
      final Constructor<T> con = c.getDeclaredConstructor(rargs);
      con.setAccessible(true);
      return con;
    } catch (final Throwable e) {
      if (e instanceof RuntimeException) {
        throw (RuntimeException) e;
      }
      throw new RuntimeException(e);
    }
  }

  public static <T> Constructor<T> getConstructor(final Class<T> c, final Class<?>... args)
  {
    try {
      final Constructor<T> con = c.getDeclaredConstructor(args);
      con.setAccessible(true);
      return con;
    } catch (final Throwable e) {
      if (e instanceof RuntimeException) {
        throw (RuntimeException) e;
      }
      throw new RuntimeException(e);
    }
  }

  public static Class<?> getInnerClass(final Class<?> parent, final String name)
  {
    final Class<?>[] classes = parent.getDeclaredClasses();
    for (final Class<?> i : classes) {
      if (i.getSimpleName().equals(name)) {
        return i;
      }
    }
    return null;
  }

  public static Method getMethod(final Class<?> c, final String n, final Class<?>... t)
  {
    try {
      final Method m = c.getDeclaredMethod(n, t);
      m.setAccessible(true);
      return m;
    } catch (final Throwable e) {
      if (e instanceof RuntimeException) {
        throw (RuntimeException) e;
      }
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> T invokeMethod(final Method m, final Object o, final Object... args)
  {
    try {
      m.setAccessible(true);
      return (T) m.invoke(o, args);
    } catch (final Throwable e) {
      if (e instanceof RuntimeException) {
        throw (RuntimeException) e;
      }
      throw new RuntimeException(e);
    }
  }

  public static <T> T invokeStaticMethod(final Method m, final Object... args)
  {
    m.setAccessible(true);
    return ReflectionWrapper.invokeMethod(m, null, args);
  }

  public static Class<?> getNMSClass(final String c)
  {
    try {
      return Class.forName(
          Bukkit.getServer().getClass().getPackage().getName().replace("org.bukkit.craftbukkit", "net.minecraft.server")
              + "." + c);
    } catch (final Throwable e) {
      if (e instanceof RuntimeException) {
        throw (RuntimeException) e;
      }
      throw new RuntimeException(e);
    }
  }

  public static Class<?> getCraftBukkitClass(final String c)
  {
    try {
      return Class.forName(Bukkit.getServer().getClass().getPackage().getName() + "." + c);
    } catch (final Throwable e) {
      if (e instanceof RuntimeException) {
        throw (RuntimeException) e;
      }
      throw new RuntimeException(e);
    }
  }

  public static Field getField(final Class<?> c, final String n)
  {
    try {
      final Field f = c.getDeclaredField(n);
      f.setAccessible(true);
      if (Modifier.isFinal(f.getModifiers())) {
        final Field modifiers = f.getClass().getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.set(f, f.getModifiers() & ~Modifier.FINAL);
      }
      return f;
    } catch (final Throwable e) {
      if (e instanceof RuntimeException) {
        throw (RuntimeException) e;
      }
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> T getFieldValue(final Field f, final Object o)
  {
    try {
      f.setAccessible(true);
      return (T) f.get(o);
    } catch (final Throwable e) {
      if (e instanceof RuntimeException) {
        throw (RuntimeException) e;
      }
      throw new RuntimeException(e);
    }
  }

  public static <T> T getStaticFIeldValue(final Field f)
  {
    return ReflectionWrapper.getFieldValue(f, null);
  }

  public static <T> T setFieldValue(final Field f, final Object o, final T v)
  {
    try {
      f.setAccessible(true);
      if (Modifier.isFinal(f.getModifiers())) {
        final Field modifiers = f.getClass().getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.set(f, f.getModifiers() & ~Modifier.FINAL);
      }
      f.set(o, v);
      return v;
    } catch (final Throwable e) {
      if (e instanceof RuntimeException) {
        throw (RuntimeException) e;
      }
      throw new RuntimeException(e);
    }
  }

  public static <T> T setStaticFieldValue(final Field f, final T v)
  {
    return ReflectionWrapper.setFieldValue(f, null, v);
  }

}
