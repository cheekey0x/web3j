/*
 * Copyright 2019 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.web3j.abi;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Array;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Bytes;
import org.web3j.abi.datatypes.BytesType;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.DynamicStruct;
import org.web3j.abi.datatypes.NumericType;
import org.web3j.abi.datatypes.StaticArray;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Ufixed;
import org.web3j.abi.datatypes.Uint;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.primitive.PrimitiveType;
import org.web3j.utils.Numeric;

import static org.web3j.abi.datatypes.Type.MAX_BIT_LENGTH;
import static org.web3j.abi.datatypes.Type.MAX_BYTE_LENGTH;

/**
 * Ethereum Contract Application Binary Interface (ABI) encoding for types. Further details are
 * available <a href="https://github.com/ethereum/wiki/wiki/Ethereum-Contract-ABI">here</a>.
 */
public class TypeEncoder {

    private TypeEncoder() {}

    static boolean isDynamic(final Type<?> parameter) {
        return parameter instanceof DynamicBytes
                || parameter instanceof Utf8String
                || parameter instanceof DynamicArray
                || (parameter instanceof StaticArray
                        && DynamicStruct.class.isAssignableFrom(
                                ((StaticArray) parameter).getComponentType()));
    }

    public static String encode(final Type<?> parameter) {
        if (parameter instanceof NumericType) {
            return encodeNumeric(((NumericType) parameter));
        } else if (parameter instanceof Address) {
            return encodeAddress((Address) parameter);
        } else if (parameter instanceof Bool) {
            return encodeBool((Bool) parameter);
        } else if (parameter instanceof Bytes) {
            return encodeBytes((Bytes) parameter);
        } else if (parameter instanceof DynamicBytes) {
            return encodeDynamicBytes((DynamicBytes) parameter);
        } else if (parameter instanceof Utf8String) {
            return encodeString((Utf8String) parameter);
        } else if (parameter instanceof StaticArray) {
            if (DynamicStruct.class.isAssignableFrom(
                    ((StaticArray) parameter).getComponentType())) {
                return encodeStaticArrayWithDynamicStruct((StaticArray) parameter);
            } else {
                return encodeArrayValues((StaticArray<?>) parameter);
            }
        } else if (parameter instanceof DynamicStruct) {
            return encodeDynamicStruct((DynamicStruct) parameter);
        } else if (parameter instanceof DynamicArray) {
            return encodeDynamicArray((DynamicArray<?>) parameter);
        } else if (parameter instanceof PrimitiveType) {
            return encode(((PrimitiveType<?>) parameter).toSolidityType());
        } else {
            throw new UnsupportedOperationException(
                    "Type cannot be encoded: " + parameter.getClass());
        }
    }

    /**
     * Encodes a static array containing a dynamic struct type. In this case, the array items are
     * decoded as dynamic values and have their offsets at the beginning of the encoding. Example:
     * For the following static array containing three elements: <code>StaticArray3</code>
     * enc([struct1, struct2, struct2]) = offset(enc(struct1)) offset(enc(struct2))
     * offset(enc(struct3)) enc(struct1) enc(struct2) enc(struct3)
     *
     * @param
     * @return
     */
    private static <T extends Type> String encodeStaticArrayWithDynamicStruct(Array<T> value) {
        String valuesOffsets = encodeStructsArraysOffsets(value);
        String encodedValues = encodeArrayValues(value);

        StringBuilder result = new StringBuilder();
        result.append(valuesOffsets);
        result.append(encodedValues);
        return result.toString();
    }

    static String encodeAddress(final Address address) {
        return encodeNumeric(address.toUint());
    }

    static String encodeNumeric(final NumericType numericType) {
        final byte[] rawValue = toByteArray(numericType);
        final byte paddingValue = getPaddingValue(numericType);
        final byte[] paddedRawValue = new byte[MAX_BYTE_LENGTH];
        if (paddingValue != 0) {
            Arrays.fill(paddedRawValue, paddingValue);
        }

        System.arraycopy(
                rawValue, 0, paddedRawValue, MAX_BYTE_LENGTH - rawValue.length, rawValue.length);
        return Numeric.toHexStringNoPrefix(paddedRawValue);
    }

    private static byte getPaddingValue(final NumericType numericType) {
        if (numericType.getValue().signum() == -1) {
            return (byte) 0xff;
        } else {
            return 0;
        }
    }

    private static byte[] toByteArray(final NumericType numericType) {
        final BigInteger value = numericType.getValue();
        if (numericType instanceof Ufixed || numericType instanceof Uint) {
            if (value.bitLength() == MAX_BIT_LENGTH) {
                // As BigInteger is signed, if we have a 256 bit value, the resultant byte array
                // will contain a sign byte in it's MSB, which we should ignore for this unsigned
                // integer type.
                final byte[] byteArray = new byte[MAX_BYTE_LENGTH];
                System.arraycopy(value.toByteArray(), 1, byteArray, 0, MAX_BYTE_LENGTH);
                return byteArray;
            }
        }
        return value.toByteArray();
    }

    static String encodeBool(final Bool value) {
        final byte[] rawValue = new byte[MAX_BYTE_LENGTH];
        if (value.getValue()) {
            rawValue[rawValue.length - 1] = 1;
        }
        return Numeric.toHexStringNoPrefix(rawValue);
    }

    static String encodeBytes(final BytesType bytesType) {
        final byte[] value = bytesType.getValue();
        final int length = value.length;
        final int mod = length % MAX_BYTE_LENGTH;

        final byte[] dest;
        if (mod != 0) {
            final int padding = MAX_BYTE_LENGTH - mod;
            dest = new byte[length + padding];
            System.arraycopy(value, 0, dest, 0, length);
        } else {
            dest = value;
        }
        return Numeric.toHexStringNoPrefix(dest);
    }

    static String encodeDynamicBytes(final DynamicBytes dynamicBytes) {
        final int size = dynamicBytes.getValue().length;
        final String encodedLength = encode(new Uint(BigInteger.valueOf(size)));
        final String encodedValue = encodeBytes(dynamicBytes);

        return encodedLength + encodedValue;
    }

    static String encodeString(final Utf8String string) {
        final byte[] utfEncoded = string.getValue().getBytes(StandardCharsets.UTF_8);
        return encodeDynamicBytes(new DynamicBytes(utfEncoded));
    }

    static <T extends Type<?>> String encodeArrayValues(final Array<T> value) {
        final StringBuilder result = new StringBuilder();
        for (final Type<?> type : value.getValue()) {
            result.append(encode(type));
        }
        return result.toString();
    }

    static String encodeDynamicStruct(final DynamicStruct value) {
        String encodedValues = encodeDynamicStructValues(value);

        StringBuilder result = new StringBuilder();
        result.append(encodedValues);
        return result.toString();
    }

    private static String encodeDynamicStructValues(final DynamicStruct value) {
        int staticSize = 0;
        for (int i = 0; i < value.getValue().size(); ++i) {
            final Type type = value.getValue().get(i);
            if (isDynamic(type)) {
                staticSize += 32;
            } else {
                staticSize += type.bytes32PaddedLength();
            }
        }
        int dynamicOffset = staticSize;
        final List<String> offsetsAndStaticValues = new ArrayList<>();
        final List<String> dynamicValues = new ArrayList<>();
        for (int i = 0; i < value.getValue().size(); ++i) {
            final Type type = value.getValue().get(i);
            if (isDynamic(type)) {
                offsetsAndStaticValues.add(
                        Numeric.toHexStringNoPrefix(
                                Numeric.toBytesPadded(
                                        new BigInteger(Long.toString(dynamicOffset)),
                                        MAX_BYTE_LENGTH)));
                String encodedValue = encode(type);
                dynamicValues.add(encodedValue);
                dynamicOffset += encodedValue.length() >> 1;
            } else {
                offsetsAndStaticValues.add(encode(value.getValue().get(i)));
            }
        }
        final List<String> data = new ArrayList<>();
        data.addAll(offsetsAndStaticValues);
        data.addAll(dynamicValues);
        return String.join("", data);
    }

    static <T extends Type<?>> String encodeDynamicArray(final DynamicArray<T> value) {
        final int size = value.getValue().size();
        final String encodedLength = encode(new Uint(BigInteger.valueOf(size)));
        final String valuesOffsets = encodeArrayValuesOffsets(value);
        final String encodedValues = encodeArrayValues(value);

        return encodedLength + valuesOffsets + encodedValues;
    }

    /**
     * Encodes the array values offsets of the to be encrypted dynamic array, which are in our case
     * the heads of the encryption. Refer to
     *
     * @see <a
     *     href="https://docs.soliditylang.org/en/v0.5.3/abi-spec.html#formal-specification-of-the-encoding">encoding
     *     formal specification</a>
     *     <h2>Dynamic structs array encryption</h2>
     *     <p>An array of dynamic structs (ie, structs containing dynamic datatypes) is encoded in
     *     the following way: Considering X = [struct1, struct2] for example enc(X) = head(struct1)
     *     head(struct2) tail(struct1) tail(struct2) with: - tail(struct1) = enc(struct1) -
     *     tail(struct2) = enc(struct2) - head(struct1) = enc(len( head(struct1) head(struct2))) =
     *     enc(64), because the heads are 256bits - head(struct2) = enc(len( head(struct1)
     *     head(struct2) tail(struct1)))
     */
    private static <T extends Type<?>> String encodeArrayValuesOffsets(
            final DynamicArray<T> value) {
        final StringBuilder result = new StringBuilder();
        final boolean arrayOfBytes =
                !value.getValue().isEmpty() && value.getValue().get(0) instanceof DynamicBytes;
        final boolean arrayOfString =
                !value.getValue().isEmpty() && value.getValue().get(0) instanceof Utf8String;
        boolean arrayOfDynamicStructs =
                !value.getValue().isEmpty() && value.getValue().get(0) instanceof DynamicStruct;
        if (arrayOfBytes || arrayOfString) {
            long offset = 0;
            for (int i = 0; i < value.getValue().size(); i++) {
                if (i == 0) {
                    offset = value.getValue().size() * MAX_BYTE_LENGTH;
                } else {
                    final int bytesLength =
                            arrayOfBytes
                                    ? ((byte[]) value.getValue().get(i - 1).getValue()).length
                                    : ((String) value.getValue().get(i - 1).getValue()).length();
                    final int numberOfWords = (bytesLength + MAX_BYTE_LENGTH - 1) / MAX_BYTE_LENGTH;
                    final int totalBytesLength = numberOfWords * MAX_BYTE_LENGTH;
                    offset += totalBytesLength + MAX_BYTE_LENGTH;
                }
                result.append(
                        Numeric.toHexStringNoPrefix(
                                Numeric.toBytesPadded(
                                        new BigInteger(Long.toString(offset)), MAX_BYTE_LENGTH)));
            }
        } else if (arrayOfDynamicStructs) {
            result.append(encodeStructsArraysOffsets(value));
        }
        return result.toString();
    }

    /**
     * Encodes arrays of structs elements offsets. To be used when encoding a dynamic array or a
     * static array containing dynamic structs,
     *
     * @param value DynamicArray or StaticArray containing dynamic structs
     * @return encoded array offset
     */
    private static <T extends Type> String encodeStructsArraysOffsets(Array<T> value) {
        StringBuilder result = new StringBuilder();
        long offset = value.getValue().size();
        List<String> tailsEncoding =
                value.getValue().stream().map(TypeEncoder::encode).collect(Collectors.toList());
        for (int i = 0; i < value.getValue().size(); i++) {
            if (i == 0) {
                offset = offset * MAX_BYTE_LENGTH;
            } else {
                offset += tailsEncoding.get(i - 1).length() / 2;
            }
            result.append(
                    Numeric.toHexStringNoPrefix(
                            Numeric.toBytesPadded(
                                    new BigInteger(Long.toString(offset)), MAX_BYTE_LENGTH)));
        }
        return result.toString();
    }
}
