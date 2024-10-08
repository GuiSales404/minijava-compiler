package visitor;
import java.util.*;
import syntaxtree.*;
import table.*;

public class SymbolTableVisitor implements Visitor {
  
  public ClassTable mainTable;
  public List<ClassTable> classTableList = new ArrayList();
  
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

    this.mainTable = new ClassTable(n.i1.toString());
  }
  
  public void visit(ClassDeclSimple n) {
    n.i.accept(this);

    ClassTable newClass = new ClassTable(n.i.toString());
    Method newMethod;
    boolean method_exists = false;
    
    for ( int i = 0; i < n.vl.size(); i++ ) {
        n.vl.elementAt(i).accept(this);
        if(!newClass.attrs.table.containsKey(n.vl.elementAt(i).i.toString())){
          
          newClass.attrs.table.put(n.vl.elementAt(i).i.toString(), n.vl.elementAt(i).t.toString());
        }else{
          //Serror.complain("A variável já foi declarada");
        }
    }

    for ( int i = 0; i < n.ml.size(); i++ ) {
        n.ml.elementAt(i).accept(this);
        newMethod = new Method(n.ml.elementAt(i).i.toString(), n.ml.elementAt(i).t.toString());
        for(Iterator<Method> it =  newClass.methods.iterator(); it.hasNext();)
        {
          Method temp = it.next();
          if(temp.name.equals(newMethod.getName())){
            method_exists = true;
            //error.complain("O método já foi declarado");
            break;
          }
        }
        if(!method_exists)
        {
          for ( int j = 0; j < n.ml.elementAt(i).fl.size(); j++ )
          {
            Formal f     = n.ml.elementAt(i).fl.elementAt(j);
            FormalArg nf = new FormalArg(f.i.toString(), f.t);
            boolean formal_exists = false;
            
            for(Iterator<FormalArg> it = newMethod.formals.iterator(); it.hasNext();)
            {
              FormalArg cf = it.next();
              if(cf.getName().equals(nf.getName()))
              {
                formal_exists= true;
                break;
              }
            }

            if(!formal_exists)
            {
              newMethod.formals.add(nf);
            }

          }
          for ( int j = 0; j < n.ml.elementAt(i).vl.size(); j++ )
          {
            VarDecl var = n.ml.elementAt(i).vl.elementAt(j);
            boolean var_exists = false;
            for(Iterator<FormalArg> it = newMethod.formals.iterator(); it.hasNext();)
            {
              FormalArg f = it.next();
              if(f.getName().equals(var.i.toString()))
              {
                var_exists = true;
                break;
              }
            }

            if( !var_exists && !newMethod.locals.table.containsKey(var.i.toString()) )
            {
              newMethod.locals.table.put(var.i.toString(), var.t.toString());
            }
          }
          newClass.methods.add(newMethod);
        }
    }
  
    this.classTableList.add(newClass);
  }
 
  public void visit(ClassDeclExtends n) {
    n.i.accept(this);
    n.j.accept(this);

    ClassTable parent = null;
    Method newMethod;
    boolean method_exists = false;
    ClassTable newClass = new ClassTable(n.i.toString());

    for (Iterator<ClassTable> it = classTableList.iterator(); it.hasNext();)
    {
      parent = it.next();
      if(parent.getName().equals(n.j.toString())) 
      {
        newClass.parent = parent;
        break;
      }
    }

    if(newClass.parent != null)
    {
      for ( int i = 0; i < n.vl.size(); i++ ) {
          n.vl.elementAt(i).accept(this);
          if(!newClass.attrs.table.containsKey(n.vl.elementAt(i).i.toString())){
            newClass.attrs.table.put(n.vl.elementAt(i).i.toString(), n.vl.elementAt(i).t.toString());
          }else{
            //error.complain("Variável já declarada");
            break;
          }
      }

      for ( int i = 0; i < n.ml.size(); i++ ) {
          n.ml.elementAt(i).accept(this);
          newMethod = new Method(n.ml.elementAt(i).i.toString(), n.ml.elementAt(i).t.toString());
          for(Iterator<Method> it =  newClass.methods.iterator(); it.hasNext();)
          {
            Method temp = it.next();
            if(temp.name.equals(newMethod.getName())){
              method_exists = true;
              //error.complain("O método já foi declarado");
              break;
            }
          }
          if(!method_exists)
          {
            for ( int j = 0; j < n.ml.elementAt(i).fl.size(); j++ )
            {
              Formal f     = n.ml.elementAt(i).fl.elementAt(i);
              FormalArg nf = new FormalArg(f.i.toString(), f.t);
              boolean formal_exists = false;
              
              for(Iterator<FormalArg> it = newMethod.formals.iterator(); it.hasNext();)
              {
                FormalArg cf = it.next();
                if(cf.getName().equals(nf.getName()))
                {
                  formal_exists= true;
                  break;
                }
              }

              if(!formal_exists)
              {
                newMethod.formals.add(nf);
              }

            }
            for ( int j = 0; j < n.ml.elementAt(i).vl.size(); j++ )
            {
              VarDecl var = n.ml.elementAt(i).vl.elementAt(j);
              boolean var_exists = false;
              for(Iterator<FormalArg> it = newMethod.formals.iterator(); it.hasNext();)
              {
                FormalArg f = it.next();
                if(f.getName().equals(var.i.toString()))
                {
                  var_exists = true;
                  break;
                }
              }

              if( !var_exists && !newMethod.locals.table.containsKey(var.i.toString()) )
              {
                newMethod.locals.table.put(var.i.toString(), var.t.toString());
              }
            }
            newClass.methods.add(newMethod);
          }
      }
      this.classTableList.add(newClass);
    }
    else
    {
      System.out.println("Parent class not declared");
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
