package net.kyrptonaught.inventorysorter.sorttree.node;

import net.kyrptonaught.inventorysorter.InventorySorterMod;
import net.kyrptonaught.inventorysorter.sorttree.node.constraints.DataConstraint;
import net.kyrptonaught.inventorysorter.sorttree.node.constraints.DataConstraints;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SortNodeData {
    private static final Logger logger = InventorySorterMod.logger;

    public final SortTreeNode node;
    private final HashMap<String, DataConstraint> constraintMap;

    public SortNodeData(SortTreeNode node){
        this.node = node;
        this.constraintMap = new HashMap<>();
    }

    public boolean parseDataStr(String dataStr){
        return DataParser.parse(this, dataStr);
    }

    public HashMap<String, DataConstraint> getConstraintMap() {
        return constraintMap;
    }

    public DataConstraint getConstraint(String name){
        return constraintMap.get(name);
    }

    public boolean hasConstraint(String name){
        return constraintMap.containsKey(name);
    }

    private static class DataParser{

        private enum TokenType{
            NONE,
            IDENT,
            OP,
            L_CURLY,
            R_CURLY,
            D_QUOTE,
            COMMA,
            IN,
            NOT,
        }

        private record Token(TokenType type, String content, int startPos){}

        private static final Pattern TOKEN_PATTERN = Pattern.compile("\"((?:[^\\\\]|\\\\.)*?)\"|(,)|([{])|(})|(==)|(!=)|(<=)|(>=)|(<)|(>)|(not)|(in)|([A-Za-z_][A-Za-z_\\-0-9]*)", Pattern.DOTALL);
        private final SortNodeData data;
        private final ArrayList<Token> tokens;

        private DataParser(SortNodeData data){
            this.data = data;
            this.tokens = new ArrayList<>();
        }

//        private Token grabNext(String remainingStr){
//
//        }

        private boolean lexTokens(String dataStr){
            Matcher matcher = TOKEN_PATTERN.matcher(dataStr);

            while (matcher.find()){
                int numGroups = matcher.groupCount();
                for(int groupNum = 1; groupNum<=numGroups; ++groupNum){
                    String match = matcher.group(groupNum);
                    if(match == null){
                        continue;
                    }

                    TokenType tokenType;
                    switch (groupNum){
                        case 1, 13:
                            tokenType = TokenType.IDENT;
                            break;
                        case 2:
                            tokenType = TokenType.COMMA;
                            break;
                        case 3:
                            tokenType = TokenType.L_CURLY;
                            break;
                        case 4:
                            tokenType = TokenType.R_CURLY;
                            break;
                        case 5, 6, 7, 8, 9, 10:
                            tokenType = TokenType.OP;
                            break;
                        case 11:
                            tokenType = TokenType.NOT;
                            break;
                        case 12:
                            tokenType = TokenType.IN;
                            break;
                        default:
                            return false;
                    }
                    Token token = new Token(tokenType, match, matcher.start());
                    tokens.add(token);
                    break;
                }
            }

            return true;
        }

        private Token tokenGet(int index) throws ParserException{
            if(index >= tokens.size()){
                throw new ParserException("Unexpectedly encountered end of constraint list.", tokens.size() - 1);
            }
            return tokens.get(index);
        }

        private Token tokenGet(int index, TokenType expectedType) throws ParserException{
            Token token = tokenGet(index);
            if(token.type != expectedType){
                throw new ParserException("Unexpected token '" + token.content + "'", index);
            }
            return token;
        }

        private Token tokenGet(int index, TokenType expectedType1, TokenType expectedType2) throws ParserException{
            Token token = tokenGet(index);
            if(token.type != expectedType1 && token.type != expectedType2){
                throw new ParserException("Unexpected token '" + token.content + "'", index);
            }
            return token;
        }

        private int constraintStmt(int currIndex) throws ParserException{
            Token identToken = tokenGet(currIndex, TokenType.IDENT);
            ++currIndex;

            Token notToken = tokenGet(currIndex);
            if(notToken.type == TokenType.NOT){
                ++currIndex;
            }

            Token operatorToken = tokenGet(currIndex, TokenType.OP, TokenType.IN);
            ++currIndex;

            Token exprToken = tokenGet(currIndex, TokenType.IDENT);
            ++currIndex;

            String name = identToken.content;
            String op = operatorToken.content;
            String rValue = exprToken.content;
            HashMap<String, DataConstraint> constraintMap = data.getConstraintMap();
            if(constraintMap.containsKey(name)){
                throw new ParserException("Constraint '"+name+"' already used in this expression.", currIndex);
            }
            Optional<DataConstraint> optConstraint = DataConstraints.makeConstraint(data.node, name, rValue);
            if(optConstraint.isEmpty()){
                throw new ParserException("Bad constraint '" + name + "'.", currIndex);
            }
            constraintMap.put(name, optConstraint.get());

            return currIndex;
        }

        private void constraintList(int currIndex) throws ParserException{
            Token tokenAt;
            tokenGet(currIndex, TokenType.L_CURLY);
            ++currIndex;

            tokenAt = tokenGet(currIndex);
            if(tokenAt.type == TokenType.R_CURLY){
                return;
            }

            while (true) {

                currIndex = constraintStmt(currIndex);
                tokenAt = tokenGet(currIndex);

                if(tokenAt.type == TokenType.COMMA) {
                    ++currIndex;
                    tokenAt = tokenGet(currIndex);
                    if(tokenAt.type == TokenType.R_CURLY) {
                        return;
                    }else {
                        continue;
                    }
                }

                tokenGet(currIndex, TokenType.R_CURLY);
                return;
            }
        }

        static boolean parse(SortNodeData data, String dataStr){
            DataParser parser = new DataParser(data);
            if(!parser.lexTokens(dataStr)){
                logger.debug("Sort data lexical error.");
                return false;
            }

            try {
                parser.constraintList(0);
                return true;
            }catch (ParserException e){
                if(e.tokenIndex >= parser.tokens.size()){
                    logger.error("Parser error contains bad token index, this is a bug.");
                    return false;
                }
                Token token = parser.tokens.get(e.tokenIndex);
                int startPos = token.startPos+1;
                logger.warn("Error while parsing constraint list '{}' at position {}: '{}'\n", dataStr, startPos, e.getMessage());
                logger.trace(e);
                return false;
            }
        }

        private static class ParserException extends Exception{
            public final int tokenIndex;
            ParserException(String message, int tokenIndex){
                super(message);
                this.tokenIndex = tokenIndex;
            }
        }
    }
}
