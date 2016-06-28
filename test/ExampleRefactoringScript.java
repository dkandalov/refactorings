public class ExampleRefactoringScript {

    // to apply to class ExampleRefactoringTarget
    void extractUserServiceFromExampleRefactoringTarget(){
        extractMethod("user.save()", "saveUser");
        introduceParameter("user", "saveUser", "user");
        makeStatic("saveUser");
        moveMethod("saveUser", "apackage.UserService");
        replaceStaticCallWithField("UserService", "userService");
        removeInitialization("userService");
    }

    private void extractMethod(String code, String methodName) {
        // do the extract method refactoring
    }

    private void introduceParameter(String parameterName, String methodName, String defaultValue) {
        // do the introduce parameter refactoring
    }

    private void makeStatic(String methodName) {
        // do the make static refactoring. This step is not necessary in java, but is required for groovy.
    }

    private void moveMethod(String methodName, String fullClassName) {
        // do the move method refactoring. If the method already exists, replace the calls with the call to the method (later).
    }

    private void replaceStaticCallWithField(String className, String fieldName) {
        // no refactoring exists that I know of. Not necessary in Java, but required for groovy.
    }

    // initialization done through dependency injection, but in tests need to add a mock
    private void removeInitialization(String fieldName) {
        // can use a mix of "separate initialization from declaration" refactoring and remove the line of code where initialization is done.
        // in tests, we typically need to add a line that stubs the new field
    }
}
