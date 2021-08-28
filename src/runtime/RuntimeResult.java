package runtime;

import errors.Error;
import values.Value;

public class RuntimeResult {
    public Value value;
    public Error error;
    public boolean isError;

    public RuntimeResult() {
        isError = false;
    }

    public Value register(RuntimeResult result) {
        if (result.isError) {
            this.isError = true;
            this.error = result.error;
        }

        return result.value;
    }

    public RuntimeResult setResult(Value value) {
        this.value = value;
        return this;
    }

    public RuntimeResult setError(Error error) {
        this.error = error;
        this.isError = true;

        return this;

    }
}
