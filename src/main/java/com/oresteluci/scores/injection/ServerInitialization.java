package com.oresteluci.scores.injection;

import com.oresteluci.scores.server.Server;
import sun.net.www.protocol.file.FileURLConnection;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Class that creates bean dependencies and starts server.
 *
 * @author Oreste Luci
 */
public class ServerInitialization {

    public static Map<String,Object> autoBeanMap = new HashMap<>();

    /**
     * Inject dependencies and runs the server.
     * @param scanPackage Base package to scan for bean definition and dependencies
     * @param serverClass Canonical name of Server Class which has the run method.
     * @param args
     * @throws Exception
     */
    public static void run(String scanPackage, String serverClass, String[] args) throws Exception {

        // Creating Injection Dependencies
        ServerInitialization.inject(scanPackage);

        // Getting server and running it
        Server server = (Server)autoBeanMap.get(serverClass);
        server.run(args);
    }


    /**
     * Main method for injecting dependencies.
     *
     * @param scanPackage
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private static void inject(String scanPackage) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {

        ArrayList<Class<?>> classes = ServerInitialization.getClassesForPackage(scanPackage);

        List<String> pending = new ArrayList<>();

        // Instantiating AutoBeans
        for (Class clazz : classes) {

            Annotation[] annotations = clazz.getAnnotations();

            for (Annotation annotation : annotations) {

                if (annotation.annotationType().equals(AutoComponent.class)) {

                    Object obj = clazz.newInstance();

                    autoBeanMap.put(clazz.getCanonicalName(), obj);

                    pending.addAll(autoInjectBeans(obj));
                }
            }
        }

        // Injecting pending bean dependencies
        for (String canonicalName : pending) {

            Object obj = autoBeanMap.get(canonicalName);

            List<String> result = autoInjectBeans(obj);

            if (result != null && result.size()>0) {
                throw new ClassNotFoundException("Could not AutoInject Bean dependencies in: " + obj.getClass().getCanonicalName());
            }
        }
    }

    /**
     * Method to inject beans into the objects annotated fields
     * @param obj
     * @return
     * @throws IllegalAccessException
     */
    private static List<String> autoInjectBeans(Object obj) throws IllegalAccessException {

        List<String> result = new ArrayList<>();

        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {

            field.setAccessible(true);

            Annotation[] fieldAnnotations = field.getAnnotations();

            for (Annotation fieldAnnotation : fieldAnnotations) {

                if (fieldAnnotation.annotationType().equals(com.oresteluci.scores.injection.AutoInject.class)) {

                    Object injectBean = autoBeanMap.get(field.getAnnotatedType().getType().getTypeName());

                    if (injectBean == null) {
                        result.add(obj.getClass().getCanonicalName());
                    } else {
                        field.set(obj, injectBean);
                    }
                }
            }
        }

        return result;
    }

    /**
     * Gets all the classes in the specified package as determined by the context class loader
     *
     * @param pckgName the package name to search
     * @return a list of classes that exist within that package
     * @throws ClassNotFoundException if something goes wrong
     */
    public static ArrayList<Class<?>> getClassesForPackage(String pckgName) throws ClassNotFoundException {

        final ArrayList<Class<?>> classes = new ArrayList<Class<?>>();

        try {
            final ClassLoader cld = Thread.currentThread().getContextClassLoader();

            if (cld == null)
                throw new ClassNotFoundException("Can't get class loader.");

            final Enumeration<URL> resources = cld.getResources(pckgName.replace('.', '/'));

            URLConnection connection;

            for (URL url = null; resources.hasMoreElements() && ((url = resources.nextElement()) != null);) {

                try {

                    connection = url.openConnection();

                    if (connection instanceof JarURLConnection) {

                        checkJarFile((JarURLConnection) connection, pckgName,classes);

                    } else if (connection instanceof FileURLConnection) {

                        try {

                            checkDirectory(new File(URLDecoder.decode(url.getPath(),"UTF-8")), pckgName, classes);

                        } catch (final UnsupportedEncodingException ex) {

                            throw new ClassNotFoundException(pckgName + " does not appear to be a valid package (Unsupported encoding)", ex);
                        }

                    } else {
                        throw new ClassNotFoundException(pckgName + " (" + url.getPath() + ") does not appear to be a valid package");
                    }

                } catch (final IOException ioex) {
                    throw new ClassNotFoundException("IOException was thrown when trying to get all resources for " + pckgName, ioex);
                }
            }
        } catch (final NullPointerException ex) {
            throw new ClassNotFoundException(pckgName + " does not appear to be a valid package (Null pointer exception)", ex);
        } catch (final IOException ioex) {
            throw new ClassNotFoundException("IOException was thrown when trying to get all resources for " + pckgName, ioex);
        }

        return classes;
    }

    /**
     * @param directory The directory to start with
     * @param pckgName The package name to search for. Will be needed for getting the Class object.
     * @param classes if a file isn't loaded but still is in the directory
     * @throws ClassNotFoundException
     */
    private static void checkDirectory(File directory, String pckgName, ArrayList<Class<?>> classes) throws ClassNotFoundException {

        File tmpDirectory;

        if (directory.exists() && directory.isDirectory()) {

            final String[] files = directory.list();

            for (final String file : files) {

                if (file.endsWith(".class")) {

                    try {
                        classes.add(Class.forName(pckgName + '.' + file.substring(0, file.length() - 6)));
                    } catch (final NoClassDefFoundError e) {
                        // do nothing. this class hasn't been found by the
                        // loader, and we don't care.
                    }
                } else if ((tmpDirectory = new File(directory, file)).isDirectory()) {
                    checkDirectory(tmpDirectory, pckgName + "." + file, classes);
                }
            }
        }
    }

    /**
     * @param connection the connection to the jar
     * @param pckgname the package name to search for
     * @param classes the current ArrayList of all classes. This method will simply add new classes.
     * @throws ClassNotFoundException if a file isn't loaded but still is in the jar file
     * @throws IOException if it can't correctly read from the jar file.
     */
    private static void checkJarFile(JarURLConnection connection, String pckgname, ArrayList<Class<?>> classes) throws ClassNotFoundException, IOException {

        final JarFile jarFile = connection.getJarFile();

        final Enumeration<JarEntry> entries = jarFile.entries();

        String name;

        for (JarEntry jarEntry = null; entries.hasMoreElements() && ((jarEntry = entries.nextElement()) != null);) {

            name = jarEntry.getName();

            if (name.contains(".class")) {

                name = name.substring(0, name.length() - 6).replace('/', '.');

                if (name.contains(pckgname)) {
                    classes.add(Class.forName(name));
                }
            }
        }
    }
}
