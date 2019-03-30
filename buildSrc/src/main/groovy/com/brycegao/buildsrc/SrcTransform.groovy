package com.brycegao.buildsrc

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import javassist.ClassPool
import javassist.Loader
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

//核心类
class SrcTransform extends Transform {
  Project mProject
  SrcConfig mConfig
  ClassPool mClassPool

  SrcTransform(Project project, SrcConfig config) {
    this.mProject = project
    this.mConfig  = config
    mClassPool = ClassPool.getDefault()
  }

  //干活的
  @Override
  void transform(TransformInvocation transformInvocation)
      throws TransformException, InterruptedException, IOException {
    if (!mConfig.enabled) {
      println("禁用SrcTransform")
      return
    }

    def jarPath = SrcUtils.getJarPostion(mProject)

    transformInvocation.inputs.each { TransformInput input ->

      //遍历所有jar
      input.jarInputs.each { JarInput jarInput ->
        def jarName = jarInput.name
        def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
        if (jarName.endsWith(".jar")) {
          jarName = jarName.substring(0, jarName.length() - 4)  //删除后缀
        }

        //重命名输出文件 （同名文件拷贝时会冲突）
        def dest = transformInvocation.outputProvider.getContentLocation(jarName+md5Name,
                   jarInput.contentTypes, jarInput.scopes, Format.JAR)

        //字节码注入
        def modifyJar = SrcUtils.modifyJar(jarInput.file,
            transformInvocation.context.getTemporaryDir(), md5Name)

        //替换为目标jar
        FileUtils.copyFile(modifyJar, dest)
      }


      //遍历所有文件夹
      input.directoryInputs.each { DirectoryInput directoryInput ->

        //println("目录：" + directoryInput.name)
      }

    }
    super.transform(transformInvocation)
  }

  //名称
  @Override
  String getName() {
    return "SrcTransform"
  }

  /**
   * Returns the type(s) of data that is consumed by the Transform. This may be more than
   * one type.
   * 表示当前类能处理的数据类型
   * <strong>This must be of type {@link QualifiedContent.DefaultContentType}</strong>
   */
  @Override
  Set<QualifiedContent.ContentType> getInputTypes() {
    return TransformManager.CONTENT_CLASS
  }


  /**
   * Returns the scope(s) of the Transform. This indicates which scopes the transform consumes.
   * 作用域
   */
  @Override
  Set<? super QualifiedContent.Scope> getScopes() {
    return TransformManager.SCOPE_FULL_PROJECT
  }

  /**
   * Returns whether the Transform can perform incremental work.
   *
   * <p>If it does, then the TransformInput may contain a list of changed/removed/added files, unless
   * something else triggers a non incremental run.
   */
  @Override
  boolean isIncremental() {
    return false   //不支持差异编译
  }


}
