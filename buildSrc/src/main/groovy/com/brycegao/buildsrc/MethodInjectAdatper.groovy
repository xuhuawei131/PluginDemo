package com.brycegao.buildsrc

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes;

class MethodInjectAdatper extends ClassVisitor implements Opcodes{

  MethodInjectAdatper(ClassVisitor classVisitor) {
    super(Opcodes.ASM5, classVisitor)
  }

  @Override
  MethodVisitor visitMethod(int access, final String name, String desc, String signature,
      String[] exceptions) {

    MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions)


    return methodVisitor
  }
}
