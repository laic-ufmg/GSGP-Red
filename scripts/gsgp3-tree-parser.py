
# coding: utf-8

# In[ ]:

# Packages to import
import pandas as pd
import numpy as np

# In[ ]:

func_set = ['*', '+', '-', 'AQ']
delimiters = ["(", ")"]

# A binary tree node 
class Node:
    
    # Constructor to create a new node
    def __init__(self):
        self.data = None
        self.left = None
        self.right = None
        self.parent = None
    
    def __str__(self):
        output_str = ""
        
        if self.left:
            output_str += self.left.data
        else:
            output_str += "None"
        
        output_str += ", " + self.data + ", "
        
        if self.right:
            output_str += self.right.data
        else:
            output_str += "None"
        
        return output_str
    
    def __repr__(self):
        return self.__str__()
    
    def prefix_str(self):
        aux_stack = [self]
        output_str = ""   
        
        while aux_stack:
            node = aux_stack.pop()
            output_str += str(node.data) + " "
            if node.right != None:
                aux_stack.append(node.right)
            if node.left != None:
                aux_stack.append(node.left)
        return output_str.rstrip()
    
    def infix_str(self):
        aux_stack = []
        output_str = ""
        node = self
        while aux_stack or node:
            if node:
                aux_stack.append(node)
                node = node.left
            else:
                node = aux_stack.pop()
                output_str += str(node.data) + " "
                node = node.right                
        return output_str

def build_parse_tree(exp_string):
    # Split the string by white space
    token_stack = exp_string.split(" ")
    # Remove delimiters 
    token_stack = [token for token in token_stack if token not in delimiters]
    # Using the tokens list as a stack
    token_stack.reverse()
    # Root of the expression tree
    root  = Node()
    # We need an auxiliary stack to avoid recursion
    aux_stack = [root]
        
    #fmt = '{:<26}{:<15}{:<15}{}'
    #print(fmt.format('Token', '|Aux Stack|', '|Token Stack|', 'Token Stack'))
    
    while aux_stack and token_stack:
        token = token_stack.pop()
        node = aux_stack.pop()
        node.data = token
        
        #print(fmt.format(token, len(aux_stack), len(token_stack), debug_stack[-5:]))        
        
        if token in func_set:
            node.right = Node()
            node.right.parent = node
            aux_stack.append(node.right)
            node.left = Node()
            node.left.parent = node
            aux_stack.append(node.left)
    return root


# In[ ]:

exp = open('/home/luiz/Dados/Trabalho/Pesquisa/Projetos/GSGP3/scripts/airfoil.out', 'r').readline().rstrip()
#tree = build_parse_tree("+ ( * ( c1 t1 ) * ( c2 t2 ) )")
tree = build_parse_tree(exp)


# In[ ]:



print(tree.prefix_str())


# In[ ]:


def check_par(exp_string):
    # Split the string by white space
    token_stack = exp_string.split(" ")
    token_stack.reverse()
    space_stack = [0]
    while token_stack:
        token = token_stack.pop()
        if token not in [")", "("]:
            print(token, end = " ")
        if token == "(":
            space_stack.append(space_stack[-1] + 3)
            print("(\n" + " " * space_stack[-1], end = "")
        if token == ")":
            print("\n" + " " * (space_stack.pop() - 3) + ")")
            
    


# In[ ]:


def read_tokens(token_stack, tree):
    term = None
    
    #set_trace()
    
    while (term == None or term in delimiters) and token_stack:
        term = token_stack.pop()
    
    tree.data = term
    if term in func_set and term != None:
        tree.left = Node()
        read_tokens(token_stack, tree.left)
        tree.right = Node()
        read_tokens(token_stack, tree.right)

        