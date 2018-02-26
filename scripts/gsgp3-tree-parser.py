
# coding: utf-8

# In[ ]:

# Packages to import
import csv
from math import sqrt

# In[ ]:
def f_add(a, b):
    return a + b

def f_sub(a, b):
    return a - b
            
def f_mul(a, b):
    return a * b

def f_aq(a, b):
    return a / sqrt(1 + b**2)  

delimiters = ["(", ")"]

# A binary tree node 
class Node:    
    operators = {'*': f_mul,
                 '+': f_add,
                 '-': f_sub,
                 'AQ': f_aq
                 }
    node_types = {"None": 0,
                  "Operator": 1,
                  "Input data": 2,
                  "Constant": 3,
                  }
    
    # Constructor to create a new node
    def __init__(self, data = None):
        self.data = data
        self.left = None
        self.right = None
    
    @property
    def data(self):
        return self.__data
    
    @data.setter
    def data(self, data):
        if not data:
            self.type = Node.node_types["None"]
            self.__data = None
        elif data in Node.operators:
            self.type = Node.node_types["Operator"]
            self.__data = Node.operators[data]
        elif data.startswith("x"):
            self.type = Node.node_types["Input data"]
            self.__data = int(data.replace("x", ""))
        else:
            self.type = Node.node_types["Constant"]
            self.__data = float(data)
    
    def __str__(self):
        return str(self.data)
#        if self.left and self.right:
#            return str(self.left.data) + " | " + str(self.data) + " | " + str(self.right.data)
#        if self.left:
#            return str(self.left.data) + " | " + str(self.data) + " | None" 
#        if self.right:
#            return "None | " + str(self.data) + " | " + str(self.right.data)
#        return "None | " + str(self.data) + " | None"
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
    
    def __repr__(self):
        return self.__str__()
    
    def is_leaf(self):
        return not (self.left or self.right)
    
    def is_operator(self):
        return self.type == Node.node_types["Operator"]
    
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
    
    def evaluate(self, input_data):
        if self.type == Node.node_types["Operator"]:
            return self.data(self.left.evaluate(input_data),
                             self.right.evaluate(input_data))
        elif self.type == Node.node_types["Constant"]:
            return self.data
        elif self.type == Node.node_types["Input data"]:
            return input_data[self.data]
        else:
            raise SyntaxError("The node is empty.")
    
    def evaluate_iterative(self, input_data):
        # In case the tree is compposed by a constant
        if not self.is_operator():
            return self.evaluate(input_data)
        
        parent = self
        node = self.left
        stack = [self]
        
        while node:
            stack.append(node)
            
            if node.is_leaf():
                # node is a left child
                if node == parent.left:
                    node = parent.right
                # node is a right child
                else:
                    while True:
                        term_2 = stack.pop().evaluate(input_data)
                        term_1 = stack.pop().evaluate(input_data)
                        op = stack.pop().data
                        result = op(term_1, term_2)
                        if not stack:
                            return result
                        else:
                            stack.append(Node(data = str(result)))
                        
                        if not stack or stack[-2].is_operator():
                            node = stack[-2].right
                            break                    
            else:
                parent = node
                node = node.left
        
      
    
def lin_comb_from_gsgp3_tree(gsgp3_parse_tree):
    func_lst = []
    coeff_lst = []
    node = gsgp3_parse_tree
    if not node or node.data != Node.operators["+"]:
        raise SyntaxError("The root node must be an '+' operator.")
    while node and node.right.data == Node.operators["+"]:
        lin_comb_term = node.left
        # Check if the coefficient + subtree is OK
        if lin_comb_term.data != Node.operators["*"]:
            raise SyntaxError("The root of a linear combination term must be '*'.")
        if not lin_comb_term.left:
            raise SyntaxError("The coefficient of a linear combination term must not be 'None'.")
        if not lin_comb_term.right:
            raise SyntaxError("The function termo of a linear combination term must not be 'None'.")
        # Add the linear combination term to the list
        # Notice that the coefficient is implicitly cast to float
        coeff_lst.append(float(lin_comb_term.left.data))
        func_lst.append(lin_comb_term.right)
        node = node.right
        
    return [coeff_lst, func_lst]

    

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
        
    while aux_stack and token_stack:
        token = token_stack.pop()
        node = aux_stack.pop()
        node.data = token
        if token in Node.operators:
            node.right = Node()
            aux_stack.append(node.right)
            node.left = Node()
            aux_stack.append(node.left)
    return root

def read_csv(path):
    with open(path, 'r') as csvfile:
        csv_obj = csv.reader(csvfile, delimiter=',')
        data_table = []
        for row in csv_obj:
            data_table.append([float(i) for i in row])
    return data_table

# In[ ]:

exp = open('/home/luiz/Dados/Trabalho/Pesquisa/Projetos/GSGP3/scripts/airfoil.out', 'r').readline().rstrip()
sol = build_parse_tree(exp)
lin_comb = lin_comb_from_gsgp3_tree(sol)
#df = pd.DataFrame(lin_comb, columns = ["Coeff", "Function"])
#df = df.assign(Coeff_Abs = df.Coeff.abs())
#df = df.assign(Prop_Coeff_Rel = df.Coeff_Abs / df.Coeff_Abs.sum())
#df = df.assign(Prop_Coeff = df.Coeff / df.Coeff.sum())
#names = df.columns.tolist()
#df = df[[names[i] for i in [1, 0, 4, 2, 3]]]

# In[]:
data = read_csv('/home/luiz/Dropbox/Pesquisa/luiz_otavio_operador_semantico/'
                'dados/original/airfoil-train-0.dat')

#pd.read_csv('/home/luiz/Dropbox/Pesquisa/luiz_otavio_operador_semantico/'
#                   'dados/original/airfoil-test-0.dat', header = None)
# Rename columns according to variable names
#names = ["x" + str(i) for i in range(data.shape[1]-1)] + ["y"]
#data = data.rename(columns = lambda i : names[i])

#y_hat = pd.DataFrame(np.zeros(data.shape[0]))
#n_col = data.shape[1]


# In[]:

import timeit

start_time = timeit.default_timer()
rmse = 0

for i in range(len(data)):
    err = -data[i][-1]
    for j in range(len(lin_comb[0])):
        err += lin_comb[0][j] * lin_comb[1][j].evaluate(data[i])
    rmse += (err * err)
    if i % 10 == 0:
        print("Function " + str(i))

print(timeit.default_timer() - start_time, "seconds")
    
rmse = sqrt(rmse / len(data))
print(rmse)

# In[]:
sol = build_parse_tree("AQ 0.0508 AQ 15.4 + - - 15.4 400 + 400 39.6 400")
sol.evaluate_iterative([])

# In[]:

#n_col = data.shape[1]
#for i in range(df.shape[0]):
#    data.insert(n_col + i, "f" + str(i), data.apply(df.iloc[i,1].evaluate, axis = 1))