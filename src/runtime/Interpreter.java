package runtime;

import errors.Error;
import errors.RunTimeError;
import nodes.*;
import run.CerealFile;
import tokens.Token;
import traceback.Context;
import values.*;
import values.functions.CFunction;

import java.util.ArrayList;
import java.util.List;

import static tokens.Token.TokenType.*;
import static values.CNumber.*;
import static values.CBoolean.*;
import static values.CString.*;

public class Interpreter {

    public CerealFile cFile;

    public Interpreter(CerealFile cFile) {
        this.cFile = cFile;
    }

    public RuntimeResult visit(Node node, Context context) {
        if (node instanceof NumberNode) {
            return visitNumberNode((NumberNode) node, context);
        } else if (node instanceof BooleanNode) {
            return visitBooleanNode((BooleanNode) node, context);
        } else if (node instanceof NullNode) {
            return visitNullNode((NullNode) node, context);
        } else if (node instanceof UnaryOperationNode) {
            return visitUnaryNode((UnaryOperationNode) node, context);
        } else if (node instanceof BinaryOperationNode) {
            return visitBinaryNode((BinaryOperationNode) node, context);
        } else if (node instanceof VariableAssignNode) {
            return visitVariableAssignNode((VariableAssignNode) node, context);
        } else if (node instanceof VariableReAssignNode) {
            return visitVariableReAssignNode((VariableReAssignNode) node, context);
        } else if (node instanceof VariableAccessNode) {
            return visitVariableAccessNode((VariableAccessNode) node, context);
        } else if (node instanceof IfNode) {
            return visitIfNode((IfNode) node, context);
        } else if (node instanceof ForNode) {
            return visitForNode((ForNode) node, context);
        } else if (node instanceof WhileNode) {
            return visitWhileNode((WhileNode) node, context);
        } else if (node instanceof FunctionDefinitionNode) {
            return visitFunctionDefinitionNode((FunctionDefinitionNode) node, context);
        } else if (node instanceof CallNode) {
            return visitCallNode((CallNode) node, context);
        } else if (node instanceof StringNode) {
            return visitStringNode((StringNode) node, context);
        } else if (node instanceof ListNode) {
            return visitListNode((ListNode) node, context);
        } else if (node instanceof ListGetNode) {
            return visitListGetNode((ListGetNode) node, context);
        } else if (node instanceof ListReAssignNode) {
            return visitListReAssignNode((ListReAssignNode) node, context);
        } else {
            return noVisit(node, context);
        }
    }

    private RuntimeResult visitListReAssignNode(ListReAssignNode LRANode, Context context) {
        RuntimeResult result = new RuntimeResult();

        Value value = result.register(visit(LRANode.listName, context));

        if (result.isError)
            return result;

        if (!(value instanceof CList)) {
            result.setError(new RunTimeError("Not a list", LRANode.listName.startPosition, LRANode.listName.endPosition,
                    cFile, context));
            return result;
        }

        CList list = (CList) value;

        Value ind = result.register(visit(LRANode.index, context));

        if (result.isError)
            return result;

        if (!(ind instanceof CNumber) || !((CNumber) ind).isInteger) {
            result.setError(new RunTimeError("Invalid type for list index", LRANode.index.startPosition,
                    LRANode.index.endPosition, cFile, context));
            return result;
        }

        CNumber index = (CNumber) ind;

        if (((CNumber) index).value.intValue() >= list.elements.size() || ((CNumber) index).value.intValue() < 0) {
            result.setError(new RunTimeError("List assignment index out of range", LRANode.index.startPosition,
                    LRANode.index.endPosition, cFile, context));
            return result;
        }

        Value newValue = result.register(visit(LRANode.newValue, context));

        if (result.isError)
            return result;

        list.elements.set(index.value.intValue(), newValue);

        if (LRANode.listName instanceof VariableAccessNode) {
            String varName = ((VariableAccessNode) LRANode.listName).variableNameToken.keyString;
            context.symbolTable.set(varName, list);
        }

        result.setResult(newValue);

        return result;
    }

    private RuntimeResult visitListGetNode(ListGetNode node, Context context) {
        RuntimeResult result = new RuntimeResult();

        Value value = result.register(visit(node.name, context));

        if (result.isError)
            return result;

        if (!(value instanceof CList)) {
            result.setError(
                    new RunTimeError("Not a list", node.name.startPosition, node.name.endPosition, cFile, context));
            return result;
        }

        CList list = (CList) value;

        Value index = result.register(visit(node.index, context));

        if (result.isError)
            return result;

        if (!(index instanceof CNumber) || !((CNumber) index).isInteger) {
            result.setError(new RunTimeError("Invalid type for list index", node.index.startPosition,
                    node.index.endPosition, cFile, context));
            return result;
        }

        if (((CNumber) index).value.intValue() >= list.elements.size() || ((CNumber) index).value.intValue() < 0) {
            result.setError(new RunTimeError("List index out of range", node.index.startPosition,
                    node.index.endPosition, cFile, context));
            return result;
        }

        result.setResult(list.elements.get(((CNumber) index).value.intValue()));

        return result;
    }

    private RuntimeResult visitListNode(ListNode listNode, Context context) {
        RuntimeResult result = new RuntimeResult();
        List<Value> elements = new ArrayList<Value>();

        for (Node node : listNode.elementNodes) {
            elements.add(result.register(visit(node, context)));
            if (result.isError)
                return result;
        }

        CList list = new CList(elements);
        list.setContext(context);
        list.setPositon(listNode.startPosition, listNode.endPosition);

        result.setResult(list);
        return result;
    }

    private RuntimeResult visitStringNode(StringNode stringNode, Context context) {
        RuntimeResult result = new RuntimeResult();
        CString str = new CString(stringNode.stringToken.keyString);
        str.setPositon(stringNode.startPosition, stringNode.endPosition);
        str.setContext(context);

        return result.setResult(str);
    }

    private RuntimeResult visitFunctionDefinitionNode(FunctionDefinitionNode funcNode, Context context) {
        RuntimeResult result = new RuntimeResult();
        String funcName = funcNode.variableName != null ? funcNode.variableName.keyString : null;
        Node bodyNode = funcNode.bodyNode;
        List<String> argumentNames = new ArrayList<>();

        for (Token arg : funcNode.arguments)
            argumentNames.add(arg.keyString);

        CFunction func = new CFunction(funcName, bodyNode, argumentNames);
        func.setContext(context);
        func.setPositon(funcNode.startPosition, funcNode.endPosition);

        if (funcNode.variableName != null)
            context.symbolTable.set(funcName, func);
        return result.setResult(func);
    }

    private RuntimeResult visitCallNode(CallNode callNode, Context context) {
        RuntimeResult result = new RuntimeResult();
        List<Value> arguments = new ArrayList<>();

        Value valueToCall = result.register(visit(callNode.nodeToCall, context));
        if (result.isError)
            return result;

        if (!(valueToCall instanceof CFunction)) {
            result.setError(new RunTimeError("variable is not callable", callNode.startPosition, callNode.endPosition,
                    cFile, context));
            return result;
        }

        valueToCall = valueToCall.copy();
        valueToCall.setPositon(callNode.startPosition, callNode.endPosition);

        for (Node arg : callNode.argumentNodes) {
            arguments.add(result.register(visit(arg, context)));
            if (result.isError)
                return result;
        }

        // TODO Not actual return value
        Value returnValue = result.register(((CFunction) valueToCall).execute(callNode, arguments, context, cFile));
        if (result.isError)
            return result;

        return result.setResult(returnValue);
    }

    private RuntimeResult visitWhileNode(WhileNode whileNode, Context context) {
        RuntimeResult result = new RuntimeResult();
        List<Value> elements = new ArrayList<Value>();

        Value condition = result.register(visit(whileNode.conditionNode, context));
        if (result.isError)
            return result;

        if (!(condition instanceof CBoolean)) {
            result.setError(new RunTimeError("Expected a boolean", whileNode.conditionNode.startPosition,
                    whileNode.conditionNode.endPosition, cFile, context));
            return result;
        }

        while (((CBoolean) condition).bool) {
            Value bodyValue = result.register(visit(whileNode.bodyNode, context));
            if (result.isError)
                return result;

            condition = result.register(visit(whileNode.conditionNode, context));
            if (result.isError)
                return result;

            elements.add(bodyValue);
        }
        CList list = new CList(elements);
        list.setPositon(whileNode.startPosition, whileNode.endPosition);
        list.setContext(context);

        result.setResult(list);
        return result;
    }

    private RuntimeResult visitForNode(ForNode forNode, Context context) {
        RuntimeResult result = new RuntimeResult();
        List<Value> elements = new ArrayList<Value>();

        if (forNode.isSimplified) {
            Value reps = result.register(visit(forNode.repetitions, context));
            if (result.isError)
                return result;

            if (!(reps instanceof CNumber)) {
                result.setError(new RunTimeError("Expected a number", forNode.repetitions.startPosition,
                        forNode.repetitions.endPosition, cFile, context));
                return result;
            }

            for (int i = 0; i < ((CNumber) reps).value.intValue(); i++) {

                Value body = result.register(visit(forNode.bodyNode, context));
                if (result.isError)
                    return result;

                elements.add(body);

            }
            CList list = new CList(elements);
            list.setPositon(forNode.startPosition, forNode.endPosition);
            list.setContext(context);

            return result.setResult(list);
        }

        result.register(visit(forNode.initialNode, context)); // Initial Declaration
        if (result.isError)
            return result;
        Value endCondition = result.register(visit(forNode.endCondition, context));
        if (result.isError)
            return result;

        if (!(endCondition instanceof CBoolean)) {
            result.setError(new RunTimeError("Expected a boolean", forNode.endCondition.startPosition,
                    forNode.endCondition.endPosition, cFile, context));
            return result;
        }

        while (((CBoolean) endCondition).bool) {

            Value bodyValue = result.register(visit(forNode.bodyNode, context));

            elements.add(bodyValue);
            if (result.isError)
                return result;

            result.register(visit(forNode.incrementNode, context)); // Increment evaluation
            if (result.isError)
                return result;

            endCondition = result.register(visit(forNode.endCondition, context));
            if (result.isError)
                return result;
        }

        CList list = new CList(elements);
        list.setPositon(forNode.startPosition, forNode.endPosition);
        list.setContext(context);

        result.setResult(list);
        return result;

    }

    private RuntimeResult visitIfNode(IfNode ifNode, Context context) {
        RuntimeResult result = new RuntimeResult();

        for (Node[] ifCase : ifNode.cases) {
            Value condition = result.register(visit(ifCase[0], context));
            if (result.isError)
                return result;

            if (!(condition instanceof CBoolean)) {
                result.setError(new RunTimeError("Expected a boolean", ifCase[0].startPosition, ifCase[0].endPosition,
                        cFile, context));
                return result;
            }

            if (((CBoolean) condition).bool) {
                Value expression = result.register(visit(ifCase[1], context));
                if (result.isError)
                    return result;

                result.setResult(expression);
                return result;
            }
        }

        if (ifNode.elseCase != null) {
            Value elseValue = result.register(visit(ifNode.elseCase, context));
            if (result.isError)
                return result;

            result.setResult(elseValue);
            return result;
        }

        result.setResult(new CNull());
        return result;
    }

    private RuntimeResult visitVariableReAssignNode(VariableReAssignNode vNode, Context context) {
        RuntimeResult result = new RuntimeResult();
        String variableName = vNode.variableNameToken.keyString;
        Value value = result.register(visit(vNode.valueNode, context));

        if (result.isError)
            return result;

        // Checks if variable is in symboltable
        if (context.symbolTable.get(variableName) == null) {
            result.setError(new RunTimeError("'" + variableName + "' is not defined", vNode.startPosition,
                    vNode.endPosition, cFile, context));
            return result;
        }

        if (vNode.operationToken.type == EQUALS) {
            context.symbolTable.set(variableName, value);
            return result.setResult(value);
        }

        Value var = context.symbolTable.get(variableName);

        if (var instanceof CNumber && value instanceof CNumber) {
            switch (vNode.operationToken.type) {
                case ADD_EQ:
                    value = add((CNumber) var, (CNumber) value);
                    break;
                case SUB_EQ:
                    value = subtract((CNumber) var, (CNumber) value);
                    break;
                case MUL_EQ:
                    value = multiply((CNumber) var, (CNumber) value);
                    break;
                case DIV_EQ:
                    if (value.equals(new CNumber(0.0)) || value.equals(new CNumber(0))) {
                        result.setError(new RunTimeError("Division by 0", value.startPosition, value.endPosition, cFile,
                                context));
                        return result;
                    }
                    value = divide((CNumber) var, (CNumber) value);
                    break;
                case MOD_EQ:
                    value = modulo((CNumber) var, (CNumber) value);
                    break;
                case INTDIV_EQ:
                    value = integerDivide((CNumber) var, (CNumber) value);
                    break;
                case POW_EQ:
                    value = power((CNumber) var, (CNumber) value);
                    break;
                default:
                    result.setError(new RunTimeError("Expected a number", value.startPosition, value.endPosition, cFile,
                            context));
                    return result;
            }
        } else if (var instanceof CBoolean && value instanceof CBoolean) {
            switch (vNode.operationToken.type) {
                case AND_EQ:
                    value = and((CBoolean) var, (CBoolean) value);
                    break;
                case OR_EQ:
                    value = or((CBoolean) var, (CBoolean) value);
                    break;
                default:
                    result.setError(new RunTimeError("Expected a boolean", value.startPosition, value.endPosition,
                            cFile, context));
                    return result;
            }
        } else if (var instanceof CList) {
            if (vNode.operationToken.type == ADD_EQ) {
                value = CList.add((CList) var, value);
            } else {
                result.setError(new RunTimeError("Invalid operation for lists", vNode.startPosition, vNode.endPosition,
                        cFile, context));
                return result;
            }
        } else if (var instanceof CString) {
            if (vNode.operationToken.type == ADD_EQ) {
                value = add((CString) var, value instanceof CString ? (CString) value : new CString(value.toString()));
            } else {
                result.setError(new RunTimeError("Invalid operation for strings", vNode.startPosition,
                        vNode.endPosition, cFile, context));
                return result;
            }
        } else if (value instanceof CString) {
            if (vNode.operationToken.type == ADD_EQ) {
                value = add(var instanceof CString ? (CString) var : new CString(var.toString()), (CString) value);
            } else {
                result.setError(new RunTimeError("Invalid operation for strings", vNode.startPosition,
                        vNode.endPosition, cFile, context));
                return result;
            }
        } else {
            result.setError(new RunTimeError("Cannot do operations on incompatible types", vNode.startPosition,
                    vNode.endPosition, cFile, context));
            return result;
        }

        context.symbolTable.set(variableName, value);
        return result.setResult(value);
    }

    private RuntimeResult visitVariableAssignNode(VariableAssignNode vNode, Context context) {
        RuntimeResult result = new RuntimeResult();
        String variableName = vNode.variableNameToken.keyString;

        Value value = result.register(visit(vNode.valueNode, context));
        if (result.isError)
            return result;
        context.symbolTable.set(variableName, value);

        result.setResult(value);

        return result;

    }

    private RuntimeResult visitVariableAccessNode(VariableAccessNode vNode, Context context) {
        RuntimeResult result = new RuntimeResult();
        String variableName = vNode.variableNameToken.keyString;

        Value value = context.symbolTable.get(variableName);

        if (value == null) {
            result.setError(new RunTimeError("'" + variableName + "' is not defined", vNode.startPosition,
                    vNode.endPosition, cFile, context));
            return result;
        }

        value = value.copy();
        value.setPositon(vNode.startPosition, vNode.endPosition);
        result.setResult(value);

        return result;
    }

    private RuntimeResult visitNumberNode(NumberNode numberNode, Context context) {
        CNumber number;
        if (numberNode.numberToken.type == INT) {
            number = new CNumber((Integer) numberNode.numberToken.value);
        } else {
            number = new CNumber((Double) numberNode.numberToken.value);
        }

        number.setPositon(numberNode.startPosition, numberNode.endPosition);
        RuntimeResult result = new RuntimeResult();
        result.setResult(number); // Temp
        return result;
    }

    private RuntimeResult visitBooleanNode(BooleanNode boolNode, Context context) {

        CBoolean bool = new CBoolean(boolNode.boolToken.boolValue);

        bool.setPositon(boolNode.startPosition, boolNode.endPosition);
        RuntimeResult result = new RuntimeResult();
        result.setResult(bool);

        return result;
    }

    private RuntimeResult visitNullNode(NullNode node, Context context) {
        CNull nullValue = new CNull();

        nullValue.setPositon(node.startPosition, node.endPosition);
        RuntimeResult result = new RuntimeResult();
        result.setResult(nullValue);
        return result;
    }

    private RuntimeResult visitBinaryNode(BinaryOperationNode binOpNode, Context context) {
        RuntimeResult result = new RuntimeResult();

        Value left = result.register(this.visit(binOpNode.leftNode, context));

        if (result.isError)
            return result;

        Value right = result.register(this.visit(binOpNode.rightNode, context));

        if (result.isError)
            return result;

        Value value = new CNull();

        if (left instanceof CNumber && right instanceof CNumber) {

            switch (binOpNode.operatorToken.type) {
                case ADD:
                    value = add((CNumber) left, (CNumber) right);
                    break;
                case SUB:
                    value = subtract((CNumber) left, (CNumber) right);
                    break;
                case MUL:
                    value = multiply((CNumber) left, (CNumber) right);
                    break;
                case DIV:
                    if (right.equals(new CNumber(0.0)) || right.equals(new CNumber(0))) {
                        result.setError(new RunTimeError("Division by 0", right.startPosition, right.endPosition, cFile,
                                context));
                        return result;
                    }
                    value = divide((CNumber) left, (CNumber) right);
                    break;
                case MOD:
                    value = modulo((CNumber) left, (CNumber) right);
                    break;
                case INTDIV:
                    value = integerDivide((CNumber) left, (CNumber) right);
                    break;
                case POW:
                    value = power((CNumber) left, (CNumber) right);
                    break;
                case EQUALS_EQ:
                    value = isEqual((CNumber) left, (CNumber) right);
                    break;
                case NOT_EQ:
                    value = isNotEqual((CNumber) left, (CNumber) right);
                    break;
                case LT:
                    value = lessThan((CNumber) left, (CNumber) right);
                    break;
                case LT_EQ:
                    value = lessThanEquals((CNumber) left, (CNumber) right);
                    break;
                case GT:
                    value = greaterThan((CNumber) left, (CNumber) right);
                    break;
                case GT_EQ:
                    value = greaterThanEquals((CNumber) left, (CNumber) right);
                    break;
                default:
                    // Set Runtime Result to error here
                    result.setError(new RunTimeError("Invalid operation for numbers", binOpNode.startPosition,
                            binOpNode.endPosition, cFile, context));
                    return result;
            }

            value.setPositon(binOpNode.startPosition, binOpNode.endPosition);
            result.setResult(value);
            return result;
        }

        if (left instanceof CBoolean && right instanceof CBoolean) {

            switch (binOpNode.operatorToken.type) {
                case AND:
                    value = and((CBoolean) left, (CBoolean) right);
                    break;
                case OR:
                    value = or((CBoolean) left, (CBoolean) right);
                    break;
                case EQUALS_EQ:
                    value = isEqual((CBoolean) left, (CBoolean) right);
                    break;
                case NOT_EQ:
                    value = notEqual((CBoolean) left, (CBoolean) right);
                    break;
                default:
                    result.setError(new RunTimeError("Invalid operation for booleans", binOpNode.startPosition,
                            binOpNode.endPosition, cFile, context));
                    return result;
            }
            value.setPositon(binOpNode.startPosition, binOpNode.endPosition);
            result.setResult(value);
            return result;
        }

        if (left instanceof CString || right instanceof CString) {
            if (binOpNode.operatorToken.type == ADD) {
                if (!(left instanceof CString)) {
                    value = add(new CString(left.toString()), (CString) right);
                } else if (!(right instanceof CString)) {
                    value = add((CString) left, new CString(right.toString()));
                } else
                    value = add((CString) left, (CString) right);
            } else if (left instanceof CString && right instanceof CString) {
                if (binOpNode.operatorToken.type == EQUALS_EQ) {
                    value = new CBoolean(((CString) left).equals((CString) right));
                }

                if (binOpNode.operatorToken.type == NOT_EQ) {
                    value = new CBoolean(!((CString) left).equals((CString) right));
                }

            } else {
                result.setError(new RunTimeError("Invalid operation for strings", left.startPosition, right.endPosition,
                        cFile, context));
                return result;
            }

            value.setPositon(binOpNode.startPosition, binOpNode.endPosition);
            result.setResult(value);
            return result;
        }

        if (left instanceof CList && right instanceof CList) {
            if (binOpNode.operatorToken.type == EQUALS_EQ) {
                value = new CBoolean(left.equals(right));

                value.setPositon(binOpNode.startPosition, binOpNode.endPosition);
                result.setResult(value);
                return result;
            } else if (binOpNode.operatorToken.type == NOT_EQ) {
                value = new CBoolean(!left.equals(right));

                value.setPositon(binOpNode.startPosition, binOpNode.endPosition);
                result.setResult(value);
                return result;
            }
        }

        if (left instanceof CList) {
            if (binOpNode.operatorToken.type == ADD) {
                value = left.copy();
                ((CList) value).elements.add(right);
            } else {
                result.setError(new RunTimeError("Invalid operation for lists", left.startPosition, right.endPosition,
                        cFile, context));
                return result;
            }

            value.setPositon(binOpNode.startPosition, binOpNode.endPosition);
            result.setResult(value);
            return result;
        } else if (right instanceof CList) {
            if (binOpNode.operatorToken.type == ADD) {
                value = right.copy();
                ((CList) value).elements.add(0, left);
            } else {
                result.setError(new RunTimeError("Invalid operation for lists", left.startPosition, right.endPosition,
                        cFile, context));
                return result;
            }

            value.setPositon(binOpNode.startPosition, binOpNode.endPosition);
            result.setResult(value);
            return result;
        }

        result.setError(new RunTimeError("Cannot do operations on incompatible types", binOpNode.startPosition,
                binOpNode.endPosition, cFile, context));
        return result;
    }

    private RuntimeResult visitUnaryNode(UnaryOperationNode uNode, Context context) {

        RuntimeResult operandResult = this.visit(uNode.getOperand(), context);

        if (operandResult.isError)
            return operandResult;

        Value value = operandResult.value;
        RuntimeResult result = new RuntimeResult();

        if (value instanceof CNull) {
            result.setError(new RunTimeError("Cannot do operations on null", uNode.startPosition, uNode.endPosition,
                    cFile, context));
            return result;
        }

        if (value instanceof CNumber) {

            if (uNode.operatorToken.type == SUB) {
                value = multiply(new CNumber(-1), (CNumber) value);

            } else if (uNode.operatorToken.type != ADD) {
                result.setError(new RunTimeError("Cannot apply this operation to numbers",
                        uNode.operatorToken.startPosition, uNode.operatorToken.endPosition, cFile, context));
            }

            result.setResult(value);

            return result;

        } else if (value instanceof CBoolean) {
            if (uNode.operatorToken.type == NOT) {
                value = not((CBoolean) value);
            } else {
                result.setError(new RunTimeError("Cannot apply this operation to booleans",
                        uNode.operatorToken.startPosition, uNode.operatorToken.endPosition, cFile, context));
                return result;
            }

            result.setResult(value);
            return result;

        }

        result.setError(new RunTimeError("Exception in visitUnaryNode() for node " + uNode, uNode.startPosition,
                uNode.endPosition, cFile, context));
        return result;
    }

    private RuntimeResult noVisit(Node node, Context context) {
        RuntimeResult result = new RuntimeResult();
        if (node.toString().equals("EMPTY_NODE")) {
            return result.setResult(new CNull());
        }
        Error error = new RunTimeError("Exception in noVisit() for node " + node, node.startPosition, node.endPosition,
                cFile, context);

        result.setError(error);

        return result;
    }

}
