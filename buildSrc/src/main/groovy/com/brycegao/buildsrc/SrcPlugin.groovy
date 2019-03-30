package com.brycegao.buildsrc

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 新建java类，然后修改文件后缀为groovy
 * 这是插件入口类， 在resources/META-INF/gradle-plugins/目录下配置
 */
class SrcPlugin implements Plugin<Project> {
  @Override
  void apply(Project project) {
    println("this is SrcPlugin")

    def config = project.extensions.create("plugintest", SrcConfig) //配置信息

    def android = project.extensions.findByType(AppExtension)
    //android.registerTransform(new SrcTransform(project, config))
    android.registerTransform(new TimecodeTransform(project, config))
  }
}
