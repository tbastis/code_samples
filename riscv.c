#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include "linkedlist.h"
#include "hashtable.h"
#include "riscv.h"

const int R_TYPE = 0;
const int I_TYPE = 1;
const int MEM_TYPE = 2;
const int U_TYPE = 3;
const int UNKNOWN_TYPE = 4;

/**
 * Return the type of instruction for the given operation
 * Available options are R_TYPE, I_TYPE, MEM_TYPE, UNKNOWN_TYPE
 */
static int get_op_type(char *op)
{
    const char *r_type_op[] = {"add", "sub", "and", "or", "xor", "nor", "slt", "sll", "sra"};
    const char *i_type_op[] = {"addi", "andi", "ori", "xori", "slti"};
    const char *mem_type_op[] = {"lw", "lb", "sw", "sb"};
    const char *u_type_op[] = {"lui"};
    for (int i = 0; i < (int)(sizeof(r_type_op) / sizeof(char *)); i++)
    {
        if (strcmp(r_type_op[i], op) == 0)
        {
            return R_TYPE;
        }
    }
    for (int i = 0; i < (int)(sizeof(i_type_op) / sizeof(char *)); i++)
    {
        if (strcmp(i_type_op[i], op) == 0)
        {
            return I_TYPE;
        }
    }
    for (int i = 0; i < (int)(sizeof(mem_type_op) / sizeof(char *)); i++)
    {
        if (strcmp(mem_type_op[i], op) == 0)
        {
            return MEM_TYPE;
        }
    }
    for (int i = 0; i < (int)(sizeof(u_type_op) / sizeof(char *)); i++)
    {
        if (strcmp(u_type_op[i], op) == 0)
        {
            return U_TYPE;
        }
    }
    return UNKNOWN_TYPE;
}

registers_t *registers;
hashtable_t *memory;

void init(registers_t *starting_registers)
{
    registers = starting_registers;
    memory = ht_init(256);
}

// Removes all ' ' characters from string
char *remove_spaces(char *str)
{
    char *new_str = str;
    int counter = 0;
    for (int i = 0; str[i]; i++)
    {
        if (str[i] != ' ')
        {
            new_str[counter] = str[i];
            counter++;
        }
    }
    new_str[counter] = '\0';
    return new_str;
}

// Gets integer value of string containing either decimal or hex.
int get_immediate(char *str)
{
    if ((str[0] == '0') && (str[1] == 'x')) // positive hex
    {
        return strtol(&str[2], NULL, 16);
    }
    if ((str[0] == '-') && (str[1] == '0') && (str[2] == 'x')) // negative hex
    {
        return -1 * strtol(&str[3], NULL, 16);
    }

    return atoi(str); // Not hex, so must be decimal
}

void step(char *instruction)
{
    // Extracts and returns the substring before the first space character,
    // by replacing the space character with a null-terminator.
    // `instruction` now points to the next character after the space
    char *op = strsep(&instruction, " ");
    // Uses the provided helper function to determine the type of instruction
    int op_type = get_op_type(op);

    // Skip this instruction if it is not in our supported set of instructions
    if (op_type == UNKNOWN_TYPE)
    {
        return;
    }

    //remove all spaces from instruction
    char *reduced_ins = remove_spaces(instruction);

    if (op_type == R_TYPE)
    {
        // R-type format:
        // "xr1,xr2,xr3"

        char *store_reg_str = strsep(&reduced_ins, ","); //reduced_ins now contains "xr2,xr3"
        int store_reg = atoi(store_reg_str + 1);         //get int of r1

        char *arg1_reg_str = strsep(&reduced_ins, ","); //reduced_ins now contains "xr3"
        int arg1_reg = atoi(arg1_reg_str + 1);          //get int of r2

        char *arg2_reg_str = strsep(&reduced_ins, ","); //reduced_ins is now empty
        int arg2_reg = atoi(arg2_reg_str + 1);          //get int of r3

        int arg1_val = registers->r[arg1_reg]; //get value of r2
        int arg2_val = registers->r[arg2_reg]; //get value of r3

        if (strcmp(op, "add") == 0)
        {
            registers->r[store_reg] = arg1_val + arg2_val;
        }

        else if (strcmp(op, "sub") == 0)
        {
            registers->r[store_reg] = arg1_val - arg2_val;
        }

        else if (strcmp(op, "and") == 0)
        {
            registers->r[store_reg] = arg1_val & arg2_val;
        }

        else if (strcmp(op, "or") == 0)
        {
            registers->r[store_reg] = arg1_val | arg2_val;
        }

        else if (strcmp(op, "xor") == 0)
        {
            registers->r[store_reg] = arg1_val ^ arg2_val;
        }

        else if (strcmp(op, "nor") == 0)
        {
            registers->r[store_reg] = ~(arg1_val | arg2_val); // not (a or b)
        }

        else if (strcmp(op, "slt") == 0)
        {
            registers->r[store_reg] = (arg1_val < arg2_val) ? 1 : 0;
        }

        else if (strcmp(op, "sll") == 0)
        {
            registers->r[store_reg] = arg1_val << (0x1f & arg2_val); //shift by amount in lower 5 bits
        }

        else if (strcmp(op, "sra") == 0)
        {
            registers->r[store_reg] = arg1_val >> (0x1f & arg2_val); //shift by amount in lower 5 bits
        }
    }

    else if (op_type == I_TYPE)
    {
        // I-type format:
        // "xr1,xr2,imm"

        char *store_reg_str = strsep(&reduced_ins, ","); //reduced_ins now contains:"xr2,imm"
        int store_reg = atoi(store_reg_str + 1);         //get int of r1

        char *arg1_reg_str = strsep(&reduced_ins, ","); //reduced_ins now contains "imm"
        int arg1_reg = atoi(arg1_reg_str + 1);          //get int of r2

        int arg1_val = registers->r[arg1_reg]; //get value of r2
        int imm = get_immediate(reduced_ins);  //get value of imm

        if (strcmp(op, "addi") == 0)
        {
            registers->r[store_reg] = arg1_val + imm;
        }

        else if (strcmp(op, "andi") == 0)
        {
            registers->r[store_reg] = arg1_val & imm;
        }

        else if (strcmp(op, "ori") == 0)
        {
            registers->r[store_reg] = arg1_val | imm;
        }

        else if (strcmp(op, "xori") == 0)
        {
            registers->r[store_reg] = arg1_val ^ imm;
        }

        else if (strcmp(op, "slti") == 0)
        {
            registers->r[store_reg] = (arg1_val < imm) ? 1 : 0;
        }
    }

    else if (op_type == MEM_TYPE)
    {
        // Mem-type formats:
        // "xr1,offset(r2)"

        char *reg1_str = strsep(&reduced_ins, ","); //reduced_ins now contains "offset(r2)"
        int reg1 = atoi(reg1_str + 1);              //get int of r1

        char *offset_str = strsep(&reduced_ins, "("); //reduced_ins now contains "r2)"
        int offset = get_immediate(offset_str);       //get value of offset

        char *reg2_str = strsep(&reduced_ins, ")"); //reduced_ins is now empty
        int reg2 = atoi(reg2_str + 1);              //get int of r2

        int address = registers->r[reg2] + offset; //address in memory to load from/store to

        if (strcmp(op, "lw") == 0)
        {
            int word = 0;
            word += ht_get(memory, address);           //get least significant byte
            word += ht_get(memory, address + 1) << 8;  //want next byte, so we offset by 8 before adding
            word += ht_get(memory, address + 2) << 16; //then offset by 16
            word += ht_get(memory, address + 3) << 24; //finally offset by 24
            registers->r[reg1] = word;                 //all together, word contains the entire word from memory.
        }

        else if (strcmp(op, "lb") == 0)
        {
            int byte = ht_get(memory, address);      //get only the byte we want from memory
            registers->r[reg1] = (byte << 24) >> 24; //sign extend from 8 to 32
        }

        else if (strcmp(op, "sw") == 0)
        {
            int word = registers->r[reg1];                    // word we want to store
            ht_add(memory, address, word & 0xff);             // store the least significant byte
            ht_add(memory, address + 1, (word >> 8) & 0xff);  // then store the next byte by shifting right 8 bits
            ht_add(memory, address + 2, (word >> 16) & 0xff); // then shift by 16
            ht_add(memory, address + 3, (word >> 24) & 0xff); // finally shift by 24
            //all together, word has been added in its entirety to our byte-addressed memory.
        }

        else if (strcmp(op, "sb") == 0)
        {
            int byte = registers->r[reg2] & 0xff; // we only want the least significant byte
            ht_add(memory, address, byte);        // store that byte
        }
    }

    else if (op_type == U_TYPE)
    {
        // U-type format:
        // "xr1,imm"

        char *store_reg_str = strsep(&reduced_ins, ","); //reduced_ins now contains "imm"

        int store_reg = atoi(store_reg_str + 1); //get int of r1
        int imm = get_immediate(reduced_ins);    //get value of imm

        registers->r[store_reg] = imm << 12; //only U-type instruction we support is "lui"
    }

    registers->r[0] = 0; // If the instruction stored anything in x0, we need to nullify this.
    return;
}
