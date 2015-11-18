package com.oresteluci.scores.injection;

import com.oresteluci.scores.server.Server;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

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

        Class[] classes = getClasses(scanPackage);
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
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private static Class[] getClasses(String packageName) throws ClassNotFoundException, IOException {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();

        while (resources.hasMoreElements()) {
            URL resource = (URL)resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }

        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }

        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List findClasses(File directory, String packageName) throws ClassNotFoundException {

        List classes = new ArrayList();

        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();

        for (File file : files) {

            if (file.isDirectory()) {

                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));

            } else if (file.getName().endsWith(".class")) {

                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }

        return classes;
    }
}
