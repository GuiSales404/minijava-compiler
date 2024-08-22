package visitor;
import java.util.*;
import syntaxtree.*;
import table.*;

public class TypeCheckVisitor implements TypeVisitor {
  
  SymbolTableVisitor stv;
  ClassTable mainTable;
  List<ClassTable> classTableList;
  
  ClassTable classe_atual = null;
  ClassTable classe_call_global  = null;
  Method     metodo_atual = null;


  public TypeCheckVisitor(SymbolTableVisitor stv)
  {
    this.stv       = stv;
    mainTable      = stv.mainTable;
    classTableList = stv.classTableList;
  }


  public Type visit(Program n) {
    this.classe_atual = this.mainTable;
    n.m.accept(this);
    for ( int i = 0; i < n.cl.size(); i++ ) {
        this.classe_atual = this.classTableList.get(i);
        n.cl.elementAt(i).accept(this);
    }
    return null;
  }
  
  public Type visit(MainClass n) {
    n.i1.accept(this);
    n.s.accept(this);

    return null;
  }
  
  public Type visit(ClassDeclSimple n) {
    n.i.accept(this);
    for ( int i = 0; i < n.vl.size(); i++ ) {
        n.vl.elementAt(i).accept(this);
    }
    for ( int i = 0; i < n.ml.size(); i++ ) {
        this.metodo_atual = this.classe_atual.methods.get(i);
        n.ml.elementAt(i).accept(this);
    }
    return null;
  }

  public Type visit(ClassDeclExtends n) {
    n.i.accept(this);
    n.j.accept(this);
    for ( int i = 0; i < n.vl.size(); i++ ) {
        n.vl.elementAt(i).accept(this);
    }
    for ( int i = 0; i < n.ml.size(); i++ ) {
        this.metodo_atual = this.classe_atual.methods.get(i);
        n.ml.elementAt(i).accept(this);
    }
    return null;
  }

  public Type visit(VarDecl n) {
    n.t.accept(this);
    n.i.accept(this);
    return null;
  }

  public Type visit(MethodDecl n) {
    Type mt = n.t.accept(this);

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
    Type et = n.e.accept(this);

    if(!(et.toString().equals(mt.toString())))
    {
      System.out.println("Tipo de retorno diferente do tipo do método");
      return null;
    }
    
    if(mt.toString().equals("int"))
    {
      return new IntegerType();
    }
    else if(mt.toString().equals("int[]"))
    {
      return new IntArrayType();
    }
    else if(mt.toString().equals("boolean"))
    {
      return new BooleanType();
    }
    else 
    {
      return new IdentifierType(mt.toString());
    }
  }


  public Type visit(Formal n) {
    n.t.accept(this);
    n.i.accept(this);
    return null;
  }

  public Type visit(IntArrayType n) {
    return new IntArrayType();
  }

  public Type visit(BooleanType n) {
    return new BooleanType();
  }

  public Type visit(IntegerType n) {
    return new IntegerType();
  }


  public Type visit(IdentifierType n) {
    boolean declared = false;
    for( Iterator<ClassTable> it = this.classTableList.iterator(); it.hasNext(); )
    {
      ClassTable cl = it.next();
      if(cl.getName().equals(n.s))
      {
        declared = true;
      }
    }
    if(declared)
    {
      return new IdentifierType(n.toString());
    }
    return null;

  }

  public Type visit(Block n) {
    for ( int i = 0; i < n.sl.size(); i++ ) {
        n.sl.elementAt(i).accept(this);
    }
    return null;
  }

  public Type visit(If n) {
    Type et = n.e.accept(this);

    if(!(et instanceof BooleanType))
    {
      System.out.println("Expressão precisa ser do tipo Boolean");
      return null;
    }
    n.s1.accept(this);
    n.s2.accept(this);
    return null;
  }

  public Type visit(While n) {
    Type et = n.e.accept(this);

    if(!(et instanceof BooleanType))
    {
      System.out.println("Expressão precisa ser do tipo Boolean");
      return null;
    }

    n.s.accept(this);
    return null;
  }

  public Type visit(Print n) {
    Type et = n.e.accept(this);
    if (!(et instanceof IntegerType))
    {
      System.out.println("Só imprime inteiro");
      return null;
    }
    return null;
  }
  
  public Type visit(Assign n) {
      Type itype = n.i.accept(this);
      Type et = n.e.accept(this);
      List<String> possible_types = new ArrayList();
      if(et instanceof IdentifierType)
      {
        ClassTable type_table = null;
        for(Iterator<ClassTable> it = classTableList.iterator(); it.hasNext();)
        {
          ClassTable temp = it.next();
          if (temp.getName().equals(et.toString()))
          {
            type_table = temp;
          }
        }
        
        possible_types.add(type_table.getName());
        boolean has_parent = false;
        if(type_table.parent != null)
        {
          has_parent = true;
          type_table = type_table.parent;
        }

        while(has_parent)
        {
          possible_types.add(type_table.getName());
          if(type_table.parent != null )
          {
            type_table = type_table.parent;
          }
          else
          {
            has_parent=false;
          }
        }

        boolean match = false;
        for(Iterator<String> it = possible_types.iterator(); it.hasNext();)
        {
          String t = it.next();
          if(t.equals(itype.toString()))
          {
            match = true;
            break;
          }
        }
        if (!match)
        {
          System.out.println("Tipo esperado diferente do tipo passado1");
          return null;
        }

      }
      else
      {
        if(!(itype.toString().equals(et.toString())))
        {
          System.out.println("Tipo esperado diferente do tipo passado2");
          return null;
        }
      }
      return null;
  } 

  public Type visit(ArrayAssign n) {

    Type it  = n.i.accept(this);
    Type et1 = n.e1.accept(this);
    Type et2 = n.e2.accept(this);

    if(!(it instanceof IntArrayType))
    {
      System.out.println("Variável não é um vetor");
      return null;
    }

    if(!(et1 instanceof IntegerType))
    {
      System.out.println("Index precisar ser um inteiro");
      return null;
    }
    if(!(et2 instanceof IntegerType))
    {
      System.out.println("Valor atribuído precisa ser um inteiro");
      return null;
    }
    return null;
  }

  public Type visit(And n) {

    Type et1 = n.e1.accept(this);
    Type et2 = n.e2.accept(this);

    if(!((et1 instanceof BooleanType) && (et2 instanceof BooleanType)))
    {
      System.out.println("Ambos operandos precisam ter tipo boolean");
      return null;
    }

    return new BooleanType();
  }

  public Type visit(LessThan n) {

    Type et1 = n.e1.accept(this);
    Type et2 = n.e2.accept(this);

    if(!((et1 instanceof IntegerType) && (et2 instanceof IntegerType)))
    {
      System.out.println("Ambos operandos precisam ter tipo int");
      return null;
    }

    return new BooleanType();
  }

  public Type visit(Plus n) {

    Type et1 = n.e1.accept(this);
    Type et2 = n.e2.accept(this);

    if(!((et1 instanceof IntegerType) && (et2 instanceof IntegerType)))
    {
      System.out.println("Ambos operandos precisam ter tipo int");
      return null;
    }

    return new IntegerType();
  }

  public Type visit(Minus n) {

    Type et1 = n.e1.accept(this);
    Type et2 = n.e2.accept(this);

    if(!((et1 instanceof IntegerType) && (et2 instanceof IntegerType)))
    {
      System.out.println("Ambos operandos precisam ter tipo int");
      return null;
    }

    return new IntegerType();
  }

  public Type visit(Times n) {

    Type et1 = n.e1.accept(this);
    Type et2 = n.e2.accept(this);

    if(!((et1 instanceof IntegerType) && (et2 instanceof IntegerType)))
    {
      System.out.println("Ambos operandos precisam ter tipo int");
      return null;
    }

    return new IntegerType();
  }

  public Type visit(ArrayLookup n) {
    Type et1 = n.e1.accept(this);
    Type et2 = n.e2.accept(this);

    if(!(et1 instanceof IntArrayType))
    {
      System.out.println("Variável não é um vetor");
      return null;
    }

    if(!(et2 instanceof IntegerType))
    {
      System.out.println("O index precisa ser um inteiro");
      return null;
    }

    return new IntegerType();
  }


  public Type visit(ArrayLength n) {
    Type et = n.e.accept(this);

    if(!(et instanceof IntArrayType))
    {
      System.out.println("Variável não é um vetor");
      return null;
    }

    return new IntegerType();
  }


  public Type visit(Call n) {
    Type et = n.e.accept(this);
    ClassTable class_call = null;
    Method method_call = null;

    if (!(et instanceof IdentifierType)) 
    {
      System.out.println("Erro de tipo");
      return null;
    }

    for(Iterator<ClassTable> it =  classTableList.iterator(); it.hasNext();)
    {
      ClassTable class_it = it.next();
      if(et.toString().equals(class_it.name))
      {
        class_call = class_it;
        this.classe_call_global = class_it;
        break;
      }
    }

    if(class_call == null)
    {
      System.out.println("Classe não declarada");
      return null;
    }
    
    n.i.accept(this); 
    this.classe_call_global = null; 

    for(Iterator<Method> it = class_call.methods.iterator(); it.hasNext();)
    {
      Method method_it = it.next();
      if(n.i.toString().equals(method_it.name))
      {
        method_call = method_it;
        break;
      }
    }
    
    if(method_call == null)
    {
      boolean has_parent = false;
      ClassTable parent = null;
      if(class_call.parent != null)
      {
        has_parent = true;
        parent = class_call.parent;
      }

      while(has_parent && method_call==null)
      {
        for(Iterator<Method> it = parent.methods.iterator(); it.hasNext();)
        {
          Method method_it = it.next();
          if(n.i.toString().equals(method_it.name))
          {
            method_call = method_it;
            break;
          }
        }
        if(parent.parent != null)
        {
          parent = parent.parent;
        }
        else
        {
          has_parent = false;
        }
      }
    }


    if(method_call == null)
    {
      System.out.println("Método não declarado");
      return null;
    }

    for ( int i = 0; i < n.el.size(); i++ ) {
        Type pt = n.el.elementAt(i).accept(this);
        List<String> possible_types = new ArrayList();
        if(pt instanceof IdentifierType)
        {
          ClassTable type_table = null;
          for(Iterator<ClassTable> it = classTableList.iterator(); it.hasNext();)
          {
            ClassTable temp = it.next();
            if (temp.getName().equals(pt.toString()))
            {
              type_table = temp;
            }
          }
          
          possible_types.add(type_table.getName());
          boolean has_parent = false;
          if(type_table.parent != null)
          {
            has_parent = true;
            type_table = type_table.parent;
          }

          while(has_parent)
          {
            possible_types.add(type_table.getName());
            if(type_table.parent != null )
            {
              type_table = type_table.parent;
            }
            else
            {
              has_parent=false;
            }
          }

          boolean match = false;
          for(Iterator<String> it = possible_types.iterator(); it.hasNext();)
          {
            String t = it.next();
            if(t.equals(method_call.formals.get(i).getType().toString()))
            {
              match = true;
              break;
            }
          }
          if (!match)
          {
            System.out.println("Tipo esperado diferente do tipo passado3");
            return null;
          }

        }
        else
        {
          if(!(method_call.formals.get(i).getType().toString().equals(pt.toString())))
          {
            System.out.println("Tipo esperado diferente do tipo passado4");
            return null;
          }
        }
    }


    if(method_call.type == "int")
    {
      return new IntegerType();
    }
    else if(method_call.type == "int[]")
    {
      return new IntArrayType();
    }
    else if(method_call.type == "boolean")
    {
      return new BooleanType();
    }
    else
    {
      return new IdentifierType(method_call.type);
    }
  }


  public Type visit(IntegerLiteral n) {
    return new IntegerType();
  }

  public Type visit(True n) {
    return new BooleanType();
  }

  public Type visit(False n) {
    return new BooleanType();

  }


  public Type visit(IdentifierExp n) {
    String t = null;
    

    for(Iterator<FormalArg> it = this.metodo_atual.formals.iterator(); it.hasNext();)
    {
      FormalArg fa = it.next();
      if (fa.getName().equals(n.s))
      {
        t = fa.getType().toString();
      }
    }


    if((t == null) && metodo_atual.locals.table.containsKey(n.s))
    {
      t = metodo_atual.locals.table.get(n.s);
    }


    if((t == null) && classe_atual.attrs.table.containsKey(n.s))
    {
      t = classe_atual.attrs.table.get(n.s);
    }

    boolean has_parent = false;
    ClassTable parent = null;
    if((t == null) && (classe_atual.parent != null))
    {
      has_parent = true;
      parent = classe_atual.parent;
    }

    while(has_parent)
    {
      if(parent.attrs.table.containsKey(n.s))
      {
        t = parent.attrs.table.get(n.s);
        break;
      }
      if(parent.parent != null)
      {
        parent = parent.parent;
      }
      else
      {
        has_parent = false;
      }
    }

    if(t == null)
    {
      System.out.println("Identificador não declarado");
      return null;
    }
    else
    {
      if(t.equals("int"))
      {
        return new IntegerType();
      }
      else if(t.equals("int[]"))
      {
        return new IntArrayType();
      }
      else if(t.equals("boolean"))
      {
        return new BooleanType();
      }
      else
      {
        return new IdentifierType(t);
      }
    }
  }

  public Type visit(This n) {
    return new IdentifierType(classe_atual.getName());
  }


  public Type visit(NewArray n) {
    Type et = n.e.accept(this);

    if(!(et instanceof IntegerType))
    {
      System.out.println("Expressão precisa ser um inteiro");
      return null;
    }
    return new IntArrayType();
  }


  public Type visit(NewObject n) {
    Type t = n.i.accept(this);
    if(!(t instanceof IdentifierType))
    {
      System.out.println("Classe não é um identificador válido");
      return null;
    }
    return new IdentifierType(t.toString());
    
  }


  public Type visit(Not n) {
    Type et = n.e.accept(this);

    if(!(et instanceof BooleanType))
    {
      System.out.println("Expressão precisa ter tipo boolean");
      return null;
    }
    return new BooleanType();
  }


  public Type visit(Identifier n) {
    Type t = null;

    if(this.mainTable.getName().equals(n.s))
    {
      return null;
    }

    for(Iterator<ClassTable> it =  this.classTableList.iterator(); it.hasNext();)
    {
      ClassTable cl = it.next();
      if(cl.getName().equals(n.s))
      {
        return new IdentifierType(n.s);
      }
    }

    if(metodo_atual != null) 
    {
      if(this.metodo_atual.getName().equals(n.s))
      {
        if(this.metodo_atual.getType().equals("int"))
        {
          return new IntegerType();
        }
        else if(this.metodo_atual.getType().equals("int[]"))
        {
          return new IntArrayType();
        }
        else if(this.metodo_atual.getType().equals("boolean"))
        {
          return new BooleanType();
        }
        else
        {
          return new IdentifierType(this.metodo_atual.getType());
        }
      }

      for(Iterator<FormalArg> it = this.metodo_atual.formals.iterator(); it.hasNext();)
      {
        FormalArg fa = it.next();
        if (fa.getName().equals(n.s))
        {
          t = fa.getType();
        }
      }

      if(metodo_atual.locals.table.containsKey(n.s))
      {
        String temp = metodo_atual.locals.table.get(n.s);

        if(temp.equals("int"))
        {
          return new IntegerType();
        }
        else if(temp.equals("int[]"))
        {
          return new IntArrayType();
        }
        else if(temp.equals("boolean"))
        {
          return new BooleanType();
        }
        else
        {
          return new IdentifierType(temp);
        }
      }
    }

    if(classe_atual.attrs.table.containsKey(n.s))
    {
      String temp = classe_atual.attrs.table.get(n.s);

      if(temp.equals("int"))
      {
        return  new IntegerType();
      }
      else if(temp.equals("int[]"))
      {
        return new IntArrayType();
      }
      else if(temp.equals("boolean"))
      {
        return new BooleanType();
      }
      else
      {
        return new IdentifierType(temp);
      }
    }

    boolean has_parent = false;
    ClassTable parent = null;
    if(classe_atual.parent != null)
    {
      has_parent = true;
      parent = classe_atual.parent;
    }

    while(has_parent)
    {
      if(parent.attrs.table.containsKey(n.s))
      {
        String temp = parent.attrs.table.get(n.s);
        if(temp.equals("int"))
        {
          return  new IntegerType();
        }
        else if(temp.equals("int[]"))
        {
          return new IntArrayType();
        }
        else if(temp.equals("boolean"))
        {
          return new BooleanType();
        }
        else
        {
          return new IdentifierType(temp);
        }
      }
      else
      {
        if(parent.parent != null)
        {
          parent = parent.parent;
        }
        else
        {
          has_parent = false;
        }
      }
    }
    

    if(classe_call_global != null)
    {
        for (Iterator<Method> it = this.classe_call_global.methods.iterator(); it.hasNext();)
        {
          Method metodo = it.next();
          String temp = metodo.getType();
          if(metodo.getName().equals(n.s))
          {
            if(metodo.getType().equals("int"))
            {
              return  new IntegerType();
            }
            else if(metodo.getType().equals("int[]"))
            {
              return new IntArrayType();
            }
            else if(metodo.getType().equals("boolean"))
            {
              return new BooleanType();
            }
            else
            {
              return new IdentifierType(temp);
            }
          }
        }
    }

    if(t==null)
    {
      System.out.println("Variável não declarada");
      return null;
    }
    else
    {
      if(t instanceof IdentifierType)
      {
        return new IdentifierType(t.toString());
      }
      if(t instanceof IntegerType)
      {
        return new IntegerType();
      }
      if(t instanceof IntArrayType)
      {
        return new IntArrayType();
      }
      if(t instanceof BooleanType)
      {
        return new BooleanType();
      }
    }
    return null;
  }

}
