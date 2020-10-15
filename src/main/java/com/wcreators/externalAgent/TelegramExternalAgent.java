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
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
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
        try {
            execute(new SendMessage().setChatId(task.sendTo()).setText("pie " + task.toString()));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
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
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = getChatId(update);
            org.telegram.telegrambots.meta.api.objects.User tgUser = getUser(update);
            String[] arguments = update.getMessage().getText().split("\n");
            SendMessage message = new SendMessage()
                    .setChatId(update.getMessage().getChatId());

            switch (arguments[0]) {
                case "/reg":
                    message.setText(reg(chatId, tgUser.getFirstName(), tgUser.getLastName(), arguments));
                    break;
                case "/add":
                    message.setText(add(chatId, arguments));
                    break;
            }

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
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

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
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
