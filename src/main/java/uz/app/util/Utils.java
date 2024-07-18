package uz.app.util;

public interface Utils {

    String CARD = "card";
    String CURRENCIES="currencies";
    //    String SHOW="show";
    String[][] mainMenu = {
            {CARD, CURRENCIES},
    };
    String DEP="dep";
    String WITH="with";
    String CONV="conv";
    String[][] card={
            {DEP,WITH,CONV},
            {"back"}
    };

    String UZS="uzs";
    String USD="usd";
    String RUB="rub";
    String EUR="eur";
    String[][] conver={
            {USD,UZS},
            {RUB,EUR}
    };
//    String ADD_ADMIN = "Add admin";
//    String ADD_QUESTION = "add question";
//    String DELETE_QUESTION = "delete question";
//    String SHOW_QUESTIONS = "show question";
//    String[][] mainMenuAdmin = {
//            {ADD_QUESTION},
//            {DELETE_QUESTION,SHOW_QUESTIONS}
//    };
//
//    String FILIALS = "filials";
//    String PRODUCTS = "products";
//    String MAIN_ASKED = "main asked";
//    String WORKING_DAYS = "working days";
//    String BACK = "back";
//    String WORKING_HOURS = "working hours";
//    String[][] info = {
//            {FILIALS, PRODUCTS, MAIN_ASKED},
//            {WORKING_DAYS, WORKING_HOURS},
//            {BACK}
//    };


}
