
# coding: utf-8

# In[ ]:

# Packages to import
import pandas as pd
from math import sqrt

# In[ ]:

operators = {'*': lambda x, y : x * y, 
            '+': lambda x, y : x + y, 
            '-': lambda x, y : x - y,
            'AQ': lambda x, y : x / sqrt(1 + y**2) 
            }
delimiters = ["(", ")"]

# A binary tree node 
class Node:
    
    # Constructor to create a new node
    def __init__(self, data = None):
        self.data = data
        self.left = None
        self.right = None
    
    def __str__(self):
#        output_str = ""
#        if self.left:
#            output_str += str(self.left.data)
#        else:
#            output_str += "None"
#        
#        output_str += "  <  " + str(self.data) + "  >  "
#        
#        if self.right:
#            output_str += str(self.right.data)
#        else:
#            output_str += "None"
        
        return str(self.data)
    
    def __repr__(self):
        return self.__str__()
    
    def is_leaf(self):
        return not (self.left or self.right)
    
    def is_operator(self):
        return self.data in operators
    
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
    
    def get_value(self, input_data):
        if self.data in input_data:
            new_node = Node(data = input_data[self.data])
        else:
            new_node = Node(data = float(self.data))
        return new_node
    
    def evaluate(self, input_data):
        # In case the tree is compposed by a constant
        if not self.is_operator():
            return self.get_value(input_data).data
        
        parent = self
        node = self.left
        stack = [self]
        
        while node:
            if node.is_operator():
                stack.append(node)
            else:
                stack.append(node.get_value(input_data))
            
            if node.is_leaf():
                # node is a left child
                if node == parent.left:
                    node = parent.right
                # node is a right child
                else:
                    while True:
                        term_2 = stack.pop().data
                        term_1 = stack.pop().data
                        op = operators[stack.pop().data]
                        result = op(term_1, term_2)
                        if not stack:
                            return result
                        else:
                            stack.append(Node(data = result))
                        
                        if not stack or stack[-2].is_operator():
                            node = stack[-2].right
                            break                    
            else:
                parent = node
                node = node.left
        
                
        
    
def lin_comb_from_gsgp3_tree(gsgp3_parse_tree):
     el_list = []
     node = gsgp3_parse_tree
     if not node or node.data != '+':
         raise SyntaxError("The root node must be an '+' operator.")
     while node and node.right.data == "+":
         lin_comb_term = node.left
         # Check if the coefficient + subtree is OK
         if lin_comb_term.data != "*":
             raise SyntaxError("The root of a linear combination term must be '*'.")
         if not lin_comb_term.left:
             raise SyntaxError("The coefficient of a linear combination term must not be 'None'.")
         if not lin_comb_term.right:
             raise SyntaxError("The function termo of a linear combination term must not be 'None'.")
         # Add the linear combination term to the list
         # Notice that the coefficient is implicitly cast to float
         el_list.append([float(lin_comb_term.left.data), lin_comb_term.right])
         node = node.right
         
     return el_list

    

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
        
        if token in operators:
            node.right = Node()
            aux_stack.append(node.right)
            node.left = Node()
            aux_stack.append(node.left)
    return root

# In[ ]:

exp = open('/home/luiz/Dados/Trabalho/Pesquisa/Projetos/GSGP3/scripts/airfoil.out', 'r').readline().rstrip()
sol = build_parse_tree(exp)
lin_comb = lin_comb_from_gsgp3_tree(sol)
df = pd.DataFrame(lin_comb, columns = ["Coeff", "Function"])

# In[]:

df = df.assign(Coeff_Abs = df.Coeff.abs())
df = df.assign(Coeff_Rel = df.Coeff_Abs / df.Coeff.sum())

# In[]:



# In[]:

data = pd.read_csv('/home/luiz/Dropbox/Pesquisa/luiz_otavio_operador_semantico/'
                   'dados/original/airfoil-train-0.dat', header = None)
# Rename columns according to variable names
names = ["x" + str(i) for i in range(data.shape[1]-1)] + ["y"]
data = data.rename(columns = lambda i : names[i])

n_col = data.shape[1]
for i in range(df.shape[0]):
    data.insert(n_col + i, "f" + str(i), data.apply(df.iloc[i,1].evaluate, axis = 1))