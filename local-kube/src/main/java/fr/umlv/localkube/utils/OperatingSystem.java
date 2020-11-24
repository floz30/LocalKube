package fr.umlv.localkube.utils;

/**
 * This interface contains methods related to the operating system used.
 */
public interface OperatingSystem {
    /**
     * Return a string containing path separator according to the OS.
     * @return the resulting string
     */
    String getSeparator();

    /**
     * Return a string containing command to open console according to the OS.
     * @return the resulting string
     */
    String getCMD();

    /**
     * Return a string containing parent path according to the OS.
     * @return the resulting string
     */
    String getParent();

    /**
     * Return a string containing an option for command according to the OS.
     * @return the resulting string
     */
    String getOption();

    String getHostOption();

    /**
     * Checks on which OS this program is running.
     * @return a new object which represents the OS (ex: Windows, Unix)
     */
    static OperatingSystem checkOS() {
        var os = System.getProperty("os.name");
        if (os.startsWith("Windows")) {
            return new Windows();
        } else {
            return new Unix();
        }
    }
}
