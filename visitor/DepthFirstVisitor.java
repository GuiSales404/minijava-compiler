package visitor;

import syntaxtree.*;

public class DepthFirstVisitor implements Visitor {

  public void visit(Program n) {
    n.m.accept(this);
    for ( int i = 0; i < n.cl.size(); i++ ) {
        n.cl.elementAt(i).accept(this);
    }
  }

  public void visit(MainClass n) {
    n.i1.accept(this);
    n.i2.accept(this);
    n.s.accept(this);
  }

  public void visit(ClassDeclSimple n) {
    n.i.accept(this);
    for ( int i = 0; i < n.vl.size(); i++ ) {
        n.vl.elementAt(i).accept(this);
    }
    for ( int i = 0; i < n.ml.size(); i++ ) {
        n.ml.elementAt(i).accept(this);
    }
  }
 
  public void visit(ClassDeclExtends n) {
    n.i.accept(this);
    n.j.accept(this);
    for ( int i = 0; i < n.vl.size(); i++ ) {
        n.vl.elementAt(i).accept(this);
    }
    for ( int i = 0; i < n.ml.size(); i++ ) {
        n.ml.elementAt(i).accept(this);
    }
  }

  public void visit(VarDecl n) {
    n.t.accept(this);
    n.i.accept(this);
  }

  public void visit(MethodDecl n) {
    n.t.accept(this);
    n.i.accept(this);
    for ( int i = 0; i < n.fl.size(); i++ ) {
        n.fl.elementAt(i).accept(this);
    }
    for ( int i = 0; i < n.vl.size(); i++ ) {
        n.vl.elementAt(i).accept(this);
    }
    for ( int i = 0; i < n.sl.size(); i++ ) {
        n.sl.elementAt(i).accept(this);
    }
    n.e.accept(this);
  }

  public void visit(Formal n) {
    n.t.accept(this);
    n.i.accept(this);
  }

  public void visit(IntArrayType n) {
  }

  public void visit(BooleanType n) {
  }

  public void visit(IntegerType n) {
  }

  public void visit(IdentifierType n) {
  }

  public void visit(Block n) {
    for ( int i = 0; i < n.sl.size(); i++ ) {
        n.sl.elementAt(i).accept(this);
    }
  }

  public void visit(If n) {
    n.e.accept(this);
    n.s1.accept(this);
    n.s2.accept(this);
  }

  public void visit(While n) {
    n.e.accept(this);
    n.s.accept(this);
  }

  public void visit(Print n) {
    n.e.accept(this);
  }
  
  public void visit(Assign n) {
    n.i.accept(this);
    n.e.accept(this);
  }

  public void visit(ArrayAssign n) {
    n.i.accept(this);
    n.e1.accept(this);
    n.e2.accept(this);
  }

  public void visit(And n) {
    n.e1.accept(this);
    n.e2.accept(this);
  }

  public void visit(LessThan n) {
    n.e1.accept(this);
    n.e2.accept(this);
  }

  public void visit(Plus n) {
    n.e1.accept(this);
    n.e2.accept(this);
  }

  public void visit(Minus n) {
    n.e1.accept(this);
    n.e2.accept(this);
  }

  public void visit(Times n) {
    n.e1.accept(this);
    n.e2.accept(this);
  }

  public void visit(ArrayLookup n) {
    n.e1.accept(this);
    n.e2.accept(this);
  }

  public void visit(ArrayLength n) {
    n.e.accept(this);
  }

  public void visit(Call n) {
    n.e.accept(this);
    n.i.accept(this);
    for ( int i = 0; i < n.el.size(); i++ ) {
        n.el.elementAt(i).accept(this);
    }
  }

  public void visit(IntegerLiteral n) {
  }

  public void visit(True n) {
  }

  public void visit(False n) {
  }

  public void visit(IdentifierExp n) {
  }

  public void visit(This n) {
  }

  public void visit(NewArray n) {
    n.e.accept(this);
  }

  public void visit(NewObject n) {
  }

  public void visit(Not n) {
    n.e.accept(this);
  }

  public void visit(Identifier n) {
  }
}
