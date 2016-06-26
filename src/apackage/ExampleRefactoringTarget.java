package apackage;

public class ExampleRefactoringTarget {

    public void action1(){
        User user = getAUser();
        // do something here
        user.save();
    }

    public void action2(){
        User user = getAUserInAnotherWay();

        // do something else here
        user.save();
    }

    private User getAUser() {
        return new User();
    }

    private User getAUserInAnotherWay() {
        return new User();
    }
}
