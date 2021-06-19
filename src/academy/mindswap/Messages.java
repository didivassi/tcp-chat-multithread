package academy.mindswap;

public final class Messages {

    public static final String WELCOME = "Welcome to MindSwapChat\n";
    public static final String GREETING = "Hello ";
    public static final String GOODBYE = "GoodBye ";
    public static final String INPUT_NAME = "Please input your name";
    public static final String USER_LIST = "MindSwappers connected\n";

    public static final String COMMAND_NOT_EXISTS = "Command does not exists\n";
    public static final String MESSAGE_NOT_PROVIDED = "You didn't provide a message to send\n";
    public static final String USER_NOT_EXISTS = "The MindSwapper does not exists\n";
    public static final String USER_ALREADY_EXISTS = "The MindSwapper already exists\n";
    public static final String HELP_HELP = "use /help to get all available commands\n";
    public static final String WHISPER_HELP = "use /whisper username message "+
                                                "to send a message just for that MindSwappers\n";
    public static final String QUIT_HELP = "use /quit to exit\n";
    public static final String LIST_HELP = "use /list to get all users\n";
    public static final String FULL_HELP = "\nThis is how we do it\n"+
                                        HELP_HELP+
                                        QUIT_HELP+
                                        LIST_HELP+
                                        WHISPER_HELP;

}
