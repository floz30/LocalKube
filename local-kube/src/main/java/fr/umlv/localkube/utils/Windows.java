package fr.umlv.localkube.utils;

/**
 * Windows operating system.
 */
public class Windows implements OperatingSystem {
    @Override
    public String getCMD() {
        return "cmd.exe";
    }

    @Override
    public String getOption() {
        return "/c";
    }

}
