package ninja.plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.google.inject.Singleton;

@Singleton
public class PluginRegistrator implements NinjaPluginHooksInterface {

    private List<NinjaPluginHooksInterface> plugins;

    public PluginRegistrator() {
        plugins = new ArrayList<NinjaPluginHooksInterface>();
    }

    public void registerPlugins() {

        Reflections reflections =
                new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage("")).setScanners(new TypeAnnotationsScanner()));
        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(NinjaPlugin.class);
        Iterator<Class<?>> iterator = typesAnnotatedWith.iterator();
        while(iterator.hasNext()) {
            Class<?> plugin = iterator.next();
            NinjaPlugin ninjaPluginAnnotation = (NinjaPlugin) plugin.getAnnotation(NinjaPlugin.class);
            if (ninjaPluginAnnotation.autoRegister()) {
                registerPlugin(plugin, ninjaPluginAnnotation);
            }
        }
    }

    public boolean registerPlugin(Class clazz) {
        NinjaPlugin ninjaPluginAnnotation = (NinjaPlugin) clazz.getAnnotation(NinjaPlugin.class);
        return registerPlugin(clazz, ninjaPluginAnnotation);
    }

    private boolean registerPlugin(Class clazz, NinjaPlugin ninjaPluginAnnotation) {
        // check if class has desired superclass for a plugin
        if (ninjaPluginAnnotation.enabled()) {
            if (clazz.getSuperclass().getName().contentEquals(NinjaPluginHooks.class.getName())) {
                try {
                    plugins.add((NinjaPluginHooksInterface) clazz.newInstance());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                    return false;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }

    public boolean unregisterPlugin(Class clazz) {
        return true;
    }

    @Override
    public void exampleHook() {
        // TODO Auto-generated method stub

    }
}
