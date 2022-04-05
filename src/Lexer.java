import java.util.List;
import java.util.ArrayList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class Lexer{


    public static int bloco = 0;
    public static int linha = 1;
    public static int auxiliarLinha = 0;
    public static boolean comentarioComposto;
    public static boolean comentarioSimples;


    public static enum Type {
        // This Scheme-like language has three token types:
        // open parens, close parens, and an "atom" type
        LPAREN, RPAREN, ATOM, SUM, MINUS, TIMES, DIV, MODULE, EQUAL, DOT, LBRACK, RBRACK, LKEY, RKEY, QUOTE, LESSTHEN, MORETHEN, COMMA, SEMICOLON, COMMENT, ENDCOMMENT, OR, AND, ESCAPE, CIRC, EXCLAMATION, CIF, UNDERLINE, SINGLEQUOTE, ARROBA, HASH, INTERROGATION, TWOPOINT;
    }

    public static class Token {
        public final Type t;
        public final String c; // contents mainly for atom tokens
        public final Integer l;

        // could have column and line number fields too, for reporting errors later
        public Token(Type t, String c, Integer l) {
            this.t = t;
            this.c = c;
            this.l = l;
        }

        public String toString() {
            if (t == Type.ATOM) {
                return c ;
            }
            return t.toString();
        }
    }

    /*
     * Given a String, and an index, get the atom starting at that index
     */
    public static String getAtom(String s, int i) {
        int j = i;
        char asterisco = '*';
        char barraComentario = '/';
        char doubleQuote = '"';
        char quebraLinha = '\n';
        char site = 'w';
        boolean booleanDoubleQuote = false;
        int blocoDoubleQuote = 0;
        boolean comentarioMultilinha = false;

        for (; j < s.length(); ) {

            if((Character.compare(s.charAt(j), doubleQuote) == 0 )){
                booleanDoubleQuote = true;
                if(blocoDoubleQuote%2==0){
                    blocoDoubleQuote++;
                }else{
                    blocoDoubleQuote--;
                    bloco = 0;
                }
            }else{
                booleanDoubleQuote = false;
            }
            if(((Character.compare(s.charAt(j), barraComentario)) == 0) && (j+1 < s.length())){
                    if(Character.compare(s.charAt(j+1), barraComentario) == 0 ){
                        //if(Character.compare(s.charAt(j+2), site) != 0 )
                        comentarioSimples = true;
                    }
            }
            if((Character.compare(s.charAt(j), quebraLinha) == 0 )){
                comentarioSimples = false;
            }
            if((Character.compare(s.charAt(j), barraComentario) == 0 && (j+1 < s.length()))){
                if(Character.compare(s.charAt(j+1), asterisco) == 0){
                    comentarioMultilinha = true;
                    comentarioComposto = true;
                }
            }

            if (Character.isLetter(s.charAt(j)) || Character.isDigit(s.charAt(j)) || booleanDoubleQuote || comentarioSimples || comentarioComposto || comentarioMultilinha || (Character.isWhitespace(s.charAt(j)) && blocoDoubleQuote%2==1) ){
                //System.out.print(s.charAt(j));
                if((Character.compare(s.charAt(j), asterisco) == 0 && (j+1 < s.length()))){
                    if(Character.compare(s.charAt(j+1), barraComentario) == 0 ){
                        //System.out.print("entrei aqui agora\n");
                        comentarioMultilinha = false;
                        comentarioComposto = false;
                        j++;
                    }
                }

                j++;
            }else if((blocoDoubleQuote%2==1 && !(Character.isLetter(s.charAt(j))) && bloco != 0)) {
                //System.out.print(s.charAt(j));
                j++;
            }
            else {
                //System.out.print(s.charAt(j));
                return s.substring(i, j);
            }
        }
        comentarioSimples = false;
        return s.substring(i, j);
    }

    public static List<Token> Lex(String input) {

        char singleQuote = '\'';
        char doubleQuote = '"';
        char quebraLinha = '\n';
        List<Token> result = new ArrayList<Token>();
        for (int i = 0; i < input.length(); ) {
            switch (input.charAt(i)) {
                case '(':
                    if(bloco == 1|| comentarioComposto){
                        String atom = getAtom(input, i);
                        i += atom.length();
                        result.add(new Token(Type.ATOM, atom, linha));
                    }else{
                        result.add(new Token(Type.LPAREN, "(", linha));
                        i++;
                    }
                    break;
                case ')':
                    if(bloco == 1|| comentarioComposto){
                        String atom = getAtom(input, i);
                        i += atom.length();
                        result.add(new Token(Type.ATOM, atom, linha));
                    }else {
                        result.add(new Token(Type.RPAREN, ")", linha));
                        i++;
                    }
                    break;
                case '+':
                    if(bloco == 1|| comentarioComposto){
                        String atom = getAtom(input, i);
                        i += atom.length();
                        result.add(new Token(Type.ATOM, atom, linha));
                    }else{
                        result.add(new Token(Type.SUM, "+", linha));
                        i++;
                    }
                    break;
                case '-':
                    if(bloco == 1|| comentarioComposto){
                        String atom = getAtom(input, i);
                        i += atom.length();
                        result.add(new Token(Type.ATOM, atom, linha));
                    }else{
                        result.add(new Token(Type.MINUS, "-", linha));
                        i++;
                    }
                    break;
                case '*':
                   // System.out.print("oi9");
                    if(bloco == 1 || comentarioComposto){
                        String atom = getAtom(input, i);
                        i += atom.length();
                        result.add(new Token(Type.ATOM, atom, linha));
                    }else{
                        result.add(new Token(Type.TIMES, "*", linha));
                        i++;
                    }
                    break;
                case '%':
                    if(bloco == 1|| comentarioComposto){
                        String atom = getAtom(input, i);
                        i += atom.length();
                        result.add(new Token(Type.ATOM, atom, linha));
                    }else{
                        result.add(new Token(Type.MODULE, "%", linha));
                        i++;
                    }
                    break;
                case '/':
                    if(bloco == 1|| comentarioComposto){
                        String atom = getAtom(input, i);
                        i += atom.length();
                        result.add(new Token(Type.ATOM, atom, linha));
                    }else{
                        if(i+1 < input.length()){
                            if ((input.charAt(i + 1) == '*' || input.charAt(i + 1) == '/')) {
                               // result.add(new Token(Type.COMMENT, "/", linha));
                                auxiliarLinha = linha;
                                String atom = getAtom(input, i);
                                i += atom.length();
                                result.add(new Token(Type.ATOM, atom, auxiliarLinha));
                            }
                        }else{
                            if ((input.charAt(i-1) != '*')){
                                result.add(new Token(Type.DIV, "/", linha));
                            }

                        }
                        i++;
                    }
                    break;
                case '=':
                    if(bloco == 1|| comentarioComposto){
                        String atom = getAtom(input, i);
                        i += atom.length();
                        result.add(new Token(Type.ATOM, atom, linha));
                    }else{
                        result.add(new Token(Type.EQUAL, "=", linha));
                        i++;
                    }
                    break;
                case '&':
                    if(bloco == 1|| comentarioComposto){
                        String atom = getAtom(input, i);
                        i += atom.length();
                        result.add(new Token(Type.ATOM, atom, linha));
                    }else{
                        result.add(new Token(Type.AND, "&", linha));
                        i++;
                    }
                    break;
                case '|':
                    if(bloco == 1|| comentarioComposto){
                        String atom = getAtom(input, i);
                        i += atom.length();
                        result.add(new Token(Type.ATOM, atom, linha));
                    }else{
                        result.add(new Token(Type.OR, "|", linha));
                        i++;
                    }
                    break;
                case '?':
                    if(bloco == 1|| comentarioComposto){
                        String atom = getAtom(input, i);
                        i += atom.length();
                        result.add(new Token(Type.ATOM, atom, linha));
                    }else {
                        result.add(new Token(Type.INTERROGATION, "?", linha));
                        i++;
                    }
                    break;
                case ':':
                    if(bloco == 1|| comentarioComposto){
                        String atom = getAtom(input, i);
                        i += atom.length();
                        result.add(new Token(Type.ATOM, atom, linha));
                    }else{
                        result.add(new Token(Type.TWOPOINT, ":", linha));
                        i++;
                    }
                    break;
                case '\\':
                    if(bloco == 1|| comentarioComposto){
                        String atom = getAtom(input, i);
                        i += atom.length();
                        result.add(new Token(Type.ATOM, atom, linha));
                    }else{
                        result.add(new Token(Type.ESCAPE, "\\", linha));
                        i++;
                    }
                    break;
                case '.':
                    if(bloco == 1|| comentarioComposto){
                        String atom = getAtom(input, i);
                        i += atom.length();
                        result.add(new Token(Type.ATOM, atom, linha));
                    }else{
                        result.add(new Token(Type.DOT, ".", linha));
                        i++;
                    }
                    break;
                case '\'':
                    result.add(new Token(Type.SINGLEQUOTE, "\'", linha));
                    i++;
                    break;
                case '@':
                    if(bloco == 1|| comentarioComposto){
                        String atom = getAtom(input, i);
                        i += atom.length();
                        result.add(new Token(Type.ATOM, atom, linha));
                    }else{
                        result.add(new Token(Type.ARROBA, "@", linha));
                        i++;
                    }
                    break;
                case '#':
                    if(bloco == 1|| comentarioComposto){
                        String atom = getAtom(input, i);
                        i += atom.length();
                        result.add(new Token(Type.ATOM, atom, linha));
                    }else{
                        result.add(new Token(Type.HASH, "#", linha));
                        i++;
                    }
                    break;
                case '^':
                    if(bloco == 1|| comentarioComposto){
                        String atom = getAtom(input, i);
                        i += atom.length();
                        result.add(new Token(Type.ATOM, atom, linha));
                    }else{
                        result.add(new Token(Type.CIRC, "^", linha));
                        i++;
                    }
                    break;
                case '!':
                    if(bloco == 1|| comentarioComposto){
                        String atom = getAtom(input, i);
                        i += atom.length();
                        result.add(new Token(Type.ATOM, atom, linha));
                    }else{
                        result.add(new Token(Type.EXCLAMATION, "!", linha));
                        i++;
                    }
                    break;
                case '$':
                    if(bloco == 1|| comentarioComposto){
                        String atom = getAtom(input, i);
                        i += atom.length();
                        result.add(new Token(Type.ATOM, atom, linha));
                    }else{
                        result.add(new Token(Type.CIF, "$", linha));
                        i++;
                    }
                    break;
                case '_':
                    if(bloco == 1|| comentarioComposto){
                        String atom = getAtom(input, i);
                        i += atom.length();
                        result.add(new Token(Type.ATOM, atom, linha));
                    }else{
                        result.add(new Token(Type.UNDERLINE, "_", linha));
                        i++;
                    }
                    break;
                case '[':
                    if(bloco == 1|| comentarioComposto){
                        String atom = getAtom(input, i);
                        i += atom.length();
                        result.add(new Token(Type.ATOM, atom, linha));
                    }else{
                        result.add(new Token(Type.LBRACK, "[", linha));
                        i++;
                    }
                    break;
                case ']':
                    if(bloco == 1|| comentarioComposto){
                        String atom = getAtom(input, i);
                        i += atom.length();
                        result.add(new Token(Type.ATOM, atom, linha));
                    }else{
                        result.add(new Token(Type.RBRACK, "]", linha));
                        i++;
                    }
                    break;
                case '}':
                    if(bloco == 1|| comentarioComposto){
                        String atom = getAtom(input, i);
                        i += atom.length();
                        result.add(new Token(Type.ATOM, atom, linha));
                    }else{
                        result.add(new Token(Type.RKEY, "}", linha));
                        i++;
                    }
                    break;
                case '{':
                    if(bloco == 1|| comentarioComposto){
                        String atom = getAtom(input, i);
                        i += atom.length();
                        result.add(new Token(Type.ATOM, atom, linha));
                    }else{
                        result.add(new Token(Type.LKEY, "}", linha));
                        i++;
                    }
                    break;
                /*case '"':
                    result.add(new Token(Type.QUOTE, "\""));
                    i++;
                    break;*/
                case '<':
                    if(bloco == 1|| comentarioComposto){
                        String atom = getAtom(input, i);
                        i += atom.length();
                        result.add(new Token(Type.ATOM, atom, linha));
                    }else{
                        result.add(new Token(Type.LESSTHEN, "<", linha));
                        i++;
                    }
                    break;
                case '>':
                    if(bloco == 1|| comentarioComposto){
                        String atom = getAtom(input, i);
                        i += atom.length();
                        result.add(new Token(Type.ATOM, atom, linha));
                    }else{
                        result.add(new Token(Type.MORETHEN, ">", linha));
                        i++;
                    }
                    break;
                case ',':
                    if(bloco == 1|| comentarioComposto){
                        String atom = getAtom(input, i);
                        i += atom.length();
                        result.add(new Token(Type.ATOM, atom, linha));
                    }else{
                        result.add(new Token(Type.COMMA, ",", linha));
                        i++;
                    }
                    break;
                case ';':
                    if(bloco == 1|| comentarioComposto){
                        String atom = getAtom(input, i);
                        i += atom.length();
                        result.add(new Token(Type.ATOM, atom, linha));
                    }else{
                        result.add(new Token(Type.SEMICOLON, ";", linha));
                        i++;
                    }
                    break;
                default:

                    if((Character.compare(input.charAt(i), doubleQuote) == 0 )&& bloco == 0){
                        bloco = 1;
                    }
                    else{
                        bloco = 0;
                    }

                    if (Character.isWhitespace(input.charAt(i)) && bloco==0) {
                        i++;
                    }
                    else {
                        String atom = getAtom(input, i);
                        i += atom.length();
                        result.add(new Token(Type.ATOM, atom, linha));
                    }
                    break;
            }
        }
        return result;
    }

    public static void main(String args[]) {
        //String aux =  "/*******************************************************************************\n * Mirakel is an Android App for managing your Lists\n *\n *   Copyright (c) 2013-2015 Anatolij Zelenin, Georg Semmler.\n *\n *       This program is free software: you can redistribute it and/or modify\n *       it under the terms of the GNU General Public License as published by\n *       the Free Software Foundation, either version 3 of the License, or\n *       any later version.\n *\n *       This program is distributed in the hope that it will be useful,\n *       but WITHOUT ANY WARRANTY; without even the implied warranty of\n *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n *       GNU General Public License for more details.\n *\n *       You should have received a copy of the GNU General Public License\n *       along with this program.  If not, see <http://www.gnu.org/licenses/>.\n ******************************************************************************/\n\npackage de.azapps.mirakel.helper;\n\nimport android.support.annotation.NonNull;\nimport android.support.annotation.Nullable;\n\npublic abstract class AnalyticsWrapperBase {\n    @Nullable\n    protected static AnalyticsWrapperBase singleton;\n\n\n    public static AnalyticsWrapperBase getWrapper() {\n        if (singleton == null) {\n            singleton = new AnalyticsWrapperBase() {\n            };\n        }\n        return singleton;\n    }\n\n    public AnalyticsWrapperBase() {\n\n    }\n\n    public static AnalyticsWrapperBase init(final AnalyticsWrapperBase analyticsWrapperBase) {\n        singleton = analyticsWrapperBase;\n        return singleton;\n    }\n\n    public void doNotTrack() {\n\n    }\n\n    public enum CATEGORY {\n        DUMB_USER, NORMAL_USER, ADVANCED_USER, POWER_USER, HANDLE_INTENT\n    }\n\n    public enum ACTION {\n        // DUMB USER\n        IMPLICIT_SAVE_TASK_NAME(CATEGORY.DUMB_USER), DISMISS_CRASH_DIALOG(CATEGORY.DUMB_USER),\n\n        // NORMAL USER\n        ADD_FILE(CATEGORY.NORMAL_USER), ADD_PHOTO(CATEGORY.NORMAL_USER), ADD_RECORDING(CATEGORY.NORMAL_USER),\n        ADD_SUBTASK(CATEGORY.NORMAL_USER), SET_PROGRESS(CATEGORY.NORMAL_USER), SET_PRIORITY(CATEGORY.NORMAL_USER),\n        ADD_NOTE(CATEGORY.NORMAL_USER), SEARCHED(CATEGORY.NORMAL_USER), SET_REMINDER(CATEGORY.NORMAL_USER), SET_DUE(CATEGORY.NORMAL_USER),\n\n        // ADVANCED USER\n        USED_SEMANTICS(CATEGORY.ADVANCED_USER), SET_RECURRING_REMINDER(CATEGORY.ADVANCED_USER), ADD_TAG(CATEGORY.ADVANCED_USER),\n\n        // POWER USER\n        CREATED_SEMANTIC(CATEGORY.POWER_USER), CREATED_META_LIST(CATEGORY.POWER_USER), ACTIVATED_DEVELOPMENT_SETTINGS(CATEGORY.POWER_USER);\n\n        private CATEGORY category;\n\n        ACTION(CATEGORY category) {\n            this.category = category;\n        }\n\n        public CATEGORY getCategory() {\n            return category;\n        }\n    }\n\n    public static void track(final ACTION action) {\n        getWrapper().track(action.getCategory(), action.toString(), null, null);\n    }\n\n    public static void track(final CATEGORY category, final String label) {\n        getWrapper().track(category, label, null, null);\n\n    }\n\n    public static void setScreen(Object screen) {\n        getWrapper().mSetScreen(screen);\n    }\n\n    public void mSetScreen(Object screen) {\n\n    }\n\n    public void track(@NonNull final CATEGORY category, @NonNull final String action,\n                      @Nullable final String label, @Nullable final Long value) {\n\n    }\n}\n";

        String aux = "";
        String snapshot = "";
        String refactoringType = "";
        String res= aux;
        String query = "";
        String query2 = "";
        String query3 = "";
        //System.out.print("\nAUX: " + aux);
        List<Token> tokens = Lex(res);

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mestrado", "root", "admin");

            Statement statement = connection.createStatement();
            PreparedStatement preparedStmt = null;
            ResultSet resultSet = null;
            query2 = "select id from mestrado.snapshot;";
            PreparedStatement preparedStmt2 = connection.prepareStatement(query2);
            ResultSet resultSet2 = preparedStmt2.executeQuery();

            while(resultSet2.next()) {
                query3 = "select * from mestrado.chunk where snapshotId=?;";
                preparedStmt2 = connection.prepareStatement(query3);
                preparedStmt2.setString(1, resultSet2.getString("id"));
                resultSet = preparedStmt2.executeQuery(); //Resultset result = null

                query = " insert into token (token, snapshotId, refactoringType, codeStatement)"
                        + " values (?, ?, ?, ?)";
                preparedStmt = connection.prepareStatement(query);

                while (resultSet.next()) {
                    /*query = " insert into token (token, snapshotId, refactoringType, codeStatement)"
                            + " values (?, ?, ?, ?)";*/
                    aux = resultSet.getString("codeStatement");
                    snapshot = resultSet.getString("snapshotId");
                    refactoringType = resultSet.getString("refactoringType");
                    res = aux;
                    tokens = Lex(res);
                    for (Token t : tokens) {
                        preparedStmt.setString(1, t.c);
                        preparedStmt.setString(2, snapshot);
                        preparedStmt.setString(3, refactoringType);
                        preparedStmt.setString(4, aux);
                        System.out.print(t + "\n"); //t.c
                        preparedStmt.execute();
                    }
                }
                //connection.close();
            }
        }catch (SQLException e){
            System.err.println("Got an exception! ");
            e.printStackTrace();
        }

        /*String res= aux;
        System.out.print("\nAUX: " + aux);
        List<Token> tokens = Lex(res);
        for (Token t : tokens) {
            System.out.print(t + "\n"); //t.c
        }*/
    }
}
