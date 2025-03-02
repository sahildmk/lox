
class Interpreter implements Expr.Visitor<Object> {

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case TokenType.MINUS -> {
                return (double) left - (double) right;
            }
            case TokenType.PLUS -> {
                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }

                if (left instanceof String & right instanceof String) {
                    return (String) left + (String) right;
                }
            }
            case TokenType.SLASH -> {
                return (double) left / (double) right;
            }
            case TokenType.STAR -> {
                return (double) left * (double) right;
            }
            case TokenType.GREATER -> {
                return (double) left > (double) right;
            }
            case TokenType.GREATER_EQUAL -> {
                return (double) left >= (double) right;
            }
            case TokenType.LESS -> {
                return (double) left < (double) right;
            }
            case TokenType.LESS_EQUAL -> {
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
                return -(double) right;
            }
            case TokenType.BANG -> {
                return !isTruthy(right);
            }
        }
        // unreachable
        return null;
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

}
