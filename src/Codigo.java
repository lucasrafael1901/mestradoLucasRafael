
import java.util.ArrayList;
import java.util.List;

public class Codigo {

    private String codeStatement;
    private String refactoringType;

    /*public Codigo(String codeStatement, String refactoringType){
        this.codeStatement = codeStatement;
        this.refactoringType = refactoringType;
    }*/

    public String getCodeStatement() {
        return codeStatement;
    }
    public void setCodeStatement(String codeStatement) {
        this.codeStatement = codeStatement;
    }
    public String getRefactoringType() {
        return refactoringType;
    }
    public void setRefactoringType(String refactoringType) {
        this.refactoringType = refactoringType;
    }
}