package fr.umlv.localkube.utils;

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
