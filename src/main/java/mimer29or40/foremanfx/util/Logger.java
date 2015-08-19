package mimer29or40.foremanfx.util;

public class Logger
{
    private static void log(String logLevel, Object object)
    {
        System.out.println(String.format("[%s][ForemanFX]: %s", logLevel, String.valueOf(object)));
    }

    public static void all(Object object)
    {
        log(ALL, object);
    }

    public static void debug(Object object)
    {
        log(DEBUG, object);
    }

    public static void error(Object object)
    {
        log(ERROR, object);
    }

    public static void fatal(Object object)
    {
        log(FATAL, object);
    }

    public static void info(Object object)
    {
        log(INFO, object);
    }

    public static void infoS(String format, Object... args)
    {
        info(String.format(format, args));
    }

    public static void off(Object object)
    {
        log(OFF, object);
    }

    public static void trace(Object object)
    {
        log(TRACE, object);
    }

    public static void warn(Object object)
    {
        log(WARN, object);
    }

    public static final String OFF   = "OFF";
    public static final String FATAL = "FATAL";
    public static final String ERROR = "ERROR";
    public static final String WARN  = "WARN";
    public static final String INFO  = "INFO";
    public static final String DEBUG = "DEBUG";
    public static final String TRACE = "TRACE";
    public static final String ALL   = "ALL";
}
