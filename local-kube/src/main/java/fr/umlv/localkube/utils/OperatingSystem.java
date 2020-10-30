package fr.umlv.localkube.utils;

public interface OperatingSystem {
    String getSeparator();
    String getCMD();

    static OperatingSystem checkOS() {
        var os = System.getProperty("os.name");
        if (os.startsWith("Windows")) {
            return new Windows();
        } else {
            return new Unix();
        }
    }
}
