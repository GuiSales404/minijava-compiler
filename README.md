# MiniJava Compiler - Team 06

## Project Description
This project is a compiler developed for the Compiler Construction course, focused on the MiniJava language. The compiler implements various phases of the compilation process, from lexical analysis to intermediate code generation and instruction selection for the MIPS architecture.

## Project Structure
The project is organized as follows:

- **Lexical and Syntactic Analysis:** Implementation of lexical and syntactic analysis using JavaCC, responsible for the initial interpretation of the source code and the construction of the syntax tree.
- **Semantic Analysis:** Analysis of the abstract syntax tree (AST) to ensure the correctness of the code concerning the context.
- **Intermediate Code Generation:** Conversion of the AST into an intermediate form, preparing the code for the final translation.
- **Instruction Selection:** Translation of the intermediate code into MIPS instructions, based on the Jouette architecture.

## Usage Instructions

### Prerequisites
- **Java Development Kit (JDK)** - version 8 or higher
- **JavaCC** - Java Compiler Compiler

### Compiling the Project
1. Clone the repository or extract the project files.
2. Navigate to the project directory:
    ```bash
    cd team-06-parser_v5
    ```
3. Compile the Java files:
    ```bash
    javac *.java
    ```
4. To generate the parser, use JavaCC on the `parser.jj` file:
    ```bash
    javacc parser.jj
    ```

### Running the Compiler
After compilation, you can run the compiler on a sample MiniJava file:

```bash
java Main example.minijava
```

### Testing
To ensure the compiler works correctly, run tests with the sample files provided in the project.

## Authors
- Bruna Maia
- Domingos Mykaeull 
- Guilherme Fernandes

## License
This project is licensed under the terms of the MIT license.
