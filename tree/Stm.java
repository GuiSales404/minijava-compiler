package tree;

import java.util.*;
import assem.*;

abstract public class Stm {
	abstract public List<EXP> kids();

	abstract public Stm build(List<EXP> kids);
	abstract public void accept(Codegen codegen);
}
