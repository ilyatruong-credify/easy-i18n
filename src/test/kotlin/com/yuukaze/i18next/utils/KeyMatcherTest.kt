package com.yuukaze.i18next.utils

import com.intellij.openapi.util.Pair
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class KeyMatcherTest {
  @Test
  fun testOneVariable() {
    val searchText = "Hello, {{name}}"
    val key = Pair("key.name", "Hello, {{name}}");
    val keyMatcher = VariableKeyMatcher.run(searchText, key)
    assertNotNull(keyMatcher)
    assertEquals(keyMatcher.key, "key.name")
    assertEquals(keyMatcher.params.size, 1)
    assertEquals(keyMatcher.params["name"], "name")
  }

  @Test
  fun testOneVariableDifferentName() {
    val searchText = "Hello, {{personName}}"
    val key = Pair("key.name", "Hello, {{name}}");
    val keyMatcher = VariableKeyMatcher.run(searchText, key)
    assertNotNull(keyMatcher)
    assertEquals(keyMatcher.key, "key.name")
    assertEquals(keyMatcher.params.size, 1)
    assertEquals(keyMatcher.params["name"], "personName")
  }

  @Test
  fun testTwoVariables() {
    val searchText = "Hello, {{personName}} with age {{age}}"
    val key = Pair("key.name", "Hello, {{name}} with age {{age}}");
    val keyMatcher = VariableKeyMatcher.run(searchText, key)
    assertNotNull(keyMatcher)
    assertEquals(keyMatcher.key, "key.name")
    assertEquals(keyMatcher.params.size, 2)
  }

  @Test
  fun testOneRawStringValue() {
    val searchText = "Hello, {{\"Phong Truong Hung\"}}"
    val key = Pair("key.name", "Hello, {{name}}");
    val keyMatcher = VariableKeyMatcher.run(searchText, key)
    assertNotNull(keyMatcher)
    assertEquals(keyMatcher.key, "key.name")
    assertEquals(keyMatcher.params.size, 1)
    assertEquals(keyMatcher.params["name"], "\"Phong Truong Hung\"")
  }

  @Test
  fun testOneRawNumberValue() {
    val searchText = "Hello, {{28}}"
    val key = Pair("key.age", "Hello, {{age}}");
    val keyMatcher = VariableKeyMatcher.run(searchText, key)
    assertNotNull(keyMatcher)
    assertEquals(keyMatcher.key, "key.age")
    assertEquals(keyMatcher.params.size, 1)
    assertEquals(keyMatcher.params["age"], "28")
  }

  @Test
  fun testNoVariable() {
    val searchText = "Hello, Phong"
    val key = Pair("key.name", "Hello, Phong")
    val keyMatcher = KeyMatcherBuilder.run(searchText, key)
    assertNotNull(keyMatcher)
    assertTrue { keyMatcher is SingleKeyMatcher }
//    assertIs<SingleKeyMatcher>(keyMatcher)
  }
}