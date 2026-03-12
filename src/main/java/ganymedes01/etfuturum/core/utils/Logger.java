package ganymedes01.etfuturum.core.utils;

import cpw.mods.fml.common.FMLLog;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.lib.Reference;
import org.apache.logging.log4j.Level;

public class Logger {
	/*
	 * Taken from Village Names with permission from AstroTibs
	 */
	public static void log(Level logLevel, Object object) {
		FMLLog.log(Tags.MOD_NAME, logLevel, String.valueOf(object));
	}

	public static void all(Object object) {
		log(Level.ALL, object);
	}

	public static void debug(Object object) {
		if (Reference.DEV_ENVIRONMENT || Reference.SNAPSHOT_BUILD) {
			log(Level.DEBUG, object);
		}
	}

	public static void error(Object object) {
		log(Level.ERROR, object);
	}

	public static void fatal(Object object) {
		log(Level.FATAL, object);
	}

	public static void info(Object object) {
		log(Level.INFO, object);
	}

	public static void off(Object object) {
		log(Level.OFF, object);
	}

	public static void trace(Object object) {
		log(Level.TRACE, object);
	}

	public static void warn(Object object) {
		log(Level.WARN, object);
	}

}
