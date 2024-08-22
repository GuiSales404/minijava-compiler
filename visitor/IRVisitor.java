package visitor;

import tree.*;
import translate.Ex;
import syntaxtree.*;

public interface IRVisitor {
  public Ex visit(syntaxtree.Program n);            
  public Ex visit(syntaxtree.MainClass n);          
  public Ex visit(syntaxtree.ClassDeclSimple n);    
  public Ex visit(syntaxtree.ClassDeclExtends n);   
  public Ex visit(syntaxtree.VarDecl n);            
  public Ex visit(syntaxtree.MethodDecl n);         
  public Ex visit(syntaxtree.Formal n);             
  public Ex visit(syntaxtree.IntArrayType n);       
  public Ex visit(syntaxtree.BooleanType n);        
  public Ex visit(syntaxtree.IntegerType n);        
  public Ex visit(syntaxtree.IdentifierType n);     
  public Ex visit(syntaxtree.Block n);              
  public Ex visit(syntaxtree.If n);                 
  public Ex visit(syntaxtree.While n);              
  public Ex visit(syntaxtree.Print n);              
  public Ex visit(syntaxtree.Assign n);             
  public Ex visit(syntaxtree.ArrayAssign n);        
  public Ex visit(syntaxtree.And n);                
  public Ex visit(syntaxtree.LessThan n);           
  public Ex visit(syntaxtree.Plus n);               
  public Ex visit(syntaxtree.Minus n);              
  public Ex visit(syntaxtree.Times n);              
  public Ex visit(syntaxtree.ArrayLookup n);        
  public Ex visit(syntaxtree.ArrayLength n);        
  public Ex visit(syntaxtree.Call n);               
  public Ex visit(syntaxtree.IntegerLiteral n);     
  public Ex visit(syntaxtree.True n);               
  public Ex visit(syntaxtree.False n);              
  public Ex visit(syntaxtree.IdentifierExp n);      
  public Ex visit(syntaxtree.This n);               
  public Ex visit(syntaxtree.NewArray n);           
  public Ex visit(syntaxtree.NewObject n);           
  public Ex visit(syntaxtree.Not n);                
  public Ex visit(syntaxtree.Identifier n);         
}
