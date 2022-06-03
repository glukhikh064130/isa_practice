import exceptions.ArgsException;

public class Config {
    private static final String USAGE = "Usage: app <dbUrl> <dbUser> <dbPass>";
    private final String dbUrl;
    private final String dbUser;
    private final String dbPass;

    public Config(String dbUrl, String dbUser, String dbPass) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPass = dbPass;
    }

    public static Config parse(String[] args) throws ArgsException {
        if (args.length < 3) {
            throw new ArgsException(USAGE);
        }
        return new Config(args[0], args[1], args[2]);
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPass() {
        return dbPass;
    }
}
