# Team 06

##  Instruções MIPS Usadas Na Seleção de Instruções (Instruções Equivalentes à Máquina Jouette)

Usando o algoritmo Maximal Munch, fizemos alguns métodos munch, como MunchJump e MunchExp, para gerar códigos para as instruções MIPS que escolhemos. Essas instruções foram escolhidas baseadas nas equivalências do MIPS para a arquitetura Jouette. Além disso, a escolha de tais instruções ficou limitada ao código intermediário (IRTree) do projeto, por exemplo, só é possível realizar operações aritméticas presentes na operação BINOP da IRTree.
Link para as instruções da arquitetura MIPS que utilizamos como referência: 
- [Instruções Mips](https://www.dsi.unive.it/~gasparetto/materials/MIPS_Instruction_Set.pdf)
- [Operações Jouette](https://www.cs.princeton.edu/courses/archive/spr03/cs320/notes/instr-selection.pdf)

### Instruções Aritméticas e Lógicas
- li d0, value: Carrega um valor imediato em um registrador (li $t0, 10`)
- move d0, s0: Move o valor de um registrador para outro (move $t0, $t1)
- addi d0, s0, value: Adiciona um valor imediato a um registrador (addi $t0, $t1, 10)
- add d0, s0, s1: Soma dois registradores (add $t0, $t1, $t2`)
- sub d0, s0, s1: Subtrai dois registradores (sub $t0, $t1, $t2`)
- mul d0, s0, s1: Multiplica dois registradores (mul $t0, $t1, $t2`)
- div s0, s1: Divide dois registradores, o resultado vai para LO e o resto para HI (div $t0, $t1)
- mflo d0: Move o valor do registrador LO para um registrador (mflo $t0`)

### Instruções de Controle de Fluxo
- j label: Salta para a etiqueta especificada (j L1)
- jr s0: Salta para o endereço no registrador (jr $ra`)
- jal label: Salta para a sub-rotina na etiqueta e salva o endereço de retorno em $ra (jal Test_testMethod)
- beq s0, s1, label: Salta para a etiqueta se os registradores forem iguais (beq $t0, $t1, L1)
- bne s0, s1, label: Salta para a etiqueta se os registradores forem diferentes (bne $t0, $t1, L2)
- blt s0, s1, label: Salta para a etiqueta se s0 for menor que s1 (blt $t0, $t1, L3)
- ble s0, s1, label: Salta para a etiqueta se s0 for menor ou igual a s1 (ble $t0, $t1, L4)
- bgt s0, s1, label: Salta para a etiqueta se s0 for maior que s1 (bgt $t0, $t1, L5)
- bge s0, s1, label: Salta para a etiqueta se s0 for maior ou igual a s1 (bge $t0, $t1, L6)

### Instruções de Memória
- sw s0, offset(s1): Armazena um valor de um registrador na memória (sw $t0, 0($t1)`)
- lw d0, offset(s1): Carrega um valor da memória para um registrador (lw $t0, 0($t1)`)

## Status do Projeto por Etapas
- **Analisador Léxico e Sintático:** Implementamos com sucesso o analisador léxico e sintático, fundamentais para a interpretação inicial do código-fonte e a construção da árvore sintática.

- **Árvores Sintática Abstrata e Análise Semântica:** A partir da árvore sintática, geramos a árvore sintática abstrata (AST), permitindo uma análise semântica eficaz que reforça a correção do código em relação ao contexto.

- **Tradução para o Código Intermediário:** A conversão das estruturas de alto nível da AST para uma forma intermediária simplificou a manipulação posterior durante a geração de código, estabelecendo uma representação mais próxima da máquina.

- **Seleção de Instruções:** A fase de seleção de instruções foi implementada para traduzir o código intermediário para instruções do MIPS equivalentes às instruções da arquitetura Jouette. A montagem da IRTree foi bem sucedida, mas há problemas para montar o MIPS com a IRTree.

### Desafios Encontrados
Durante a seleção de instruções, enfrentamos um desafio específico relacionado à carga dos identificadores dos registradores. Apesar do sucesso em implementar todas as etapas até a geração de código para MIPS, os identificadores dos registradores não estão sendo carregados adequadamente no contexto da função codegen no arquivo Codegen.java. Acreditamos que o problema realmente está na nossa função Codegen.java, já que a geração da IRTree está correta. 

### Exemplo de Código
A função codegen ilustra como as instruções são geradas a partir de declarações intermediárias:

```Java
public InstrList codegen(tree.Stm s) {
    munchStm(s);
    InstrList l = ilist;
    ilist = last = null;
    return l;
}
```
Neste método, munchStm(s) é responsável por decompor cada instrução da árvore de sintaxe abstrata em instruções mais específicas da máquina Jouette, gerando uma lista de instruções (InstrList).

## Conclusão
Conseguimos implementar todas as fases de desenvolvimento do compilador até agora, com exceção da carga correta de identificadores de registradores durante a seleção de instruções. 