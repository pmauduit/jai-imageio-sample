package fr.spironet.nativeutils;

import java.util.Vector;

public class ClassScope {
    private static java.lang.reflect.Field LIBRARIES;
    static {
      try {
        LIBRARIES = ClassLoader.class.getDeclaredField("loadedLibraryNames");
        LIBRARIES.setAccessible(true);
      } catch (Exception e) {
        // whatever
      }
    }

    public static String[] getLoadedLibraries(final ClassLoader loader) {
      try {
        final Vector<String> libraries = (Vector<String>) LIBRARIES.get(loader);
        return libraries.toArray(new String[] {});
      } catch (Exception e) {
        return new String[0];
      }
    }
}
