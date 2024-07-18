package uz.app;

import lombok.Data;
import lombok.Getter;
import uz.app.entity.Answer;
import uz.app.entity.Test;
import uz.app.entity.User;
import uz.app.service.BotLogicService;

import java.util.*;

@Data
//@Getter
public class Db {
    @Getter
    private static ArrayList<Test> tests = new ArrayList<>();
    @Getter
    private static Set<User> users = new HashSet<>();
    private Db (){}
    public void addQuestion(Test test){
        tests.add(test);
    }
    public static void setTests(ArrayList<Test> tests) {
        Db.tests = tests;
    }

    public static void setUsers(Set<User> users) {
        Db.users = users;
    }

    public Optional<User> getUserById(String chatId) {
        for (User user : users) {
            if(user.getId().equals(chatId)){
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    private static Db db;

    public static Db getInstance() {
        if (db == null) {
            db=new Db();
        }
        return db;
    }

    public void deleteQuestion(int i) {
        tests.remove(i);
    }

    public void addUser(User main) {
        users.add(main);
    }
}
