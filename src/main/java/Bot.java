import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
    List<User> userList = new ArrayList<>();

    public static void main(String[] args) {

        ApiContextInitializer.init();

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }


    public void sendMsg(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage().setChatId(chatId);
        sendMessage.setText(text);
        try {
            setButtons(sendMessage);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void onUpdateReceived(Update update) {
        String message = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        int userIndex = 0;
        for (int i = 0; i < userList.size(); i++) {
            if(update.getMessage().getChatId().equals(userList.get(i).getChatId())) {
                userIndex = i;
                break;
            }
        }

        if (message.equals("/start")) {
            boolean isExist = false;
            for (User user : userList) {
                if(update.getMessage().getChatId().equals(user.getChatId())) {
                    isExist = true;
                }
            }
            if(!isExist) {
                sendMsg(chatId, "UzGram - o'zbekcha ijtimoiy tarmoqqa xush kelibsiz!");
                User newUser = new User(update.getMessage().getChat().getFirstName(), update.getMessage().getChatId(), (long)0, "Oddiy", 0);
                userList.add(newUser);
                String text = EmojiParser.parseToUnicode("Siz muvaffaqiyatli ro'yxatdan o'tdingiz! :smile:");
                sendMsg(chatId, text);
            } else {
                sendMsg(chatId, "Siz allaqachon ro'yxatdan o'tgansiz!");
            }
        }
        else if(userList != null &&
                userList.get(userIndex).isViewUser()) {
            viewUser(update.getMessage().getChatId(), update.getMessage().getText());
        }
        else if (message.equals("/profile")) {
            int i = 0;
            int index = 0;
            for (User user : userList) {
                if (chatId.equals(user.getChatId())) {
                    index = i;
                    break;
                }
                i++;
            }
            sendMsg(chatId, "Sizning profilingiz:");
            sendMsg(chatId, "Nik: " + userList.get(index).getNickname() +
                    "\nBallaringiz: " + userList.get(index).getPoints() +
                    "\nDarajangiz: " + userList.get(index).getLevel() +
                    "\nDo'stlaringiz soni: " + userList.get(index).getFriends());
        }
        else if (message.equals("/users")) {
            int j = 1;
            String usersNames = "";
            for (User users : userList) {
                usersNames += (j++) + ". " + users.getNickname() + "\n";
            }
            sendMsg(chatId, usersNames);
        }
        else if(message.equals("/view_user_profile")) {
            int index = 0;
            int counter = 0;
            for (User user : userList) {
                if(update.getMessage().getChat().getFirstName().equals(user.getNickname())) {
                    index = counter;
                    break;
                }
                counter++;
            }
            userList.get(index).setViewUser(true);
            int j = 1;
            String usersNames = "";
            for (User users : userList) {
                usersNames += (j++) + ". " + users.getNickname() + "\n";
            }

            ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
            List<KeyboardRow> keyboard = new ArrayList<>();
            KeyboardRow row = new KeyboardRow();


            sendMsg(chatId, usersNames);
        }
        else if (message.equals("/about")) {
            sendMsg(chatId, "UzGram - o'zbekcha ijtimoiy tarmoq! Bu yerda siz yangi do'stlar orttirishingiz, " +
                    "do'stlaringiz bilan qiziqarli musobaqalar uyushtirishingiz, ajoyib vazifalarni bajarib " +
                    "ko'pgina foyda keltiruvchi ballarga ega bo'lishingiz mumkin!");
        }

        System.out.println(update.getMessage().getFrom().getFirstName() + " (@" +
                update.getMessage().getFrom().getUserName() + ") : " + message);
    }

    private void viewUser(Long chatId, String userNumber) {
        int userIndex = Integer.parseInt(userNumber) - 1;
        sendMsg(chatId, "Nik: " + userList.get(userIndex).getNickname() +
                "\nBallaringiz: " + userList.get(userIndex).getPoints() +
                "\nDarajangiz: " + userList.get(userIndex).getLevel() +
                "\nDo'stlaringiz soni: " + userList.get(userIndex).getFriends());
        userList.get(userIndex).setViewUser(false);
    }

    public void setButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow startRow = new KeyboardRow();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        KeyboardRow viewUserRow = new KeyboardRow();
        KeyboardRow keyboardThirdRow = new KeyboardRow();
        startRow.add(new KeyboardButton("/start"));
        keyboardFirstRow.add(new KeyboardButton("/profile"));
        keyboardSecondRow.add(new KeyboardButton("/users"));
        viewUserRow.add(new KeyboardButton("/view_user_profile"));
        keyboardThirdRow.add(new KeyboardButton("/about"));

        keyboardRowList.add(startRow);
        keyboardRowList.add(keyboardFirstRow);
        keyboardRowList.add(keyboardSecondRow);
        keyboardRowList.add(viewUserRow);
        keyboardRowList.add(keyboardThirdRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }

    public String getBotUsername() {
        return "uzgram_bot";
    }

    public String getBotToken() {
        return "1063965615:AAHBh_QFJIaC76ltUSGFbS117K6Us9f-DCs";
    }
}
