/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]".
 *
 * Copyright © 2010–2011 ApexIdentity Inc. All rights reserved.
 * Portions Copyrighted 2011 ForgeRock AS.
 */

package org.forgerock.openidm.script.javascript;

// Java Standard Edition
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// FEST-Assert
import static org.fest.assertions.Assertions.assertThat;

// TestNG
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

// ForgeRock OpenIDM Core
import org.forgerock.openidm.script.ScriptException;

/**
 * @author Paul C. Bryan
 */
public class JavaScriptTest {

    private HashMap<String, Object> scope;

    // ----- preparation ----------

    @BeforeMethod
    public void beforeMethod() {
        scope = new HashMap<String, Object>();
    }

    // ----- yielded value unit tests ----------

    @Test
    @SuppressWarnings("unchecked")
    public void yieldMapValue() throws ScriptException {
        Object result = new JavaScript("x = { foo: 'bar'}").exec(scope);
        assertThat(result).isInstanceOf(Map.class);
        Map<String, Object> map = (Map)result;
        assertThat(map.get("foo")).isEqualTo("bar");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void yieldListVaue() throws ScriptException {
        Object result = new JavaScript("x = [ 'foo', 'bar' ]").exec(scope);
        assertThat(result).isInstanceOf(List.class);
        List<Object> list = (List)result;
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo("foo");
        assertThat(list.get(1)).isEqualTo("bar");
    }

    @Test
    public void yieldStringValue() throws ScriptException {
        assertThat(new JavaScript("'foo'").exec(scope)).isEqualTo("foo");
    }

    @Test
    public void yieldIntegerValue() throws ScriptException {
        assertThat(new JavaScript("1.0").exec(scope)).isEqualTo(Integer.valueOf(1));
    }

    @Test
    public void yieldDoubleValue() throws ScriptException {
        assertThat(new JavaScript("1.1").exec(scope)).isEqualTo(Double.valueOf(1.1d));
    }

    @Test
    public void yieldTrueValue() throws ScriptException {
        assertThat(new JavaScript("true").exec(scope)).isEqualTo(true);
    }

    @Test
    public void yieldFalseValue() throws ScriptException {
        assertThat(new JavaScript("false").exec(scope)).isEqualTo(false);
    }

    @Test
    public void yieldNullValue() throws ScriptException {
        assertThat(new JavaScript("null").exec(scope)).isNull();
    }

    @Test
    public void notFoundValue() throws ScriptException {
        assertThat(new JavaScript("x = { a: 'b' }; x.c").exec(scope)).isNull();
    }

    // ----- java → javascript type conversion ----------

    @Test
    public void javaMapToJavaScriptObject() throws ScriptException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("boo", "yah");
        map.put("foo", "bar");
        scope.put("map", map);
        assertThat(new JavaScript("map").exec(scope)).isEqualTo(map);
        assertThat(new JavaScript("map.foo == 'bar'").exec(scope)).isEqualTo(true);
    }

    @Test
    public void javaListToJavaScriptArray() throws ScriptException {
        ArrayList<Object> list = new ArrayList<Object>();
        list.add("boo");
        list.add("yah");
        list.add("foo");
        list.add("bar");
        scope.put("list", list);
        assertThat(new JavaScript("list").exec(scope)).isEqualTo(list);
        assertThat(new JavaScript("list.length == 4").exec(scope)).isEqualTo(true);
        assertThat(new JavaScript("list[2] == 'foo'").exec(scope)).isEqualTo(true);
    }

    @Test
    public void javaIntegerToJavaScriptNumber() throws ScriptException {
        scope.put("intValue", Integer.valueOf(1234));
        assertThat(new JavaScript("typeof intValue == 'number'").exec(scope)).isEqualTo(true);
        assertThat(new JavaScript("intValue == 1234").exec(scope)).isEqualTo(true);
    }

    @Test
    public void javaDoubleToJavaScriptNumber() throws ScriptException {
        scope.put("doubleValue", Double.valueOf(12345.6789d));
        assertThat(new JavaScript("typeof doubleValue == 'number'").exec(scope)).isEqualTo(true);
        assertThat(new JavaScript("doubleValue == 12345.6789").exec(scope)).isEqualTo(true);
    }

    // ----- property assignment ----------

    @Test
    @SuppressWarnings("unchecked")
    public void mapPropertyValue() throws ScriptException {
        scope.put("foo", null);
        new JavaScript("foo = { baz: 'qux', boo: 'yah' }").exec(scope);
        Map<String, Object> map = (Map)scope.get("foo");
        assertThat(map.get("baz")).isEqualTo("qux");
        assertThat(map.get("boo")).isEqualTo("yah");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void listPropertyValue() throws ScriptException {
        scope.put("foo", null);
        new JavaScript("foo = [ 'foo', 'bar' ]").exec(scope);
        List<Object> list = (List)scope.get("foo");
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo("foo");
        assertThat(list.get(1)).isEqualTo("bar");
    }

    @Test
    public void stringPropertyValue() throws ScriptException {
        scope.put("foo", null);
        new JavaScript("foo = 'bar'").exec(scope);
        assertThat(scope.get("foo")).isEqualTo("bar");
    }

    @Test
    public void integerPropertyValue() throws ScriptException {
        scope.put("foo", null);
        new JavaScript("foo = 4.0").exec(scope);
        assertThat(scope.get("foo")).isEqualTo(Integer.valueOf(4));
    }

    // ----- scope unit tests ----------

    @Test
    public void excludeTransientProperties() throws ScriptException {
        scope.put("foo", "bar");
        assertThat(new JavaScript("zzz = foo").exec(scope)).isEqualTo("bar");
        assertThat(scope.containsKey("zzz")).isEqualTo(false);
    }

    // ----- exception unit tests ----------

    @Test(expectedExceptions=ScriptException.class)
    public void scriptThrowingException() throws ScriptException {
        new JavaScript("throw 'NotGonnaDoIt'").exec(scope);
    }

    @Test(expectedExceptions=ScriptException.class)
    public void undefinedReference() throws ScriptException {
        new JavaScript("x").exec(scope);
    }

    @Test(expectedExceptions=ScriptException.class)
    public void syntaxError() throws ScriptException {
        new JavaScript("--").exec(scope);
    }
}
