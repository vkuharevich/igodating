package com.igodating.commons.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.StringJoiner;

public class ParameterizedTypeImpl implements ParameterizedType {

    private final Type rawType;
    private final Type[] actualTypeArguments;

    /**
     * A new parameterized type
     *
     * @param rawType             The raw type of this type
     * @param actualTypeArguments The actual type arguments
     */
    public ParameterizedTypeImpl(Type rawType, Type... actualTypeArguments) {
        this.rawType = rawType;
        this.actualTypeArguments = actualTypeArguments;
    }

    /* (non-Javadoc)
     * @see java.lang.reflect.ParameterizedType#getActualTypeArguments()
     */
    @Override
    public Type[] getActualTypeArguments() {
        return actualTypeArguments;
    }

    /* (non-Javadoc)
     * @see java.lang.reflect.ParameterizedType#getRawType()
     */
    @Override
    public Type getRawType() {
        return rawType;
    }

    /* (non-Javadoc)
     * @see java.lang.reflect.ParameterizedType#getOwnerType()
     * This is only used for top level types
     */
    @Override
    public Type getOwnerType() {
        return null;
    }

    @Override
    public int hashCode() {
        int retVal = Arrays.hashCode(actualTypeArguments);
        if (rawType == null) return retVal;
        return retVal ^ rawType.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof ParameterizedType other)) return false;

        if (!rawType.equals(other.getRawType())) return false;

        Type[] otherActual = other.getActualTypeArguments();

        if (otherActual.length != actualTypeArguments.length) {
            return false;
        }

        for (int lcv = 0; lcv < otherActual.length; lcv++) {
            if (!actualTypeArguments[lcv].equals(otherActual[lcv])) {
                return false;
            }
        }

        return true;
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(rawType.getTypeName());
        if (actualTypeArguments != null) {
            StringJoiner sj = new StringJoiner(", ", "<", ">");
            sj.setEmptyValue("");
            for (Type t : actualTypeArguments) {
                sj.add(t.getTypeName());
            }
            sb.append(sj);
        }

        return sb.toString();
    }
}

