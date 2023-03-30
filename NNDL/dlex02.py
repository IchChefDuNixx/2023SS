# %% imports

# %% main
# 1

input = [-2, 5, -4]

def simple_function(x,y,z):
    q = x+y
    f = q*z
    print(f)
simple_function(*input)

def simple_backpropagation(x,y,z):
    print("df/dq: 1*z =", z)
    print("df/dz: q*1 =", x+y)
    print("dq/dx: 1+y =", 1+y)
    print("dq/dy: x+1 =", x+1)
    print("df/dx: df/dq * dq/dx =", z*(1+y))
    print("df/dy: df/dq * dq/dy =", z*(x+1))
    # df/dq = z
    # dq/dx * z
    # dq/dy * z
simple_backpropagation(*input)