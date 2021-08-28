package values;

public class CNumber extends Value {

    public Number value;
    public boolean isInteger;

    public CNumber(Number value) {
        this.value = value;
        isInteger = value instanceof Integer;
    }

    public static CNumber add(CNumber num1, CNumber num2) {
        CNumber sum;
        if (num1.isInteger && num2.isInteger) {
            sum = new CNumber(num1.value.intValue() + num2.value.intValue());
        } else {
            sum = new CNumber(num1.value.doubleValue() + num2.value.doubleValue());
        }
        sum.setContext(num1.context);
        return sum;

    }

    public static CNumber subtract(CNumber num1, CNumber num2) {
        CNumber difference;
        if (num1.isInteger && num2.isInteger) {
            difference = new CNumber(num1.value.intValue() - num2.value.intValue());
        } else {
            difference = new CNumber(num1.value.doubleValue() - num2.value.doubleValue());
        }
        difference.setContext(num1.context);
        return difference;
    }

    public static CNumber multiply(CNumber num1, CNumber num2) {
        CNumber product;

        if (num1.isInteger && num2.isInteger) {
            product = new CNumber(num1.value.intValue() * num2.value.intValue());

        } else {
            product = new CNumber(num1.value.doubleValue() * num2.value.doubleValue());
        }

        product.setContext(num1.context);
        return product;
    }

    public static CNumber divide(CNumber num1, CNumber num2) {
        CNumber quotient = new CNumber(num1.value.doubleValue() / num2.value.doubleValue());
        quotient.setContext(num1.context);
        return quotient;
    }

    public static CNumber modulo(CNumber num1, CNumber num2) {
        CNumber modulus;

        if (num1.isInteger && num2.isInteger) {
            modulus = new CNumber(num1.value.intValue() % num2.value.intValue());

        } else {
            modulus = new CNumber(num1.value.doubleValue() % num2.value.doubleValue());
        }

        modulus.setContext(num1.context);
        return modulus;
    }

    public static CNumber integerDivide(CNumber num1, CNumber num2) {
        CNumber quotient = new CNumber(num1.value.intValue() / num2.value.intValue());
        quotient.setContext(num1.context);
        return quotient;
    }

    public static CNumber power(CNumber num1, CNumber num2) {
        CNumber pow = new CNumber(Math.pow(num1.value.doubleValue(), num2.value.doubleValue()));
        pow.setContext(num1.context);
        return pow;
    }

    public static CBoolean isEqual(CNumber num1, CNumber num2) {
        if (num1.isInteger && num2.isInteger) {
            return new CBoolean(num1.value.intValue() == num2.value.intValue());
        } else {
            return new CBoolean(num1.value.doubleValue() == num2.value.doubleValue());
        }
    }

    public static CBoolean isNotEqual(CNumber num1, CNumber num2) {
        if (num1.isInteger && num2.isInteger) {
            return new CBoolean(num1.value.intValue() != num2.value.intValue());
        } else {
            return new CBoolean(num1.value.doubleValue() != num2.value.doubleValue());
        }
    }

    public static CBoolean lessThan(CNumber num1, CNumber num2) {
        if (num1.isInteger && num2.isInteger) {
            return new CBoolean(num1.value.intValue() < num2.value.intValue());
        } else {
            return new CBoolean(num1.value.doubleValue() < num2.value.doubleValue());
        }
    }

    public static CBoolean lessThanEquals(CNumber num1, CNumber num2) {
        if (num1.isInteger && num2.isInteger) {
            return new CBoolean(num1.value.intValue() <= num2.value.intValue());
        } else {
            return new CBoolean(num1.value.doubleValue() <= num2.value.doubleValue());
        }
    }

    public static CBoolean greaterThan(CNumber num1, CNumber num2) {
        if (num1.isInteger && num2.isInteger) {
            return new CBoolean(num1.value.intValue() > num2.value.intValue());
        } else {
            return new CBoolean(num1.value.doubleValue() > num2.value.doubleValue());
        }
    }

    public static CBoolean greaterThanEquals(CNumber num1, CNumber num2) {
        if (num1.isInteger && num2.isInteger) {
            return new CBoolean(num1.value.intValue() >= num2.value.intValue());
        } else {
            return new CBoolean(num1.value.doubleValue() >= num2.value.doubleValue());
        }
    }

    public CNumber copy() {
        CNumber copy = new CNumber((this.value));
        copy.setPositon(this.startPosition, this.endPosition);
        copy.context = this.context;
        return copy;
    }

    public String toString() {
        return this.value.toString();
    }

    public boolean equals(Object secondNumber) {
        if (secondNumber instanceof CNumber)
            return this.value.doubleValue() == ((CNumber) secondNumber).value.doubleValue();

        return false;
    }

}
