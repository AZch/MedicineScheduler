package com.wcreators.externalAgent;

import com.wcreators.common.annotations.*;
import com.wcreators.common.annotations.externalAgents.UsingExternalAgent;
import com.wcreators.db.dao.AgentDao;
import com.wcreators.db.dao.MedicineDao;
import com.wcreators.db.dao.UserDao;
import com.wcreators.db.dao.UserMedicineDao;
import com.wcreators.db.entities.Medicine;
import com.wcreators.db.entities.agent.Agent;
import com.wcreators.db.entities.agent.AgentType;
import com.wcreators.db.entities.User;
import com.wcreators.db.entities.userMedicine.ExecutionType;
import com.wcreators.db.entities.userMedicine.UserMedicine;
import com.wcreators.task.Task;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;
import static org.telegram.abilitybots.api.util.AbilityUtils.getUser;

@Singleton
@UsingExternalAgent
public class TelegramExternalAgent extends TelegramLongPollingBot implements ExternalAgent {
    @InjectProperty("TELEGRAM_API_TOKEN")
    private String token;

    @InjectProperty("TELEGRAM_BOT_USERNAME")
    private String username;

    @InjectByType
    private UserDao userDao;

    @InjectByType
    private MedicineDao medicineDao;

    @InjectByType
    private AgentDao agentDao;

    @InjectByType
    private UserMedicineDao userMedicineDao;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    private final Function<String, String> makeSelectCallbackData = title -> "/select\n" + title;
    private final Function<String, String> makeEditCallbackData = title -> "/edit\n" + title;
    private final Function<String, String> makeDeleteCallbackData = title -> "/delete\n" + title;
    private final Function<Task, String> makeDoneCallbackData = task -> "/done\n" + task.hashCode();
    private final Function<String, String[]> getArgumentsFromCommand = command -> command.split("\n");

    private final Map<Integer, Task> processingTask = new ConcurrentHashMap<>();

    @PostConstruct
    @SneakyThrows
    public void init() {
        TelegramBotsApi botsApi = new TelegramBotsApi();
        botsApi.registerBot(this);
    }

    private boolean isUserExist(String email) {
        return userDao.getByEmail(email) != null;
    }

    @Override
    public void sendEvent(Task task) {
        Arrays.stream(task.agentIdsByType(AgentType.telegram)).forEach(agentId -> {
            try {
                List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                List<InlineKeyboardButton> row = new ArrayList<>();
                row.add(
                    new InlineKeyboardButton()
                        .setText("Done")
                        .setCallbackData(makeDoneCallbackData.apply(task))
                );
                processingTask.put(task.hashCode(), task);
                rows.add(row);
                execute(
                    new SendMessage()
                        .setChatId(agentId)
                        .setText("It's time to " + task.toString())
                        .setReplyMarkup(new InlineKeyboardMarkup().setKeyboard(rows))
                );
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        });
    }

    private boolean registrationUser(String email, String firstName, String lastName, Long chatId) {
        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        userDao.create(user);

        Agent tgAgent = new Agent();
        tgAgent.setAgentType(AgentType.telegram);
        tgAgent.setUser(user);
        tgAgent.setAgentId(chatId);
        agentDao.create(tgAgent);

        return true;
    }

    private boolean addMedicine(User user, String title, List<Integer> times, ExecutionType executionType, int notifyEveryMinutes) {
        Medicine medicine = new Medicine();
        medicine.setTitle(title);
        medicineDao.create(medicine); // TODO check if medicine already exist

        UserMedicine userMedicine = new UserMedicine();
        userMedicine.setUser(user);
        userMedicine.setMedicine(medicine);
        userMedicine.setExecutionTimes(times);
        userMedicine.setExecutionType(executionType);
        userMedicine.setNotifyEveryMinutes(notifyEveryMinutes);

        userMedicineDao.create(userMedicine);

        return true;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = getChatId(update);
        SendMessage message = new SendMessage().setChatId(chatId);
        if (update.hasMessage() && update.getMessage().hasText()) {
            org.telegram.telegrambots.meta.api.objects.User tgUser = getUser(update);
            String[] arguments = getArgumentsFromCommand.apply(update.getMessage().getText());
            switch (arguments[0]) {
                case "/reg":
                    message.setText(reg(chatId, tgUser.getFirstName(), tgUser.getLastName(), arguments));
                    break;
                case "/add":
                    message.setText(add(chatId, arguments));
                    break;
                case "/all":
                    message.setText(allText()).setReplyMarkup(allReplayMarkup(chatId));
                    break;
                case "/edit":
                    message.setText(edit(chatId, arguments));
                    break;
                default:
                    message.setText("Sorry, command not exist");
            }
        } else if(update.hasCallbackQuery()) {
            String[] arguments = getArgumentsFromCommand.apply(update.getCallbackQuery().getData());
            switch (arguments[0]) {
                case "/done":
                    message.setText(doneCallbackCommand(arguments));
                    break;
                case "/select":
                    if (arguments.length < 2) {
                        message.setText("data inconsistency");
                        break;
                    }
                    List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                    List<InlineKeyboardButton> row = new ArrayList<>();
                    row.add(new InlineKeyboardButton().setText("Edit").setCallbackData(makeEditCallbackData.apply(arguments[1])));
                    row.add(new InlineKeyboardButton().setText("Delete").setCallbackData(makeDeleteCallbackData.apply(arguments[1])));
                    rows.add(row);
                    message
                        .setText("Selected: " + arguments[1])
                        .setReplyMarkup(new InlineKeyboardMarkup().setKeyboard(rows));
                    break;
                case "/edit":
                    if (arguments.length < 2) {
                        message.setText("data inconsistency");
                        break;
                    }
                    message.setText("Enter all:\n/edit\nold name\nnew name\nnew time\n");
                    break;
                case "/delete":
                    if (arguments.length < 2) {
                        message.setText("data inconsistency");
                        break;
                    }
                    medicineDao.delete(arguments[1], chatId, AgentType.telegram);
                    message.setText("Ok Boss");
                    break;
                default:
            }
        }
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String allText() {
        return "Your available medicine list:";
    }

    private String doneCallbackCommand(String[] arguments) {
        // 1 - hash code of task object
        if (arguments.length < 2) {
            return "data inconsistency";
        }
        int hashCode = Integer.parseInt(arguments[1]);
        Task task = processingTask.get(hashCode);
        task.done();
        return "Ok Boss";
    }

    private InlineKeyboardMarkup allReplayMarkup(Long chatId) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<Medicine> medicines = medicineDao.getMedicineListByUserAgent(AgentType.telegram, chatId);
        for (int i = 0; i < medicines.size(); i += 3) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int j = i; j < medicines.size() && j < i + 3; j++) {
                Medicine medicine = medicines.get(j);
                row.add(
                    new InlineKeyboardButton()
                        .setText(medicine.getTitle())
                        .setCallbackData(
                            makeSelectCallbackData.apply(medicine.getTitle())
                        )
                );
            }
            rows.add(row);
        }
        return new InlineKeyboardMarkup(rows);
    }

    private String reg(Long chatId, String firstName, String lastName, String[] args) {
        // 1 - email
        if (args.length != 2) {
            return "Not enough data";
        }
        String email = args[1];
        if (isUserExist(email)) {
            return "User with this email already exist";
        }
        if (!registrationUser(email, firstName, lastName, chatId)) {
            return "Something went wrong in registration";
        }
        return "You registered now!";
    }

    private ExecutionType getExecutionTypeFromString(String str) {
        switch (str) {
            case "ed": return ExecutionType.everyDay;
            case "ew": return ExecutionType.everyWeek;
            case "em": return ExecutionType.everyMonth;
            default: throw new Error("Does not know execution type " + str);
        }
    }

    private String edit(Long chatId, String[] args) {
        // 1 - old medicine name
        // 2 - new medicine name
        // 3 - new time executions
        // 4 - notify every
        if (args.length != 5) {
            return "Not enough data";
        }
        String oldMedicineTitle = args[1];
        Medicine medicine = medicineDao.getMedicineByTitleAgent(oldMedicineTitle, AgentType.telegram, chatId);
        if (medicine.getUserMedicines().size() != 1) {
            return "too much relations";
        }
        String newMedicineTitle = args[2];
        List<Integer> times = Arrays.stream(args[3].split(" ")).map(time -> LocalTime.parse(time, dateTimeFormatter).toSecondOfDay()).collect(Collectors.toList());
        Integer notifyEvery = Integer.parseInt(args[4]);
        if (!oldMedicineTitle.equals(newMedicineTitle)) {
            medicineDao.delete(oldMedicineTitle, chatId, AgentType.telegram);
            String[] argsAdd = new String[] {"/add", newMedicineTitle, args[3], "ed", args[4]};
            add(chatId, argsAdd);
        } else {
            medicine.getUserMedicines().iterator().next().setExecutionTimes(times);
            medicine.getUserMedicines().iterator().next().setNotifyEveryMinutes(notifyEvery);

            medicineDao.update(medicine);

        }

        return "Ok Boss";
    }

    private String add(Long chatId, String[] args) {
        // 1 - title
        // 2 - times
        // 3 - execution type
        // 4 - notify every
        if (args.length != 5) {
            return "Not enough data";
        }

        try {
            User user = agentDao.getUserByAgent(chatId);

            String title = args[1];
            List<Integer> times = Arrays.stream(args[2].split(" ")).map(time -> LocalTime.parse(time, dateTimeFormatter).toSecondOfDay()).collect(Collectors.toList());
            ExecutionType executionType = getExecutionTypeFromString(args[3]);
            int notifyEveryMinutes = Integer.parseInt(args[4]);

            return addMedicine(user, title, times, executionType, notifyEveryMinutes) ? "Success" : "Failure";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
