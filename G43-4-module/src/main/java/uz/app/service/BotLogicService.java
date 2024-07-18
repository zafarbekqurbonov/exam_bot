package uz.app.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import uz.app.Enum.State;
import uz.app.entity.Answer;
import uz.app.entity.CurrencyInfo;
import uz.app.entity.Test;
import uz.app.entity.User;
import uz.app.payload.InlineString;
import uz.app.util.Utils;

import java.io.IOException;
import java.lang.annotation.Target;
import java.net.URISyntaxException;
import java.util.*;

import static uz.app.Enum.State.*;
import static uz.app.util.Utils.*;

public class BotLogicService {
    private SendMessage sendMessage = new SendMessage();
    private SendMessage sendMessageToAdmin = new SendMessage();
    Db db = Db.getInstance();
    CurrencyService currencyService = new CurrencyService();
    BotService botService = BotService.getInstance();
    private final ReplyMarkupService replyService = new ReplyMarkupService();
    private final InlineMarkupService inlineService = new InlineMarkupService();


//    6436944940
//    6174277436


    public void messageHandler(Update update) throws Exception {
        User currentUser;
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        Optional<User> userById = db.getUserById(chatId.toString());
        if (userById.isEmpty()) {
            db.addUser(new User(update.getMessage().getChatId().toString(), MAIN, 0d, 0d, 0d, 0d, "", ""));
            currentUser = db.getUserById(update.getMessage().getChatId().toString()).get();
        } else currentUser = userById.get();

        if (currentUser.getState().equals(PAYNET)) {
            currentUser.setUSZ(currentUser.getUSZ() + chek(text));
            sendMessage.setText("Completed");
            sendMessage.setChatId(currentUser.getId());
            sendMessage.setReplyMarkup(replyService.keyboardMaker(mainMenu));
            currentUser.setState(MAIN);
            botService.executeMessages(sendMessage);
            return;
        }
        if (currentUser.getState().equals(FIRST)) {
            currentUser.setFirst(text);
            sendMessage.setText("Choose any");
            sendMessage.setChatId(currentUser.getId());
            sendMessage.setReplyMarkup(replyService.keyboardMaker(conver));
            currentUser.setState(SECOND);
            sendMessage.setReplyMarkup(replyService.keyboardMaker(new String[][]{}));
            botService.executeMessages(sendMessage);
            return;
        }
        if (currentUser.getState().equals(AMOUNT)) {
            setBalance(currentUser, chek(text));
            return;
        }
        if (currentUser.getState().equals(SECOND)) {
            sendMessage.setText("Enter amount");
            sendMessage.setChatId(currentUser.getId());
            currentUser.setState(AMOUNT);
            currentUser.setSecond(text);

            botService.executeMessages(sendMessage);
            return;

        }
        if (currentUser.getState().equals(WITHROW)) {
            Double usz = currentUser.getUSZ();
            Double chek = chek(text);
            if (usz < chek) {
                sendMessage.setText("Mablag yetarli emas");
                sendMessage.setChatId(currentUser.getId());
                sendMessage.setReplyMarkup(replyService.keyboardMaker(mainMenu));
                botService.executeMessages(sendMessage);
                return;
            }
            currentUser.setUSZ(usz - chek);
            currentUser.setState(MAIN);
            sendMessage.setReplyMarkup(replyService.keyboardMaker(mainMenu));
            sendMessage.setChatId(currentUser.getId());
            sendMessage.setText("Completed");
            botService.executeMessages(sendMessage);
        }
        switch (text) {
            case "/start" -> {
                sendMessage.setText("Welcome to bot");
                db.addUser(new User(update.getMessage().getChatId().toString(), MAIN, 0d, 0d, 0d, 0d, "", ""));
                sendMessage.setChatId(update.getMessage().getChatId());
                sendMessage.setReplyMarkup(replyService.keyboardMaker(mainMenu));
                botService.executeMessages(sendMessage);
            }
            case CARD -> {
                StringBuilder sb = new StringBuilder();
                sb.append("\nUZS = ");
                sb.append(currentUser.getUSZ());
                sb.append("\nUSD = ");
                sb.append(currentUser.getUSD());
                sb.append("\nRUB = ");
                sb.append(currentUser.getRUB());
                sb.append("\nEUR = ");
                sb.append(currentUser.getEUR());
                SendMessage sendMessage1 = new SendMessage();
                sendMessage1.setText(sb.toString());
                sendMessage1.setChatId(currentUser.getId());
                botService.executeMessages(sendMessage1);
                sendMessage.setText("Cards");
                sendMessage.setReplyMarkup(replyService.keyboardMaker(card));
                botService.executeMessages(sendMessage);
            }
            case CURRENCIES -> {
                StringBuilder sb = new StringBuilder();
                CurrencyInfo[] currencyUSD = currencyService.getCurrencyByName(USD);
                CurrencyInfo[] currencyEUR = currencyService.getCurrencyByName(EUR);
                CurrencyInfo[] currencyRUB = currencyService.getCurrencyByName(RUB);
                ArrayList<CurrencyInfo> f = new ArrayList<>();
                f.addAll(List.of(currencyRUB));
                f.addAll(List.of(currencyEUR));
                f.addAll(List.of(currencyUSD));
                for (CurrencyInfo currencyInfo : f) {
                    sb.append(currencyInfo.getCcy());
                    sb.append(" -> ");
                    sb.append(currencyInfo.getRate());
                    sb.append("\n");
                }
                sendMessage.setText(sb.toString());
                sendMessage.setChatId(chatId);
                sendMessage.setReplyMarkup(replyService.keyboardMaker(new String[][]{}));
                botService.executeMessages(sendMessage);

            }
            case DEP -> {
                sendMessage.setText("Enter amount");
                sendMessage.setText(currentUser.getId());
                sendMessage.setReplyMarkup(replyService.keyboardMaker(new String[][]{}));
                currentUser.setState(PAYNET);
                botService.executeMessages(sendMessage);
            }
            case WITH -> {
                sendMessage.setText("Enter amount");
                sendMessage.setChatId(currentUser.getId());
                currentUser.setState(WITHROW);
                sendMessage.setReplyMarkup(replyService.keyboardMaker(new String[][]{}));
                botService.executeMessages(sendMessage);
            }
            case CONV -> {
                SendMessage sendMessage1 = new SendMessage();
                sendMessage1.setText("Choose any");
                sendMessage1.setChatId(currentUser.getId());
                sendMessage1.setReplyMarkup(replyService.keyboardMaker(conver));
                currentUser.setState(FIRST);
                botService.executeMessages(sendMessage1);
            }
            case "back"->{
                sendMessage.setChatId(currentUser.getId());
                sendMessage.setReplyMarkup(replyService.keyboardMaker(mainMenu));
                botService.executeMessages(sendMessage);
            }

        }

    }

    public void setBalance(User currentUser, Double amont) throws Exception {
        Double USD = currencyService.getRate(Utils.USD);
        Double UZS = currencyService.getRate(Utils.UZS);
        Double RUB = currencyService.getRate(Utils.RUB);
        Double EUR = currencyService.getRate(Utils.EUR);
        switch (currentUser.getFirst()) {
            case Utils.USD -> {
                switch (currentUser.getSecond()) {
                    case Utils.USD -> {

                    }
                    case Utils.UZS -> {
                        currentUser.setUSZ(currentUser.getUSZ() + (amont * USD));
                        currentUser.setUSD(currentUser.getUSD() - amont);
                    }
                    case Utils.RUB -> {
                        currentUser.setRUB(currentUser.getRUB() + (amont * (USD / RUB)));
                        currentUser.setUSD(currentUser.getUSD() - amont);
                    }
                    case Utils.EUR -> {
                        currentUser.setEUR(currentUser.getEUR() + (amont * (USD / EUR)));
                        currentUser.setUSD(currentUser.getUSD() - amont);
                    }
                }
            }
            case Utils.UZS -> {
                switch (currentUser.getSecond()) {
                    case Utils.USD -> {
                        currentUser.setUSD(currentUser.getUSD() + (amont * (1 / USD)));
                        currentUser.setUSZ(currentUser.getUSZ() - amont);
                    }
                    case Utils.UZS -> {
                    }
                    case Utils.RUB -> {
                        currentUser.setRUB(currentUser.getRUB() + (amont * (1 / RUB)));
                        currentUser.setUSZ(currentUser.getUSZ() - amont);
                    }
                    case Utils.EUR -> {
                        currentUser.setEUR(currentUser.getEUR()+(amont*(1/EUR)));
                        currentUser.setUSZ(currentUser.getUSZ() - amont);
                    }
                }
            }
            case Utils.RUB -> {
                switch (currentUser.getSecond()) {
                    case Utils.USD -> {
                        currentUser.setUSD(currentUser.getUSD()+(amont*(RUB/USD)));
                        currentUser.setRUB(currentUser.getRUB() - amont);
                    }
                    case Utils.UZS -> {
                        currentUser.setUSZ(currentUser.getUSZ()+(amont*RUB));
                        currentUser.setRUB(currentUser.getRUB() - amont);
                    }
                    case Utils.RUB -> {
                    }
                    case Utils.EUR -> {
                        currentUser.setEUR(currentUser.getEUR()+(amont*(RUB/EUR)));
                        currentUser.setRUB(currentUser.getRUB() - amont);
                    }
                }
            }
            case Utils.EUR -> {
                switch (currentUser.getSecond()) {
                    case Utils.USD -> {
                        currentUser.setUSD(currentUser.getUSD()+(amont*(USD/EUR)));
                        currentUser.setEUR(currentUser.getEUR() - amont);
                    }
                    case Utils.UZS -> {
                        currentUser.setUSZ(currentUser.getUSZ()+(amont*EUR));
                        currentUser.setEUR(currentUser.getEUR() - amont);
                    }
                    case Utils.RUB -> {
                        currentUser.setRUB(currentUser.getRUB()+(amont*(EUR/RUB)));
                        currentUser.setEUR(currentUser.getEUR() - amont);
                    }
                    case Utils.EUR -> {
                    }
                }
            }

        }
        currentUser.setState(MAIN);
        sendMessage.setText("Complted");
        sendMessage.setReplyMarkup(replyService.keyboardMaker(mainMenu));
        sendMessage.setChatId(currentUser.getId());
        botService.executeMessages(sendMessage);
    }

    private Double chek(String text) {
        try {
            Double v = Double.valueOf(text);
            return v;
        } catch (Exception e) {
            return 0.0;
        }
    }


    //    public void addAdmin(Long id){
//        admins.put(id,"main");
//    }
    public void callbackHandler(Update update) {
    }

//    public void adminHandler(Long adminid, Update update){
//        String text = update.getMessage().getText();
//        sendMessageToAdmin.setChatId(adminid);
//        switch (text){
//            case "/start"->{
//               sendMessageToAdmin.setText("Welcome Admin");
//               sendMessageToAdmin.setReplyMarkup(replyService.keyboardMaker(mainMenuAdmin));
//               botService.executeMessages(sendMessageToAdmin);
//            }
//            case ADD_QUESTION -> {
//                sendMessageToAdmin.setText("Question kiriting: ");
//                botService.executeMessages(sendMessageToAdmin);
//                admins.put(adminid,"question_name");
//            }
//            case SHOW_QUESTIONS -> {
//                StringBuilder fullList = getStringBuilder();
//                sendMessageToAdmin.setText(fullList.toString());
//                botService.executeMessages(sendMessageToAdmin);
//            }
//            case DELETE_QUESTION -> {
//                StringBuilder fullList = getStringBuilder();
//                sendMessageToAdmin.setText(fullList.toString() + "\n \n Savol Raqamni Tanlang Tanlang : ");
//                botService.executeMessages(sendMessageToAdmin);
//                admins.put(adminid,"chooseQ");
//            }
//            case "back"->{
////                sendMessageToAdmin.setText("back endi");
//                sendMessageToAdmin.setReplyMarkup(replyService.keyboardMaker(mainMenuAdmin));
//                botService.executeMessages(sendMessageToAdmin);
//            }
//            default -> {
//                stateAdminHandler(adminid,update);
//            }
//        }
//    }
//
//    private StringBuilder getStringBuilder() {
//        ArrayList<Test> tests = db.getTests();
//        StringBuilder fullList = new StringBuilder();
//        int i=1;
//        for (Test test : tests) {
//            String question = "\n"+(i++)+") Savol : " + test.getQuestion() + "\n";
//            String answers = "A)" + test.getAnswers().get(0).toStringforAdmin() +"\nB) " + test.getAnswers().get(1).toStringforAdmin() +
//                    "\nD) " + test.getAnswers().get(2).toStringforAdmin() + "\nC) " + test.getAnswers().get(3).toStringforAdmin();
//            fullList.append(question);
//            fullList.append(answers);
//        }
//        return fullList;
//    }
//
//    private void stateAdminHandler(Long adminI, Update update) {
//        String text = update.getMessage().getText();
//        String s = admins.get(adminI);
//        switch (s){
//            case "question_name"->{
//                questionField.add(text);
//                sendMessageToAdmin.setText("To'g'ri Javobni kiriting: ");
//                botService.executeMessages(sendMessageToAdmin);
//                admins.put(adminI,"answer1");
//            }
//            case "answer1"->{
//                questionField.add(text);
//                sendMessageToAdmin.setText("enter 2 answer: ");
//                botService.executeMessages(sendMessageToAdmin);
//                admins.put(adminI,"answer2");
//            }
//            case "answer2"-> {
//                questionField.add(text);
//                sendMessageToAdmin.setText("enter 3 answer: ");
//                botService.executeMessages(sendMessageToAdmin);
//                admins.put(adminI, "answer3");
//            }
//            case "answer3"->{
//                questionField.add(text);
//                sendMessageToAdmin.setText("enter 4 answer: ");
//                botService.executeMessages(sendMessageToAdmin);
//                admins.put(adminI,"answer4");
//            }
//            case "answer4"->{
//                questionField.add(text);
//                Test test1 = createQuiz(questionField.get(0),questionField.get(1),questionField.get(2),questionField.get(3),questionField.get(4));
//                db.addQuestion(test1);
//                sendMessageToAdmin.setText("finish add Question");
//                sendMessageToAdmin.setReplyMarkup(replyService.keyboardMaker(new String[][]{{"add question","back"}}));
//                botService.executeMessages(sendMessageToAdmin);
//                admins.put(adminI,"main");
//            }
//            case "chooseQ" ->{
//                db.deleteQuestion(Integer.parseInt(text)-1);
//                sendMessageToAdmin.setText("Savol O'chirildi.");
//                sendMessageToAdmin.setReplyMarkup(replyService.keyboardMaker(new String[][]{{DELETE_QUESTION,"back"}}));
//                botService.executeMessages(sendMessageToAdmin);
//                admins.put(adminI,"main");
//            }
//            default -> {
//                sendMessageToAdmin.setText("finish");
//                sendMessageToAdmin.setReplyMarkup(replyService.keyboardMaker(new String[][]{{"add question","back"}}));
//                botService.executeMessages(sendMessageToAdmin);
//            }
//
//        }
//
//    }
//
//    private Test createQuiz(String question, String answer1, String answer2, String answer3, String answer4) {
//        int trueAnswerNum = new Random().nextInt(1,5);
//        List<Answer> answers = new ArrayList<>();
//
//        switch (trueAnswerNum){
//            case 1->{
//                answers.add(new Answer(answer1,true));
//                answers.add(new Answer(answer2,false));
//                answers.add(new Answer(answer3,false));
//                answers.add(new Answer(answer4,false));
//
//            }case 2->{
//                answers.add(new Answer(answer2,false));
//                answers.add(new Answer(answer1,true));
//                answers.add(new Answer(answer3,false));
//                answers.add(new Answer(answer4,false));
//            }case 3->{
//                answers.add(new Answer(answer2,false));
//                answers.add(new Answer(answer3,false));
//                answers.add(new Answer(answer1,true));
//                answers.add(new Answer(answer4,false));
//            }case 4->{
//                answers.add(new Answer(answer2,false));
//                answers.add(new Answer(answer3,false));
//                answers.add(new Answer(answer4,false));
//                answers.add(new Answer(answer1,true));
//            }
//        }
//
//        return new Test(question,answers);
//    }


    private static BotLogicService botLogicService;

    public static BotLogicService getInstance() {
        if (botLogicService == null) {
            botLogicService = new BotLogicService();
//            botLogicService.admins.put(6436944940l,"main");
//            botLogicService.admins.put(5661993330l,"main");
//            botLogicService.admins.put(787921993l,"main");
//            botLogicService.admins.put(5661993330l,"main");
////            1332358480
////            botLogicService.admins.put(6174277436l,"main");

        }
        return botLogicService;
    }

}
