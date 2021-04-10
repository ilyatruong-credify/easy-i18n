package com.yuukaze.i18next

import java.nio.file.Path
import java.nio.file.Paths

private val USER_HOME_PATH = System.getProperty("user.home")
val PLUGIN_DIRECTORY: Path = Paths.get(USER_HOME_PATH, ".easyI18n")