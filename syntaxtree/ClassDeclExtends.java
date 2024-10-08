package syntaxtree;

import visitor.Visitor;
import visitor.TypeVisitor;
import visitor.IRVisitor;
import translate.Ex;

public class ClassDeclExtends extends ClassDecl {
  public Identifier i;
  public Identifier j;
  public VarDeclList vl;
  public MethodDeclList ml;

  public ClassDeclExtends(Identifier ai, Identifier aj,
      VarDeclList avl, MethodDeclList aml) {
    i = ai;
    j = aj;
    vl = avl;
    ml = aml;
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }

  public Ex accept(IRVisitor v) {
    return v.visit(this);
  }
}
