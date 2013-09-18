/**
 * Copyright (C) 2013 Julian Atienza Herrero <j.atienza at har.mrc.ac.uk>
 *
 * MEDICAL RESEARCH COUNCIL UK MRC
 *
 * Harwell Mammalian Genetics Unit
 *
 * http://www.har.mrc.ac.uk
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.mousephenotype.dcc.exportlibrary.xmlvalidationresourcescollection.impress.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import org.mousephenotype.dcc.exportlibrary.xmlvalidationdatastructure.external.impress.ImpressParameterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class Instantiator {

    protected static final Logger logger = LoggerFactory.getLogger(Instantiator.class);
    public static final Set<Class> BASIC_TYPES = new HashSet<Class>(Arrays.asList(
            Boolean.class, boolean.class,
            Character.class, char.class,
            Byte.class, byte.class,
            Short.class, short.class,
            Integer.class, int.class,
            Long.class, long.class,
            Float.class, float.class,
            Double.class, double.class,
            BigDecimal.class,
            BigInteger.class));

    public static Object cast(Class<?> clazz, String value) {
        if(value == null || value.equals("") || value.isEmpty())
            return null;
        if (clazz.equals(String.class)) {
            return value;
        }
        if (BASIC_TYPES.contains(clazz)) {
            if (clazz.equals(Boolean.class) || clazz.equals(boolean.class)) {
                return Boolean.valueOf(value);
            }
            if (clazz.equals(Short.class) || clazz.equals(short.class)) {
                return Short.valueOf(value);
            }
            if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
                return Integer.valueOf(value);
            }
            if (clazz.equals(Long.class) || clazz.equals(long.class)) {
                return Long.valueOf(value);
            }

            if (clazz.equals(Float.class) || clazz.equals(float.class)) {
                return Float.valueOf(value);
            }
            if (clazz.equals(Double.class) || clazz.equals(double.class)) {
                return Double.valueOf(value);
            }

            if (clazz.equals(BigDecimal.class)) {
                return BigDecimal.valueOf(Double.valueOf(value));
            }
            if (clazz.equals(BigInteger.class)) {
                return BigInteger.valueOf(Long.valueOf(value));
            }
        }
        if(clazz.equals(ImpressParameterType.class)){
                return ImpressParameterType.fromValue(value);
            }

        return null;
    }

    public static <T> void getInstance(Class<T> clazz, T object, HashMap<String, String> items) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Field[] declaredFields = clazz.getDeclaredFields();
        javax.xml.bind.annotation.XmlAttribute annotation = null;
        StringBuilder sb = new StringBuilder();
        for (Field field : declaredFields) {
            if (!field.getType().equals(List.class)) {
                annotation = (javax.xml.bind.annotation.XmlAttribute) field.getAnnotation(javax.xml.bind.annotation.XmlAttribute.class);
                if (annotation.required()) {
                    sb.append("set");
                    sb.append(field.getName().substring(0, 1).toUpperCase());
                    sb.append(field.getName().substring(1));
                    logger.debug("invoking {} over {} ", sb.toString(), Instantiator.cast(field.getType(), items.get(annotation.name())));
                    logger.debug("type {}", field.getType().toString());
                    clazz.getMethod(sb.toString(), field.getType()).invoke(object, Instantiator.cast(field.getType(), items.get(annotation.name())));
                    sb = new StringBuilder();
                }
            }
        }
    }
}
