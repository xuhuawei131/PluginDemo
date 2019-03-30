package com.brycegao.buildsrc

import javassist.CannotCompileException
import javassist.ClassPool
import javassist.CtClass
import javassist.NotFoundException
import javassist.Translator;

class SrcTranslator implements Translator{

  @Override
  void start(ClassPool pool) throws NotFoundException, CannotCompileException {
  }

  @Override
  void onLoad(ClassPool pool, String classname) throws NotFoundException, CannotCompileException {
    println("加载类名称：" + classname)
    CtClass cc = pool.get(classname)
    //cc.detach()
  }
}
