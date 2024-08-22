import java.util.*;
import syntaxtree.*;
import visitor.*;
import table.*;
import frame.*;
import mips.*;
import translate.*;
import tree.*;
import temp.*;
import canon.*;
import assem.*;
import dumb.DumbAlloc;
import temp.DefaultMap;
import temp.CombineMap;

public class Main {
   public static void main(String [] args) {
      try {
         Program root = new MiniJavaParser(System.in).Goal();
         System.out.println("Program parsed successfully");
         
         SymbolTableVisitor stv = new SymbolTableVisitor();
         root.accept(stv);
         
         TypeCheckVisitor tcv = new TypeCheckVisitor(stv);
         root.accept(tcv);
         
         // PrettyPrintVisitor printSyntaxTree = new PrettyPrintVisitor();
         // root.accept(printSyntaxTree);
         
         MipsFrame frame = new MipsFrame();    
         
         IRTranslatorVisitor irtv = new IRTranslatorVisitor(frame, tcv);
         root.accept(irtv);
         
         tree.Print printer = new tree.Print(System.out);
         
         Frag next_frag = irtv.frags.getNext();
         
         List<TraceSchedule> trace_list = new ArrayList<TraceSchedule>();

         // while(next_frag.hasNext())
         // {
         //    Stm body = ((ProcFrag)next_frag).body;
         //    TraceSchedule trace_schedule = new TraceSchedule(new BasicBlocks(Canon.linearize(body)));
         //    trace_list.add(trace_schedule);
         //    StmList stmList = trace_schedule.stms;

         //    System.out.println("------ Intermediate code ------");
         //    while (stmList!=null) {
         //       printer.prStm(stmList.head);
         //       stmList = stmList.tail;
         //    }

         //    InstrList instr_list = frame.codegen(trace_schedule.stms);
         //    // ((ProcFrag)next_frag).frame.procEntryExit2(instr_list);
         //    // ((ProcFrag)next_frag).frame.procEntryExit3(instr_list);

         //    System.out.println("-------- Mips code -------- ");
         //    while (instr_list != null) {
         //       if (instr_list.head != null) {
         //          System.out.println(instr_list.head.Assem);
         //       }
         //       instr_list = instr_list.tail;
         //    }

         //    System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxx");
         //    next_frag = next_frag.getNext();
         // }

         while(next_frag.hasNext())
         {
            Stm body = ((ProcFrag)next_frag).body;
            TraceSchedule trace_schedule = new TraceSchedule(new BasicBlocks(Canon.linearize(body)));
            trace_list.add(trace_schedule);
            next_frag = next_frag.getNext();
         }

         Stm body = ((ProcFrag)next_frag).body;
         TraceSchedule trace_schedule = new TraceSchedule(new BasicBlocks(Canon.linearize(body)));
         trace_list.add(trace_schedule);
         StmList stml = trace_schedule.stms;
         
         System.out.println("------ Intermediate code ------");
         printer.prStm(stml.head);
         while(stml.tail != null)
         {
            stml = stml.tail;
            printer.prStm(stml.head);
         }

         System.out.println("-------- Mips code -------- ");
         for(Iterator<TraceSchedule> it = trace_list.iterator(); it.hasNext();)
         {
            InstrList instrList = frame.codegen(it.next().stms);

            // DumbAlloc dumb = new DumbAlloc(((ProcFrag)next_frag).frame, instrList);

            while(instrList != null)
            {
               if(instrList.head != null)
               {
                  System.out.println(instrList.head.Assem);
               }
               instrList = instrList.tail;
            }
         }

         System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxx");
      }
      catch (ParseException e) {
         System.out.println(e.toString());
      }
   }
}