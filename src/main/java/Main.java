import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.*;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws Exception {
        GitService gitService = new GitServiceImpl();
        GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();

        Repository repo = gitService.cloneIfNotExists(
                "tmp/refactoring-toy-example",
                "https://github.com/danilofes/refactoring-toy-example.git");

        miner.detectAll(repo, "master", new RefactoringHandler() {
            @Override
            public void handle(String commitId, List<Refactoring> refactorings) {
                System.out.println("Refactoring at: " + commitId);
                for (Refactoring ref : refactorings) {
                    //System.out.println(ref.toString());
                    if(ref.getRefactoringType().equals(RefactoringType.MOVE_OPERATION)){
                        System.out.println("MOVE OPERATION DETECTED -->" + ref.toJSON());
                    }
                    if(ref.getRefactoringType().equals(RefactoringType.ADD_METHOD_ANNOTATION)){
                        System.out.println("METHOD ANNOTATION DETECTED -->" + ref.toJSON());
                    }
                    if(ref.getRefactoringType().equals(RefactoringType.REMOVE_METHOD_ANNOTATION)){
                        System.out.println("REMOVE METHOD ANNOTATION DETECTED -->" + ref.toJSON());
                    }
                    if(ref.getRefactoringType().equals(RefactoringType.PULL_UP_OPERATION)){
                        System.out.println("PULL UP OPERATION DETECTED -->" + ref.toJSON());
                    }
                    if(ref.getRefactoringType().equals(RefactoringType.PUSH_DOWN_OPERATION)){
                        System.out.println("PUSH DOWN OPERATION DETECTED -->" + ref.toJSON());
                    }
                    if(ref.getRefactoringType().equals(RefactoringType.MERGE_OPERATION)){
                        System.out.println("MERGE OPERATION DETECTED -->" + ref.toJSON());
                    }
                    if(ref.getRefactoringType().equals(RefactoringType.MOVE_AND_RENAME_OPERATION)){
                        System.out.println("MOVE AND RENAME OPERATION DETECTED -->" + ref.toJSON());
                    }
                    if(ref.getRefactoringType().equals(RefactoringType.MOVE_AND_INLINE_OPERATION)){
                        System.out.println("MOVE AND INLINE OPERATION DETECTED -->" + ref.toJSON());
                    }
                    if(ref.getRefactoringType().equals(RefactoringType.INLINE_OPERATION)){
                        System.out.println("INLINE OPERATION DETECTED -->" + ref.toJSON());
                    }
                    if(ref.getRefactoringType().equals(RefactoringType.EXTRACT_OPERATION)){
                        System.out.println("EXTRACT OPERATION DETECTED -->" + ref.toJSON());
                    }

                }

             /*     List<Refactoring> allMoveMethod = new ArrayList<Refactoring>();

                allMoveMethod.forEach(refactoring -> {
                    if (refactoring != null){
                        System.out.println(refactoring.getName());
                    }
                });

                allMoveMethod = refactorings.stream().map(refactoring -> refactoring.getRefactoringType() == RefactoringType.MOVE_OPERATION ? refactoring : null).collect(Collectors.toList());

                System.out.println(allMoveMethod.size());
            */

            }
        });
    }

}
