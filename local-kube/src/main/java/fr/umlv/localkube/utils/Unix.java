package fr.umlv.localkube.utils;

public class Unix implements OperatingSystem {

    @Override
    public String getCMD() {
        return "bash";
    }

    @Override
    public String getOption() {
        return "-c";
    }

    @Override
    public String getHostOption(String option) {
        return option + " host.docker.internal:$(ip addr show docker0 | grep -Po 'inet \\K[\\d.]+')";
    }

    @Override
    public String getWlo1IpAddress() {
        return "--advertise-addr $(ip -4 addr show wlo1 | grep -oP '(?<=inet\\s)\\d+(.\\d+){3}')";
    }
}
