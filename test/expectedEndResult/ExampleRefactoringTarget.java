package expectedEndResult;

import apackage.User;

public class ExampleRefactoringTarget {

    UserService userService;

    public void action1() {
        User user = getAUser();
        // do something here
        userService.save(user);
    }

    public void action2() {
        User user = getAUserInAnotherWay();

        // do something else here
        userService.save(user);
    }

    private User getAUser() {
        return new User();
    }

    private User getAUserInAnotherWay() {
        return new User();
    }

}
