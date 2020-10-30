package fr.umlv.localkube.utils;

public class Windows implements OperatingSystem {

    @Override
    public String getSeparator() {
        return "\\\\";
    }
}
