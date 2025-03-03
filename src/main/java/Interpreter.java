
class Interpreter implements Expr.Visitor<Object> {

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case TokenType.MINUS -> {
                checkNumberOperand(expr.operator, left, right);
                return (double) left - (double) right;
            }
            case TokenType.PLUS -> {
                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }

                if (left instanceof String && right instanceof Double) {
                    return (String) left + String.valueOf(right);
                }

                if (left instanceof Double && right instanceof String) {
                    return String.valueOf(left) + (String) right;
                }

                throw new RuntimeError(expr.operator,
                        "Operands must be numbers or strings.");
            }
            case TokenType.SLASH -> {
                checkNumberOperand(expr.operator, left, right);

                if ((double) right == 0) {
                    throw new RuntimeError(expr.operator, "Detected division by 0.");
                }

                return (double) left / (double) right;
            }
            case TokenType.STAR -> {
                checkNumberOperand(expr.operator, left, right);
                return (double) left * (double) right;
            }
            case TokenType.GREATER -> {
                checkNumberOperand(expr.operator, left, right);
                return (double) left > (double) right;
            }
            case TokenType.GREATER_EQUAL -> {
                checkNumberOperand(expr.operator, left, right);
                return (double) left >= (double) right;
            }
            case TokenType.LESS -> {
                checkNumberOperand(expr.operator, left, right);
                return (double) left < (double) right;
            }
            case TokenType.LESS_EQUAL -> {
                checkNumberOperand(expr.operator, left, right);
                return (double) left <= (double) right;
            }
            case BANG_EQUAL -> {
                return !isEqual(left, right);
            }
            case EQUAL_EQUAL -> {
                return isEqual(left, right);
            }
        }

        return null;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case TokenType.MINUS -> {
                checkNumberOperand(expr.operator, right);
                return -(double) right;
            }
            case TokenType.BANG -> {
                return !isTruthy(right);
            }
        }
        // unreachable
        return null;
    }

    void interpret(Expr expression) {
        try {
            Object value = evaluate(expression);
            System.err.println(stringify(value));
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private boolean isTruthy(Object object) {
        if (object == null) {
            return false;
        }

        if (object instanceof Boolean) {
            return (boolean) object;
        }

        return true;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null) {
            return false;
        }

        return a.equals(b);
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) {
            return;
        }
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperand(Token operator, Object leftOp, Object rightOp) {
        if (leftOp instanceof Double && rightOp instanceof Double) {
            return;
        }

        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private String stringify(Object object) {
        if (object == null) {
            return "nil";
        }

        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return object.toString();
    }
}
