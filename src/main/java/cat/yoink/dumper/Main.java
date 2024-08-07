package cat.yoink.dumper;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Mod(modid = "dumper")
public class Main
{
    private final Logger logger = LogManager.getLogger("Dumper");

    @SuppressWarnings("unchecked")
    @Mod.EventHandler
    public void initialize(final FMLInitializationEvent event) throws NoSuchFieldException, IOException, IllegalAccessException
    {
        this.logger.info("Dumping class loader...");

        final Field field = LaunchClassLoader.class.getDeclaredField("resourceCache");
        field.setAccessible(true);
        final Map<String, byte[]> loader = (Map<String, byte[]>) field.get(Launch.classLoader);

        final File file = new File(System.getenv("USERPROFILE") + "\\Desktop\\dump.jar"); /* Desktop */
        final ZipOutputStream stream = new ZipOutputStream(new FileOutputStream(file));

        loader.forEach((name, bytes) -> {
            final ZipEntry entry = new ZipEntry(name.replace(".", "/") + ".class");

            try
            {
                stream.putNextEntry(entry);

                stream.write(bytes);
                stream.closeEntry();
            }
            catch (final Exception ex)
            {
                this.logger.info("Failed to dump " + name.replace("/", "."));
            }
        });

        stream.closeEntry();

        this.logger.info("Finished dumping classloader");
    }
}
